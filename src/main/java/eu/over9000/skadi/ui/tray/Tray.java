package eu.over9000.skadi.ui.tray;

import java.io.IOException;

import javafx.application.Platform;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.over9000.skadi.model.StateContainer;

public class Tray {

	private static final Logger LOGGER = LoggerFactory.getLogger(Tray.class);
	
	private final Stage stage;

	private final StateContainer state;

	private java.awt.TrayIcon trayIcon;
	
	private java.awt.SystemTray tray;
	
	public Tray(final Stage stage, final StateContainer state) {
		this.stage = stage;
		this.state = state;
		
		javax.swing.SwingUtilities.invokeLater(this::buildTray);
	}
	
	private void buildTray() {
		try {

			java.awt.Toolkit.getDefaultToolkit();

			if (!java.awt.SystemTray.isSupported()) {
				Tray.LOGGER.warn("No system tray support, disabling minimize to tray.");
				this.state.setMinimizeToTray(false);
				return;
			}

			this.tray = java.awt.SystemTray.getSystemTray();

			final java.awt.Image image = ImageIO.read(this.getClass().getResourceAsStream("/icons/skadi.png"));
			this.trayIcon = new java.awt.TrayIcon(image);

			this.trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

			this.tray.add(this.trayIcon);
		} catch (java.awt.AWTException | IOException e) {
			Tray.LOGGER.error("Unable to init system tray", e);
		}
	}

	private void showStage() {
		this.stage.show();
		this.stage.setIconified(false);
		this.stage.toFront();
	}

	public void onShutdown() {
		if (!java.awt.SystemTray.isSupported()) {
			return;
		}
		this.tray.remove(this.trayIcon);
	}

}