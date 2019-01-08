package parser.olx.parsing;

import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import java.io.IOException;

public class ItemLinksFetcherTest {

	@Test
	public void test() throws TransformerException, SAXException, IOException {
		ItemLinksFetcher itemLinksFetcher = new ItemLinksFetcher(null);
		itemLinksFetcher.fetch("https://www.olx.ro/imobiliare/apartamente-garsoniere-de-vanzare/1-camera/cluj-napoca",1);

	}
}
