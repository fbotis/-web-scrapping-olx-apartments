package parser.olx.parsing;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import parser.olx.dao.OlxApartmentRepository;
import parser.olx.dao.OlxItemLinkRepository;
import parser.olx.dao.model.OlxApartament;
import parser.xml.XHTMLUtils;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ApartmentFetcher {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Transformer transformer;
	private final OlxApartmentRepository olxApartmentRepository;
	private final OlxItemLinkRepository olxItemLinkRepository;

	public ApartmentFetcher(OlxItemLinkRepository olxItemLinkRepository, OlxApartmentRepository olxApartmentRepository, OlxApartmentRepository olxApartmentRepository1)
			throws TransformerConfigurationException {
		this.olxItemLinkRepository = olxItemLinkRepository;
		this.olxApartmentRepository = olxApartmentRepository1;
		TransformerFactory factory = TransformerFactory.newInstance();
		StreamSource xslStream = new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream("fetch_apartment.xsl"));
		transformer = factory.newTransformer(xslStream);
	}

	public void startFetching() {
		olxItemLinkRepository.findAll().forEach(
				olxItemLink->{
					if (!olxApartmentRepository.existsById(olxItemLink.getId())) {
						try {
							fetch(olxItemLink.getLink());
						} catch (Exception e) {
							log.error("Error", e);
						}
					}

				}
		);

	}

	public void fetch(String startUrl) throws IOException, SAXException, TransformerException {
		log.info("Fetching url={} \n page={}", startUrl);
		URL url = new URL(startUrl);
		Document doc = XHTMLUtils.parse(url.openStream(), "UTF-8", false);
		DOMSource in = new DOMSource(doc);
		StringWriter sw = new StringWriter();
		StreamResult out = new StreamResult(sw);
		transformer.transform(in, out);

		//
		Gson gson = new Gson();
		String json = sw.toString().replaceAll("<.+>", "");
		OlxApartament olxApartament = gson.fromJson(json, OlxApartament.class);
		olxApartament.setLink(startUrl);
		olxApartament.fixFetchDateAndId();
		validateFields(olxApartament);




		olxApartmentRepository.save(olxApartament);
	}

	private void validateFields(OlxApartament olxApartament) {
		if (Strings.isNullOrEmpty(olxApartament.getTitle()) ||
				olxApartament.getId() == null ||
				Strings.isNullOrEmpty(olxApartament.getOfferedBy()) ||
				Strings.isNullOrEmpty(olxApartament.getSuprafata()) ||
				Strings.isNullOrEmpty(olxApartament.getPrice())) {
			throw new RuntimeException("Invalid apartment=" + olxApartament);
		}
	}
}
