/*
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaVersionUtil {
	public static final String REQUIRED_VERSION = "1.8.0_60";
	private static final Logger LOGGER = LoggerFactory.getLogger(JavaVersionUtil.class);
	private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d)\\.(\\d)\\.(\\d)(_(\\d+))?(-(.*))?");

	public static boolean checkRequiredVersionIsPresent() {
		final JavaVersion required = extractVersion(REQUIRED_VERSION);
		final JavaVersion present = extractVersion(SystemUtils.JAVA_VERSION);

		if (present == null || required == null) {
			LOGGER.error("failed to parse java versions: required=" + REQUIRED_VERSION + ", present=" + SystemUtils.JAVA_VERSION);
			return false;
		}

		return present.compareTo(required) >= 0;
	}

	private static JavaVersion extractVersion(final String versionString) {
		try {
			final Matcher urlMatcher = VERSION_PATTERN.matcher(versionString);

			if (!urlMatcher.matches()) {
				return null;
			}

			final String majorStr = urlMatcher.group(1);
			final String minorStr = urlMatcher.group(2);
			final String patchStr = urlMatcher.group(3);
			final String updateStr = urlMatcher.group(5);
			final String tagStr = urlMatcher.group(7);


			final int digit1 = Integer.valueOf(majorStr);
			final int digit2 = Integer.valueOf(minorStr);
			final int digit3 = Integer.valueOf(patchStr);
			final int update = updateStr == null ? 0 : Integer.valueOf(updateStr);
			final String tag = tagStr == null ? "" : tagStr;


			return new JavaVersion(digit1, digit2, digit3, update, tag);
		} catch (final NumberFormatException e) {
			LOGGER.error("failed to parse version string: ", e);
			return null;
		}
	}

	private static class JavaVersion implements Comparable<JavaVersion> {
		private final int digit1;
		private final int digit2;
		private final int digit3;
		private final int update;
		private final String tag;

		public JavaVersion(final int digit1, final int digit2, final int digit3, final int update, final String tag) {
			this.digit1 = digit1;
			this.digit2 = digit2;
			this.digit3 = digit3;
			this.tag = tag;
			this.update = update;
		}

		@Override
		public int compareTo(final JavaVersion other) {
			return new CompareToBuilder().append(digit1, other.digit1).append(digit2, other.digit2).append(digit3, other.digit3).append(update, other.update).toComparison();
		}

		@Override
		public String toString() {
			return "JavaVersion{" + digit1 + "." + digit2 + "." + digit3 + "_" + update + "-" + tag + "}";
		}
	}
}
