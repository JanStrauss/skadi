package eu.over9000.skadi.util;

public class StringUtil {
	
	public static String extractChannelName(final String url) {
		final String[] splitted = url.split("/");
		return splitted[splitted.length - 1];
	}
	
	public static String escape(String string) {
		string = string.replaceAll("'", "\\\\'");
		string = string.replaceAll("\"", "\\\\\"");
		return string;
	}
}
