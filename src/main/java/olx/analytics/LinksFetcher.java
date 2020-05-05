package olx.analytics;

import com.google.gson.Gson;
import olx.analytics.common.http.ProxyHttpClient;
import olx.analytics.model.LinkApartamentOlx;
import olx.analytics.model.LinksApartamentOlx;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Component
public class LinksFetcher {

	private static final long SECONDS_TO_WAIT_BETWEEN_REQUESTS = 3;
	public static final String OLX_LINK_APARTAMENT_OLX_FETCHER_XSL = "olx_analytics/link_apartament_olx_fetcher.xsl";
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Transformer transformer;
	private final ProxyHttpClient proxyHttpClient;
	private final ArrayList<LinksFetcherListener> listeners = new ArrayList<>();

	public LinksFetcher(int retries, long waitBetweenRetries, int maxProxyFailuresBeforeRemoval)
			throws TransformerConfigurationException, IOException {
		this.proxyHttpClient = new ProxyHttpClient(retries, waitBetweenRetries, maxProxyFailuresBeforeRemoval);
		TransformerFactory factory = TransformerFactory.newInstance();
		StreamSource xslStream = new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(OLX_LINK_APARTAMENT_OLX_FETCHER_XSL));
		transformer = factory.newTransformer(xslStream);
	}

	public void addListener(LinksFetcherListener listener) {
		listeners.add(listener);
	}

	private void fetchAll(String startUrl, Integer page, Long untilLinkId, LocalDate untilPublicatLaDate)
			throws InterruptedException, SAXException, TransformerException, IOException {
		String url = startUrl;
		if (page > 1) {
			url += "/?page=" + page;
		}
		LinksApartamentOlx links = scrapeLinksWithNextPageInfo(url);
		AtomicBoolean stop = new AtomicBoolean(false);
		links.getItems().stream()
				.peek(LinkApartamentOlx::fixPulicatLaDate)
				.peek(link -> {
					if (shouldStopFetchingNextPage(link, untilLinkId, untilPublicatLaDate)) {
						stop.set(true);
					}
				})
				.forEach(this::notifyListeners);

		log.info("Found ads={} ", links.getItems().

				size());
		if (links.getItems().

				size() == 0) {
			log.info("Retrying...");
			fetchAll(startUrl, page, untilLinkId, untilPublicatLaDate);
		}

		if (!StringUtils.isEmpty(links.getNextPage()) && !stop.get()) {
			fetchAll(startUrl, page + 1, untilLinkId, untilPublicatLaDate);
		} else {
			log.info("No next page for ulr={} stop={} nextPage={}", url, stop.get(), links.getNextPage());
		}

	}

	private boolean shouldStopFetchingNextPage(LinkApartamentOlx link, Long untilLinkId, LocalDate untilPublicatLaDate) {
		if (untilLinkId != null && untilPublicatLaDate != null) {
			return untilLinkId.equals(link.getId()) && untilPublicatLaDate.equals(link.getPublicatLaDate());
		}
		return false;
	}

	private void notifyListeners(LinkApartamentOlx link) {
		//TODO make this async, put it in some consumer queue or smth
		listeners.forEach(listener -> listener.onNewLink(link));
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

	public static void main(String[] args)
			throws IOException, TransformerException, InterruptedException, SAXException {
		LinksFetcher linksFetcher = new LinksFetcher(1, 100, 1);
		linksFetcher.addListener(System.out::println);
		linksFetcher.fetchAll("https://www.olx.ro/imobiliare/apartamente-garsoniere-de-vanzare/cluj-napoca/", 0, 197060787L, LocalDate.of(2020, 5, 4));
	}
}
