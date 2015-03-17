/*******************************************************************************
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014-2015 s1mpl3x <jan[at]over9000.eu>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package eu.over9000.skadi.remote;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.remote.data.ChannelMetadata;
import eu.over9000.skadi.remote.data.ChannelMetadataBuilder;
import eu.over9000.skadi.util.HttpUtil;

/**
 * This class provides static methods to retrieve channel metadata from the twitch API.
 *
 * @author Jan Strauß
 *
 */
public class ChannelDataRetriever {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ChannelDataRetriever.class);

	private static final JsonParser JSON_PARSER = new JsonParser();

	private static long getChannelUptime(final JsonObject channelObject) throws ParseException {

		final String start = channelObject.get("created_at").getAsString();
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

		final Date start_date = sdf.parse(start);
		final Date now_date = new Date();

		final long uptime = now_date.getTime() - start_date.getTime();
		return uptime;

	}

	public static ChannelMetadata getChannelMetadata(final Channel channel) {
		
		try {
			final JsonObject streamResponse = ChannelDataRetriever.getStreamData(channel.getName());
			final ChannelMetadataBuilder builder = new ChannelMetadataBuilder();

			final JsonObject streamObject;
			final JsonObject channelObject;

			final boolean isOnline = !streamResponse.get("stream").isJsonNull();
			builder.setOnline(isOnline);
			
			if (isOnline) {
				// Handle Online Stream
				streamObject = streamResponse.getAsJsonObject("stream");
				channelObject = streamObject.getAsJsonObject("channel");

				builder.setUptime(ChannelDataRetriever.getChannelUptime(streamObject));
				builder.setViewer(streamObject.get("viewers").getAsInt());

			} else {
				// Handle Offline Stream
				channelObject = ChannelDataRetriever.getChannelDataForOfflineStream(channel.getName());
			}
			
			builder.setTitle(ChannelDataRetriever.getStringIfPresent("status", channelObject));
			builder.setGame(ChannelDataRetriever.getStringIfPresent("game", channelObject));
			builder.setLogoURL(ChannelDataRetriever.getStringIfPresent("logo", channelObject));
			builder.setViews(ChannelDataRetriever.getIntIfPresent("views", channelObject));
			builder.setFollowers(ChannelDataRetriever.getIntIfPresent("followers", channelObject));
			builder.setPartner(ChannelDataRetriever.getBoolIfPresent("partner", channelObject));
			
			return builder.build();
		} catch (final Exception e) {
			ChannelDataRetriever.LOGGER.error("Exception getting metadata for channel " + channel + ": "
					+ e.getMessage());
			return null;
		}
	}
	
	private static Boolean getBoolIfPresent(final String name, final JsonObject jsonObject) {
		if (jsonObject.has(name) && !jsonObject.get(name).isJsonNull()) {
			return jsonObject.get(name).getAsBoolean();
		}
		return null;
	}
	
	private static String getStringIfPresent(final String name, final JsonObject jsonObject) {
		if (jsonObject.has(name) && !jsonObject.get(name).isJsonNull()) {
			return jsonObject.get(name).getAsString();
		}
		return null;
	}
	
	private static Integer getIntIfPresent(final String name, final JsonObject jsonObject) {
		if (jsonObject.has(name) && !jsonObject.get(name).isJsonNull()) {
			return jsonObject.get(name).getAsInt();
		}
		return null;
	}

	private static JsonObject getChannelDataForOfflineStream(final String channel) throws ClientProtocolException,
	        URISyntaxException, IOException {
		final String response = HttpUtil.getAPIResponse("https://api.twitch.tv/kraken/channels/" + channel);
		return ChannelDataRetriever.JSON_PARSER.parse(response).getAsJsonObject();
	}

	private static JsonObject getStreamData(final String channel) throws ClientProtocolException, URISyntaxException,
	        IOException {
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
