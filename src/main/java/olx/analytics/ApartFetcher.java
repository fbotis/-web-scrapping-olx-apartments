package olx.analytics;

import com.google.gson.Gson;
import olx.analytics.common.http.ProxyHttpClient;
import olx.analytics.model.ApartamentOlx;
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
import java.util.concurrent.*;

@Component
public class ApartFetcher {

	private static final long SECONDS_TO_WAIT_BETWEEN_REQUESTS = 3;
	private static final int THREADS_NO = 3;
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final ProxyHttpClient proxyHttpClient;
	private final BlockingQueue<Transformer> transformers = new LinkedBlockingQueue<>();
	private final ExecutorService thPool = Executors.newFixedThreadPool(THREADS_NO);

	public ApartFetcher()
			throws IOException, TransformerConfigurationException {
		this.proxyHttpClient = new ProxyHttpClient(10, 5L, 1);
		TransformerFactory factory = TransformerFactory.newInstance();
		for (int i = 0; i < THREADS_NO; i++) {
			StreamSource xslStream = new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream("olx_analytics/apartament_olx_fetcher.xsl"));
			transformers.add(factory.newTransformer(xslStream));
		}
	}

	private ApartamentOlx fetchLink(String link)
			throws InterruptedException, TransformerException, IOException, SAXException {
		log.info("Fetching from url={}", link);
		TimeUnit.SECONDS.sleep(SECONDS_TO_WAIT_BETWEEN_REQUESTS);
		String json = fetchTransformedJson(link);
		System.out.println(json);
		return null;
		//return transformJsonToJava(json);
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
			//	printDoc(doc);
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

	private void printDoc(Document doc) throws TransformerException {
		DOMSource domSource = new DOMSource(doc);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform(domSource, result);
		System.out.println(writer.toString());
	}

	private InputStream fetchInputStreamFromUrl(String searchUrl) throws IOException, InterruptedException {
		return proxyHttpClient.get(searchUrl);
	}

	public static void main(String[] args)
			throws IOException, TransformerException, InterruptedException, SAXException {
		ApartFetcher apartFetcher = new ApartFetcher();
		System.out.println(apartFetcher.fetchLink("https://www.olx.ro/oferta/parcare-apartament-modern-cu-3-camere-zorilor-strada-mircea-eliade-IDdrIKF.html#2423e5cf43"));
	}

}
