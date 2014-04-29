package eu.over9000.skadi;

import java.util.TreeMap;
import java.util.regex.Pattern;

import eu.over9000.skadi.channel.ChannelInstance;
import eu.over9000.skadi.gui.SkadiGUI;
import eu.over9000.skadi.io.PersistenceManager;

public class SkadiMain {
	
	private static SkadiMain instance;
	
	private TreeMap<String, ChannelInstance> channels = new TreeMap<>();
	
	public String livestreamer_exec = "livestreamer";
	public String chrome_exec = "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe";
	
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
		PersistenceManager.getInstance().loadData();
		SkadiGUI.create();
	}
	
	private void addShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("KILLING STREAMS/CHATS..");
				for (final ChannelInstance instance : SkadiMain.this.channels.values()) {
					instance.closeStreamAndChat();
				}
				System.out.println("SAVING DATA..");
				PersistenceManager.getInstance().saveData();
				System.out.println("SHUTDOWN COMPLETE");
				
			}
		}));
	}
	
	public void addNewChannel(String url) {
		if (!url.endsWith("/")) {
			url = url + "/";
		}
		
		if (!SkadiMain.validateURL(url)) {
			System.out.println("invalid url given");
			return;
		}
		if (url.startsWith("twitch.tv/")) {
			url = "http://www." + url;
		}
		if (url.startsWith("www.twitch.tv/")) {
			url = "http://" + url;
		}
		
		if (this.channels.containsKey(url)) {
			System.out.println("Channel already in list");
			return;
		}
		
		final ChannelInstance newChannel = new ChannelInstance(url, "best");
		
		this.channels.put(url, newChannel);
		
		System.out.println("ADDED AND OPENED STREAM AND CHAT FOR URL " + url);
		
		SkadiGUI.handleChannelListUpdate();
		
	}
	
	private static boolean validateURL(final String url) {
		return Pattern.matches("(http://)?(www\\.)?(twitch\\.tv/)[A-Za-z0-9_]+/", url);
		
	}
	
	public TreeMap<String, ChannelInstance> getChannels() {
		return this.channels;
	}
	
	public void setChannels(final TreeMap<String, ChannelInstance> newChannels) {
		this.channels = newChannels;
	}
	
	public void deleteChannel(final ChannelInstance channel) {
		this.channels.remove(channel.getURL());
		channel.closeStreamAndChat();
		SkadiGUI.handleChannelListUpdate();
	}
}
