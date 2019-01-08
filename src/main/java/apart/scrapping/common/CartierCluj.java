package apart.scrapping.common;

public enum CartierCluj {

	ANDREI_MURESANU("Andrei Muresanu"),
	APAHIDA("Apahida"),
	BACIU("Baciu"),
	BECAS("Becas"),
	BORHANCI("Borhanci"),
	BULGARIA("Bulgaria"),
	BUNA_ZIUA("Buna Ziua"),
	CENTRU("Centru"),
	DAMBUL_ROTUND("Dambul Rotund"),
	DEZMIR("Dezmir"),
	EUROPA("Europa"),
	FAGET("Faget"),
	FELEACU("Feleacu"),
	FLORESTI("Floresti"),
	GHEORGHENI("Gheorgheni"),
	GILAU("Gilau"),
	GRIGORESCU("Grigorescu"),
	GRUIA("Gruia"),
	INTRE_LACURI("Între Lacuri", "Intre Lacuri"),
	IRIS("Iris"),
	MANASTUR("Manastur"),
	MARASTI("Marasti"),
	PLOPILOR("Plopilor"),
	SANICOARA("Sanicoara"),
	SOMESENI("Someseni"),
	SOPOR("Sopor"),
	ZORILOR("Zorilor");

	private final String[] keywords;

	CartierCluj(String... keywords) {
		this.keywords = keywords;
	}

	public String[] getKeywords() {
		return keywords;
	}
}
