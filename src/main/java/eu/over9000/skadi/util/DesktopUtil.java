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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.URI;

public class DesktopUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(DesktopUtil.class);

	public static void openWebpage(final String url) {
		openWebpage(URI.create(url));
	}

	public static void openWebpage(final URI uri) {
		final Runnable instance = new DekstopInstance(uri);
		ExecutorUtil.getExecutorService().submit(instance);
	}

	private static class DekstopInstance implements Runnable {

		private final URI uri;

		public DekstopInstance(final URI uri) {
			this.uri = uri;
		}

		@Override
		public void run() {
			final Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			if ((desktop != null) && desktop.isSupported(Desktop.Action.BROWSE)) {
				try {
					desktop.browse(uri);
				} catch (final Exception e) {
					LOGGER.error("Exception opening url", e);
				}
			}
		}
	}
}
