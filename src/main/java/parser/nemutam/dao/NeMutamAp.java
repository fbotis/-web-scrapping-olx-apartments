package parser.nemutam.dao;

import java.time.LocalDateTime;

public class NeMutamAp {

	private String link;
	private String pret;
	private String titlu;
	private String zona;
	private String camere;
	private String suprafata;
	private String oferit;
	private String publicat;
	private LocalDateTime fetched = LocalDateTime.now();
	private LocalDateTime publicatDate;
	private Integer suprafataInt;

	public LocalDateTime getFetched() {
		return fetched;
	}

	public void setFetched(LocalDateTime fetched) {
		this.fetched = fetched;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getPret() {
		return pret;
	}

	public void setPret(String pret) {
		this.pret = pret;
	}

	public String getTitlu() {
		return titlu;
	}

	public void setTitlu(String titlu) {
		this.titlu = titlu;
	}

	public String getZona() {
		return zona;
	}

	public void setZona(String zona) {
		this.zona = zona;
	}

	public String getCamere() {
		return camere;
	}

	public void setCamere(String camere) {
		this.camere = camere;
	}

	public String getSuprafata() {
		return suprafata;
	}

	public void setSuprafata(String suprafata) {
		this.suprafata = suprafata;
	}

	public String getOferit() {
		return oferit;
	}

	public void setOferit(String oferit) {
		this.oferit = oferit;
	}

	public String getPublicat() {
		return publicat;
	}

	public void setPublicat(String publicat) {
		this.publicat = publicat;
	}

	public void setPublicatDate(LocalDateTime publicatDate) {
		this.publicatDate = publicatDate;
	}

	public LocalDateTime getPublicatDate() {
		return publicatDate;
	}

	public void setSuprafataInt(Integer suprafataInt) {
		this.suprafataInt = suprafataInt;
	}
}
