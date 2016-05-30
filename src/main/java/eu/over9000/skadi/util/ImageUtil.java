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

package eu.over9000.skadi.util;

import eu.over9000.cathode.Result;
import eu.over9000.cathode.data.parameters.ImageSize;
import eu.over9000.skadi.model.Channel;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class ImageUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImageUtil.class);

	private static final Function<InputStream, Image> CONVERTER = Image::new;

	public static Image getImageInternal(final String url) {

		final Result<Image> imageResponse = TwitchUtil.getTwitch().imageUtil.getOther(url, CONVERTER);

		if (!imageResponse.isOk()) {
			LOGGER.error("failed to load image" + url, imageResponse.getErrorRaw());
			return null;
		}

		return imageResponse.getResultRaw();
	}

	public static ImageView getGameBoxFromTwitch(final String game) {

		final Result<Image> imageResponse = TwitchUtil.getTwitch().imageUtil.getGameBox(game, ImageSize.SMALL_GAME_BOX, CONVERTER);

		if (!imageResponse.isOk()) {
			LOGGER.error("exception getting game logo for " + game, imageResponse.getErrorRaw());
			return null;
		}

		return new ImageView(imageResponse.getResultRaw());
	}

	public static Image getPreviewFromTwitch(final Channel channel) {

		final Result<Image> imageResponse = TwitchUtil.getTwitch().imageUtil.getStreamPreview(channel.getName(), ImageSize.LARGE_PREVIEW, CONVERTER);

		if (!imageResponse.isOk()) {
			LOGGER.error("exception getting channel preview for " + channel, imageResponse.getErrorRaw());
			return null;
		}

		return imageResponse.getResultRaw();
	}

	public static ImageView getChannelLogo(final String logoURL) {
		final ImageView iv = new ImageView(getImageInternal(logoURL));
		iv.setFitHeight(72);
		iv.setFitWidth(72);
		iv.setSmooth(true);
		iv.setCache(true);
		return iv;
	}

	public static void updatePreviewAsyncFromTwitch(final Channel channel) {
		ExecutorUtil.getExecutorService().submit(new AsyncImageUpdateTask(channel));
	}

	public static class AsyncImageUpdateTask implements Callable<Void> {
		private final Channel channel;


		public AsyncImageUpdateTask(final Channel channel) {
			this.channel = channel;
		}

		@Override
		public Void call() throws Exception {
			channel.setPreview(getPreviewFromTwitch(channel));
			return null;
		}
	}
}