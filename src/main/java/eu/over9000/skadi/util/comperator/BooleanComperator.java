package eu.over9000.skadi.util.comperator;

import java.util.Comparator;

public class BooleanComperator implements Comparator<Boolean> {
	
	@Override
	public int compare(final Boolean o1, final Boolean o2) {
		return Boolean.compare(o1, o2);
	}
	
}
