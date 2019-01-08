package apart.scrapping.olx.dao.model;

import com.google.common.base.MoreObjects;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class ApartamentOlx {

	private static transient DateTimeFormatter publicatDateFormatter = new DateTimeFormatterBuilder().parseCaseInsensitive().parseLenient()
			.appendPattern("d MMM yyyy HH:mm").toFormatter();

	private Long id;
	private String detaliiAnunt;
	private String dataConstructie;

	private String descriere;
	private String locatie;
	private String oferitDe;
	private String titlu;
	private String link;
	private Integer vizualizari;
	private String compartimentare;
	private String linkUtilizator;
	private String etaj;
	private BigDecimal pret;
	private LocalDateTime publicatLa;
	private LocalDateTime fetched;
	private Integer suprafata;
	private Integer camere;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDetaliiAnunt() {
		return detaliiAnunt;
	}

	public void setDetaliiAnunt(String detaliiAnunt) {
		this.detaliiAnunt = detaliiAnunt;
	}

	public String getDataConstructie() {
		return dataConstructie;
	}

	public void setDataConstructie(String dataConstructie) {
		this.dataConstructie = dataConstructie;
	}

	public String getDescriere() {
		return descriere;
	}

	public void setDescriere(String descriere) {
		this.descriere = descriere;
	}

	public String getLocatie() {
		return locatie;
	}

	public void setLocatie(String locatie) {
		this.locatie = locatie;
	}

	public String getOferitDe() {
		return oferitDe;
	}

	public void setOferitDe(String oferitDe) {
		this.oferitDe = oferitDe;
	}

	public String getTitlu() {
		return titlu;
	}

	public void setTitlu(String titlu) {
		this.titlu = titlu;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Integer getVizualizari() {
		return vizualizari;
	}

	public void setVizualizari(Integer vizualizari) {
		this.vizualizari = vizualizari;
	}

	public String getCompartimentare() {
		return compartimentare;
	}

	public void setCompartimentare(String compartimentare) {
		this.compartimentare = compartimentare;
	}

	public String getLinkUtilizator() {
		return linkUtilizator;
	}

	public void setLinkUtilizator(String linkUtilizator) {
		this.linkUtilizator = linkUtilizator;
	}

	public String getEtaj() {
		return etaj;
	}

	public void setEtaj(String etaj) {
		this.etaj = etaj;
	}

	public BigDecimal getPret() {
		return pret;
	}

	public void setPret(BigDecimal pret) {
		this.pret = pret;
	}

	public LocalDateTime getPublicatLa() {
		return publicatLa;
	}

	public void setPublicatLa(LocalDateTime publicatLa) {
		this.publicatLa = publicatLa;
	}

	public Integer getSuprafata() {
		return suprafata;
	}

	public void setSuprafata(Integer suprafata) {
		this.suprafata = suprafata;
	}

	public LocalDateTime getFetched() {
		return fetched;
	}

	public void setFetched(LocalDateTime fetched) {
		this.fetched = fetched;
	}

	public void fixFetchedAndPublishedDates() {
		setFetched(LocalDateTime.now());
		String oraMin = detaliiAnunt.split(" La ")[1].trim().split(",")[0];
		String data = detaliiAnunt.split(" La ")[1].split(",")[1];
		data = data.replaceAll("ianuarie", "jan")
				.replaceAll("februarie", "feb")
				.replaceAll("martie", "mar")
				.replaceAll("aprilie", "apr")
				.replaceAll("mai", "may")
				.replaceAll("iunie", "jun")
				.replaceAll("iulie", "jul")
				.replaceAll("august", "aug")
				.replaceAll("septembrie", "sep")
				.replaceAll("octombrie", "oct")
				.replaceAll("noiembrie", "nov")
				.replaceAll("decembrie", "dec")
				.trim();
		this.publicatLa = LocalDateTime.parse(data + " " + oraMin, publicatDateFormatter);

	}

	public Integer getCamere() {
		return camere;
	}

	public void setCamere(Integer camere) {
		this.camere = camere;
	}

	@Override public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", id)
				.add("detaliiAnunt", detaliiAnunt)
				.add("dataConstructie", dataConstructie)
				.add("descriere", descriere)
				.add("locatie", locatie)
				.add("oferitDe", oferitDe)
				.add("titlu", titlu)
				.add("link", link)
				.add("vizualizari", vizualizari)
				.add("compartimentare", compartimentare)
				.add("linkUtilizator", linkUtilizator)
				.add("etaj", etaj)
				.add("pret", pret)
				.add("publicatLa", publicatLa)
				.add("fetched", fetched)
				.add("suprafata", suprafata)
				.add("camere", camere)
				.toString();
	}
}
