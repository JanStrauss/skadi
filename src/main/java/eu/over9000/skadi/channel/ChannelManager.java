/*******************************************************************************
 * Copyright (c) 2014 Jan Strauß
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package eu.over9000.skadi.channel;

import java.util.ArrayList;
import java.util.List;

import eu.over9000.skadi.logging.SkadiLogging;
import eu.over9000.skadi.util.ChannelDataRetriever;
import eu.over9000.skadi.util.ChannelURLUtil;

/**
 * Singleton class storing the channels and listeners on channel events.
 * 
 * @author Jan Strauß
 * 
 */
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
		
		synchronized (this.listeners) {
			for (final ChannelEventListener listener : this.listeners) {
				listener.added(newChannel, this.channels.size());
			}
		}
		
		if (userAdd) {
			SkadiLogging.log("added new channel " + fixedUrl);
		}
		
		return true;
	}
	
	public void removeChannel(final Channel channel) {
		this.channels.remove(channel);
		synchronized (this.listeners) {
			for (final ChannelEventListener listener : this.listeners) {
				listener.removed(channel, this.channels.size());
			}
		}
		SkadiLogging.log("removed channel " + channel.getURL());
	}
	
	public List<Channel> getChannels() {
		return this.channels;
	}
	
	public void fireMetadataUpdated(final Channel channel) {
		synchronized (this.listeners) {
			for (final ChannelEventListener listener : this.listeners) {
				listener.updatedMetadata(channel);
			}
		}
	}
	
	public void fireStreamdataUpdated(final Channel channel) {
		synchronized (this.listeners) {
			
			for (final ChannelEventListener listener : this.listeners) {
				listener.updatedStreamdata(channel);
			}
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
