package eu.over9000.skadi.stream;

import java.util.List;

import eu.over9000.skadi.channel.Channel;

public class StreamDataset {
	private final Channel channel;
	private final List<StreamQuality> qualities;
	
	public StreamDataset(final Channel channel, final List<StreamQuality> qualities) {
		this.channel = channel;
		this.qualities = qualities;
	}
	
	public Channel getChannel() {
		return this.channel;
	}
	
	public List<StreamQuality> getQualities() {
		return this.qualities;
	}
	
	public StreamQuality getHighestQuality() {
		return this.qualities.get(0);
	}
	
}
