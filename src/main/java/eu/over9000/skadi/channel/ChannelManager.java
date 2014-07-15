package eu.over9000.skadi.channel;

import java.util.ArrayList;
import java.util.List;

import eu.over9000.skadi.logging.SkadiLogging;
import eu.over9000.skadi.util.ChannelDataRetriever;
import eu.over9000.skadi.util.ChannelURLUtil;

public class ChannelManager {
	private static ChannelManager instance;
	
	private final List<Channel> channels = new ArrayList<>();
	private final List<ChannelEventListener> listeners = new ArrayList<>();
	
	public static ChannelManager getInstance() {
		if (ChannelManager.instance == null) {
			ChannelManager.instance = new ChannelManager();
		}
		return ChannelManager.instance;
	}
	
	private ChannelManager() {
	}
	
	public void addListener(final ChannelEventListener listener) {
		SkadiLogging.log("added channel listener: " + listener.getListenerName());
		this.listeners.add(listener);
	}
	
	public boolean addChannel(final String url, final boolean userAdd) {
		final String fixedUrl = ChannelURLUtil.fixURL(url);
		
		if (!ChannelURLUtil.validateURL(fixedUrl)) {
			SkadiLogging.log("Channel " + fixedUrl + " is no valid twitch channel");
			return false;
		}
		
		if (this.containsChannel(fixedUrl)) {
			SkadiLogging.log("Channel " + fixedUrl + " already added");
			return false;
		}
		
		if (userAdd) {
			if (!ChannelDataRetriever.checkIfChannelExists(fixedUrl)) {
				SkadiLogging.log("Channel " + fixedUrl + " does not exist");
				return false;
			}
		}
		
		final Channel newChannel = new Channel(fixedUrl);
		
		this.channels.add(newChannel);
		
		for (final ChannelEventListener listener : this.listeners) {
			listener.added(newChannel);
		}
		
		if (userAdd) {
			SkadiLogging.log("added new channel " + fixedUrl);
		}
		
		return true;
	}
	
	public void removeChannel(final Channel channel) {
		this.channels.remove(channel);
		for (final ChannelEventListener listener : this.listeners) {
			listener.removed(channel);
		}
		SkadiLogging.log("removed channel " + channel.getURL());
	}
	
	public List<Channel> getChannels() {
		return this.channels;
	}
	
	public void fireMetadataUpdated(final Channel channel) {
		for (final ChannelEventListener listener : this.listeners) {
			listener.updatedMetadata(channel);
		}
	}
	
	public void fireStreamdataUpdated(final Channel channel) {
		for (final ChannelEventListener listener : this.listeners) {
			listener.updatedStreamdata(channel);
		}
	}
	
	public boolean containsChannel(final String url) {
		for (final Channel channel : this.channels) {
			if (url.equals(channel.getURL())) {
				return true;
			}
		}
		return false;
	}
}
