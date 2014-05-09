package eu.over9000.skadi;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import eu.over9000.skadi.channel.Channel;
import eu.over9000.skadi.gui.SkadiGUI;
import eu.over9000.skadi.io.PersistenceManager;
import eu.over9000.skadi.updater.Updater;
import eu.over9000.skadi.util.ChannelDataRetriever;
import eu.over9000.skadi.util.SkadiVersionChecker;

public class SkadiMain {
	
	private static SkadiMain instance;
	
	private List<Channel> channels = new ArrayList<>();
	
	public String livestreamer_exec = "livestreamer";
	public String chrome_exec = "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe";
	
	private Updater updater;
	
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
		this.checkVersion();
		
		this.addShutdownHook();
		PersistenceManager.getInstance().loadData();
		SkadiGUI.create();
		this.updater = new Updater();
		
		for (final Channel channel : this.channels) {
			this.updater.scheduleChannel(channel);
		}
	}
	
	private void checkVersion() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				final String localVersion = this.getClass().getPackage().getImplementationVersion();
				System.out.println("starting Skadi " + localVersion);
				
				final String remoteVersion = SkadiVersionChecker.getLatestVersion();
				
				if (!remoteVersion.equals(localVersion)) {
					System.out.println("There is newer version (" + remoteVersion
					        + ") of Skadi available. Download from https://github.com/s1mpl3x/skadi/releases");
				} else {
					System.out.println("This is the latest version.");
				}
				
			}
		}).start();
	}
	
	private void addShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("STOPPING UPDATER");
				SkadiMain.this.updater.stopUpdater();
				System.out.println("KILLING STREAMS/CHATS..");
				for (final Channel instance : SkadiMain.this.channels) {
					instance.closeStreamAndChat();
				}
				System.out.println("SAVING DATA..");
				PersistenceManager.getInstance().saveData();
				System.out.println("SHUTDOWN COMPLETE");
				
			}
		}));
	}
	
	public boolean addNewChannel(String url) {
		if (!url.endsWith("/")) {
			url = url + "/";
		}
		
		if (!SkadiMain.validateURL(url)) {
			System.out.println("invalid url given");
			return false;
		}
		if (url.startsWith("twitch.tv/")) {
			url = "http://www." + url;
		}
		if (url.startsWith("www.twitch.tv/")) {
			url = "http://" + url;
		}
		
		if (this.checkIfStreamIsInList(url)) {
			System.out.println("Channel already in list");
			return false;
		}
		
		if (!ChannelDataRetriever.checkIfChannelExists(url)) {
			System.out.println("Channel does not exist");
			return false;
		}
		
		final Channel newChannel = new Channel(url, "best");
		
		this.channels.add(newChannel);
		
		System.out.println("ADDED NEW CHANNEL:" + url);
		
		SkadiGUI.handleChannelTableAdd(newChannel);
		
		this.updater.scheduleChannel(newChannel);
		
		return true;
		
	}
	
	private boolean checkIfStreamIsInList(final String url) {
		for (final Channel channel : this.channels) {
			if (url.equals(channel.getURL())) {
				return true;
			}
		}
		return false;
	}
	
	public int importFollowedChannelsFromTwitch(final String username) {
		final Set<String> newChannels = ChannelDataRetriever.getFollowedChannels(username);
		
		int count = 0;
		for (final String url : newChannels) {
			final boolean result = this.addNewChannel(url);
			if (result) {
				count++;
			}
		}
		return count;
	}
	
	private static boolean validateURL(final String url) {
		return Pattern.matches("(http://)?(www\\.)?(twitch\\.tv/)[A-Za-z0-9_]+/", url);
	}
	
	public List<Channel> getChannels() {
		return this.channels;
	}
	
	public void setChannels(final List<Channel> newChannels) {
		this.channels = newChannels;
	}
	
	public void deleteChannel(final Channel channel) {
		this.channels.remove(channel);
		this.updater.cancelChannel(channel);
		channel.closeStreamAndChat();
		SkadiGUI.handleChannelTableDelete(channel);
	}
}
