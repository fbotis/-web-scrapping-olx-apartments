package parser.olx.parsing;

import com.google.gson.Gson;
import com.google.maps.FindPlaceFromTextRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.FindPlaceFromText;
import org.springframework.stereotype.Component;
import parser.olx.dao.OlxApartmentRepository;
import parser.olx.dao.model.OlxApartament;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class LocationFromLocationStringResolver {

	private final OlxApartmentRepository olxApartmentRepository;
	private final GeoApiContext geoApiContext;

	public LocationFromLocationStringResolver(OlxApartmentRepository olxApartmentRepository) {
		this.olxApartmentRepository = olxApartmentRepository;
		geoApiContext = new GeoApiContext.Builder()
				.apiKey("AIzaSyCGG6qLt0-hhLsPpmoJ0pGHfYZTcFvAP6c")
				.build();

	}

	public void resolve() {
		for (OlxApartament olxApartament : olxApartmentRepository.findAll()) {
			Map<String, String> placeIdCache = new HashMap<>();
			if (olxApartament.getLocationString() != null && olxApartament.getGooglePlaceId() == null) {
				if (placeIdCache.get(olxApartament.getLocationString().trim()) != null) {
					olxApartament.setGooglePlaceId(placeIdCache.get(olxApartament.getLocationString().trim()));
					olxApartmentRepository.save(olxApartament);
					continue;
				}

				FindPlaceFromTextRequest req = PlacesApi.findPlaceFromText(geoApiContext, olxApartament.getLocationString() + " ,Cluj Napoca", FindPlaceFromTextRequest.InputType.TEXT_QUERY);
				try {
					FindPlaceFromText result = req.await();
					if (result.candidates.length == 1) {
						olxApartament.setGooglePlaceId(result.candidates[0].placeId);
						olxApartmentRepository.save(olxApartament);
						placeIdCache.put(olxApartament.getLocationString().trim(), result.candidates[0].placeId);
						System.out.println(olxApartament.getLocationString());
					}

				} catch (ApiException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void resolveAddress() {
		for (OlxApartament olxApartament : olxApartmentRepository.findAll()) {
			if (olxApartament.getGooglePlaceId() != null) {
				try {
					String details = new Gson().toJson(PlacesApi.placeDetails(geoApiContext, olxApartament.getGooglePlaceId()).await());
					olxApartament.setGooglePlaceDetails(details);
					olxApartmentRepository.save(olxApartament);
					System.out.println(details);
				} catch (ApiException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
