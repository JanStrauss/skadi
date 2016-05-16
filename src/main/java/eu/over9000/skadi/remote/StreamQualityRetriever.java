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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.model.StateContainer;
import eu.over9000.skadi.model.StreamQuality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class provides static methods that retrieve available stream qualities from livestreamer.
 */
public class StreamQualityRetriever {

	private static final Logger LOGGER = LoggerFactory.getLogger(StreamQualityRetriever.class);

	private static final JsonParser parser = new JsonParser();

	public static List<StreamQuality> getQualitiesFromLivestreamer(final Channel channel, final StateContainer state) {
		final String livestreamerExec = state.getExecutableLivestreamer();

		try {
			final Process process = new ProcessBuilder(livestreamerExec, "-j", channel.buildURL()).redirectErrorStream(true).start();

			final JsonObject jsonQualList = parser.parse(new JsonReader(new BufferedReader(new InputStreamReader(process.getInputStream())))).getAsJsonObject();

			process.waitFor();

			final JsonObject jsonQualities = jsonQualList.get("streams").getAsJsonObject();

			final List<StreamQuality> qualities = new ArrayList<>();
			jsonQualities.entrySet().forEach(entry -> qualities.add(new StreamQuality(entry.getKey())));
			return qualities.stream().filter(sq -> !"best".equals(sq.getQuality())).filter(sq -> !"worst".equals(sq.getQuality())).collect(Collectors.toList());

		} catch (final IOException | InterruptedException e) {
			LOGGER.error("failed to retrieve stream qualities for " + channel.getName() +
					"," +
					" reason: " + e.getMessage());
		}

		return Arrays.asList(StreamQuality.getBestQuality(), StreamQuality.getWorstQuality());
	}
}
