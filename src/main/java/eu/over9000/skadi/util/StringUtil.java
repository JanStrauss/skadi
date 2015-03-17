package eu.over9000.skadi.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	final static Pattern urlPattern = Pattern.compile("http(s)*://(www\\.)*twitch\\.tv/(\\w+{2,25})(/.*)?");

	public static boolean containsIgnoreCase(final List<String> list, final String lookup) {
		for (final String current : list) {
			if (current.equalsIgnoreCase(lookup)) {
				return true;
			}
		}
		return false;
	}
	
	public static String extractUsernameFromURL(final String url) {
		final Matcher urlMatcher = StringUtil.urlPattern.matcher(url);

		if (urlMatcher.matches()) {
			return urlMatcher.group(3);
		}
		
		return null;
	}
}
