package parser.olx.dao.model;

import java.time.LocalDate;

public class OlxItemLink {

	private Long id;
	private String title;
	private String link;
	private String publishedDate;
	private String category;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(String publishedDate) {
		this.publishedDate = publishedDate;
	}

	public void fixPublishedDate() {
		if (publishedDate.startsWith("Azi")) {
			this.publishedDate = LocalDate.now().getDayOfMonth() + " " + LocalDate.now().getMonth().name().substring(0, 3).toLowerCase();
		} else if (publishedDate.startsWith("Ieri")) {
			this.publishedDate = LocalDate.now().minusDays(1).getDayOfMonth() + " " + LocalDate.now().minusDays(1).getMonth().name().substring(0, 3).toLowerCase();
		}
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
