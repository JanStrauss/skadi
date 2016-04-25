/*
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
 */

package eu.over9000.skadi.remote;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.over9000.skadi.model.Panel;
import eu.over9000.skadi.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class PanelDataRetriever {

	private static final Logger LOGGER = LoggerFactory.getLogger(PanelDataRetriever.class);

	private static final JsonParser JSON_PARSER = new JsonParser();

	public static List<Panel> retrievePanels(final String channel) {
		final List<Panel> result = new ArrayList<>();

		try {
			final String response = HttpUtil.getAPIResponse("http://api.twitch.tv/api/channels/" + channel +
					"/panels");
			final JsonArray parsedResponse = JSON_PARSER.parse(response).getAsJsonArray();

			for (final JsonElement jpe : parsedResponse) {
				String link = null;
				String image = null;
				String title = null;
				String description = null;

				final JsonObject jpo = jpe.getAsJsonObject();

				final JsonObject data = jpo.get("data").getAsJsonObject();

				if (data.has("link")) {
					link = data.get("link").getAsString();
				}
				if (data.has("image")) {
					image = data.get("image").getAsString();
				}
				if (data.has("title")) {
					title = data.get("title").getAsString();
				}
				if (data.has("description")) {
					description = data.get("description").getAsString();
				}

				result.add(new Panel(link, image, title, description));
			}

		} catch (URISyntaxException | IOException e) {
			LOGGER.error("error getting panels data for " + channel + ": " + e.getMessage());
		}

		return result;
	}
}
