package eu.over9000.skadi.util;

import java.util.concurrent.TimeUnit;

public class TimeUtil {
	/**
	 * Convert a millisecond duration to a string format
	 * 
	 * @param millis
	 *            A duration to convert to a string form
	 * @return A string of the form "X Days Y Hours Z Minutes A Seconds".
	 */
	public static String getDurationBreakdown(long millis) {
		if (millis < 0) {
			throw new IllegalArgumentException("Duration must be greater than zero!");
		}
		
		final long days = TimeUnit.MILLISECONDS.toDays(millis);
		millis -= TimeUnit.DAYS.toMillis(days);
		final long hours = TimeUnit.MILLISECONDS.toHours(millis);
		millis -= TimeUnit.HOURS.toMillis(hours);
		final long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
		millis -= TimeUnit.MINUTES.toMillis(minutes);
		final long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
		
		final StringBuilder sb = new StringBuilder(64);
		if (days > 0) {
			sb.append(days);
			sb.append(" days ");
		}
		if (hours > 0) {
			sb.append(String.format("%02d", hours));
			sb.append(" h ");
		}
		if (minutes > 0) {
			sb.append(String.format("%02d", minutes));
			sb.append(" min ");
		}
		if (seconds > 0) {
			sb.append(String.format("%02d", seconds));
			sb.append(" s");
		}
		
		return (sb.toString());
	}
}
