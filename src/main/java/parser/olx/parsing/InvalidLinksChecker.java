package parser.olx.parsing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import parser.olx.dao.OlxApartmentRepository;
import parser.olx.dao.OlxItemLinkRepository;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Component
public class InvalidLinksChecker {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final OlxApartmentRepository olxApartmentRepository;
	private final OlxItemLinkRepository olxItemLinkRepository;

	public InvalidLinksChecker(OlxApartmentRepository olxApartmentRepository, OlxItemLinkRepository olxItemLinkRepository) {
		this.olxApartmentRepository = olxApartmentRepository;
		this.olxItemLinkRepository = olxItemLinkRepository;
	}

	public void check() {
		olxItemLinkRepository.findAll()
				.forEach(
						olxItemLink->{
							if (!olxApartmentRepository.existsById(olxItemLink.getId())) {
								try {
									if (!linkValid(olxItemLink.getLink())) {
										olxItemLinkRepository.deleteById(olxItemLink.getId());
									}
								} catch (IOException e) {
									log.error("", e);
								}
							}
						}
				);
	}

	private boolean linkValid(String link) throws IOException {
		String page = readStringFromURL(link);
		if (page.contains("nu mai este activ") || page.contains("nu mai este disponibil")) {
			log.info("Link={} invalid", link);
			return false;
		}
		return true;

	}

	public static String readStringFromURL(String requestURL) throws IOException {
		try (Scanner scanner = new Scanner(new URL(requestURL).openStream(),
				StandardCharsets.UTF_8.toString())) {
			scanner.useDelimiter("\\A");
			return scanner.hasNext() ? scanner.next() : "";
		}
	}
}

