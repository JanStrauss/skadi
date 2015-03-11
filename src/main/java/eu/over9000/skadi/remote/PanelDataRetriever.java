package eu.over9000.skadi.remote;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.over9000.skadi.model.Panel;
import eu.over9000.skadi.util.HttpUtil;

public class PanelDataRetriever {
	
	private static final JsonParser JSON_PARSER = new JsonParser();

	public static List<Panel> retrievePanels(final String channel) {
		final List<Panel> result = new ArrayList<>();

		try {
			final String response = HttpUtil.getAPIResponse("http://api.twitch.tv/api/channels/" + channel + "/panels");
			final JsonArray parsedResponse = PanelDataRetriever.JSON_PARSER.parse(response).getAsJsonArray();
			
			for (final JsonElement jpe : parsedResponse) {
				String link = null;
				String image = null;
				String title = null;
				String description = null;
				
				final JsonObject jpo = jpe.getAsJsonObject();

				final JsonObject data = jpo.get("data").getAsJsonObject();

				if (data.has("link")) {
					link = data.get("link").getAsString();
				}
				if (data.has("image")) {
					image = data.get("image").getAsString();
				}
				if (data.has("title")) {
					title = data.get("title").getAsString();
				}
				if (data.has("description")) {
					description = data.get("description").getAsString();
				}
				
				result.add(new Panel(link, image, title, description));
			}
			
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static void main(final String[] args) {
		final List<Panel> q = PanelDataRetriever.retrievePanels("draskyl");

		System.out.println(q);
	}
}
