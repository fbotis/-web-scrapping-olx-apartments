package apart.scrapping.olx.fetch;

import apart.scrapping.common.ProxyHttpClient;
import apart.scrapping.olx.dao.ApartamentOlxRepository;
import apart.scrapping.olx.dao.LinksApartamentOlxRepository;
import apart.scrapping.olx.dao.model.ApartamentOlx;
import apart.scrapping.olx.dao.model.LinkApartamentOlx;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
public class ApartamentOlxFetcher {

	private static final long SECONDS_TO_WAIT_BETWEEN_REQUESTS = 3;
	private static final int THREADS_NO = 3;
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final ApartamentOlxRepository apartamentOlxRepository;
	private final LinksApartamentOlxRepository linksApartamentOlxRepository;
	private final ProxyHttpClient proxyHttpClient;
	private final BlockingQueue<Transformer> transformers = new LinkedBlockingQueue<>();
	private final ExecutorService thPool = Executors.newFixedThreadPool(THREADS_NO);

	public ApartamentOlxFetcher(ApartamentOlxRepository apartamentOlxRepository, LinksApartamentOlxRepository linksApartamentOlxRepository)
			throws IOException, TransformerConfigurationException {
		this.apartamentOlxRepository = apartamentOlxRepository;
		this.linksApartamentOlxRepository = linksApartamentOlxRepository;
		this.proxyHttpClient = new ProxyHttpClient(10, 5L, 1);
		TransformerFactory factory = TransformerFactory.newInstance();
		for (int i = 0;i < THREADS_NO;i++) {
			StreamSource xslStream = new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream("olx/apartament_olx_fetcher.xsl"));
			transformers.add(factory.newTransformer(xslStream));
		}
	}

	public void fetch() throws InterruptedException, TransformerException, IOException, SAXException {
		for (LinkApartamentOlx link : linksApartamentOlxRepository.findByProcessed(false)) {
			if (!link.isProcessed()) {
				thPool.submit(()->{
					try {
						ApartamentOlx apartamentOlx = fetchLink(link);
						apartamentOlx.setId(link.getId());
						apartamentOlx.setLink(link.getLink());
						apartamentOlx.fixFetchedAndPublishedDates();
						apartamentOlxRepository.save(apartamentOlx);
						link.setProcessed(true);
						linksApartamentOlxRepository.save(link);
					} catch (Exception ex) {
						log.error("Failed fetching {} ", link, ex);
					}
				});
			}

		}
	}

	private ApartamentOlx fetchLink(LinkApartamentOlx link)
			throws InterruptedException, TransformerException, IOException, SAXException {
		log.info("Fetching from url={}", link.getLink());
		TimeUnit.SECONDS.sleep(SECONDS_TO_WAIT_BETWEEN_REQUESTS);
		String json = fetchTransformedJson(link.getLink());
		return transformJsonToJava(json);
	}

	private ApartamentOlx transformJsonToJava(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, ApartamentOlx.class);
	}

	private String fetchTransformedJson(String searchUrl)
			throws TransformerException, IOException, SAXException, InterruptedException {
		Transformer transformer = transformers.poll();
		try {
			try (InputStream inputStream = fetchInputStreamFromUrl(searchUrl);) {
				Document doc = XHTMLUtils.parse(inputStream, "UTF-8", false);
				DOMSource in = new DOMSource(doc);
				StringWriter sw = new StringWriter();
				StreamResult out = new StreamResult(sw);
				transformer.transform(in, out);
				return sw.toString().replaceAll("<.+>", "");
			}
		} finally {
			transformers.add(transformer);
		}

	}

	private InputStream fetchInputStreamFromUrl(String searchUrl) throws IOException, InterruptedException {
		return proxyHttpClient.get(searchUrl);
	}

}
