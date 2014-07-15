package eu.over9000.skadi.util;

import java.util.regex.Pattern;

public class ChannelURLUtil {
	public static boolean validateURL(final String url) {
		return Pattern.matches("(http://)?(www\\.)?(twitch\\.tv/)?[A-Za-z0-9_]+/", url);
	}
	
	public static String fixURL(String url) {
		if (!url.endsWith("/")) {
			url = url + "/";
		}
		
		if (!(url.startsWith("http://www.twitch.tv/") || url.startsWith("https://www.twitch.tv/"))) {
			if (url.startsWith("www.twitch.tv/")) {
				url = "http://" + url;
			} else if (url.startsWith("twitch.tv/")) {
				url = "http://www." + url;
			} else {
				url = "http://www.twitch.tv/" + url;
			}
		}
		
		return url;
	}
}
