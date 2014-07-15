package eu.over9000.skadi.channel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import eu.over9000.skadi.util.ChannelDataRetriever;

public class ChannelUpdater implements ChannelEventListener {
	
	private static ChannelUpdater instance;
	
	public static ChannelUpdater getInstance() {
		if (ChannelUpdater.instance == null) {
			ChannelUpdater.instance = new ChannelUpdater();
		}
		return ChannelUpdater.instance;
	}
	
	private ChannelUpdater() {
		ChannelManager.getInstance().addListener(this);
	}
	
	private static final int THREAD_COUNT = 5;
	private static final int UPDATE_PERIOD = 1;
	
	private final ScheduledExecutorService executorService = Executors
	        .newScheduledThreadPool(ChannelUpdater.THREAD_COUNT);
	private final Map<Channel, ScheduledFuture<?>> tasks = new HashMap<>();
	
	private Runnable createUpdateTask(final Channel channel) {
		return new Runnable() {
			
			@Override
			public void run() {
				final ChannelMetadata newMetadata = ChannelDataRetriever.getChannelMetadata(channel.getURL());
				channel.updateMetadata(newMetadata);
			}
		};
	}
	
	public void stopUpdater() {
		this.executorService.shutdown();
	}
	
	@Override
	public void added(final Channel channel) {
		final Runnable updateJob = this.createUpdateTask(channel);
		final ScheduledFuture<?> future = this.executorService.scheduleAtFixedRate(updateJob, 0,
		        ChannelUpdater.UPDATE_PERIOD, TimeUnit.MINUTES);
		this.tasks.put(channel, future);
	}
	
	@Override
	public void removed(final Channel channel) {
		final ScheduledFuture<?> task = this.tasks.remove(channel);
		if (task != null) {
			task.cancel(false);
		}
	}
	
	@Override
	public void updatedMetadata(final Channel channel) {
	}
	
	@Override
	public void updatedStreamdata(final Channel channel) {
	}
	
	@Override
	public String getListenerName() {
		return this.getClass().getName();
	}
	
}
