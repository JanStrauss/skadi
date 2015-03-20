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

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;

public class JavaVersionUtil {

	public static final String REQUIRED_VERSION = "1.8.0_40";

	public static boolean checkRequiredVersionIsPresent() {
		final JavaVersion required = new JavaVersion(REQUIRED_VERSION);
		final JavaVersion present = new JavaVersion(SystemUtils.JAVA_VERSION);

		return present.compareTo(required) >= 0;
	}

	private static class JavaVersion implements Comparable<JavaVersion> {
		private int digit1;
		private int digit2;
		private int digit3;
		private int update;

		public JavaVersion(final String versionString) {
			final String[] split = versionString.split("\\.|_|-");

			if (split.length > 0) {
				this.digit1 = Integer.parseInt(split[0]);
			}

			if (split.length > 1) {
				this.digit2 = Integer.parseInt(split[1]);
			}

			if (split.length > 2) {
				this.digit3 = Integer.parseInt(split[2]);
			}

			if (split.length > 3) {
				this.update = Integer.parseInt(split[3]);
			}
		}

		@Override
		public int compareTo(final JavaVersion other) {
			return new CompareToBuilder().append(this.digit1, other.digit1).append(this.digit2, other.digit2).append(this.digit3, other.digit3).append(this.update, other.update).toComparison();
		}

	}
}
