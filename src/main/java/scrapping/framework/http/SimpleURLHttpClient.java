package scrapping.framework.http;

import scrapping.framework.HttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class SimpleURLHttpClient implements HttpClient {

	@Override public InputStream get(String urlString) throws IOException {
		URL url = new URL(urlString);
		return url.openStream();
	}
}
