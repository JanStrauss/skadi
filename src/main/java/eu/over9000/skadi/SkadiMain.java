package eu.over9000.skadi;

import java.util.Map;
import java.util.regex.Pattern;

import javafx.application.Application;
import eu.over9000.skadi.channel.ChannelInstance;
import eu.over9000.skadi.gui.SkadiGUI;
import eu.over9000.skadi.io.PersistenceManager;

public class SkadiMain {
	
	private static SkadiMain instance;
	
	private final Map<String, ChannelInstance> channels = PersistenceManager.getInstance().loadChannels();
	
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
		Application.launch(SkadiGUI.class, args);
	}
	
	private void addShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("BEGIN HOOK");
				for (final ChannelInstance instance : SkadiMain.this.channels.values()) {
					instance.closeStreamAndChat();
				}
				System.out.println("END HOOK");
				
			}
		}));
	}
	
	public void addAndOpenNewChannel(String url) {
		if (!SkadiMain.validateURL(url)) {
			return;
		}
		if (url.startsWith("twitch.tv/")) {
			url = "http://www." + url;
		}
		if (url.startsWith("www.twitch.tv/")) {
			url = "http://" + url;
		}
		
		if (!url.endsWith("/")) {
			url = url + "/";
		}
		
		final ChannelInstance newChannel = new ChannelInstance(url, "best");
		
		this.channels.put(url, newChannel);
		
		newChannel.openStreamAndChat();
		
		System.out.println("ADDED AND OPENED STREAM AND CHAT FOR URL " + url);
		
	}
	
	private static boolean validateURL(final String url) {
		return Pattern.matches("(http://)?(www\\.)?(twitch\\.tv/)[0-9a-zA-Z]+/", url);
	}
}
