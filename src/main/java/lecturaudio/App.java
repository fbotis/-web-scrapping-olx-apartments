package lecturaudio;

import static java.util.stream.Collectors.toList;
import lecturaudio.dao.AudioBookRepository;
import lecturaudio.model.AudioBooksWithNextPage;
import lecturaudio.model.Audiobook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import scrapping.framework.ToBeanTransformer;
import scrapping.framework.http.SimpleURLHttpClient;

import java.util.Objects;

@SpringBootApplication
public class App implements CommandLineRunner {

	@Autowired
	private AudioBookRepository audioBookRepository;

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@Override public void run(String... args) throws Exception {
		ToBeanTransformer<AudioBooksWithNextPage> audioBooksWithNextPageToBeanTransformer =
				new ToBeanTransformer<>("lecturaaudio/lectura-audio-links.xsl", AudioBooksWithNextPage.class, new SimpleURLHttpClient());
		AudioBooksWithNextPage audioBooksWithNextPage = audioBooksWithNextPageToBeanTransformer.scrapePage("http://lectura-audio.blogspot.com/");
		for (Audiobook audiobook : audioBooksWithNextPage.getItems().stream().filter(Objects::nonNull).collect(toList())) {
			audioBookRepository.save(audiobook);
		}
	}
}
