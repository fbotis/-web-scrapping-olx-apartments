package apart.scrapping.olx;

import apart.scrapping.olx.fetch.ApartamentOlxFetcher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages =
		{ "apart.scrapping" })
public class ApartamentOlxFetcherApp {

	public static void main(String[] args) {
		SpringApplication.run(ApartamentOlxFetcherApp.class);
	}

	@Bean
	public CommandLineRunner run(ApartamentOlxFetcher fetcher) {
		return (args)->
				//				fetcher.fetch();
				fetcher.fetch();
	}

}
