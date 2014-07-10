package eu.over9000.skadi.stream;

public class StreamQuality {
	private final boolean isDummy;
	private final String url;
	private final String quality;
	private final int bandwidth;
	
	public StreamQuality(final String url, final String quality, final int bandwidth) {
		this.url = url;
		this.quality = quality;
		this.bandwidth = bandwidth;
		this.isDummy = false;
	}
	
	private StreamQuality(final String quality) {
		this.url = null;
		this.quality = quality;
		this.bandwidth = 0;
		this.isDummy = true;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public String getQuality() {
		return this.quality;
	}
	
	public int getBandwidth() {
		return this.bandwidth;
	}
	
	public boolean isDummy() {
		return this.isDummy;
	}
	
	public static StreamQuality[] getDefaultQualities() {
		return new StreamQuality[] { new StreamQuality("best"), new StreamQuality("worst") };
	}
}