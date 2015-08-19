/*
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
 */

package eu.over9000.skadi.remote;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jcabi.manifests.Manifests;

import eu.over9000.skadi.service.helper.RemoteVersionResult;

public class VersionRetriever {
	private static final HttpClient httpClient = HttpClients.createMinimal();

	private static final JsonParser parser = new JsonParser();

	private static final String API_URL = "https://api.github.com/repos/s1mpl3x/skadi/releases";
	private static final Logger LOGGER = LoggerFactory.getLogger(VersionRetriever.class);

	private static final String SKADI_BUILD = "Skadi-Build";
	private static final String SKADI_VERSION = "Skadi-Version";
	private static final String SKADI_TIMESTAMP = "Skadi-Timestamp";

	public static RemoteVersionResult getLatestVersion() {
		try {
			final URI URL = new URI(API_URL);
			final HttpResponse response = httpClient.execute(new HttpGet(URL));

			final String responseString = new BasicResponseHandler().handleResponse(response);

			final JsonArray tagsArray = parser.parse(responseString).getAsJsonArray();
			final JsonObject latest = tagsArray.get(0).getAsJsonObject();
			final JsonObject latestFiles = latest.getAsJsonArray("assets").get(0).getAsJsonObject();

			final String downloadURL = latestFiles.get("browser_download_url").getAsString();
			final int downloadSize = latestFiles.get("size").getAsInt();
			final String version = latest.get("tag_name").getAsString();
			final String published = latest.get("published_at").getAsString();
			final String changeLog = latest.get("body").getAsString();

			return new RemoteVersionResult(version, published, downloadURL, changeLog, downloadSize);

		} catch (final Exception e) {
			LOGGER.error("VersionRetriever exception", e);
			return null;
		}
	}

	public static boolean isLocalInfoAvailable() {
		return Manifests.exists(SKADI_VERSION) && Manifests.exists(SKADI_BUILD) && Manifests.exists(SKADI_TIMESTAMP);
	}

	public static String getLocalVersion() {
		return Manifests.read(SKADI_VERSION);
	}

	public static String getLocalBuild() {
		return Manifests.read(SKADI_BUILD);
	}

	public static String getLocalTimestamp() {
		return Manifests.read(SKADI_TIMESTAMP);
	}
}
