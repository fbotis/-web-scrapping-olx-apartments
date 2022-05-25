import com.google.maps.FindPlaceFromTextRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;

import java.io.IOException;
import java.util.stream.Stream;

public class Places {

	public static void main(String[] args) throws InterruptedException, ApiException, IOException {
		GeoApiContext geoApiContext = new GeoApiContext.Builder()
				.build();

		FindPlaceFromTextRequest req = PlacesApi.findPlaceFromText(geoApiContext, "Horea, Cluj Napoca", FindPlaceFromTextRequest.InputType.TEXT_QUERY);
		Stream.of(req.await().candidates).forEach(
				res->{
					try {
						System.out.println(PlacesApi.placeDetails(geoApiContext,res.placeId).await());
					} catch (ApiException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
		);



	}

}
