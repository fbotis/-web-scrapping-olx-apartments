import parser.xml.XHTMLUtils;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

public class Test {

	public static void main(String[] args) throws Exception {

		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("167.99.142.190", 3128));
		URLConnection conn = new URL("https://nemutam.com/?pag=1").openConnection(proxy);

		Document doc = XHTMLUtils.parse(conn.getInputStream(), "UTF-8", false);


		TransformerFactory factory = TransformerFactory.newInstance();
		StreamSource xslStream = new StreamSource(new File("/Users/fbotis/PERSONAL/parsing/parser/src/main/resources/nemutam/nemutam_fetch.xsl"));
		Transformer transformer = factory.newTransformer(xslStream);
		DOMSource in = new DOMSource(doc);
		StringWriter sw = new StringWriter();
		StreamResult out =new StreamResult(sw);
		transformer.transform(in, out);
		System.out.println("The generated HTML file is:\n" + sw.toString().replaceAll("<.+>",""));

	}

}
