package eu.over9000.skadi.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import eu.over9000.skadi.logging.SkadiLogging;

public class SkadiVersionRetriever {
	private static final HttpClient httpClient = HttpClients.createMinimal();
	
	private static final JsonParser parser = new JsonParser();
	
	private static String getAPIResponse(final String api_url) throws URISyntaxException, IOException {
		final URI URL = new URI(api_url);
		final HttpResponse response = SkadiVersionRetriever.httpClient.execute(new HttpGet(URL));
		
		final String responseString = new BasicResponseHandler().handleResponse(response);
		return responseString;
		
	}
	
	public static String getLatestVersion() {
		
		try {
			final String response = SkadiVersionRetriever
			        .getAPIResponse("https://api.github.com/repos/s1mpl3x/skadi/releases");
			
			final JsonArray tagsArray = SkadiVersionRetriever.parser.parse(response).getAsJsonArray();
			final String name = tagsArray.get(0).getAsJsonObject().get("tag_name").getAsString();
			
			return name;
		} catch (URISyntaxException | IOException e) {
			SkadiLogging.log(e);
		}
		return "";
	}
}
