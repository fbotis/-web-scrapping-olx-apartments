package apart.scrapping.olx.dao.model;

import java.util.List;

public class LinksApartamentOlx {

	private List<LinkApartamentOlx> items;

	private String nextPage;

	public List<LinkApartamentOlx> getItems() {
		return items;
	}

	public void setItems(List<LinkApartamentOlx> items) {
		this.items = items;
	}

	public String getNextPage() {
		return nextPage;
	}

	public void setNextPage(String nextPage) {
		this.nextPage = nextPage;
	}
}

