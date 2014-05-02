package eu.over9000.skadi.util;

public class StringUtil {
	
	public static String extractChannelName(final String url) {
		final String[] splitted = url.split("/");
		return splitted[splitted.length - 1];
	}
}
