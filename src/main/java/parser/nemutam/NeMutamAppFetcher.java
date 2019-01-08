package parser.nemutam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class NeMutamAppFetcher {

	private static final Logger log = LoggerFactory.getLogger(NeMutamAppFetcher.class);

	public static void main(String[] args) {
		SpringApplication.run(NeMutamAppFetcher.class);
	}

	@Bean
	public CommandLineRunner demo(NetMutamFetcherWithProxy netMutamFetcherWithProxy) {
		return (args)->{
			netMutamFetcherWithProxy.fetch(16, 0);
		};
	}
}
