package apart.scrapping.olx;

import apart.scrapping.olx.fetch.LinkApartamentOlxFetcher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages =
		{ "apart.scrapping" })
public class LinkApartamentOlxFetcherApp {

	public static void main(String[] args) {
		SpringApplication.run(LinkApartamentOlxFetcherApp.class);
	}

	@Bean
	public CommandLineRunner run(LinkApartamentOlxFetcher fetcher) {
		return (args)->{
			fetcher.fetchNonCartier("https://www.olx.ro/imobiliare/apartamente-garsoniere-de-vanzare/cluj-napoca/", false);
			fetcher.fetchNonCartier("https://www.olx.ro/imobiliare/apartamente-garsoniere-de-vanzare/oradea/", false);
		};
	}

}
