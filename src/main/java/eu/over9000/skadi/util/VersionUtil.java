package eu.over9000.skadi.util;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;

public class VersionUtil {

	public static final String REQUIRED_VERSION = "1.8.0_40";

	public static boolean checkRequiredVersionIsPresent() {
		final JavaVersion required = new JavaVersion(VersionUtil.REQUIRED_VERSION);
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
			return new CompareToBuilder().append(this.digit1, other.digit1).append(this.digit2, other.digit2)
			        .append(this.digit3, other.digit3).append(this.update, other.update).toComparison();
		}

	}
}
