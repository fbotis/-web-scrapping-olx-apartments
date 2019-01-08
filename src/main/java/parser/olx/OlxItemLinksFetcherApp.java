package parser.olx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import parser.olx.parsing.ItemLinksFetcher;

@SpringBootApplication
public class OlxItemLinksFetcherApp {

	private static final Logger log = LoggerFactory.getLogger(OlxItemLinksFetcherApp.class);

	public static void main(String[] args) {
		SpringApplication.run(OlxItemLinksFetcherApp.class);
	}

	@Bean
	public CommandLineRunner demo(ItemLinksFetcher itemLinksFetcher) {
		return (args)->{
			itemLinksFetcher.fetch("https://www.olx.ro/imobiliare/apartamente-garsoniere-de-vanzare/1-camera/cluj-napoca/", 1);
			itemLinksFetcher.fetch("https://www.olx.ro/imobiliare/apartamente-garsoniere-de-vanzare/2-camere/cluj-napoca/",1);
			itemLinksFetcher.fetch("https://www.olx.ro/imobiliare/apartamente-garsoniere-de-vanzare/3-camere/cluj-napoca/",1);
			itemLinksFetcher.fetch("https://www.olx.ro/imobiliare/apartamente-garsoniere-de-vanzare/4-camere/cluj-napoca/",1);
		};
	}

}
