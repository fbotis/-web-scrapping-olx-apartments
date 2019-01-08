package parser.olx.parsing.neighbourhood;

import parser.olx.dao.model.OlxApartament;

import java.util.HashSet;
import java.util.Set;

public enum Neighbourhood {

	ANDREI_MURESANU("Andrei Muresanu", "A Muresanu", "Andrei Muresan"),
	BULGARIA("Bulgaria"),
	BUNA_ZIUA("Buna Ziua", "Buna-Ziua", "Bună Ziua", "B Ziua","Buna Ziua-A Muresanu"),
	BACIU("Baciu"),
	CENTRAL("centrul Clujului", "Centru ", " Centru", "Piata Muzeului","zona centrala"),
	ULTRACENTRAL("Ultracentral", "Ultracentrala","zona ultracentrala"),
	SEMICENTRAL("Semicentral ", "zona semicentrala", " Semicentral"),
	FLORESTI("Floresti","Florești"),
	DAMBUL_ROTUND("Dambul Rotund", "Dimbul Rotund", "Dimbu Rotund", "Dambu Rotund"),
	GARA("Gara.", "Gara"),
	GHEORGHENI("Gheorgheni", "Gheorgeni", "Interservisan", "Mercur"),
	GRADINI_MANASTUR("Gradini Manastur"),
	GRIGORESCU("Grigorescu", "Donath"),
	GRUIA("Gruia"),
	IRIS("Iris", "Liebknecht"),
	INTRE_LACURI("Intre Lacuri", "Iulius Mall"),
	MANASTUR("Manastur", "Mănăștur", "Mnastur", "Bucium"),
	MARASTI("Marasti", "Mărăști", "Mărăşti", "Aurel Vlaicu"),
	SOMESENI("Someseni", "Traian Vuia"),
	ZORILOR("Zorilor"),
	SOPOR("Sopor"),
	PLOPILOR("Plopilor", "Platinia Shopping Center"),
	BORHANCI("Borhanci","Borhanciului"),
	BECAS("Becas"),
	FAGET("Faget"),
	EUROPA("Europa", "Zorilor sud", "Observatorului sud"),
	LOMB("Lomb");

	private final String[] keywords;

	Neighbourhood(String... keywords) {
		this.keywords = keywords;
	}

	public static Set<Neighbourhood> fromApartment(OlxApartament olxApartament) {
		Set<Neighbourhood> neighbourhoodList = new HashSet<>();
		for (Neighbourhood neighbourhood : Neighbourhood.values()) {
			for (String keyword : neighbourhood.keywords) {
				if (olxApartament.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
						olxApartament.getDescriere().toLowerCase().contains(keyword.toLowerCase())) {
					neighbourhoodList.add(neighbourhood);
				}
			}
		}
		return neighbourhoodList;
	}

	public static Set<Neighbourhood> fromString(String string) {
		Set<Neighbourhood> neighbourhoodList = new HashSet<>();
		for (Neighbourhood neighbourhood : Neighbourhood.values()) {
			for (String keyword : neighbourhood.keywords) {
				if (string.toLowerCase().contains(keyword.toLowerCase())) {
					neighbourhoodList.add(neighbourhood);
				}
			}
		}
		return neighbourhoodList;
	}
}
