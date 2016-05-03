/*
 * Copyright (c) 2014-2016 s1mpl3x <jan[at]over9000.eu>
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
 */

package eu.over9000.skadi.remote;

import com.google.gson.Gson;
import eu.over9000.skadi.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EmoteDataRetriever {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmoteDataRetriever.class);

	private static final Gson GSON = new Gson();

	public static List<Emoticon> retrieveEmotes(final String channel) {
		final List<Emoticon> result = new ArrayList<>();

		try {
			final String response = HttpUtil.getAPIResponse("https://api.twitch.tv/kraken/chat/" + channel + "/emoticons");

			final EmoticonResponse emoticonResponse = GSON.fromJson(response, EmoticonResponse.class);

			result.addAll(emoticonResponse.emoticons.stream().filter(emote -> emote.subscriber_only).collect(Collectors.toList()));

		} catch (URISyntaxException | IOException e) {
			LOGGER.error("error getting emote data for " + channel + ": " + e.getMessage());
		}

		return result;
	}

	public class Emoticon {
		final public String regex;
		final public boolean subscriber_only;
		final public String url;

		public Emoticon(final String regex, final boolean subscriber_only, final String url) {
			this.regex = regex;
			this.subscriber_only = subscriber_only;
			this.url = url;
		}
	}

	private class EmoticonResponse {
		final private List<Emoticon> emoticons;

		public EmoticonResponse(final List<Emoticon> emoticons) {
			this.emoticons = emoticons;
		}
	}
}
