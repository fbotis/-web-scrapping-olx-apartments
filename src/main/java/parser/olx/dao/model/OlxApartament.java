package parser.olx.dao.model;

import com.google.common.base.MoreObjects;

public class OlxApartament {

	private Long id;
	private String title;
	private String location;
	private String adAddingDetails;
	private String offeredBy;
	private String compartimentare;
	private String suprafata;
	private String etaj;

	private String descriere;
	private String price;
	private String dataconstructie;
	private String publishedDate;
	private String publishedHour;
	private String link;
	private String locationString;
	private String googlePlaceId;
	private String googlePlaceDetails;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLocation() {
		return location;
	}

	public String getLocationString() {
		return locationString;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getAdAddingDetails() {
		return adAddingDetails;
	}

	public void setAdAddingDetails(String adAddingDetails) {
		this.adAddingDetails = adAddingDetails;
	}

	public String getOfferedBy() {
		return offeredBy;
	}

	public void setOfferedBy(String offeredBy) {
		this.offeredBy = offeredBy;
	}

	public String getCompartimentare() {
		return compartimentare;
	}

	public void setCompartimentare(String compartimentare) {
		this.compartimentare = compartimentare;
	}

	public String getSuprafata() {
		return suprafata;
	}

	public void setSuprafata(String suprafata) {
		this.suprafata = suprafata;
	}

	public String getEtaj() {
		return etaj;
	}

	public void setEtaj(String etaj) {
		this.etaj = etaj;
	}

	public String getDescriere() {
		return descriere;
	}

	public void setDescriere(String descriere) {
		this.descriere = descriere;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getDataconstructie() {
		return dataconstructie;
	}

	public void setDataconstructie(String dataconstructie) {
		this.dataconstructie = dataconstructie;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(String publishedDate) {
		this.publishedDate = publishedDate;
	}

	public String getPublishedHour() {
		return publishedHour;
	}

	public void setPublishedHour(String publishedHour) {
		this.publishedHour = publishedHour;
	}

	public void fixFetchDateAndId() {
		String[] splitDate = adAddingDetails.split("La")[1].split(",");
		String hour = splitDate[0];
		String date = splitDate[1];

		Long id = Long.parseLong(adAddingDetails.split("Numar anunt:")[1].trim());

		this.publishedHour = hour;
		this.publishedDate = date;
		this.id = id;
	}

	@Override public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", id)
				.add("title", title)
				.add("location", location)
				.add("adAddingDetails", adAddingDetails)
				.add("offeredBy", offeredBy)
				.add("compartimentare", compartimentare)
				.add("suprafata", suprafata)
				.add("etaj", etaj)
				.add("descriere", descriere)
				.add("price", price)
				.add("dataconstructie", dataconstructie)
				.add("publishedDate", publishedDate)
				.add("publishedHour", publishedHour)
				.toString();
	}

	public void setLocationString(String locationString) {
		this.locationString = locationString;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public void setGooglePlaceId(String googlePlaceId) {
		this.googlePlaceId = googlePlaceId;
	}

	public String getGooglePlaceId() {
		return googlePlaceId;
	}

	public void setGooglePlaceDetails(String googlePlaceDetails) {
		this.googlePlaceDetails = googlePlaceDetails;
	}

	public String getGooglePlaceDetails() {
		return googlePlaceDetails;
	}
}
