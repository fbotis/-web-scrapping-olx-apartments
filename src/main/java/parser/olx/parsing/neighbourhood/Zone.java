package parser.olx.parsing.neighbourhood;

import parser.olx.dao.model.OlxApartament;

import java.util.HashSet;
import java.util.Set;

public enum Zone {

	TEST("");

	private final String[] keywords;

	Zone(String... keywords) {
		this.keywords = keywords;
	}

	public static Set<Zone> fromApartment(OlxApartament olxApartament) {
		Set<Zone> zones = new HashSet<>();
		for (Zone zone : Zone.values()) {
			for (String keyword : zone.keywords) {
				if (olxApartament.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
						olxApartament.getDescriere().toLowerCase().contains(keyword.toLowerCase())) {
					zones.add(zone);
				}
			}
		}
		return zones;
	}
	}
