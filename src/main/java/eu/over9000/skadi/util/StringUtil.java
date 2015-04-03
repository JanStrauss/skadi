/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 s1mpl3x <jan[at]over9000.eu>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package eu.over9000.skadi.util;

		import java.util.List;
		import java.util.regex.Matcher;
		import java.util.regex.Pattern;

		import eu.over9000.skadi.model.Channel;

public class StringUtil {
	public static final String USERNAME_REGEX = "\\w{2,25}";
	private static final Pattern urlPattern = Pattern.compile("http(s)*://(www\\.)*twitch\\.tv/(" + USERNAME_REGEX + ")(/.*)?");

	public static boolean containsIgnoreCase(final List<String> list, final String lookup) {
		for (final String current : list) {
			if (current.equalsIgnoreCase(lookup)) {
				return true;
			}
		}
		return false;
	}

	public static String extractUsernameFromURL(final String url) {
		final Matcher urlMatcher = urlPattern.matcher(url);

		if (urlMatcher.matches()) {
			return urlMatcher.group(3);
		}

		return null;
	}

	public static String toStreamURL(final Channel channel) {
		return "http://www.twitch.tv/" + channel.getName() + "/";
	}
}
