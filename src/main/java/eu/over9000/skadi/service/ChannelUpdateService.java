package eu.over9000.skadi.service;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;
import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.remote.ChannelDataRetriever;

public class ChannelUpdateService extends ScheduledService<Channel> {

	private final Channel toUpdate;
	
	public ChannelUpdateService(final Channel toUpdate) {
		this.toUpdate = toUpdate;
		this.setPeriod(Duration.seconds(60));
		this.setRestartOnFailure(true);
		this.setOnSucceeded(event -> {
			
			final Channel updated = (Channel) event.getSource().getValue();
			if (updated != null) {
				
				synchronized (toUpdate) {
					toUpdate.updateFrom(updated);
				}
			}
		});
	}
	
	@Override
	protected Task<Channel> createTask() {
		return new Task<Channel>() {
			
			@Override
			protected Channel call() throws Exception {
				return ChannelDataRetriever.getChannelMetadata(ChannelUpdateService.this.toUpdate.getName());
			}
		};
	}
}
