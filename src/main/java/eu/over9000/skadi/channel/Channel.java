package eu.over9000.skadi.channel;

import eu.over9000.skadi.handler.ChatHandler;
import eu.over9000.skadi.handler.StreamHandler;
import eu.over9000.skadi.stream.StreamDataset;
import eu.over9000.skadi.stream.StreamRetriever;

public class Channel {
	private final String url;
	
	private StreamHandler streamHandler;
	private ChatHandler chatHandler;
	
	private ChannelMetadata metadata;
	
	public Channel(final String url) {
		this.url = url;
	}
	
	public void openStreamAndChat() {
		this.openStream();
		this.openChat();
	}
	
	public void openStream() {
		if (this.streamHandler == null) {
			
			final StreamDataset streamDataset = StreamRetriever.getStreams(this);
			if (streamDataset != null) {
				this.streamHandler = StreamHandler.createHandler(this, streamDataset);
			}
		}
	}
	
	public void openChat() {
		if (this.chatHandler == null) {
			this.chatHandler = ChatHandler.createHandler(this, this.url);
		}
	}
	
	public void closeStream() {
		if (this.streamHandler != null) {
			this.streamHandler.closeStream();
		}
	}
	
	public void closeChat() {
		if (this.chatHandler != null) {
			this.chatHandler.closeChat();
		}
	}
	
	public void streamClosedCallback() {
		this.streamHandler = null;
		System.out.println(this.url + " --> STREAM CLOSED");
	}
	
	public void chatClosedCallback() {
		this.chatHandler = null;
		System.out.println(this.url + " --> CHAT CLOSED");
	}
	
	public void closeStreamAndChat() {
		this.closeStream();
		this.closeChat();
	}
	
	public String getURL() {
		return this.url;
	}
	
	public ChannelMetadata getMetadata() {
		return this.metadata;
	}
	
	public void setMetadata(final ChannelMetadata metadata) {
		this.metadata = metadata;
	}
	
}
