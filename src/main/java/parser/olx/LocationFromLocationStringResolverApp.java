package parser.olx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import parser.olx.parsing.LocationFromLocationStringResolver;

@SpringBootApplication
public class LocationFromLocationStringResolverApp {

	private static final Logger log = LoggerFactory.getLogger(LocationFromLocationStringResolverApp.class);

	public static void main(String[] args) {
		SpringApplication.run(LocationFromLocationStringResolverApp.class);
	}

	@Bean
	public CommandLineRunner demo(LocationFromLocationStringResolver resolver) {
		return (args)->{
			//resolver.resolve();
			resolver.resolveAddress();
		};
	}
}
