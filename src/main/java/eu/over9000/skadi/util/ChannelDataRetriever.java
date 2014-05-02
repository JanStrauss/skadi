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
	
	private static final JsonParser parser = new JsonParser();
	
	private static String getChannelData(final String api_url, final String channel) {
		try {
			final URI URL = new URI(api_url + channel);
			final HttpResponse response = ChannelDataRetriever.httpClient.execute(new HttpGet(URL));
			final String responseString = new BasicResponseHandler().handleResponse(response);
			return responseString;
		} catch (final URISyntaxException | IOException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	private static long getChannelUptime(final JsonArray response) {
		
		if (response.size() == 0) {
			return -1;
		}
		
		try {
			final JsonObject streamObject = response.get(0).getAsJsonObject();
			final String start = streamObject.get("up_time").getAsString();
			final DateFormat format = new SimpleDateFormat("EEE MMMM dd HH:mm:ss yyyy", Locale.ENGLISH);
			format.setTimeZone(java.util.TimeZone.getTimeZone("US/Pacific"));
			final Date start_date = format.parse(start);
			final Date now_date = new Date();
			final long uptime = now_date.getTime() - start_date.getTime();
			return uptime;
		} catch (final ParseException e) {
			
			e.printStackTrace();
			return -1;
		}
		
	}
	
	public static ChannelMetadata getChannelMetadata(final String url) {
		boolean online;
		int viewers = 0;
		String status;
		String game;
		long uptime = 0;
		
		final String channel = StringUtil.extractChannelName(url);
		
		final JsonObject streamResponse = ChannelDataRetriever.getStreamData(channel);
		
		final JsonObject streamObject;
		final JsonObject channelObject;
		
		if (streamResponse.get("stream").isJsonNull()) {
			online = false;
			// Handle Offline Stream
			System.out.println("CHANNEL IS OFFLINE: " + url);
			channelObject = ChannelDataRetriever.getChannelDataForOfflineStream(channel);
			
		} else {
			online = true;
			// Handle Online Stream
			streamObject = streamResponse.getAsJsonObject("stream");
			channelObject = streamObject.getAsJsonObject("channel");
			
			uptime = ChannelDataRetriever.getChannelUptime(ChannelDataRetriever.getJustinTVData(channel));
			viewers = streamObject.get("viewers").getAsInt();
		}
		
		status = channelObject.get("status").getAsString();
		game = channelObject.get("game").getAsString();
		
		return new ChannelMetadata(online, viewers, channel, status, game, uptime);
	}
	
	private static JsonObject getChannelDataForOfflineStream(final String channel) {
		final String response = ChannelDataRetriever.getChannelData("https://api.twitch.tv/kraken/channels/", channel);
		return ChannelDataRetriever.parser.parse(response).getAsJsonObject();
	}
	
	private static JsonObject getStreamData(final String channel) {
		final String response = ChannelDataRetriever.getChannelData("https://api.twitch.tv/kraken/streams/", channel);
		return ChannelDataRetriever.parser.parse(response).getAsJsonObject();
	}
	
	private static JsonArray getJustinTVData(final String channel) {
		final String response = ChannelDataRetriever.getChannelData(
		        "http://api.justin.tv/api/stream/list.json?channel=", channel);
		return ChannelDataRetriever.parser.parse(response).getAsJsonArray();
	}
	
	private static void printResponse(final String response) {
		final Gson gson = new GsonBuilder().setPrettyPrinting().create();
		final String pretty = gson.toJson(response);
		System.out.println(pretty);
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
