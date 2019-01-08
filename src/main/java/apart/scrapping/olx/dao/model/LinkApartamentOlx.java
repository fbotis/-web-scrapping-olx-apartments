package apart.scrapping.olx.dao.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class LinkApartamentOlx {

	private static transient DateTimeFormatter publicatDateFormatter = new DateTimeFormatterBuilder().parseCaseInsensitive().parseLenient()
			.appendPattern("d MMM yyyy").toFormatter();

	private Long id;
	private String link;
	private boolean processed;
	private String publicatLa;
	private LocalDateTime fetchedDate;
	private LocalDate publicatLaDate;
	private Long pret;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	public void fixPulicatLaDate() {
		this.fetchedDate = LocalDateTime.now();
		if (!Strings.isNullOrEmpty(publicatLa)) {
			publicatLaDate = parseDate(publicatLa);
		}
	}

	public LocalDate getPublicatLaDate() {
		return publicatLaDate;
	}

	private LocalDate parseDate(String publicatLa) {
		publicatLa = publicatLa.replaceAll("ian", "jan");
		publicatLa = publicatLa.replaceAll("mai", "may");
		publicatLa = publicatLa.replaceAll("iun", "jun");
		publicatLa = publicatLa.replaceAll("iul", "jul");
		publicatLa = publicatLa.replaceAll("noi", "nov");

		if (publicatLa.toLowerCase().contains("azi")) {
			return LocalDate.now();
		} else if (publicatLa.toLowerCase().contains("ieri")) {
			return LocalDate.now().minusDays(1);
		} else{
			return LocalDate.parse(publicatLa + " " + LocalDate.now().getYear(), publicatDateFormatter);
		}
	}

	public Long getPret() {
		return pret;
	}

	public void setPret(Long pret) {
		this.pret = pret;
	}

	public static DateTimeFormatter getPublicatDateFormatter() {
		return publicatDateFormatter;
	}

	public static void setPublicatDateFormatter(DateTimeFormatter publicatDateFormatter) {
		LinkApartamentOlx.publicatDateFormatter = publicatDateFormatter;
	}

	public String getPublicatLa() {
		return publicatLa;
	}

	public void setPublicatLa(String publicatLa) {
		this.publicatLa = publicatLa;
	}

	public LocalDateTime getFetchedDate() {
		return fetchedDate;
	}

	public void setFetchedDate(LocalDateTime fetchedDate) {
		this.fetchedDate = fetchedDate;
	}

	public void setPublicatLaDate(LocalDate publicatLaDate) {
		this.publicatLaDate = publicatLaDate;
	}

	@Override public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", id)
				.add("link", link)
				.add("processed", processed)
				.add("publicatLa", publicatLa)
				.add("fetchedDate", fetchedDate)
				.add("publicatLaDate", publicatLaDate)
				.add("pret", pret)
				.toString();
	}

	public static void main(String[] args) {
		System.out.println(LocalDate.parse("17 iul 2018", publicatDateFormatter));
	}

}
