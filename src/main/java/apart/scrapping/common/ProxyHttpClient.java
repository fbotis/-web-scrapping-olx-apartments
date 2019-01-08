package apart.scrapping.common;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ProxyHttpClient {

	private static final long TIME_TO_WAIT_FOR_PROXY_AVAILABLE_MS = 10000L;

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Integer maxRetries;
	private final Long waitBetweenRetries;
	private final Integer maxProxyFailuresBeforeRemove;
	private final Random random;
	private final LinkedList<HttpClient> proxyClients;
	private Map<HttpClient, Integer> proxyFailures = new ConcurrentHashMap<>();

	public ProxyHttpClient(Integer retries, Long waitBetweenRetries, Integer maxProxyFailuresBeforeRemove)
			throws IOException {
		this.maxRetries = retries;
		this.waitBetweenRetries = waitBetweenRetries;
		this.maxProxyFailuresBeforeRemove = maxProxyFailuresBeforeRemove;
		this.proxyClients = new LinkedList<>();
		this.random = new Random();
		Files.lines(Paths.get(Thread.currentThread().getContextClassLoader().getResource(("common/proxies.txt")).getPath()))
				.filter(line->line.split(":").length > 1)
				.forEach(line->{
					String[] ipPort = line.trim().split(":");
					proxyClients.add(createHttpClientWithProxy(ipPort[0].trim(), Integer.parseInt(ipPort[1].trim())));
				});
	}

	private HttpClient createHttpClientWithProxy(String proxyHost, int proxyPort) {
		return HttpClientBuilder.create()
				.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36")
				.setProxy(new HttpHost(proxyHost, proxyPort))
				.setRetryHandler((exception, executionCount, context)->false)
				.build();
	}

	public InputStream get(String url) throws InterruptedException {
		return get(url, 0);
	}

	private InputStream get(String url, int i) throws InterruptedException {
		if (i > 0) {
			log.info("Retrying retry{} call to url={}", i, url);
		}
		HttpClient httpClient = null;
		int randomProxyIndex = random.nextInt(proxyClients.size());
		synchronized (proxyClients) {
			httpClient = proxyClients.get(randomProxyIndex);
			proxyClients.remove(randomProxyIndex);
		}

		if (httpClient == null) {
			throw new RuntimeException("No http client found int he pool after milsecods=" + TIME_TO_WAIT_FOR_PROXY_AVAILABLE_MS);
		}

		try {
			HttpGet get = new HttpGet(url);
			HttpResponse response = httpClient.execute(get);
			if (response.getStatusLine().getStatusCode() >= 400) {
				throw new IOException("Invalid status code={}" + response.getStatusLine().getStatusCode());
			}
			return response.getEntity().getContent();
		} catch (IOException ex) {
			log.warn("Error while calling url={} msg={}", url, ex.getMessage());
			incrementProxyFailure(httpClient);
			if (i < maxRetries) {
				TimeUnit.MILLISECONDS.sleep(waitBetweenRetries);
				return get(url, i + 1);
			} else{
				log.error("Request for URL={} reached maxRetries={}", url, maxRetries);
			}
		} finally {
			synchronized (proxyClients) {
				if (proxyFailures.getOrDefault(httpClient, 0) < maxProxyFailuresBeforeRemove) {
					proxyClients.add(httpClient);
				} else{
					log.warn("Removing http client={} from the pool", httpClient);
				}
			}
		}
		return null;
	}

	private void incrementProxyFailure(HttpClient httpClient) {
		synchronized (proxyClients) {
			proxyFailures.put(httpClient, proxyFailures.getOrDefault(httpClient, 0) + 1);
		}
	}

}
