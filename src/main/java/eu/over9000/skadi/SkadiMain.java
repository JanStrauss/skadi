package eu.over9000.skadi;

import java.util.Iterator;
import java.util.Set;

import eu.over9000.skadi.channel.Channel;
import eu.over9000.skadi.channel.ChannelManager;
import eu.over9000.skadi.channel.ChannelUpdater;
import eu.over9000.skadi.gui.ImportDialog;
import eu.over9000.skadi.gui.SkadiGUI;
import eu.over9000.skadi.io.PersistenceManager;
import eu.over9000.skadi.logging.SkadiLogging;
import eu.over9000.skadi.util.ChannelDataRetriever;
import eu.over9000.skadi.util.SkadiVersionChecker;

public class SkadiMain {
	
	public static final String CLIENT_ID = "i2uu9j43ure9x7n4ojpgg4hvcnw6y91";
	
	private static SkadiMain instance;
	
	public String chrome_exec = "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe";
	public String livestreamer_exec = "C:\\Program Files (x86)\\Livestreamer\\livestreamer.exe";
	public String vlc_exec = "C:\\Program Files (x86)\\VideoLAN\\VLC\\vlc.exe";
	
	public boolean use_livestreamer = true;
	
	public static SkadiMain getInstance() {
		if (SkadiMain.instance == null) {
			SkadiMain.instance = new SkadiMain();
		}
		return SkadiMain.instance;
	}
	
	public static void main(final String[] args) {
		SkadiMain.getInstance().runInit(args);
	}
	
	private void runInit(final String[] args) {
		this.addShutdownHook();
		
		ChannelManager.getInstance();
		ChannelUpdater.getInstance();
		
		PersistenceManager.getInstance().loadData();
		
		SkadiGUI.createInstance();
		
		SkadiVersionChecker.checkVersion();
		
	}
	
	private void addShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			
			@Override
			public void run() {
				SkadiLogging.log("STOPPING UPDATER");
				ChannelUpdater.getInstance().stopUpdater();
				SkadiLogging.log("KILLING STREAMS/CHATS..");
				for (final Channel channel : ChannelManager.getInstance().getChannels()) {
					channel.closeStreamAndChat();
				}
				SkadiLogging.log("SAVING DATA..");
				PersistenceManager.getInstance().saveData();
				SkadiLogging.log("SHUTDOWN COMPLETE");
				
			}
		}));
	}
	
	public String importFollowedChannelsFromTwitch(final String username, final ImportDialog importDialog) {
		importDialog.setIndeterminate(true);
		final Set<String> newChannels = ChannelDataRetriever.getFollowedChannels(username, importDialog);
		
		int count = 0;
		
		final Iterator<String> iterator = newChannels.iterator();
		
		for (int index = 0; index < newChannels.size(); index++) {
			
			final String url = iterator.next();
			final boolean result = ChannelManager.getInstance().addChannel(url, false);
			importDialog.updateProgress(newChannels.size(), newChannels.size() + index, "Importing " + index + " of "
			        + newChannels.size() + " channels");
			if (result) {
				count++;
			}
		}
		return "Imported " + count + " of " + newChannels.size() + " followed channels.";
	}
	
}
