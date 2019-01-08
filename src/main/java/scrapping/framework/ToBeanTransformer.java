package scrapping.framework;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class ToBeanTransformer<T> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Transformer transformer;
	private final Class<T> type;
	private final HttpClient httpClient;

	public ToBeanTransformer(String xsltTemplatePath, Class<T> type, HttpClient httpClient)
			throws TransformerConfigurationException {
		this.type = type;
		this.httpClient = httpClient;
		TransformerFactory factory = TransformerFactory.newInstance();
		StreamSource xslStream = new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(xsltTemplatePath));
		transformer = factory.newTransformer(xslStream);
	}

	public T scrapePage(String url) throws IOException, SAXException, TransformerException {
		try (InputStream inputStream = httpClient.get(url)) {
			Document doc = XHTMLUtils.parse(inputStream, "UTF-8", false);
			DOMSource in = new DOMSource(doc);
			StringWriter sw = new StringWriter();
			StreamResult out = new StreamResult(sw);
			transformer.transform(in, out);
			String json = sw.toString().replaceAll("<.+>", "");
			log.debug("Json for url={} \n{}", url, json);
			return transformJsonToJava(json);
		}
	}

	private T transformJsonToJava(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, type);
	}

}
