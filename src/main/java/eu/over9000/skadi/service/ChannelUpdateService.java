package eu.over9000.skadi.service;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.remote.ChannelDataRetriever;
import eu.over9000.skadi.remote.data.ChannelMetadata;

public class ChannelUpdateService extends ScheduledService<ChannelMetadata> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChannelUpdateService.class);
	private final Channel toUpdate;
	
	public ChannelUpdateService(final Channel toUpdate) {
		this.toUpdate = toUpdate;
		this.setPeriod(Duration.seconds(60));
		this.setRestartOnFailure(true);
		this.setOnSucceeded(event -> {
			
			final ChannelMetadata updated = (ChannelMetadata) event.getSource().getValue();
			if (updated != null) {
				
				synchronized (toUpdate) {
					toUpdate.updateFrom(updated);
				}
			}
		});
		this.setOnFailed(event -> ChannelUpdateService.LOGGER.error(
		        "scheduled channel updater failed for " + toUpdate.getName(), event.getSource().getException()));
	}
	
	@Override
	protected Task<ChannelMetadata> createTask() {
		return new Task<ChannelMetadata>() {
			
			@Override
			protected ChannelMetadata call() throws Exception {
				return ChannelDataRetriever.getChannelMetadata(ChannelUpdateService.this.toUpdate);
			}
		};
	}
}
