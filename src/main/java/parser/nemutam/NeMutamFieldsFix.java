package parser.nemutam;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import parser.nemutam.dao.NeMutamAp;
import parser.nemutam.dao.NeMutamApRepository;

import java.time.LocalDateTime;

@Component
public class NeMutamFieldsFix {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final NeMutamApRepository neMutamApRepository;

	public NeMutamFieldsFix(NeMutamApRepository neMutamApRepository) {
		this.neMutamApRepository = neMutamApRepository;
	}

	public void fix() {
		int index = 0;
		for (NeMutamAp neMutamAp : neMutamApRepository.findAll()) {
			neMutamAp.setPublicatDate(parsePublicat(neMutamAp.getPublicat(), neMutamAp.getFetched()));
			neMutamAp.setPret(parsePret(neMutamAp.getPret().replaceAll("\\s+", "")));
			neMutamAp.setSuprafata(parseSuprafata(neMutamAp.getSuprafata().replaceAll("\\s+", "")));
			if (!Strings.isNullOrEmpty(neMutamAp.getSuprafata())) {
				neMutamAp.setSuprafataInt(Integer.parseInt(neMutamAp.getSuprafata()));
			}
			neMutamApRepository.save(neMutamAp);
			index++;
			if (index % 100 == 0) {
				log.info("Fixed count={}", index);
			}
		}
	}

	private String parseSuprafata(String suprafata) {
		return suprafata.replaceAll("mp", "");
	}

	private String parsePret(String pret) {
		return pret.replaceAll("Euro", "").replaceAll("\\.", " ");
	}

	/**
	 * o oră
	 2 ore
	 o zi
	 2 zile
	 o săptămână
	 2 săptămâni
	 o lună
	 2 luni
	 un an
	 */
	private LocalDateTime parsePublicat(String publicat, LocalDateTime fetched) {
		String xAgo = publicat.replaceAll("acum", "").trim();
		if (xAgo.contains("un minut")) {
			return fetched.minusMinutes(1);
		} else if (xAgo.contains("minute")) {
			return fetched.minusMinutes(Integer.parseInt(xAgo.replaceAll("minute", "").trim()));
		} else if (xAgo.contains("o oră")) {
			return fetched.minusHours(1);
		} else if (xAgo.contains("ore")) {
			return fetched.minusHours(Integer.parseInt(xAgo.replaceAll("ore", "").trim()));
		} else if (xAgo.contains("o zi")) {
			return fetched.minusDays(1);
		} else if (xAgo.contains("zile")) {
			return fetched.minusDays(Integer.parseInt(xAgo.replaceAll("zile", "").trim()));
		} else if (xAgo.contains("o săptămână")) {
			return fetched.minusWeeks(1);
		} else if (xAgo.contains("săptămâni")) {
			return fetched.minusWeeks(Integer.parseInt(xAgo.replaceAll("săptămâni", "").trim()));
		} else if (xAgo.contains("o lună")) {
			return fetched.minusMonths(1);
		} else if (xAgo.contains("luni")) {
			return fetched.minusMonths(Integer.parseInt(xAgo.replaceAll("luni", "").trim()));
		} else if (xAgo.contains("un an")) {
			return fetched.minusYears(1);
		} else if (xAgo.contains("ani")) {
			return fetched.minusYears(Integer.parseInt(xAgo.replaceAll("ani", "").trim()));
		}
		return null;
	}
}
