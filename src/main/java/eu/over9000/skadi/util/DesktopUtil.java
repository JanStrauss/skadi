package eu.over9000.skadi.util;

import java.awt.Desktop;
import java.net.URI;

public class DesktopUtil {
	public static void openWebpage(final String url) {
		final Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(URI.create(url.trim()));
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}
}
