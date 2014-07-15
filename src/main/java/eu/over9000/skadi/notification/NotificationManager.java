package eu.over9000.skadi.notification;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class NotificationManager extends JFrame {
	
	private static final long serialVersionUID = -2034400852951326564L;
	private final TrayIcon trayIcon;
	
	public NotificationManager() throws IOException, AWTException {
		final Image image = ImageIO.read(this.getClass().getResource("/icon.png"));
		this.trayIcon = new TrayIcon(image, "Skadi", null);
		this.trayIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if ((e.getClickCount() == 1) || (e.getClickCount() == 2)) {
					NotificationManager.this.toggleView();
				}
			}
		});
		
		this.setSize(400, 280);
		this.setTitle("kek");
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(final WindowEvent e) {
			}
			
			@Override
			public void windowIconified(final WindowEvent e) {
			}
			
			@Override
			public void windowDeiconified(final WindowEvent e) {
			}
			
			@Override
			public void windowDeactivated(final WindowEvent e) {
			}
			
			@Override
			public void windowClosing(final WindowEvent e) {
				NotificationManager.this.toggleView();
			}
			
			@Override
			public void windowClosed(final WindowEvent e) {
			}
			
			@Override
			public void windowActivated(final WindowEvent e) {
			}
		});
		
	}
	
	private void toggleView() {
		System.out.println("toggle");
		
		if (this.isVisible()) {
			this.setVisible(false);
			try {
				SystemTray.getSystemTray().add(this.trayIcon);
				this.trayIcon.displayMessage("Skadi is running in background",
				        "Click this icon to display Skadi again", MessageType.INFO);
			} catch (final AWTException e) {
				e.printStackTrace();
			}
		} else {
			this.setVisible(true);
			
			SystemTray.getSystemTray().remove(this.trayIcon);
		}
	}
	
	public static void main(final String[] args) throws IOException, AWTException {
		new NotificationManager();
	}
}
