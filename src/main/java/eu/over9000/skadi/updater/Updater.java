package eu.over9000.skadi.updater;

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
			for (final ChannelInstance channel : SkadiMain.getInstance().getChannels().values()) {
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
	
	private static void updateChannel(final ChannelInstance channel) {
		final ChannelMetadata newMetadata = ChannelDataRetriever.getChannelMetadata(channel.getURL());
		
		channel.setMetadata(newMetadata);
		
		SkadiGUI.handleChannelTableUpdate();
	}
	
}
