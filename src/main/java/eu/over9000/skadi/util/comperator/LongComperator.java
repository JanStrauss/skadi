package eu.over9000.skadi.util.comperator;

import java.util.Comparator;

public class LongComperator implements Comparator<Long> {
	
	@Override
	public int compare(final Long o1, final Long o2) {
		return Long.compare(o1, o2);
	}
	
}
