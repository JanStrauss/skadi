package eu.over9000.skadi.channel;

public enum Platform {
	TWITCH_TV("http://www.twitch.tv/");
	
	private String url;
	
	private Platform(final String url) {
		this.url = url;
	}
	
	public static Platform getPlatformFromURL(final String url) {
		for (final Platform platform : Platform.values()) {
			if (url.startsWith(platform.getURL())) {
				return platform;
			}
		}
		return null;
	}
	
	private String getURL() {
		return this.url;
	}
	
}
