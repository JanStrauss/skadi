package eu.over9000.skadi.remote;

/*******************************************************************************
 * Copyright (c) 2014 Jan Strauß
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.http.client.ClientProtocolException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.util.HttpUtil;

/**
 * This class provides static methods to retrieve channel metadata from the twitch API.
 *
 * @author Jan Strauß
 *
 */
public class ChannelDataRetriever {
	
	private static final JsonParser JSON_PARSER = new JsonParser();
	
	private static long getChannelUptime(final JsonObject channelObject) throws ParseException {
		
		final String start = channelObject.get("created_at").getAsString();
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		final Date start_date = sdf.parse(start);
		final Date now_date = new Date();
		
		// System.out.println("start: " + start_date);
		// System.out.println("now:   " + now_date);
		
		final long uptime = now_date.getTime() - start_date.getTime();
		return uptime;
		
	}
	
	public static Channel getChannelMetadata(final String channel) {
		JsonObject streamResponse = null;
		try {
			
			boolean online;
			int viewers = 0;
			String status = "-";
			String game = "-";
			String logoURL = null;
			long uptime = 0;
			
			streamResponse = ChannelDataRetriever.getStreamData(channel);
			
			final JsonObject streamObject;
			final JsonObject channelObject;
			
			if (streamResponse.get("stream").isJsonNull()) {
				online = false;
				// Handle Offline Stream
				channelObject = ChannelDataRetriever.getChannelDataForOfflineStream(channel);
				
			} else {
				online = true;
				// Handle Online Stream
				streamObject = streamResponse.getAsJsonObject("stream");
				channelObject = streamObject.getAsJsonObject("channel");
				
				uptime = ChannelDataRetriever.getChannelUptime(streamObject);
				viewers = streamObject.get("viewers").getAsInt();
			}
			if (channelObject.has("status") && !channelObject.get("status").isJsonNull()) {
				status = channelObject.get("status").getAsString();
			}
			
			if (channelObject.has("game") && !channelObject.get("game").isJsonNull()) {
				game = channelObject.get("game").getAsString();
			}
			if (channelObject.has("logo") && !channelObject.get("logo").isJsonNull()) {
				logoURL = channelObject.get("logo").getAsString();
			}
			
			final Channel c = new Channel(channel, status, game, viewers, uptime);
			c.setOnline(online);
			if (logoURL != null) {
				c.setLogoURL(logoURL);
			}
			return c;
		} catch (final Exception e) {
			System.out.println(streamResponse);
			e.printStackTrace();
			return null;
		}
	}
	
	private static JsonObject getChannelDataForOfflineStream(final String channel) throws ClientProtocolException, URISyntaxException, IOException {
		final String response = HttpUtil.getAPIResponse("https://api.twitch.tv/kraken/channels/" + channel);
		return ChannelDataRetriever.JSON_PARSER.parse(response).getAsJsonObject();
	}
	
	private static JsonObject getStreamData(final String channel) throws ClientProtocolException, URISyntaxException, IOException {
		final String response = HttpUtil.getAPIResponse("https://api.twitch.tv/kraken/streams/" + channel);
		return ChannelDataRetriever.JSON_PARSER.parse(response).getAsJsonObject();
	}

	public static boolean checkIfChannelExists(final String channel) {
		try {
			HttpUtil.getAPIResponse("https://api.twitch.tv/kraken/channels/" + channel);
			return true;
		} catch (URISyntaxException | IOException e) {
			return false;
		}
	}
	
}
