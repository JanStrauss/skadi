package eu.over9000.skadi.updater;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import eu.over9000.skadi.channel.Channel;
import eu.over9000.skadi.channel.ChannelMetadata;
import eu.over9000.skadi.gui.SkadiGUI;
import eu.over9000.skadi.util.ChannelDataRetriever;

public class ChannelUpdater {
	private static final int THREAD_COUNT = 5;
	private static final int UPDATE_PERIOD = 1;
	
	private final ScheduledExecutorService executorService = Executors
	        .newScheduledThreadPool(ChannelUpdater.THREAD_COUNT);
	private final Map<Channel, ScheduledFuture<?>> tasks = new HashMap<>();
	
	public void scheduleChannel(final Channel channel) {
		final Runnable updateJob = this.createUpdateTask(channel);
		final ScheduledFuture<?> future = this.executorService.scheduleAtFixedRate(updateJob, 0,
		        ChannelUpdater.UPDATE_PERIOD, TimeUnit.MINUTES);
		this.tasks.put(channel, future);
	}
	
	private Runnable createUpdateTask(final Channel channel) {
		return new Runnable() {
			
			@Override
			public void run() {
				ChannelUpdater.updateChannel(channel);
			}
		};
	}
	
	public void cancelChannel(final Channel channel) {
		final ScheduledFuture<?> task = this.tasks.remove(channel);
		if (task != null) {
			task.cancel(false);
		}
	}
	
	public void stopUpdater() {
		this.executorService.shutdown();
	}
	
	private static void updateChannel(final Channel channel) {
		// SkadiLogging.log("UPDATE: " + channel.getURL());
		final ChannelMetadata newMetadata = ChannelDataRetriever.getChannelMetadata(channel.getURL());
		
		channel.setMetadata(newMetadata);
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				SkadiGUI.handleChannelTableUpdate(channel);
			}
		});
		
	}
	
}
