package eu.over9000.skadi.channel;

import java.util.List;
import java.util.Objects;

import eu.over9000.skadi.handler.ChatHandler;
import eu.over9000.skadi.handler.StreamHandler;
import eu.over9000.skadi.logging.SkadiLogging;
import eu.over9000.skadi.stream.StreamDataset;
import eu.over9000.skadi.stream.StreamQuality;
import eu.over9000.skadi.stream.StreamRetriever;

public class Channel {
	private final String url;
	
	private StreamHandler streamHandler;
	private ChatHandler chatHandler;
	
	private ChannelMetadata metadata;
	
	private StreamDataset streamDataset;
	
	public Channel(final String url) {
		this.url = url;
	}
	
	public void openStreamAndChat(final String quality) {
		this.openStream(quality);
		this.openChat();
	}
	
	public void openStream(final String quality) {
		if (this.streamHandler == null) {
			this.streamHandler = StreamHandler.createHandler(this, quality);
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
		SkadiLogging.log(this.url + " --> STREAM CLOSED");
	}
	
	public void chatClosedCallback() {
		this.chatHandler = null;
		SkadiLogging.log(this.url + " --> CHAT CLOSED");
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
	
	public StreamDataset getStreamDataset() {
		return this.streamDataset;
	}
	
	public void setStreamDataset(final StreamDataset streamDataset) {
		this.streamDataset = streamDataset;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Channel)) {
			return false;
		}
		final Channel other = (Channel) obj;
		return this.url.equals(other.url);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.url);
	}
	
	public String[] getQualityArray() {
		if (this.streamDataset == null) {
			if (this.metadata != null) {
				if (this.metadata.isOnline()) {
					StreamRetriever.getStreamsDelayed(this);
				}
			}
			return new String[] { "best", "worst" };
		} else {
			final List<StreamQuality> streamQualities = this.streamDataset.getQualities();
			final String[] qualityNames = new String[streamQualities.size()];
			for (int index = 0; index < qualityNames.length; index++) {
				qualityNames[index] = streamQualities.get(index).getQuality();
			}
			return qualityNames;
		}
	}
	
}
