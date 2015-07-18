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

package eu.over9000.skadi.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Button;

import org.controlsfx.control.StatusBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.over9000.skadi.handler.ChannelHandler;
import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.remote.ChannelDataRetriever;
import eu.over9000.skadi.remote.data.ChannelMetadata;
import eu.over9000.skadi.util.TimeUtil;

public class ForcedChannelUpdateService extends Service<Void> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ForcedChannelUpdateService.class);

	private static final ExecutorService executorService = Executors.newCachedThreadPool();

	private final ChannelHandler channelHandler;

	public ForcedChannelUpdateService(final ChannelHandler channelHandler, final StatusBar statusBar, final Button refresh) {
		this.channelHandler = channelHandler;

		statusBar.progressProperty().bind(this.progressProperty());
		statusBar.textProperty().bind(this.messageProperty());
		this.setOnSucceeded(event -> {
			statusBar.progressProperty().unbind();
			statusBar.textProperty().unbind();

			statusBar.setProgress(0);
			refresh.setDisable(false);
		});
		this.setOnFailed(event -> LOGGER.error("forced channel updater failed ", event.getSource().getException()));
	}

	public static void onShutdown() {
		try {
			executorService.shutdown();
			executorService.awaitTermination(5, TimeUnit.SECONDS);
		} catch (final InterruptedException e) {
			LOGGER.error("exception during shutdown", e);
		}
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {

			private final AtomicInteger counter = new AtomicInteger();

			@Override
			protected Void call() throws Exception {
				this.updateMessage("preparing channel refresh..");

				final long start = System.currentTimeMillis();
				final List<Channel> channels = new ArrayList<>(ForcedChannelUpdateService.this.channelHandler.getChannels());

				final Set<Callable<Void>> tasks = new HashSet<>();
				for (int i = 0; i < channels.size(); i++) {

					final Channel toUpdate = channels.get(i);
					tasks.add(() -> {
						final ChannelMetadata updated = ChannelDataRetriever.getChannelMetadata(toUpdate);
						if (updated != null) {

							Platform.runLater(() -> {
								synchronized (toUpdate) {

									toUpdate.updateFrom(updated);
								}
							});
						}
						final int finished = this.counter.incrementAndGet();
						this.updateMessage("Refreshed channel " + (finished + 1) + " of " + channels.size());
						this.updateProgress(finished, channels.size());
						return null;
					});

				}

				executorService.invokeAll(tasks);

				final long duration = System.currentTimeMillis() - start;
				this.updateMessage("Refreshed " + channels.size() + " channels in " + TimeUtil.getDurationBreakdown(duration, true));
				return null;
			}
		};

	}
}
