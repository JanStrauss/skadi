package eu.over9000.skadi.notification;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.imageio.ImageIO;

import eu.over9000.skadi.SkadiMain;
import eu.over9000.skadi.channel.Channel;
import eu.over9000.skadi.channel.ChannelEventListener;
import eu.over9000.skadi.channel.ChannelManager;
import eu.over9000.skadi.util.StringUtil;

public class NotificationManager implements ChannelEventListener {
	
	private static NotificationManager instance;
	
	public static NotificationManager getInstance() {
		if (NotificationManager.instance == null) {
			NotificationManager.instance = new NotificationManager();
		}
		return NotificationManager.instance;
	}
	
	private TrayIcon trayIcon;
	
	public NotificationManager() {
		try {
			final Image image = ImageIO.read(this.getClass().getResource("/icon.png"));
			this.trayIcon = new TrayIcon(image, "Skadi", null);
			this.trayIcon.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(final MouseEvent e) {
					if ((e.getClickCount() == 1) || (e.getClickCount() == 2)) {
						System.out.println("clicked on icon");
					}
				}
			});
			
			if (SystemTray.isSupported()) {
				SystemTray.getSystemTray().add(this.trayIcon);
			}
			
			ChannelManager.getInstance().addListener(this);
			
		} catch (final IOException | AWTException e1) {
			e1.printStackTrace();
		}
	}
	
	public void displayNotification(final String header, final String message) {
		if (this.trayIcon == null) {
			return;
		}
		
		this.trayIcon.displayMessage(header, message, MessageType.NONE);
		
	}
	
	@Override
	public void added(final Channel channel) {
	}
	
	@Override
	public void removed(final Channel channel) {
	}
	
	@Override
	public void updatedMetadata(final Channel channel) {
		if (SkadiMain.getInstance().display_notifications && channel.wentOnline()) {
			this.displayNotification(StringUtil.extractChannelName(channel.getURL()) + " went live", channel
			        .getMetadata().getTitle());
		}
	}
	
	@Override
	public void updatedStreamdata(final Channel channel) {
	}
	
	@Override
	public String getListenerName() {
		return this.getClass().getName();
	}
}
