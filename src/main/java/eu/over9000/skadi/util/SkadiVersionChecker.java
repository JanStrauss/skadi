package eu.over9000.skadi.util;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import eu.over9000.skadi.gui.SkadiGUI;

public class SkadiVersionChecker {
	
	public static void checkVersion() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				final String localVersion = this.getClass().getPackage().getImplementationVersion();
				System.out.println("starting " + localVersion);
				
				final String remoteVersion = SkadiVersionRetriever.getLatestVersion();
				
				final String url = "https://github.com/s1mpl3x/skadi/releases/";
				
				if (!remoteVersion.equals(localVersion)) {
					
					final String updateMsg = "There is a newer version (" + remoteVersion
					        + ") of Skadi available. You can download it from: ";
					System.out.println(updateMsg + url);
					
					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							final JPanel updatePanel = new JPanel();
							final JLabel msgLabel = new JLabel(updateMsg);
							final JLabel linkLabel = new JLabel("<html><a href=\"#\">" + url + "</a></html>");
							linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
							linkLabel.addMouseListener(new MouseAdapter() {
								@Override
								public void mouseClicked(final MouseEvent event) {
									try {
										Desktop.getDesktop().browse(new URI(url));
									} catch (URISyntaxException | IOException e) {
										e.printStackTrace();
									}
								}
							});
							
							updatePanel.add(msgLabel);
							updatePanel.add(linkLabel);
							
							JOptionPane.showMessageDialog(SkadiGUI.getInstance(), updatePanel, "New version available",
							        JOptionPane.INFORMATION_MESSAGE);
							
						}
					});
				} else {
					System.out.println("This is the latest version.");
				}
				
			}
		}).start();
	}
	
}
