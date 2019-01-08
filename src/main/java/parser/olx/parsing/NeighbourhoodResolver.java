package parser.olx.parsing;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import parser.olx.dao.OlxApartmentRepository;
import parser.olx.dao.OlxItemLinkRepository;
import parser.olx.dao.model.OlxApartament;
import parser.olx.parsing.neighbourhood.Neighbourhood;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Component
public class NeighbourhoodResolver {

	private final OlxItemLinkRepository olxLinkRepository;
	private OlxApartmentRepository olxApartmentRepository;

	public NeighbourhoodResolver(OlxApartmentRepository olxApartmentRepository, OlxItemLinkRepository olxItemLinkRepository) {
		this.olxApartmentRepository = olxApartmentRepository;
		this.olxLinkRepository = olxItemLinkRepository;
	}

	public void test() {

	}

	public void resolve2() {
		for (OlxApartament olxApartament : olxApartmentRepository.findAll()) {

			if (olxApartament.getTitle().toLowerCase().contains("zona")) {
				StringBuilder zona = new StringBuilder();
				StringTokenizer tokenizer = new StringTokenizer(olxApartament.getTitle(), " \t\n\r\f,.:;?![]'");
				while (tokenizer.hasMoreTokens()) {
					String token = tokenizer.nextToken();
					if (token.equalsIgnoreCase("zona")) {
						zona.append(token + " ");
						while (tokenizer.hasMoreTokens()) {
							String token2 = tokenizer.nextToken();
							if (token2.equalsIgnoreCase("strazii") || Character.isUpperCase(token2.toCharArray()[0])) {
								zona.append(token2 + " ");
							} else{
								break;
							}
						}

					}
				}
				System.out.println(zona.toString() + " --> " + olxApartament.getTitle() + " --> " + olxApartament.getDescriere());
			}
		}
	}

	public void resolve3() throws IOException {
		Map<String, String> stradaToCartier = new HashMap<>();
		Files.lines(Paths.get("/Users/fbotis/PERSONAL/parsing/parser/src/main/resources/strazi_cluj.txt"))
				.forEach(
						line->{
							String[] stradaCartier = line.split(",");
							stradaToCartier.put(stradaCartier[0], stradaCartier[1]);
						});
		TreeSet<String> treeSet = new TreeSet<>();
		int found = 0;
		List<String> skipWords = Lists
				.newArrayList("apartament", "vand", "superb", "vanzare", "finisat", "Închiriez",
						"Àpartament", "Vînd", "Vânzare", "Vând", "Vindem", "Vila", "Vaznare", "Vanzarea", "Vanzare3", "Vand/inchiriez", "Vand/Schimb"
						, "Vanaze", "Garsoniera", "Ultrafinisat", "Ultimul", "Ultimele", "UNICAT", "Totul", "Nou", "Curte", "Proprie", "Parcare", "Terasa", "Superoferta"
						, "Super", "Spatiu", "Semidec", "Semidecomandate", "Semicomandat", "Se", "Schimb", "oferta", "pret", "duplex", "Repozitionare", "Proprietar", "Promotie",
						"Proiect", "Pro", "Prima", "Cas", "Persoana", "Fizica", "Pers", "Perfect", "Penthouse", "Parcare", "Boxa", "Inclusa", "PF-Vand", "PF",
						"P", "F", "Oportunitate", "Optional", "De", "Ap", "Oferta", "Comision", "reducere", "euro", "ocazie", "Nou**", "Imobil",
						"Nou-", "negociabil", "mobilat", "mansarda", "MP", "utilat", "Lux", "CF", "locatie", "locuinta", "Licitație", "Insolvență", "Cluj Napoca", "Licitatie",
						"la", "cheie", "imobil", "parcari", "investitie", "garsoniera", "garaj", "Exclusivitate", "Etaj",
						"Dezvoltator", "Decomadat", "Bloc", "Terasa", "De", "DEOSEBIT", "la", "cheie", "discount", "cu", "o", "camera", "dec",
						"Imobiliare", "Constructor", "Confort", "Intermediar", "Panorama", "Comision0", "Comision", "Cluj-Napoca", "Cluj Napoca", "Cladire", "euro"
						, "casa", "camere", "niveluri", "nivel", "mobilat", "utilat", "langa", "in", "si", "parcare/garaj", "cabinet", "CF", "etaj", "C", "boxa", "Apartamnet"
						, "Apartanment", "Apartement", "Apatament", "Apartamente", "Apartament/Garsoniera", "Apartament/Spatiu"
						, "Apart", "Ap3", "Ansamblul", "Ajustare", "Agentia", "A", "Deasupra", "Aparament", "Aparatment", "Apartamen", "Apartamenent", "Apartament-vanzare", "Apartament1"
						, "Apartamentu", "Apartamet", "Apartametnt", "Apartemente", "Apartment", "Apt", "CF-ul", "balcon", "CLUJ", "constructor", "CT", "cam", "Decomandate", "et", "Camerea", "conf", "casuta", "superba"

						, "casă", "Aaprtament", "finisaje", "finalizat", "tva", "Aparatament", "Apartamenet", "Apartamentul", "Decomandat", "Garsonierã", "Garsonieră"
						, "Investeste ", "Investitia", "Semicomandate", "Semifinisat"
				)
				.stream()
				.map(String::toLowerCase).collect(Collectors.toList());
		for (OlxApartament olxApartament : olxApartmentRepository.findAll()) {
			StringBuilder zona = new StringBuilder();
			StringTokenizer tokenizer = new StringTokenizer(olxApartament.getTitle(), " \t\n\r\f,.:;?![]'");
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				if (Character.isUpperCase(token.toCharArray()[0])
						&& !skipWords.contains(token.toLowerCase())) {
					zona.append(token + " ");
				}
			}
			if (!StringUtils.isEmpty(zona.toString())) {
				treeSet.add(zona.toString());
				found++;
			}

		}

