package parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import parser.olx.parsing.InvalidLinksChecker;

@SpringBootApplication
public class DBApp {

	private static final Logger log = LoggerFactory.getLogger(DBApp.class);

	public static void main(String[] args) {
		SpringApplication.run(DBApp.class);
	}

	@Bean
	public CommandLineRunner demo(InvalidLinksChecker invalidLinksChecker) {
		return (args)->{
		};
	}

}
