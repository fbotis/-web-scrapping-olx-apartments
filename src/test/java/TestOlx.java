import olx.analytics.common.http.ProxyHttpClient;
import apart.scrapping.olx.dao.model.ApartamentOlx;
import com.google.gson.Gson;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import parser.xml.XHTMLUtils;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class TestOlx {

	private final ProxyHttpClient proxyHttpClient;
	private final Transformer transformer;

	public TestOlx() throws Exception {
		this.proxyHttpClient = new ProxyHttpClient(10, 5L, 1);
		TransformerFactory factory = TransformerFactory.newInstance();
		StreamSource xslStream = new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream("olx/apartament_olx_fetcher.xsl"));
		transformer = factory.newTransformer(xslStream);
	}

	public void test() throws InterruptedException, IOException, SAXException, TransformerException {
		String json = fetchTransformedJson("https://www.olx.ro/oferta/schimb-apartament-3-camere-50-m-p-ID7XYc3.html#1fbc697c25");
		System.out.println(json);
		ApartamentOlx apartamentOlx = new Gson().fromJson(json, ApartamentOlx.class);
		apartamentOlx.fixFetchedAndPublishedDates();
		;

		System.out.println(apartamentOlx);

	}

	private String fetchTransformedJson(String searchUrl)
			throws TransformerException, IOException, SAXException, InterruptedException {
		try (InputStream inputStream = fetchInputStreamFromUrl(searchUrl)) {
			Document doc = XHTMLUtils.parse(inputStream, "UTF-8", false);
			DOMSource in = new DOMSource(doc);
			StringWriter sw = new StringWriter();
			StreamResult out = new StreamResult(sw);
			transformer.transform(in, out);
			return sw.toString().replaceAll("<.+>", "");
		}
	}

	private InputStream fetchInputStreamFromUrl(String searchUrl) throws IOException, InterruptedException {
		return proxyHttpClient.get(searchUrl);
	}

	public static void main(String[] args) throws Exception {
		new TestOlx().test();
	}
}
