package eu.over9000.skadi.util;

import java.util.List;

public class StringUtil {
	public static boolean containsIgnoreCase(final List<String> list, final String lookup) {
		for (final String current : list) {
			if (current.equalsIgnoreCase(lookup)) {
				return true;
			}
		}
		return false;
	}
}
