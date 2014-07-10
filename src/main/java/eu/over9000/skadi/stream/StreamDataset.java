package eu.over9000.skadi.stream;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.over9000.skadi.channel.Channel;
import eu.over9000.skadi.logging.SkadiLogging;

public class StreamDataset {
	private final Channel channel;
	private final List<StreamQuality> qualitiesList;
	private final Map<String, StreamQuality> qualitiesMap;
	
	public StreamDataset(final Channel channel, final List<StreamQuality> qualities) {
		this.channel = channel;
		this.qualitiesList = qualities;
		this.qualitiesMap = new HashMap<>();
		
		for (final StreamQuality streamQuality : qualities) {
			this.qualitiesMap.put(streamQuality.getQuality(), streamQuality);
		}
	}
	
	public Channel getChannel() {
		return this.channel;
	}
	
	public List<StreamQuality> getQualities() {
		return this.qualitiesList;
	}
	
	public StreamQuality getHighestQuality() {
		return this.qualitiesList.get(0);
	}
	
	public StreamQuality getLowestQuality() {
		return this.qualitiesList.get(this.qualitiesList.size() - 1);
	}
	
	public StreamQuality getQuality(final String quality) {
		final StreamQuality result = this.qualitiesMap.get(quality);
		
		if (result == null) {
			
			switch (quality) {
				case "best":
					return this.getHighestQuality();
				case "worst":
					return this.getLowestQuality();
				default:
					SkadiLogging.log("found no match for quality " + quality + ", will use highest quality");
					return this.getHighestQuality();
			}
		}
		
		return result;
	}
	
}
