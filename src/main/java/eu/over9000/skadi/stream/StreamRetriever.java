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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.over9000.skadi.SkadiMain;
import eu.over9000.skadi.channel.Channel;
import eu.over9000.skadi.logging.SkadiLogging;
import eu.over9000.skadi.util.M3UParser;
import eu.over9000.skadi.util.StringUtil;

public class StreamRetriever {
	
	private static final HttpClient httpClient = HttpClients.createMinimal();
	
	private static final JsonParser parser = new JsonParser();
	
	private static String getAPIResponse(final String api_url) throws URISyntaxException, ClientProtocolException,
	        IOException {
		final URI URL = new URI(api_url);
		final HttpGet request = new HttpGet(URL);
		request.setHeader("Client-ID", SkadiMain.CLIENT_ID);
		final HttpResponse response = StreamRetriever.httpClient.execute(request);
		final String responseString = new BasicResponseHandler().handleResponse(response);
		return responseString;
	}
	
	public static StreamDataset getStreams(final Channel channel) {
		for (int tryCount = 0; tryCount < 5; tryCount++) {
			try {
				final String channelname = StringUtil.extractChannelName(channel.getURL());
				
				final String tokenResponse = StreamRetriever.getAPIResponse("http://api.twitch.tv/api/channels/"
				        + channelname + "/access_token");
				
				final JsonObject parsedTokenResponse = StreamRetriever.parser.parse(tokenResponse).getAsJsonObject();
				
				// SkadiLogging.log(tokenResponse);
				
				final String token = parsedTokenResponse.get("token").getAsString();
				final String sig = parsedTokenResponse.get("sig").getAsString();
				
				// SkadiLogging.log(token);
				// SkadiLogging.log(sig);
				
				final String vidURL = "http://usher.twitch.tv/select/" + channelname + ".json?nauthsig=" + sig
				        + "&nauth=" + URLEncoder.encode(token, "UTF-8") + "&allow_source=true";
				
				// SkadiLogging.log(vidURL);
				
				final String vidResponse = StreamRetriever.getAPIResponse(vidURL);
				
				// SkadiLogging.log(vidResponse);
				
				new M3UParser();
				final List<StreamQuality> quals = M3UParser.parseString(vidResponse);
				
				if ((quals == null) || quals.isEmpty()) {
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
