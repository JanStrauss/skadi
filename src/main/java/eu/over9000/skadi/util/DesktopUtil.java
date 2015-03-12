package eu.over9000.skadi.util;

import java.awt.Desktop;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DesktopUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DesktopUtil.class);

	public static void openWebpage(final String url) {
		final Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if ((desktop != null) && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(URI.create(url.trim()));
			} catch (final Exception e) {
				DesktopUtil.LOGGER.error("Exception opening url", e);
			}
		}
	}
}
