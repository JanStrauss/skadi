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
	
	public static void main(final String[] args) throws Exception {
		final String url = "twitch.tv/weppas/";
		final Channel c = new Channel(url);
		final StreamDataset sd = StreamRetriever.getStreams(c);
		
		final ProcessBuilder pb = new ProcessBuilder(SkadiMain.getInstance().vlc_exec, "--play-and-exit",
		        "--no-video-title-show", "--qt-minimal-view", sd.getHighestQuality().getUrl());
		
		pb.start();
		
	}
	
	public static StreamDataset getStreams(final Channel channel) {
		for (int tryCount = 0; tryCount < 5; tryCount++) {
			try {
				final String channelname = StringUtil.extractChannelName(channel.getURL());
				
				final String tokenResponse = StreamRetriever.getAPIResponse("http://api.twitch.tv/api/channels/"
				        + channelname + "/access_token");
				
				final JsonObject parsedTokenResponse = StreamRetriever.parser.parse(tokenResponse).getAsJsonObject();
				
				// System.out.println(tokenResponse);
				
				final String token = parsedTokenResponse.get("token").getAsString();
				final String sig = parsedTokenResponse.get("sig").getAsString();
				
				// System.out.println(token);
				// System.out.println(sig);
				
				final String vidURL = "http://usher.twitch.tv/select/" + channelname + ".json?nauthsig=" + sig
				        + "&nauth=" + URLEncoder.encode(token, "UTF-8") + "&allow_source=true";
				
				// System.out.println(vidURL);
				
				final String vidResponse = StreamRetriever.getAPIResponse(vidURL);
				
				// System.out.println(vidResponse);
				
				new M3UParser();
				final List<StreamQuality> quals = M3UParser.parseString(vidResponse);
				
				if ((quals == null) || quals.isEmpty()) {
					return null;
				}
				
				return new StreamDataset(channel, quals);
			} catch (final URISyntaxException | IOException e) {
				System.out.println("failed to retrieve stream on try " + tryCount);
				continue;
			}
		}
		return null;
	}
	
}
