package parser.olx.parsing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import parser.olx.dao.OlxItemLinkRepository;
import parser.olx.dao.model.OlxItemLinks;
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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemLinksFetcher {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Transformer transformer;
	private final OlxItemLinkRepository olxItemLinkRepository;

	public ItemLinksFetcher(OlxItemLinkRepository olxItemLinkRepository) throws TransformerConfigurationException {
		this.olxItemLinkRepository = olxItemLinkRepository;
		TransformerFactory factory = TransformerFactory.newInstance();
		StreamSource xslStream = new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream("fetch_item_links.xsl"));
		transformer = factory.newTransformer(xslStream);
	}

	public void fetch(String startUrl, Integer page) throws IOException, SAXException, TransformerException {
		log.info("Fetching url={} \n page={}", startUrl, page);
		URL url = new URL(startUrl + "?page=" + page);
		Document doc = XHTMLUtils.parse(url.openStream(), "UTF-8", false);
		DOMSource in = new DOMSource(doc);
		StringWriter sw = new StringWriter();
		StreamResult out = new StreamResult(sw);
		transformer.transform(in, out);

		//
		Gson gson = new Gson();
		String json = sw.toString().replaceAll("<.+>", "");
		System.out.println(json);
		OlxItemLinks olxItemLinks = gson.fromJson(json, OlxItemLinks.class);

		log.info("Found items={}", olxItemLinks.getItems().size());

		final AtomicInteger duplicates = new AtomicInteger();
		olxItemLinks.getItems().stream()
				.filter(Objects::nonNull)
				.forEach(olxItemLink->{
					if (olxItemLinkRepository.existsById(olxItemLink.getId())) {
						duplicates.incrementAndGet();
					} else{
						olxItemLink.fixPublishedDate();
						olxItemLink.setCategory(startUrl);
						olxItemLinkRepository.save(olxItemLink);
					}
				});

		log.info("Duplicates found in db={}", duplicates);

		long nonNullItemsCount = olxItemLinks.getItems().stream().filter(Objects::nonNull).count();
		if (duplicates.get() != nonNullItemsCount) {
			fetch(startUrl, ++page);
		}else{
			log.info("Stopping the fetching of items because of duplicates");
		}

	}

}
