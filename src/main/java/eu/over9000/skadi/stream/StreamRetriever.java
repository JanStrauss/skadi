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
package eu.over9000.skadi.stream;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.over9000.skadi.channel.Channel;
import eu.over9000.skadi.logging.SkadiLogging;
import eu.over9000.skadi.util.HttpUtil;
import eu.over9000.skadi.util.M3UParser;
import eu.over9000.skadi.util.StringUtil;

/**
 * This class provides static methods that retrieve available stream qualities from the twitch API.
 * 
 * @author Jan Strauß
 * 
 */
public class StreamRetriever {
	
	private static final JsonParser parser = new JsonParser();
	
	public static StreamDataset getStreams(final Channel channel) {
		for (int tryCount = 0; tryCount < 5; tryCount++) {
			try {
				final String channelname = StringUtil.extractChannelName(channel.getURL());
				
				final String tokenResponse = HttpUtil.getAPIResponse("http://api.twitch.tv/api/channels/" + channelname
				        + "/access_token");
				
				final JsonObject parsedTokenResponse = StreamRetriever.parser.parse(tokenResponse).getAsJsonObject();
				
				final String token = parsedTokenResponse.get("token").getAsString();
				final String sig = parsedTokenResponse.get("sig").getAsString();
				
				final String vidURL = "http://usher.twitch.tv/select/" + channelname + ".json?nauthsig=" + sig
				        + "&nauth=" + URLEncoder.encode(token, "UTF-8") + "&allow_source=true";
				
				final String vidResponse = HttpUtil.getAPIResponse(vidURL);
				
				new M3UParser();
				final List<StreamQuality> quals = M3UParser.parseString(vidResponse);
				
				if ((quals == null) || quals.isEmpty()) {
					SkadiLogging.log("received empty/invalid quality response");
					return null;
				}
				
				return new StreamDataset(channel, quals);
			} catch (final URISyntaxException | IOException e) {
				SkadiLogging.log("failed to retrieve stream on try " + tryCount);
				continue;
			}
		}
		return null;
	}
	
	public static void updateStreamdataDelayed(final Channel channel) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				final long start = System.currentTimeMillis();
				final StreamDataset streamDataset = StreamRetriever.getStreams(channel);
				channel.updateStreamdata(streamDataset);
				final long duration = System.currentTimeMillis() - start;
				
				SkadiLogging.log("retrieved available stream qualities for channel " + channel.getURL() + ", took "
				        + duration + " ms.");
			}
		}, "delayed updater").start();
		;
	}
	
}
