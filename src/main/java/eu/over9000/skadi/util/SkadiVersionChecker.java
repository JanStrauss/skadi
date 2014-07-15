/*******************************************************************************
 * Copyright (c) 2014 Jan Strauß
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
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
import eu.over9000.skadi.logging.SkadiLogging;

/**
 * This class provides a method used to check the local version against the latest version on github.
 * 
 * @author Jan Strauß
 * 
 */
public class SkadiVersionChecker {
	
	private final static String SKADI_RELEASES_URL = "https://github.com/s1mpl3x/skadi/releases/";
	
	public static void checkVersion() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				final String localVersion = this.getClass().getPackage().getImplementationVersion();
				
				if (localVersion == null) {
					SkadiLogging.log("could not find local Version, will skip version check");
					return;
				}
				
				SkadiLogging.log("starting " + localVersion);
				
				final String remoteVersion = SkadiVersionRetriever.getLatestVersion();
				
				if (!remoteVersion.equals(localVersion)) {
					
					final String updateMsg = "There is a newer version (" + remoteVersion
					        + ") of Skadi available. You can download it from: ";
					SkadiLogging.log(updateMsg + SkadiVersionChecker.SKADI_RELEASES_URL);
					
					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							final JPanel updatePanel = new JPanel();
							final JLabel msgLabel = new JLabel(updateMsg);
							final JLabel linkLabel = new JLabel("<html><a href=\"#\">"
							        + SkadiVersionChecker.SKADI_RELEASES_URL + "</a></html>");
							linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
							linkLabel.addMouseListener(new MouseAdapter() {
								@Override
								public void mouseClicked(final MouseEvent event) {
									try {
										Desktop.getDesktop().browse(new URI(SkadiVersionChecker.SKADI_RELEASES_URL));
									} catch (URISyntaxException | IOException e) {
										SkadiLogging.log(e);
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
					SkadiLogging.log("This is the latest version.");
				}
				
			}
		}).start();
	}
	
}
