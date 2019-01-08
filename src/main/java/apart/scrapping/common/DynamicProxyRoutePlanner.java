package apart.scrapping.common;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicProxyRoutePlanner extends DefaultHttpRequestRetryHandler implements HttpRoutePlanner {

	private Map<String, Integer> proxies = new ConcurrentHashMap<>();
	private HttpHost randomProxy;

	public DynamicProxyRoutePlanner() {
	}

	public HttpRoute determineRoute(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
		return new HttpRoute(target, getRandomProxy());
	}

	public HttpHost getRandomProxy() {
		return randomProxy;
	}
}
