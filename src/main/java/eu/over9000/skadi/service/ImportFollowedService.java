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

package eu.over9000.skadi.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.over9000.skadi.model.ChannelStore;
import eu.over9000.skadi.ui.StatusBarWrapper;
import eu.over9000.skadi.util.HttpUtil;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.http.client.HttpResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.TreeSet;

public class ImportFollowedService extends Service<Set<String>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImportFollowedService.class);

	private final JsonParser parser = new JsonParser();

	private final String user;

	@SuppressWarnings("unchecked")
	public ImportFollowedService(final ChannelStore channelStore, final String user, final StatusBarWrapper statusBar) {
		this.user = user;

		setOnSucceeded(event -> {
			final Set<String> result = (Set<String>) event.getSource().getValue();

			statusBar.progressProperty().unbind();
			statusBar.textProperty().unbind();

			if (result != null) {
				channelStore.addChannels(result, statusBar);
			}

			statusBar.updateProgress(0);
		});
		setOnFailed(event -> LOGGER.error("import followed failed ", event.getSource().getException()));

		statusBar.progressProperty().bind(progressProperty());
		statusBar.textProperty().bind(messageProperty());
	}

	private void parseAndAddChannelsToSet(final Set<String> channels, final JsonObject responseObject) {

		final JsonArray follows = responseObject.getAsJsonArray("follows");

		for (final JsonElement jsonElement : follows) {
			final String followed_url = jsonElement.getAsJsonObject().getAsJsonObject("channel").get("name").getAsString();
			channels.add(followed_url);
		}
	}

	@Override
	protected Task<Set<String>> createTask() {
		return new Task<Set<String>>() {

			@Override
			protected Set<String> call() throws Exception {

				updateMessage("importing channels for " + user);
				try {
					final Set<String> channels = new TreeSet<>();

					int limit = 0;
					int offset = 0;

					String url = "https://api.twitch.tv/kraken/users/" + user +
							"/follows/channels";
					String response = HttpUtil.getAPIResponse(url);
					JsonObject responseObject = parser.parse(response).getAsJsonObject();

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
					LOGGER.debug("total channels followed: " + count);

					updateProgress(count, channels.size());
					updateMessage("Loaded " + channels.size() + " of " + count + " channels");

					while (offset < count) {

						parseAndAddChannelsToSet(channels, responseObject);

						url = "https://api.twitch.tv/kraken/users/" + user +
								"/follows/channels?limit=" + limit + "&offset=" + (offset + limit);
						response = HttpUtil.getAPIResponse(url);
						responseObject = parser.parse(response).getAsJsonObject();

						parameters = responseObject.getAsJsonObject("_links").get("self").getAsString().split("\\?")[1];
						split = parameters.split("&");
						for (final String string : split) {
							if (string.startsWith("limit")) {
								limit = Integer.valueOf(string.split("=")[1]);
							} else if (string.startsWith("offset")) {
								offset = Integer.valueOf(string.split("=")[1]);
							}
						}

						LOGGER.debug("limit=" + limit + " offset=" + offset + " channelsize=" +
								channels.size());

						updateProgress(count, channels.size());
						updateMessage("Loaded " + channels.size() + " of " + count + " channels");
					}

					return channels;

				} catch (final HttpResponseException e) {
					if (e.getStatusCode() == 404) {
						updateMessage("The given user does not exist");
						return null;
					}

					updateMessage("Error: " + e.getMessage());
					LOGGER.error("Error", e);
					return null;
				} catch (final Exception e) {
					updateMessage("Error: " + e.getMessage());
					LOGGER.error("Error", e);
					return null;
				}
			}
		};
	}
}
