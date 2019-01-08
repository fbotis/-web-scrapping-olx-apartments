package apart.scrapping.olx.fetch;

import apart.scrapping.common.CartierCluj;
import apart.scrapping.common.ProxyHttpClient;
import apart.scrapping.olx.dao.LinksApartamentOlxRepository;
import apart.scrapping.olx.dao.model.LinkApartamentOlx;
import apart.scrapping.olx.dao.model.LinksApartamentOlx;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class LinkApartamentOlxFetcher {

	private static final long SECONDS_TO_WAIT_BETWEEN_REQUESTS = 3;
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Transformer transformer;
	private final LinksApartamentOlxRepository linksApartamentOlxRepository;
	private final ProxyHttpClient proxyHttpClient;

	public LinkApartamentOlxFetcher(LinksApartamentOlxRepository linksApartamentOlxRepository)
			throws TransformerConfigurationException, IOException {
		this.proxyHttpClient = new ProxyHttpClient(10, 1000L, 1);
		this.linksApartamentOlxRepository = linksApartamentOlxRepository;
		TransformerFactory factory = TransformerFactory.newInstance();
		StreamSource xslStream = new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream("olx/link_apartament_olx_fetcher.xsl"));
		transformer = factory.newTransformer(xslStream);
	}

	public void fetch(String startUrl) throws IOException, SAXException, TransformerException {
		Stream.of(CartierCluj.values())
				.forEach(cartier->
						Stream.of("1-camera", "2-camere", "3-camere", "4-camere").forEach(
								camere->
										Stream.of(cartier.getKeywords()).forEach(cartierKeyword->{
											try {
												fetchAdsFor(cartier, cartierKeyword, camere, 1);
											} catch (FileNotFoundException ex) {

											} catch (Exception e) {
												log.error("Error while fetching keyword={}  camere={}", cartierKeyword, camere, e);
											}
										})
						)
				);
	}

	public void fetchNonCartier(String startUrl, boolean dontStopUntilFinish)
			throws IOException, SAXException, TransformerException, InterruptedException {
		fetchAll(startUrl, 1, dontStopUntilFinish);
	}

	private void fetchAll(String startUrl, Integer page, Boolean dontStopUntilFinish)
			throws InterruptedException, SAXException, TransformerException, IOException {
		String url = startUrl;
		if (page > 1) {
			url += "/?page=" + page;
		}
		LinksApartamentOlx links = scrapeLinksWithNextPageInfo(url);
		AtomicInteger duplicates = new AtomicInteger();
		AtomicInteger duplicatesFoundTimes = new AtomicInteger();
		links.getItems().stream()
				.peek(LinkApartamentOlx::fixPulicatLaDate)
				.filter(link->{
					Optional<LinkApartamentOlx> linkApartamentOlx = linksApartamentOlxRepository.findById(link.getId());
					if (linkApartamentOlx.isPresent() &&
							!linkApartamentOlx.get().getPublicatLaDate().equals(link.getPublicatLaDate())) {
						log.info("Updated ad oldPublicatLa={} newPublicatLa={} oldPret={} newPret{} link={}", linkApartamentOlx.get().getPublicatLaDate(), link.getPublicatLaDate(), linkApartamentOlx.get().getPret(), link.getPret(), link);

						return true;
					} else if (linkApartamentOlx.isPresent()) {
						duplicates.incrementAndGet();
						return false;
					}
					return true;
				})
				.forEach(link->{
					linksApartamentOlxRepository.save(link);
				});

		log.info("Found ads={} duplicates={} ", links.getItems().size(), duplicates.get());
		if (links.getItems().size() == 0) {
			log.info("Retrying...");
			fetchAll(startUrl, page, dontStopUntilFinish);
		}
		//		if (links.getItems().size() > 0 && links.getItems().size() == duplicates.get()) {
		//			log.info("Only duplicates found for {} {}  Stopping", cartierKeyword, camere);
		//		} else
		if (!dontStopUntilFinish && links.getItems().size() > 0 && links.getItems().size() == duplicates.get()) {
			duplicatesFoundTimes.incrementAndGet();
		} else if (duplicatesFoundTimes.get() == 3) {
			log.info("Only duplicates found. STOP!");
			return;
		}
		if (!StringUtils.isEmpty(links.getNextPage())) {
			fetchAll(startUrl, page + 1, dontStopUntilFinish);
		} else{
			log.info("No next page", url);
		}

	}

	private void fetchAdsFor(CartierCluj cartierCluj, String cartierKeyword, String camere, Integer page)
			throws TransformerException, SAXException, IOException, InterruptedException {
		String url = buildOlxSearchUrl(cartierKeyword, camere, page);
		LinksApartamentOlx links = scrapeLinksWithNextPageInfo(url);
		AtomicInteger duplicates = new AtomicInteger();
		links.getItems().stream()
				.peek(LinkApartamentOlx::fixPulicatLaDate)
				.filter(link->{
					Optional<LinkApartamentOlx> linkApartamentOlx = linksApartamentOlxRepository.findById(link.getId());
					if (linkApartamentOlx.isPresent() && !linkApartamentOlx.get().getPublicatLaDate().equals(link.getPublicatLaDate())) {
						log.info("Updated ad oldPublicatLa={} newPublicatLa={} link={}",
								linkApartamentOlx.get().getPublicatLaDate(), link.getPublicatLaDate(),
								link);
						linksApartamentOlxRepository.deleteById(link.getId());
						return true;
					} else if (linkApartamentOlx.isPresent()) {
						duplicates.incrementAndGet();
						return false;
					}
					return true;
				})
				.forEach(link->{
					linksApartamentOlxRepository.save(link);
				});

		log.info("Found ads={} duplicates={} ", links.getItems().size(), duplicates.get());
		//		if (links.getItems().size() > 0 && links.getItems().size() == duplicates.get()) {
		//			log.info("Only duplicates found for {} {}  Stopping", cartierKeyword, camere);
		//		} else
		if (!StringUtils.isEmpty(links.getNextPage())) {
			fetchAdsFor(cartierCluj, cartierKeyword, camere, page + 1);
		} else{
			log.info("No next page", url);
		}
	}

	private LinksApartamentOlx scrapeLinksWithNextPageInfo(String searchUrl)
			throws IOException, SAXException, TransformerException, InterruptedException {
		log.info("Fetching from url={}", searchUrl);
		TimeUnit.SECONDS.sleep(SECONDS_TO_WAIT_BETWEEN_REQUESTS);
		String json = fetchTransformedJson(searchUrl);
		return transformJsonToJava(json);
	}

	private LinksApartamentOlx transformJsonToJava(String json) {
		Gson gson = new Gson();
		LinksApartamentOlx links = gson.fromJson(json, LinksApartamentOlx.class);
		links.setItems(links.getItems().stream().filter(Objects::nonNull).collect(Collectors.toList()));
		return links;
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

	private String buildOlxSearchUrl(String cartierKeyword, String camere, Integer page)
			throws UnsupportedEncodingException {
		String keywordToSearch = cartierKeyword.replaceAll(" ", "-");
		keywordToSearch = URLEncoder.encode(keywordToSearch, "UTF-8");
		String params = "/?search[description]=1";
		if (page > 1) {
			params += "&page=" + page;
		}
		return "https://www.olx.ro/imobiliare/apartamente-garsoniere-de-vanzare/" + camere + "/cluj-napoca/q-" + keywordToSearch + params;
	}

}
