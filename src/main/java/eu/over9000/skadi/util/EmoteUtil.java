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

package eu.over9000.skadi.util;


import eu.over9000.skadi.remote.EmoteDataRetriever;
import eu.over9000.skadi.service.ImageRetrievalService;
import eu.over9000.skadi.ui.label.CopyableLabel;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class EmoteUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmoteUtil.class);

	public static List<HBox> buildEmotePanel(final List<EmoteDataRetriever.Emoticon> emotes) {

		final List<HBox> result = new ArrayList<>(emotes.size());
		final CountDownLatch latch = new CountDownLatch(emotes.size());

		emotes.forEach(emote -> {
			final ImageRetrievalService imageService = new ImageRetrievalService(emote.url);
			imageService.setOnSucceeded(event -> {
				final ImageView img = (ImageView) event.getSource().getValue();
				final CopyableLabel lbl = new CopyableLabel(emote.regex);
				result.add(new HBox(2, lbl, img));
				latch.countDown();
			});
			imageService.setOnFailed(event -> {
				final CopyableLabel lbl = new CopyableLabel(emote.regex);
				result.add(new HBox(2, lbl));
				latch.countDown();
			});
			imageService.start();
		});

		try {
			latch.await();
		} catch (InterruptedException e) {
			LOGGER.error("error in emote list retrieval: ", e);
		}

		return result;

	}
}
