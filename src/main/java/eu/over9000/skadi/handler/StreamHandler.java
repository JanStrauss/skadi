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

package eu.over9000.skadi.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javafx.collections.ListChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.model.StateContainer;
import eu.over9000.skadi.model.StreamQuality;

/**
 * The handler for the chat process.
 */
public class StreamHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(StreamHandler.class);
	private final Map<Channel, StreamProcessHandler> handlers = new HashMap<>();

	public StreamHandler(final ChannelHandler channelHandler) {

		channelHandler.getChannels().addListener((final ListChangeListener.Change<? extends Channel> c) -> {
			while (c.next()) {
				if (c.wasRemoved()) {
					c.getRemoved().stream().filter(this.handlers::containsKey).forEach(channel -> {
						final StreamProcessHandler sph = this.handlers.remove(channel);
						sph.closeStream();
					});
				}
			}
		});
	}

	public void openStream(final Channel channel, final StreamQuality quality) {
		if (this.handlers.containsKey(channel)) {
			return;
		}

		try {
			final StreamProcessHandler cph = new StreamProcessHandler(channel, quality);
			this.handlers.put(channel, cph);
		} catch (final IOException e) {
			LOGGER.error("exception opening stream", e);
		}
	}

	private class StreamProcessHandler implements Runnable {
		private final Process process;
		private final Channel channel;
		private final Thread thread;

		private StreamProcessHandler(final Channel channel, final StreamQuality quality) throws IOException {
			this.thread = new Thread(this);
			this.channel = channel;
			this.thread.setName("StreamHandler Thread for " + channel.getName());

			String videoplayerExec = StateContainer.getInstance().getExecutableVideoplayer();
			String livestreamerExec = StateContainer.getInstance().getExecutableLivestreamer();

			this.process = new ProcessBuilder(livestreamerExec, channel.buildURL(), quality.getQuality(), "-p " + videoplayerExec).redirectErrorStream(true).start();
			this.thread.start();
		}

		@Override
		public void run() {
			try {
				final BufferedReader br = new BufferedReader(new InputStreamReader(this.process.getInputStream()));

				String line;
				while ((line = br.readLine()) != null) {
					LOGGER.debug("LIVESTREAMER/VIDEOPLAYER: " + line);
				}

				this.process.waitFor();
				br.close();
			} catch (final InterruptedException | IOException e) {
				LOGGER.error("Exception handling stream process", e);
			}

			StreamHandler.this.handlers.remove(this.channel);

		}

		public void closeStream() {
			this.process.destroy();
		}
	}

}
