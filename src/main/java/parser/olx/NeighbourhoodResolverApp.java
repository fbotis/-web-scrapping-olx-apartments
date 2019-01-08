package parser.olx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import parser.olx.parsing.ApartmentFetcher;
import parser.olx.parsing.NeighbourhoodResolver;

@SpringBootApplication
public class NeighbourhoodResolverApp {

	private static final Logger log = LoggerFactory.getLogger(NeighbourhoodResolverApp.class);

	public static void main(String[] args) {
		SpringApplication.run(NeighbourhoodResolverApp.class);
	}

	@Bean
	public CommandLineRunner demo(NeighbourhoodResolver neighbourhoodResolver) {
		return (args)->{
			neighbourhoodResolver.resolve3();
//			neighbourhoodResolver.test();
		};
	}
}
