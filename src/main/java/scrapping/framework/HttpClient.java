package scrapping.framework;

import java.io.IOException;
import java.io.InputStream;

public interface HttpClient {

	public InputStream get(String url) throws IOException;

}