		treeSet.forEach(System.out::println);
		System.out.println(found);

		Map<String, List<String>> cartiere = new HashMap<>();
		cartiere.put("ANDREI_MURESANU",
				Lists.newArrayList("andrei muresanu", "andrei muresan", "andrei mur", "andrei mureseanu", "muresan", "muresanu"));

		cartiere.put("CALEA_TURZII",
				Lists.newArrayList("calea turzii", "cl turzii", "cale turzii"));

		cartiere.put("DAMBU_ROTUND",
				Lists.newArrayList("dambu rotund", "dambul rotund", "d rotund", "dâmbul rotund"));

		cartiere.put("BUNA_ZIUA",
				Lists.newArrayList("buna ziua", "b ziua", "bună ziua", "buna-ziua", "buna ziu"));

		cartiere.put("EUROPA",
				Lists.newArrayList("europa"));

		cartiere.put("MARASTI",
				Lists.newArrayList("marasti", "mărăști", "marsti", "mărăşti","expo transilvania"));

		cartiere.put("MANASTUR",
				Lists.newArrayList("manastur", "mănăștur", "manatsur", "manatur", "manastru", "manstur", "edgar quinet", "e q ", "big ", "mănăstur"));

		cartiere.put("BACIU",
				Lists.newArrayList("baciu"));

		cartiere.put("BORHANCI",
				Lists.newArrayList("borhanci"));

		cartiere.put("FLORESTI",
				Lists.newArrayList("floresti", "florești"));

		cartiere.put("PLOPILOR",
				Lists.newArrayList("plopilor"));

		cartiere.put("ZORILOR",
				Lists.newArrayList("zorilor", "zoril", "zor", "zorilo", "golden tulip", "ciresilor","sigma","mircea eliade","umf","observatorului"));

		cartiere.put("GRIGORESCU",
				Lists.newArrayList("grigorescu"));

