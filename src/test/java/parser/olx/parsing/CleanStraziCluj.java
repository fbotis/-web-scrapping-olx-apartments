package parser.olx.parsing;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class CleanStraziCluj {

	public static void main(String[] args) throws Exception {
		try (Stream<String> stream = Files.lines(Paths.get("/Users/fbotis/PERSONAL/parsing/parser/src/main/resources/strazi_cluj.txt"))) {
			stream
					.forEach(s->{
						char[] charrArray = s.toCharArray();
						boolean prevUpperCase = false;
						boolean crtUpperCase = false;
						for (int i = 0;i < charrArray.length;i++) {
							if (charrArray[i] >= 'A' && charrArray[i] <= 'Z' || charrArray[i]==' ') {
								crtUpperCase = true;
							} else{
								crtUpperCase = false;
							}
							if (prevUpperCase && !crtUpperCase) {
								System.out.println(s.substring(0, i));
							}
							prevUpperCase=crtUpperCase;
						}
					});
		}
	}
}
