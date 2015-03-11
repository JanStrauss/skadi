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

import eu.over9000.skadi.handler.ChannelHandler;
import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.remote.ChannelDataRetriever;
import eu.over9000.skadi.util.TimeUtil;

public class ForcedChannelUpdateService extends Service<Void> {
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
	}

	public static void onShutdown() {
		try {
			ForcedChannelUpdateService.executorService.shutdown();
			ForcedChannelUpdateService.executorService.awaitTermination(5, TimeUnit.SECONDS);
		} catch (final InterruptedException e) {
			e.printStackTrace();
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
					tasks.add(new Callable<Void>() {
						
						@Override
						public Void call() throws Exception {
							final Channel updated = ChannelDataRetriever.getChannelMetadata(toUpdate.getName());
							if (updated != null) {
								
								Platform.runLater(() -> {
									synchronized (toUpdate) {
										
										toUpdate.updateFrom(updated);
									}
								});
							}
							final int finished = counter.incrementAndGet();
							updateMessage("Refreshed channel " + (finished + 1) + " of " + channels.size());
							updateProgress(finished, channels.size());
							return null;
						}
					});

				}

				ForcedChannelUpdateService.executorService.invokeAll(tasks);

				final long duration = System.currentTimeMillis() - start;
				this.updateMessage("Refreshed " + channels.size() + " channels in " + TimeUtil.getDurationBreakdown(duration, true));
				return null;
			}
		};

	}
}