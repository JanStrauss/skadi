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
package eu.over9000.skadi.util;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.over9000.skadi.channel.ChannelManager;
import eu.over9000.skadi.gui.ImportDialog;
import eu.over9000.skadi.io.PersistenceManager;
import eu.over9000.skadi.logging.SkadiLogging;

/**
 * Util class used to import followed channels from twitch.
 * 
 * @author Jan Strauß
 * 
 */
public class ImportFollowedUtil {
	
	private static final JsonParser parser = new JsonParser();
	
	private static Set<String> getFollowedChannels(final String twitchUsername, final ImportDialog importDialog) {
		try {
			final Set<String> channels = new TreeSet<>();
			
			int limit = 0;
			int offset = 0;
			
			String url = "https://api.twitch.tv/kraken/users/" + twitchUsername + "/follows/channels";
			String response = HttpUtil.getAPIResponse(url);
			JsonObject responseObject = ImportFollowedUtil.parser.parse(response).getAsJsonObject();
			
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
				
				ImportFollowedUtil.parseAndAddChannelsToSet(channels, responseObject);
				
				url = "https://api.twitch.tv/kraken/users/" + twitchUsername + "/follows/channels?limit=" + limit
				        + "&offset=" + (offset + limit);
				response = HttpUtil.getAPIResponse(url);
				responseObject = ImportFollowedUtil.parser.parse(response).getAsJsonObject();
				
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
	
	public static String importFollowedChannelsFromTwitch(final String username, final ImportDialog importDialog) {
		importDialog.setIndeterminate(true);
		final Set<String> newChannels = ImportFollowedUtil.getFollowedChannels(username, importDialog);
		
		int count = 0;
		
		final Iterator<String> iterator = newChannels.iterator();
		
		for (int index = 0; index < newChannels.size(); index++) {
			
			final String url = iterator.next();
			final boolean result = ChannelManager.getInstance().addChannel(url, false);
			importDialog.updateProgress(newChannels.size(), newChannels.size() + index, "Importing " + index + " of "
			        + newChannels.size() + " channels");
			if (result) {
				count++;
			}
		}
		
		PersistenceManager.getInstance().saveData();
		
		return "Imported " + count + " of " + newChannels.size() + " followed channels.";
	}
}
