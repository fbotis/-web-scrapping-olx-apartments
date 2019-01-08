package parser.nemutam;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import parser.nemutam.dao.NeMutamAp;
import parser.nemutam.dao.NeMutamApList;
import parser.nemutam.dao.NeMutamApRepository;
import parser.xml.XHTMLUtils;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class NetMutamFetcherWithProxy {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private final Transformer transformer;

	private Map<String, Integer> proxiesMap = new HashMap<>();
	private Map<String, Integer> proxiesError = new HashMap<>();

	private final NeMutamApRepository neMutamApRepository;

	public NetMutamFetcherWithProxy(NeMutamApRepository neMutamApRepository)
			throws IOException, TransformerConfigurationException {
		this.neMutamApRepository = neMutamApRepository;
		Files.lines(Paths.get("/Users/fbotis/PERSONAL/parsing/parser/src/main/resources/nemutam/proxies.txt"))
				.filter(line->line.split(",").length > 1)
				.forEach(line->{
					String[] ipPort = line.split(",");
					proxiesMap.put(ipPort[0], Integer.parseInt(ipPort[1]));
				});
		TransformerFactory factory = TransformerFactory.newInstance();
		StreamSource xslStream = new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream("nemutam/nemutam_fetch.xsl"));
		transformer = factory.newTransformer(xslStream);
	}

	public void fetch(Integer page, Integer retryCnt) throws SAXException, TransformerException {
		Map.Entry<String, Integer> prxDetails = proxiesMap.entrySet().stream().collect(Collectors.toList()).get(new Random().nextInt(proxiesMap.size()));
		try {
			log.info("Start fetching page={} using proxy address={} port={}", page, prxDetails.getKey(), prxDetails.getValue());
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(prxDetails.getKey(), prxDetails.getValue()));
			URLConnection conn = new URL("https://nemutam.com/?pag=" + page).openConnection(proxy);

			try (InputStream inputStream = conn.getInputStream()) {
				Document doc = XHTMLUtils.parse(inputStream, "UTF-8", false);
				DOMSource in = new DOMSource(doc);
				StringWriter sw = new StringWriter();
				StreamResult out = new StreamResult(sw);
				transformer.transform(in, out);

				//
				Gson gson = new Gson();
				String json = sw.toString().replaceAll("<.+>", "");
				NeMutamApList neMutamApList = gson.fromJson(json, NeMutamApList.class);
				List<NeMutamAp> neMutamAps = neMutamApList.getItems().stream().filter(Objects::nonNull).collect(Collectors.toList());

				final AtomicInteger duplicates = new AtomicInteger();
				neMutamAps.stream()
						.filter(Objects::nonNull)
						.filter(item->{
							if (!neMutamApRepository.existsById(item.getLink())) {
								return true;
							} else{
								duplicates.incrementAndGet();
								return false;
							}
						})
						.forEach(neMutamApRepository::save);

				log.info("Found items={} of which duplicates={}", neMutamAps.size(), duplicates);
				if (duplicates.get() == neMutamAps.size()) {
					log.info("Only duplicates found, stop");
				} else{
					fetch(page + 1, 0);
				}
			}
		} catch (IOException ex) {
			log.error("IOException while fetching data.Retrying retry={}", ex, retryCnt);
			Integer errors = Optional.ofNullable(proxiesError.get(prxDetails.getKey())).orElse(0)+1;
			proxiesError.put(prxDetails.getKey(), errors);
			if (errors > 2) {
				log.info("Proxy removed from the map because too many errors prxyAddress={}", prxDetails.getKey());
				proxiesMap.remove(prxDetails.getKey());
			}
			if (retryCnt < 10) {
				fetch(page, retryCnt + 1);
			} else{
				log.error("Failed after 10 retries.STOP");
			}
		}

	}
}
