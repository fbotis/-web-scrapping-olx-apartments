package lecturaudio.model;

import java.util.List;

public class AudioBooksWithNextPage {

	private String nextPage;
	private List<Audiobook> items;

	public String getNextPage() {
		return nextPage;
	}

	public List<Audiobook> getItems() {
		return items;
	}

}
