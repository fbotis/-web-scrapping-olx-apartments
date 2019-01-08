package scrapping.framework.xml;

import com.google.common.escape.CharEscaperBuilder;
import com.google.common.escape.Escaper;

public class XSLUtils {

	private static Escaper charEscaper;

	static {
		charEscaper = new CharEscaperBuilder().addEscape('\"', "'").toEscaper();
	}

	public  String escapeString(String string) {
		return charEscaper.escape(string);
	}
}
