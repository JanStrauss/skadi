package eu.over9000.skadi.stream;

public class StreamQuality {
	private final String videoUrl;
	private final String videoQuality;
	private final int videoBandwidth;
	
	public StreamQuality(final String url, final String quality, final int bandwidth) {
		this.videoUrl = url;
		this.videoQuality = quality;
		this.videoBandwidth = bandwidth;
	}
	
	public String getUrl() {
		return this.videoUrl;
	}
	
	public String getQuality() {
		return this.videoQuality;
	}
	
	public int getBandwidth() {
		return this.videoBandwidth;
	}
	
}