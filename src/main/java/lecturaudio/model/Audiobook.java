package lecturaudio.model;

import java.util.List;

public class Audiobook {

	private String id;
	private String autor;
	private String titlu;
	private String link;
	private List<String> audioDownloadLinks;

	public String getId() {
		return id;
	}

	public String getAutor() {
		return autor;
	}

	public String getTitlu() {
		return titlu;
	}

	public String getLink() {
		return link;
	}

	public List<String> getAudioDownloadLinks() {
		return audioDownloadLinks;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public void setTitlu(String titlu) {
		this.titlu = titlu;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public void setAudioDownloadLinks(List<String> audioDownloadLinks) {
		this.audioDownloadLinks = audioDownloadLinks;
	}

}
