package parser.olx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import parser.olx.parsing.InvalidLinksChecker;

@SpringBootApplication
public class InvalidLinksCheckerApp {

	private static final Logger log = LoggerFactory.getLogger(InvalidLinksCheckerApp.class);

	public static void main(String[] args) {
		SpringApplication.run(InvalidLinksCheckerApp.class);
	}

	@Bean
	public CommandLineRunner demo(InvalidLinksChecker invalidLinksChecker) {
		return (args)->{
			invalidLinksChecker.check();
		};
	}
}
