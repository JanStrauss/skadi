package eu.over9000.skadi.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.over9000.skadi.channel.ChannelMetadata;

/**
 * TODO: use https://api.twitch.tv/kraken/users/XXX/follows/channels for channel list setup
 * 
 */
public class ChannelDataRetriever {
	private static final HttpClient httpClient = HttpClients.createMinimal();
	
	private static String getChannelData(final String url) {
		
		try {
			final URI URL = new URI("http://api.justin.tv/api/stream/list.json?channel="
			        + ChannelDataRetriever.extractChannelName(url));
			final HttpResponse response = ChannelDataRetriever.httpClient.execute(new HttpGet(URL));
			final String responseString = new BasicResponseHandler().handleResponse(response);
			return responseString;
		} catch (final URISyntaxException | IOException e) {
			e.printStackTrace();
			return "[]";
		}
	}
	
	private static String extractChannelName(final String url) {
		final String[] splitted = url.split("/");
		return splitted[splitted.length - 1];
	}
	
	public static ChannelMetadata getChannelMetadata(final String url) {
		final String response = ChannelDataRetriever.getChannelData(url);
		
		final JsonArray parsedJson = new JsonParser().parse(response).getAsJsonArray();
		
		if (parsedJson.size() == 0) {
			System.out.println("CHANNEL IS OFFLINE: " + url);
			return new ChannelMetadata();
		}
		
		final Gson gson = new GsonBuilder().setPrettyPrinting().create();
		final String pretty = gson.toJson(parsedJson);
		// System.out.println(pretty);
		
		final JsonObject streamObject = parsedJson.get(0).getAsJsonObject();
		
		final String title = streamObject.get("title").getAsString();
		final int viewers = streamObject.get("channel_count").getAsInt();
		final String game = streamObject.get("meta_game").getAsString();
		final String start = streamObject.get("up_time").getAsString();
		// final String timezone = streamObject.getAsJsonObject("channel").get("timezone").getAsString();
		// System.out.println(start);
		try {
			final DateFormat format = new SimpleDateFormat("EEE MMMM dd HH:mm:ss yyyy", Locale.ENGLISH);
			format.setTimeZone(java.util.TimeZone.getTimeZone("US/Pacific"));
			final Date start_date = format.parse(start);
			final Date now_date = new Date();
			final long uptime = now_date.getTime() - start_date.getTime();
			
			// System.out.println(new Date(now_date.getTime() - uptime));
			return new ChannelMetadata(viewers, title, game, uptime);
		} catch (final ParseException e) {
			e.printStackTrace();
			return new ChannelMetadata(viewers, title, game, -1);
		}
		
	}
	
	public static void main(final String[] args) {
		final ChannelMetadata meta = ChannelDataRetriever.getChannelMetadata("http://www.twitch.tv/luminousinverse/");
		
		System.out.println(meta.getUptime());
		System.out.println(TimeUtil.getDurationBreakdown(meta.getUptime()));
		
		System.out.println();
		
		final ChannelMetadata meta2 = ChannelDataRetriever.getChannelMetadata("http://www.twitch.tv/joindotared/");
		
		System.out.println(meta2.getUptime());
		System.out.println(TimeUtil.getDurationBreakdown(meta2.getUptime()));
	}
}
