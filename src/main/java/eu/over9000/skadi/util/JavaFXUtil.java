package eu.over9000.skadi.util;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableView;

public class JavaFXUtil {
	public static ScrollBar getVerticalScrollbar(final TableView<?> table) {
		ScrollBar result = null;
		for (final Node n : table.lookupAll(".scroll-bar")) {
			if (n instanceof ScrollBar) {
				final ScrollBar bar = (ScrollBar) n;
				if (bar.getOrientation().equals(Orientation.VERTICAL)) {
					result = bar;
				}
			}
		}
		return result;
	}
}