		cartiere.put("GHEORGHENI",
				Lists.newArrayList("gheorgheni", "interservisan", "gheorghgeni", "gheorghieni", "gherogheni", "interservisa", "interservisan", "intersevisan", "interservisan", "intersevisan","gheorgheni","titulescu"));

		cartiere.put("IULIUS",
				Lists.newArrayList("iulius", "intre lacuri", "fsega", "intre lac", "intre lacur","viva city"));

		cartiere.put("IRIS",
				Lists.newArrayList("iris"));

		cartiere.put("GRUIA",
				Lists.newArrayList("gruia"));

		cartiere.put("ULTRACENTRAL",
				Lists.newArrayList("ultracentral", "ultracentrala", "mihai viteazul", "mihai viteazu", "unirii", "emil isac", "eroilor", "muzeului", "mihaiviteazul"));

		cartiere.put("CENTRAL",
				Lists.newArrayList("centru", "centrala", "central"));

		cartiere.put("SEMICENTRAL",
				Lists.newArrayList("semicentral", "centrala", "central"));

		cartiere.put("VIVO",
				Lists.newArrayList("vivo","polus"));

		List<String> notMatch = treeSet.stream()
				.filter(keyword->!findCartier(keyword, cartiere))
				.collect(Collectors.toList());

		notMatch.forEach(System.out::println);
		System.out.println(notMatch.size());
	}

	private boolean findCartier(String keyword, Map<String, List<String>> cartiere) {
		return cartiere.values().stream().flatMap(l->l.stream())
				.anyMatch(w->keyword.toLowerCase().contains(w.toLowerCase()));
	}

	public void resolve() {
		int index = 0;
		TreeSet<String> adrese = new TreeSet<>();
		for (OlxApartament olxApartament : olxApartmentRepository.findAll()) {
			olxApartament.setLink(olxLinkRepository.findById(olxApartament.getId()).get().getLink());
			olxApartmentRepository.save(olxApartament);
			if (olxApartament.getLocationString() != null)
				continue;

			if (olxApartament.getTitle().contains("zona")) {
				StringTokenizer tokenizer = new StringTokenizer(olxApartament.getTitle().substring(olxApartament.getTitle().indexOf("zona")), " \t\n\r\f,.:;?![]'");
				StringBuilder adresa = new StringBuilder();
				while (tokenizer.hasMoreTokens()) {
					String token = tokenizer.nextToken();
					if (!token.equalsIgnoreCase("zona") && Character.isUpperCase(token.charAt(0))) {
						adresa.append(token).append(" ");
					}
				}

				if (!Strings.isNullOrEmpty(adresa.toString().trim()) && adresa.toString().trim().length() > 2
						&& !adresa.toString().equalsIgnoreCase("calea")
						&& !adresa.toString().equalsIgnoreCase("casa")
						&& !adresa.toString().equalsIgnoreCase("cluj")
						&& !adresa.toString().equalsIgnoreCase("diana")
						&& !adresa.toString().equalsIgnoreCase("diana gheorgheni")
						&& !adresa.toString().equalsIgnoreCase("E Q")) {
					olxApartament.setLocationString(adresa.toString().trim());
					adrese.add(adresa.toString().trim());
				} else{
					olxApartament.setLocationString(null);
				}
				olxApartament.setLink(olxLinkRepository.findById(olxApartament.getId()).get().getLink());
				olxApartmentRepository.save(olxApartament);

			}

			if (olxApartament.getLocationString() == null) {
				Set<Neighbourhood> neighbourhoodSet = Neighbourhood.fromString(olxApartament.getTitle());
				if (neighbourhoodSet.size() == 1) {
					System.out.println(neighbourhoodSet.iterator().next());
					olxApartament.setLocationString(neighbourhoodSet.iterator().next().toString());
					olxApartmentRepository.save(olxApartament);
				}
			}

			if (olxApartament.getLocationString() == null && olxApartament.getDescriere().toLowerCase().contains("zona")) {
				StringTokenizer tokenizer = new StringTokenizer(olxApartament.getDescriere().substring(olxApartament.getDescriere().toLowerCase().indexOf("zona")), " \t\n\r\f,.:;?![]'");
				StringBuilder adresa = new StringBuilder();
				while (tokenizer.hasMoreTokens()) {
					String token = tokenizer.nextToken();
					if (!token.equalsIgnoreCase("zona") && Character.isUpperCase(token.charAt(0))) {
						adresa.append(token).append(" ");
					} else if (!token.equalsIgnoreCase("zona") && Character.isLowerCase(token.charAt(0))) {
						break;
					}
				}
				if (!Strings.isNullOrEmpty(adresa.toString().trim())) {
					olxApartament.setLocationString(adresa.toString());
					olxApartmentRepository.save(olxApartament);
				}
				System.out.println(adresa);
			}

			if (olxApartament.getLocationString() == null && olxApartament.getDescriere().toLowerCase().contains("zona strazii")) {
				StringTokenizer tokenizer = new StringTokenizer(olxApartament.getDescriere().substring(olxApartament.getDescriere().toLowerCase().indexOf("zona strazii") + 12), " \t\n\r\f,.:;?![]'");
				StringBuilder adresa = new StringBuilder();
				while (tokenizer.hasMoreTokens()) {
					String token = tokenizer.nextToken();
					if (Character.isUpperCase(token.charAt(0))) {
						adresa.append(token).append(" ");
					} else if (Character.isLowerCase(token.charAt(0))) {
						break;
					}
				}
				if (!Strings.isNullOrEmpty(adresa.toString().trim())) {
					olxApartament.setLocationString(adresa.toString());
					olxApartmentRepository.save(olxApartament);
				}
			}

			if (olxApartament.getLocationString() == null && olxApartament.getDescriere().toLowerCase().contains("in zona centrala")) {
				olxApartament.setLocationString("CENTRU");
				olxApartmentRepository.save(olxApartament);
			}

			if (olxApartament.getLocationString() == null && olxApartament.getTitle().contains("Calea Turzii")) {
				olxApartament.setLocationString("Calea Turzii");
				olxApartmentRepository.save(olxApartament);
			}

			if (olxApartament.getLocationString() == null) {
				StringTokenizer tokenizer = new StringTokenizer(olxApartament.getTitle(), " \t\n\r\f,.:;?![]'");
				List<String> tokens = new ArrayList<>();
				while (tokenizer.hasMoreTokens()) {
					tokens.add(tokenizer.nextToken());
				}
				StringBuilder builder = new StringBuilder();

				List<String> skipWords = Lists.newArrayList("Semidecomandate", "Balcon", "Parcare", "Camera", "Camere", "Zona", "loc", "Semifinisat", "Finisat", "Apartament", "Ap", "Ultrafinisat", "Titul", "Terasa", "Superpret", "Super", "Superb", "Studenti", "Semidec", "Bloc", "Nou", "Oferta", "Rezidential", "Privat", "Prima Casa", "Penthouse", "Parter", "Inalt", "Panorama", "P+2etaje", "Oferta", "Nou", "Lux", "Langa", "Finalizare", "Comision", "Finalizat", "Etaj", "Confort", "Apartamente", "Licitație", "Insolvență", "Cluj", "Napoca", "Vand", "Vinde", "Garsoniera", "August", "Totul", "Finisaje", "Vila");
				for (int i = 0;i < tokens.size();i++) {

					int finalI = i;
					if (i >= 1 && tokens.get(i).length() > 2 && Character.isUpperCase(tokens.get(i).charAt(0))
							&& skipWords.stream().allMatch(skipWord->!tokens.get(finalI).toLowerCase().equalsIgnoreCase(skipWord))) {
						builder.append(tokens.get(i));
						builder.append(" ");
					}
				}

				if (builder.toString().trim().length() > 0) {
					olxApartament.setLocationString(builder.toString());
					olxApartmentRepository.save(olxApartament);
				}
			}

		}

	}
}
