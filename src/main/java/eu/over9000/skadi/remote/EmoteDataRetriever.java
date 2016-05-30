/*
 * Copyright (c) 2014-2016 Jan Strauß <jan[at]over9000.eu>
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

import eu.over9000.cathode.Result;
import eu.over9000.cathode.data.ChannelEmoticon;
import eu.over9000.cathode.data.ChannelEmoticonList;
import eu.over9000.skadi.service.ImageRetrievalService;
import eu.over9000.skadi.ui.label.CopyableLabel;
import eu.over9000.skadi.util.TwitchUtil;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class EmoteDataRetriever {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmoteDataRetriever.class);

	private static List<ChannelEmoticon> retrieveEmotes(final String channel) {
		final List<ChannelEmoticon> result = new ArrayList<>();

		final Result<ChannelEmoticonList> emoteResponse = TwitchUtil.getTwitch().chat.getEmoticons(channel);

		if (!emoteResponse.isOk()) {
			LOGGER.error("error getting emote data for " + channel + ": ", emoteResponse.getErrorRaw());
			return result;
		}

		result.addAll(emoteResponse.getResultRaw().getEmoticons().stream().filter(ChannelEmoticon::isSubscriberOnly).collect(Collectors.toList()));

		return result;
	}

	public static List<HBox> buildEmotePanel(final String channel) {
		final List<ChannelEmoticon> emotes = retrieveEmotes(channel);
		final List<HBox> result = new ArrayList<>(emotes.size());
		final CountDownLatch latch = new CountDownLatch(emotes.size());

		emotes.forEach(emote -> {

			final ImageRetrievalService imageService = new ImageRetrievalService(emote.getUrl());
			imageService.setOnSucceeded(event -> {
				final ImageView img = (ImageView) event.getSource().getValue();
				final CopyableLabel lbl = new CopyableLabel(emote.getRegex());
				final HBox box = new HBox(2, lbl, img);
				box.setAlignment(Pos.CENTER_LEFT);
				box.setStyle("-fx-background-color: -fx-control-inner-background");
				result.add(box);
				latch.countDown();
			});
			imageService.setOnFailed(event -> {
				final CopyableLabel lbl = new CopyableLabel(emote.getRegex());
				final HBox box = new HBox(2, lbl);
				box.setStyle("-fx-background-color: -fx-control-inner-background");
				box.setAlignment(Pos.CENTER_LEFT);
				result.add(box);
				latch.countDown();
			});
			imageService.start();
		});

		try {
			latch.await();
		} catch (final InterruptedException e) {
			LOGGER.error("error in emote list retrieval: ", e);
		}

		return result;

	}

}
