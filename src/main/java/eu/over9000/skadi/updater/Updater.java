package eu.over9000.skadi.updater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import eu.over9000.skadi.SkadiMain;
import eu.over9000.skadi.channel.ChannelInstance;
import eu.over9000.skadi.channel.ChannelMetadata;
import eu.over9000.skadi.gui.SkadiGUI;
import eu.over9000.skadi.util.ChannelDataRetriever;

public class Updater {
	private final Timer timer = new Timer("Channel Updater");
	private final TimerTask task = new TimerTask() {
		
		@Override
		public void run() {
			final Collection<ChannelInstance> set = new ArrayList<>(SkadiMain.getInstance().getChannels().values());
			for (final ChannelInstance channel : set) {
				Updater.updateChannel(channel);
			}
			
		}
	};
	
	public void startUpdater() {
		this.timer.schedule(this.task, 0, 1000 * 60);
	}
	
	public void stopUpdater() {
		this.timer.cancel();
	}
	
	public void scheduleInstantTask(final ChannelInstance channel) {
		this.timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				Updater.updateChannel(channel);
				
			}
		}, 0);
	}
	
	private static void updateChannel(final ChannelInstance channel) {
		final ChannelMetadata newMetadata = ChannelDataRetriever.getChannelMetadata(channel.getURL());
		
		channel.setMetadata(newMetadata);
		
		SkadiGUI.handleChannelTableUpdate();
	}
	
}
