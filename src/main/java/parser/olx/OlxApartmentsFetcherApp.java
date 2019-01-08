package parser.olx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import parser.olx.parsing.ApartmentFetcher;

@SpringBootApplication
public class OlxApartmentsFetcherApp {

	private static final Logger log = LoggerFactory.getLogger(OlxApartmentsFetcherApp.class);

	public static void main(String[] args) {
		SpringApplication.run(OlxApartmentsFetcherApp.class);
	}

	@Bean
	public CommandLineRunner demo(ApartmentFetcher apartmentFetcher) {
		return (args)->{
			apartmentFetcher.startFetching();
		};
	}
}
