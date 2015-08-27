/*
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

package eu.over9000.skadi.ui.tray;

import java.awt.*;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.over9000.skadi.model.StateContainer;

public class Tray {

	private static final Logger LOGGER = LoggerFactory.getLogger(Tray.class);

	private final Stage stage;

	private java.awt.TrayIcon trayIcon;

	private java.awt.SystemTray tray;

	public Tray(final Stage stage) {
		this.stage = stage;

		javax.swing.SwingUtilities.invokeLater(this::buildTray);
	}

	private void buildTray() {
		try {

			java.awt.Toolkit.getDefaultToolkit();

			if (!java.awt.SystemTray.isSupported()) {
				LOGGER.warn("No system tray support, disabling minimize to tray.");
				StateContainer.getInstance().setMinimizeToTray(false);
				return;
			}

			tray = java.awt.SystemTray.getSystemTray();

			final Dimension trayIconSize = tray.getTrayIconSize();
			final java.awt.Image image = ImageIO.read(getClass().getResourceAsStream("/icons/skadi.png")).getScaledInstance(trayIconSize.width, trayIconSize.height, Image.SCALE_SMOOTH);
			trayIcon = new java.awt.TrayIcon(image);

			trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

			tray.add(trayIcon);
		} catch (java.awt.AWTException | IOException e) {
			LOGGER.error("Unable to init system tray", e);
		}
	}

	private void showStage() {
		stage.show();
		stage.setIconified(false);
		stage.toFront();
	}

	public void onShutdown() {
		if (!java.awt.SystemTray.isSupported()) {
			return;
		}
		tray.remove(trayIcon);
	}

}
