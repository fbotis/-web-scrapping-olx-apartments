import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lecturaudio.model.AudioBookDownloadLinks;
import lecturaudio.model.AudioBooksWithNextPage;
import org.xml.sax.SAXException;
import scrapping.framework.ToBeanTransformer;
import scrapping.framework.http.SimpleURLHttpClient;

import javax.xml.transform.TransformerException;
import java.io.IOException;

public class CartiParser {

	public static void main(String[] args) throws Exception {
		//		testLinks();
		//		testDownloadLinks();
		testNextPages();
	}

	private static void testNextPages() throws Exception {
		ToBeanTransformer<JsonObject> transformer = new ToBeanTransformer<>("lecturaaudio/lectura-audio-links.xsl", JsonObject.class, new SimpleURLHttpClient());
		JsonObject audioBooksWithNextPage = transformer.scrapePage("http://lectura-audio.blogspot.com/");
		for (JsonElement element:audioBooksWithNextPage.getAsJsonArray("items").getAsJsonArray()){
			System.out.println(element);
		}
	}

	private static void testDownloadLinks() throws TransformerException, SAXException, IOException {
		ToBeanTransformer<AudioBookDownloadLinks> transformer = new ToBeanTransformer<>("lecturaaudio/lectura-audio-download-links.xsl", AudioBookDownloadLinks.class, new SimpleURLHttpClient());
		AudioBookDownloadLinks audioBookDownloadLinks = transformer.scrapePage("http://lectura-audio.blogspot.com/2018/10/victor-stoica-vior-scripcariu-compendiu.html");
		System.out.println(audioBookDownloadLinks);
	}

	private static void testLinks() throws TransformerException, SAXException, IOException {
		ToBeanTransformer<AudioBooksWithNextPage> transformer = new ToBeanTransformer<>("lecturaaudio/lectura-audio-links.xsl", AudioBooksWithNextPage.class, new SimpleURLHttpClient());
		AudioBooksWithNextPage audioBooksWithNextPage = transformer.scrapePage("http://lectura-audio.blogspot.com/");
		System.out.println(audioBooksWithNextPage);
	}
}
