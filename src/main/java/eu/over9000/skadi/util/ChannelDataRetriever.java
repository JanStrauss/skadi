package eu.over9000.skadi.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.over9000.skadi.SkadiMain;
import eu.over9000.skadi.channel.ChannelMetadata;
import eu.over9000.skadi.gui.ImportDialog;
import eu.over9000.skadi.logging.SkadiLogging;

public class ChannelDataRetriever {
	private static final HttpClient httpClient = HttpClients.createMinimal();
	
	private static final JsonParser parser = new JsonParser();
	
	private static String getAPIResponse(final String api_url) throws URISyntaxException, ClientProtocolException,
	        IOException {
		final URI URL = new URI(api_url);
		final HttpGet request = new HttpGet(URL);
		request.setHeader("Client-ID", SkadiMain.CLIENT_ID);
		final HttpResponse response = ChannelDataRetriever.httpClient.execute(request);
		final String responseString = new BasicResponseHandler().handleResponse(response);
		return responseString;
		
	}
	
	private static long getChannelUptime(final JsonArray response) throws ParseException {
		if (response.size() < 1) {
			return -1;
		}
		final JsonObject streamObject = response.get(0).getAsJsonObject();
		final String start = streamObject.get("up_time").getAsString();
		final DateFormat format = new SimpleDateFormat("EEE MMMM dd HH:mm:ss yyyy", Locale.ENGLISH);
		format.setTimeZone(java.util.TimeZone.getTimeZone("US/Pacific"));
		final Date start_date = format.parse(start);
		final Date now_date = new Date();
		final long uptime = now_date.getTime() - start_date.getTime();
		return uptime;
		
	}
	
	public static ChannelMetadata getChannelMetadata(final String url) {
		try {
			
			boolean online;
			int viewers = 0;
			String status = "-";
			String game = "-";
			long uptime = 0;
			
			final String channel = StringUtil.extractChannelName(url);
			
			final JsonObject streamResponse = ChannelDataRetriever.getStreamData(channel);
			
			final JsonObject streamObject;
			final JsonObject channelObject;
			
			if (streamResponse.get("stream").isJsonNull()) {
				online = false;
				// Handle Offline Stream
				// SkadiLogging.log("CHANNEL IS OFFLINE: " + url);
				channelObject = ChannelDataRetriever.getChannelDataForOfflineStream(channel);
				
			} else {
				online = true;
				// Handle Online Stream
				streamObject = streamResponse.getAsJsonObject("stream");
				channelObject = streamObject.getAsJsonObject("channel");
				
				uptime = ChannelDataRetriever.getChannelUptime(ChannelDataRetriever.getJustinTVData(channel));
				viewers = streamObject.get("viewers").getAsInt();
			}
			if (!channelObject.get("status").isJsonNull()) {
				status = channelObject.get("status").getAsString();
			}
			
			if (!channelObject.get("game").isJsonNull()) {
				game = channelObject.get("game").getAsString();
			}
			
			return new ChannelMetadata(online, viewers, channel, status, game, uptime);
		} catch (final Exception e) {
			SkadiLogging.log(e);
			return null;
		}
	}
	
	private static JsonObject getChannelDataForOfflineStream(final String channel) throws ClientProtocolException,
	        URISyntaxException, IOException {
		final String response = ChannelDataRetriever.getAPIResponse("https://api.twitch.tv/kraken/channels/" + channel);
		return ChannelDataRetriever.parser.parse(response).getAsJsonObject();
	}
	
	private static JsonObject getStreamData(final String channel) throws ClientProtocolException, URISyntaxException,
	        IOException {
		final String response = ChannelDataRetriever.getAPIResponse("https://api.twitch.tv/kraken/streams/" + channel);
		return ChannelDataRetriever.parser.parse(response).getAsJsonObject();
	}
	
	private static JsonArray getJustinTVData(final String channel) throws ClientProtocolException, URISyntaxException,
	        IOException {
		final String response = ChannelDataRetriever
		        .getAPIResponse("http://api.justin.tv/api/stream/list.json?channel=" + channel);
		return ChannelDataRetriever.parser.parse(response).getAsJsonArray();
	}
	
	public static Set<String> getFollowedChannels(final String twitchUsername, final ImportDialog importDialog) {
		try {
			final Set<String> channels = new TreeSet<>();
			
			int limit = 0;
			int offset = 0;
			
			String url = "https://api.twitch.tv/kraken/users/" + twitchUsername + "/follows/channels";
			String response = ChannelDataRetriever.getAPIResponse(url);
			JsonObject responseObject = ChannelDataRetriever.parser.parse(response).getAsJsonObject();
			
			String parameters = responseObject.getAsJsonObject("_links").get("self").getAsString().split("\\?")[1];
			String[] split = parameters.split("&");
			
			for (final String string : split) {
				if (string.startsWith("limit")) {
					limit = Integer.valueOf(string.split("=")[1]);
				} else if (string.startsWith("offset")) {
					offset = Integer.valueOf(string.split("=")[1]);
				}
			}
			
			final int count = responseObject.get("_total").getAsInt();
			SkadiLogging.log("total channels followed: " + count);
			
			importDialog.updateProgress(count, channels.size(), "Loaded " + channels.size() + " of " + count
			        + " channels");
			
			while (offset < count) {
				
				ChannelDataRetriever.parseAndAddChannelsToSet(channels, responseObject);
				
				url = "https://api.twitch.tv/kraken/users/" + twitchUsername + "/follows/channels?limit=" + limit
				        + "&offset=" + (offset + limit);
				response = ChannelDataRetriever.getAPIResponse(url);
				responseObject = ChannelDataRetriever.parser.parse(response).getAsJsonObject();
				
				parameters = responseObject.getAsJsonObject("_links").get("self").getAsString().split("\\?")[1];
				split = parameters.split("&");
				for (final String string : split) {
					if (string.startsWith("limit")) {
						limit = Integer.valueOf(string.split("=")[1]);
					} else if (string.startsWith("offset")) {
						offset = Integer.valueOf(string.split("=")[1]);
					}
				}
				
				SkadiLogging.log("limit=" + limit + " offset=" + offset + " channelsize=" + channels.size());
				importDialog.updateProgress(count, channels.size(), "Loaded " + channels.size() + " of " + count
				        + " channels");
			}
			
			return channels;
		} catch (final Exception e) {
			SkadiLogging.log(e);
			return new TreeSet<>();
		}
	}
	
	private static void parseAndAddChannelsToSet(final Set<String> channels, final JsonObject responseObject) {
		
		final JsonArray follows = responseObject.getAsJsonArray("follows");
		
		for (final JsonElement jsonElement : follows) {
			final String followed_url = jsonElement.getAsJsonObject().getAsJsonObject("channel").get("url")
			        .getAsString();
			channels.add(followed_url);
		}
	}
	
	public static boolean checkIfChannelExists(final String url) {
		try {
			ChannelDataRetriever.getAPIResponse("https://api.twitch.tv/kraken/channels/"
			        + StringUtil.extractChannelName(url));
			return true;
		} catch (URISyntaxException | IOException e) {
			SkadiLogging.log(e);
			return false;
		}
	}
}
