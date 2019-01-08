import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class StraziClean {

	private static Map<String, String> diacriticsReplacements = new HashMap<>();

	static {

		diacriticsReplacements.put("ă", "a");
		diacriticsReplacements.put("ă".toUpperCase(), "A");
		diacriticsReplacements.put("â", "a");
		diacriticsReplacements.put("â".toUpperCase(), "A");
		diacriticsReplacements.put("ş", "s");
		diacriticsReplacements.put("ş".toUpperCase(), "S");
		diacriticsReplacements.put("ţ", "t");
		diacriticsReplacements.put("ţ".toUpperCase(), "T");
		diacriticsReplacements.put("ț", "t");
		diacriticsReplacements.put("ț".toUpperCase(), "T");
		diacriticsReplacements.put("î", "i");
		diacriticsReplacements.put("î".toUpperCase(), "I");

		diacriticsReplacements.put("á", "a");
		diacriticsReplacements.put("á".toUpperCase(), "A");

		diacriticsReplacements.put("é", "e");
		diacriticsReplacements.put("é".toUpperCase(), "E");

		diacriticsReplacements.put("í", "i");
		diacriticsReplacements.put("í".toUpperCase(), "I");

		diacriticsReplacements.put("ö", "o");
		diacriticsReplacements.put("ö".toUpperCase(), "O");

		diacriticsReplacements.put("ó", "o");
		diacriticsReplacements.put("ó".toUpperCase(), "O");

		diacriticsReplacements.put("ő", "ő");
		diacriticsReplacements.put("ő".toUpperCase(), "O");

		diacriticsReplacements.put("ü", "u");
		diacriticsReplacements.put("ü".toUpperCase(), "U");

		diacriticsReplacements.put("ű", "u");
		diacriticsReplacements.put("ű".toUpperCase(), "U");

		diacriticsReplacements.put("ű", "u");
		diacriticsReplacements.put("ű".toUpperCase(), "U");
	}

	public static void main(String[] args) throws IOException {
		Files.lines(Paths.get("/Users/fbotis/PERSONAL/parsing/parser/src/main/resources/strazi_cluj.txt"))
				.forEach(
						line->{
							StringBuilder stringBuilder = new StringBuilder();
							StringTokenizer tokenizer = new StringTokenizer(line, " \t\n\r\f,.:;?![]'");
							boolean cartierFound = false;
							while (tokenizer.hasMoreTokens()) {
								String token = tokenizer.nextToken().trim();
								if (!cartierFound && token.length() > 1 && Character.isUpperCase(token.toCharArray()[0]) && Character.isUpperCase(token.toCharArray()[1])) {
									cartierFound = true;
									stringBuilder.append("\b," + Normalizer.normalize(token, Normalizer.Form.NFD));
								} else if (cartierFound && (token.length() < 2 || (token.length() > 1 && Character.isLowerCase(token.toCharArray()[1])))) {
									System.out.println(stringBuilder.toString());
									return;
								} else{
									for (Map.Entry<String, String> diacriticR : diacriticsReplacements.entrySet()) {
										token = token.replaceAll(diacriticR.getKey(), diacriticR.getValue());
									}
									stringBuilder.append(token.replaceAll("ş", "s") + " ");
								}
							}
							System.out.println(stringBuilder.toString());

						}

				);
	}
}
