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
package eu.over9000.skadi.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import eu.over9000.skadi.SkadiMain;
import eu.over9000.skadi.io.PersistenceManager;
import eu.over9000.skadi.logging.SkadiLogging;

/**
 * {@link JDialog} providing the possibility to modify the configuration values of Skadi.
 * 
 * @author Jan Strauß
 * 
 */
public class SettingsDialog extends JDialog {
	
	private static final long serialVersionUID = 1149200384756597400L;
	private final SkadiGUI gui;
	private JPanel pnButtons;
	private JButton btnCancel;
	private JButton btnSave;
	private JPanel pnSettings;
	private JPanel pnExecutables;
	private JPanel pnOtherSettings;
	private JLabel lbChrome;
	private JLabel lbLivestreamer;
	private JLabel lbVLC;
	private JTextField tfChrome;
	private JTextField tfLivestreamer;
	private JTextField tfVLC;
	private JCheckBox chckbxUseLivestreamerFor;
	private JPanel pnLogs;
	private JLabel lbSkadiLogSize;
	private JLabel lbStreamLogSize;
	private JLabel lbChatLogSize;
	private JButton btnClearLogs;
	private JLabel lbSkadiLogValue;
	private JLabel lbStreamLogValue;
	private JLabel lbChatLogValue;
	private JButton btnOpenSkadiDir;
	private JCheckBox chckbxDisplayNotifications;
	private JCheckBox chckbxMinimizeToTray;
	
	public SettingsDialog(final SkadiGUI gui) {
		this.gui = gui;
		this.initialize();
		this.pack();
		this.setVisible(true);
	}
	
	private void initialize() {
		this.setLocationRelativeTo(this.gui);
		this.setResizable(false);
		this.setTitle("Skadi settings");
		this.setIconImage(new ImageIcon(this.getClass().getResource("/icon.png")).getImage());
		this.setModal(true);
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		this.getContentPane().add(this.getPnButtons(), BorderLayout.SOUTH);
		this.getContentPane().add(this.getPnSettings(), BorderLayout.CENTER);
		this.getRootPane().setDefaultButton(this.getBtnSave());
	}
	
	private JPanel getPnButtons() {
		if (this.pnButtons == null) {
			this.pnButtons = new JPanel();
			this.pnButtons.add(this.getBtnSave());
			this.pnButtons.add(this.getBtnCancel());
		}
		return this.pnButtons;
	}
	
	private JButton getBtnCancel() {
		if (this.btnCancel == null) {
			this.btnCancel = new JButton("Cancel");
			this.btnCancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					SettingsDialog.this.setVisible(false);
				}
			});
		}
		return this.btnCancel;
	}
	
	private JButton getBtnSave() {
		if (this.btnSave == null) {
			this.btnSave = new JButton("Save");
			this.btnSave.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					SkadiMain.getInstance().chrome_exec = SettingsDialog.this.getTfChrome().getText();
					SkadiMain.getInstance().livestreamer_exec = SettingsDialog.this.getTfLivestreamer().getText();
					SkadiMain.getInstance().vlc_exec = SettingsDialog.this.getTfVLC().getText();
					
					SkadiMain.getInstance().use_livestreamer = SettingsDialog.this.getChckbxUseLivestreamerFor()
					        .isSelected();
					SkadiMain.getInstance().display_notifications = SettingsDialog.this.getChckbxDisplayNotifications()
					        .isSelected();
					SkadiMain.getInstance().minimize_to_tray = SettingsDialog.this.getChckbxMinimizeToTray()
					        .isSelected();
					
					SkadiLogging.log("settings updated");
					SettingsDialog.this.setVisible(false);
				}
			});
			
		}
		return this.btnSave;
	}
	
	private JPanel getPnSettings() {
		if (this.pnSettings == null) {
			this.pnSettings = new JPanel();
			this.pnSettings.setLayout(new BoxLayout(this.pnSettings, BoxLayout.Y_AXIS));
			this.pnSettings.add(this.getPnExecutables());
			this.pnSettings.add(this.getPnOtherSettings());
			this.pnSettings.add(this.getPnLogs());
		}
		return this.pnSettings;
	}
	
	private JPanel getPnExecutables() {
		if (this.pnExecutables == null) {
			this.pnExecutables = new JPanel();
			this.pnExecutables.setBorder(new CompoundBorder(new EmptyBorder(5, 5, 5, 5), new TitledBorder(UIManager
			        .getBorder("TitledBorder.border"), "Executables", TitledBorder.LEADING, TitledBorder.TOP, null,
			        null)));
			final GridBagLayout gbl_pnExecutables = new GridBagLayout();
			gbl_pnExecutables.columnWidths = new int[] { 0, 0, 0, 0 };
			gbl_pnExecutables.rowHeights = new int[] { 0, 0, 0, 0 };
			gbl_pnExecutables.columnWeights = new double[] { 0.0, 1.0, 1.0, Double.MIN_VALUE };
			gbl_pnExecutables.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
			this.pnExecutables.setLayout(gbl_pnExecutables);
			final GridBagConstraints gbc_lbChrome = new GridBagConstraints();
			gbc_lbChrome.anchor = GridBagConstraints.WEST;
			gbc_lbChrome.insets = new Insets(0, 0, 5, 5);
			gbc_lbChrome.gridx = 0;
			gbc_lbChrome.gridy = 0;
			this.pnExecutables.add(this.getLbChrome(), gbc_lbChrome);
			final GridBagConstraints gbc_tfChrome = new GridBagConstraints();
			gbc_tfChrome.insets = new Insets(0, 0, 5, 5);
			gbc_tfChrome.fill = GridBagConstraints.HORIZONTAL;
			gbc_tfChrome.gridx = 1;
			gbc_tfChrome.gridy = 0;
			this.pnExecutables.add(this.getTfChrome(), gbc_tfChrome);
			final GridBagConstraints gbc_lbLivestreamer = new GridBagConstraints();
			gbc_lbLivestreamer.anchor = GridBagConstraints.WEST;
			gbc_lbLivestreamer.insets = new Insets(0, 0, 5, 5);
			gbc_lbLivestreamer.gridx = 0;
			gbc_lbLivestreamer.gridy = 1;
			this.pnExecutables.add(this.getLbLivestreamer(), gbc_lbLivestreamer);
			final GridBagConstraints gbc_tfLivestreamer = new GridBagConstraints();
			gbc_tfLivestreamer.insets = new Insets(0, 0, 5, 5);
			gbc_tfLivestreamer.fill = GridBagConstraints.HORIZONTAL;
			gbc_tfLivestreamer.gridx = 1;
			gbc_tfLivestreamer.gridy = 1;
			this.pnExecutables.add(this.getTfLivestreamer(), gbc_tfLivestreamer);
			final GridBagConstraints gbc_lbVLC = new GridBagConstraints();
			gbc_lbVLC.anchor = GridBagConstraints.WEST;
			gbc_lbVLC.insets = new Insets(0, 0, 0, 5);
			gbc_lbVLC.gridx = 0;
			gbc_lbVLC.gridy = 2;
			this.pnExecutables.add(this.getLbVLC(), gbc_lbVLC);
			final GridBagConstraints gbc_tfVLC = new GridBagConstraints();
			gbc_tfVLC.insets = new Insets(0, 0, 0, 5);
			gbc_tfVLC.fill = GridBagConstraints.HORIZONTAL;
			gbc_tfVLC.gridx = 1;
			gbc_tfVLC.gridy = 2;
			this.pnExecutables.add(this.getTfVLC(), gbc_tfVLC);
		}
		return this.pnExecutables;
	}
	
	private JPanel getPnOtherSettings() {
		if (this.pnOtherSettings == null) {
			this.pnOtherSettings = new JPanel();
			this.pnOtherSettings.setBorder(new CompoundBorder(new EmptyBorder(5, 5, 5, 5), new TitledBorder(UIManager
			        .getBorder("TitledBorder.border"), "Other settings", TitledBorder.LEADING, TitledBorder.TOP, null,
			        null)));
			final GridBagLayout gbl_pnOtherSettings = new GridBagLayout();
			gbl_pnOtherSettings.columnWidths = new int[] { 0, 0 };
			gbl_pnOtherSettings.rowHeights = new int[] { 0, 0, 0, 0 };
			gbl_pnOtherSettings.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
			gbl_pnOtherSettings.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
			this.pnOtherSettings.setLayout(gbl_pnOtherSettings);
			final GridBagConstraints gbc_chckbxUseLivestreamerFor = new GridBagConstraints();
			gbc_chckbxUseLivestreamerFor.anchor = GridBagConstraints.WEST;
			gbc_chckbxUseLivestreamerFor.insets = new Insets(0, 0, 5, 0);
			gbc_chckbxUseLivestreamerFor.gridx = 0;
			gbc_chckbxUseLivestreamerFor.gridy = 0;
			this.pnOtherSettings.add(this.getChckbxUseLivestreamerFor(), gbc_chckbxUseLivestreamerFor);
			final GridBagConstraints gbc_chckbxDisplayNotifications = new GridBagConstraints();
			gbc_chckbxDisplayNotifications.insets = new Insets(0, 0, 5, 0);
			gbc_chckbxDisplayNotifications.anchor = GridBagConstraints.WEST;
			gbc_chckbxDisplayNotifications.gridx = 0;
			gbc_chckbxDisplayNotifications.gridy = 1;
			this.pnOtherSettings.add(this.getChckbxDisplayNotifications(), gbc_chckbxDisplayNotifications);
			final GridBagConstraints gbc_chckbxMinimizeToTray = new GridBagConstraints();
			gbc_chckbxMinimizeToTray.anchor = GridBagConstraints.WEST;
			gbc_chckbxMinimizeToTray.gridx = 0;
			gbc_chckbxMinimizeToTray.gridy = 2;
			this.pnOtherSettings.add(this.getChckbxMinimizeToTray(), gbc_chckbxMinimizeToTray);
		}
		return this.pnOtherSettings;
	}
	
	private JLabel getLbChrome() {
		if (this.lbChrome == null) {
			this.lbChrome = new JLabel("Chrome:");
		}
		return this.lbChrome;
	}
	
	private JLabel getLbLivestreamer() {
		if (this.lbLivestreamer == null) {
			this.lbLivestreamer = new JLabel("Livestreamer:");
		}
		return this.lbLivestreamer;
	}
	
	private JLabel getLbVLC() {
		if (this.lbVLC == null) {
			this.lbVLC = new JLabel("VLC:");
		}
		return this.lbVLC;
	}
	
	private JTextField getTfChrome() {
		if (this.tfChrome == null) {
			this.tfChrome = new JTextField(SkadiMain.getInstance().chrome_exec);
			this.tfChrome.setColumns(30);
		}
		return this.tfChrome;
	}
	
	private JTextField getTfLivestreamer() {
		if (this.tfLivestreamer == null) {
			this.tfLivestreamer = new JTextField(SkadiMain.getInstance().livestreamer_exec);
			this.tfLivestreamer.setColumns(30);
		}
		return this.tfLivestreamer;
	}
	
	private JTextField getTfVLC() {
		if (this.tfVLC == null) {
			this.tfVLC = new JTextField(SkadiMain.getInstance().vlc_exec);
			this.tfVLC.setColumns(30);
		}
		return this.tfVLC;
	}
	
	private JCheckBox getChckbxUseLivestreamerFor() {
		if (this.chckbxUseLivestreamerFor == null) {
			this.chckbxUseLivestreamerFor = new JCheckBox("Use livestreamer", SkadiMain.getInstance().use_livestreamer);
		}
		return this.chckbxUseLivestreamerFor;
	}
	
	private JCheckBox getChckbxDisplayNotifications() {
		if (this.chckbxDisplayNotifications == null) {
			this.chckbxDisplayNotifications = new JCheckBox("Display notifications",
			        SkadiMain.getInstance().display_notifications);
		}
		return this.chckbxDisplayNotifications;
	}
	
	private JCheckBox getChckbxMinimizeToTray() {
		if (this.chckbxMinimizeToTray == null) {
			this.chckbxMinimizeToTray = new JCheckBox("Minimize to tray", SkadiMain.getInstance().minimize_to_tray);
		}
		return this.chckbxMinimizeToTray;
	}
	
	private JPanel getPnLogs() {
		if (this.pnLogs == null) {
			this.pnLogs = new JPanel();
			this.pnLogs.setBorder(new CompoundBorder(new EmptyBorder(5, 5, 5, 5), new TitledBorder(UIManager
			        .getBorder("TitledBorder.border"), "Logging", TitledBorder.LEADING, TitledBorder.TOP, null, null)));
			final GridBagLayout gbl_pnLogs = new GridBagLayout();
			gbl_pnLogs.columnWidths = new int[] { 0, 0, 0 };
			gbl_pnLogs.rowHeights = new int[] { 0, 0, 0, 0, 0 };
			gbl_pnLogs.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
			gbl_pnLogs.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
			this.pnLogs.setLayout(gbl_pnLogs);
			final GridBagConstraints gbc_lbSkadiLogSize = new GridBagConstraints();
			gbc_lbSkadiLogSize.anchor = GridBagConstraints.WEST;
			gbc_lbSkadiLogSize.insets = new Insets(0, 0, 5, 5);
			gbc_lbSkadiLogSize.gridx = 0;
			gbc_lbSkadiLogSize.gridy = 0;
			this.pnLogs.add(this.getLbSkadiLogSize(), gbc_lbSkadiLogSize);
			final GridBagConstraints gbc_lbSkadiLogValue = new GridBagConstraints();
			gbc_lbSkadiLogValue.anchor = GridBagConstraints.EAST;
			gbc_lbSkadiLogValue.insets = new Insets(0, 0, 5, 0);
			gbc_lbSkadiLogValue.gridx = 1;
			gbc_lbSkadiLogValue.gridy = 0;
			this.pnLogs.add(this.getLbSkadiLogValue(), gbc_lbSkadiLogValue);
			final GridBagConstraints gbc_lbStreamLogSize = new GridBagConstraints();
			gbc_lbStreamLogSize.anchor = GridBagConstraints.WEST;
			gbc_lbStreamLogSize.insets = new Insets(0, 0, 5, 5);
			gbc_lbStreamLogSize.gridx = 0;
			gbc_lbStreamLogSize.gridy = 1;
			this.pnLogs.add(this.getLbStreamLogSize(), gbc_lbStreamLogSize);
			final GridBagConstraints gbc_lbStreamLogValue = new GridBagConstraints();
			gbc_lbStreamLogValue.anchor = GridBagConstraints.EAST;
			gbc_lbStreamLogValue.insets = new Insets(0, 0, 5, 0);
			gbc_lbStreamLogValue.gridx = 1;
			gbc_lbStreamLogValue.gridy = 1;
			this.pnLogs.add(this.getLbStreamLogValue(), gbc_lbStreamLogValue);
			final GridBagConstraints gbc_lbChatLogSize = new GridBagConstraints();
			gbc_lbChatLogSize.anchor = GridBagConstraints.WEST;
			gbc_lbChatLogSize.insets = new Insets(0, 0, 5, 5);
			gbc_lbChatLogSize.gridx = 0;
			gbc_lbChatLogSize.gridy = 2;
			this.pnLogs.add(this.getLbChatLogSize(), gbc_lbChatLogSize);
			final GridBagConstraints gbc_lbChatLogValue = new GridBagConstraints();
			gbc_lbChatLogValue.anchor = GridBagConstraints.EAST;
			gbc_lbChatLogValue.insets = new Insets(0, 0, 5, 0);
			gbc_lbChatLogValue.gridx = 1;
			gbc_lbChatLogValue.gridy = 2;
			this.pnLogs.add(this.getLbChatLogValue(), gbc_lbChatLogValue);
			final GridBagConstraints gbc_btnClearLogs = new GridBagConstraints();
			gbc_btnClearLogs.insets = new Insets(0, 0, 0, 5);
			gbc_btnClearLogs.gridx = 0;
			gbc_btnClearLogs.gridy = 3;
			this.pnLogs.add(this.getBtnClearLogs(), gbc_btnClearLogs);
			final GridBagConstraints gbc_btnOpenSkadiDir = new GridBagConstraints();
			gbc_btnOpenSkadiDir.anchor = GridBagConstraints.WEST;
			gbc_btnOpenSkadiDir.gridx = 1;
			gbc_btnOpenSkadiDir.gridy = 3;
			this.pnLogs.add(this.getBtnOpenSkadiDir(), gbc_btnOpenSkadiDir);
		}
		return this.pnLogs;
	}
	
	private JLabel getLbSkadiLogSize() {
		if (this.lbSkadiLogSize == null) {
			this.lbSkadiLogSize = new JLabel("Skadi log size:");
		}
		return this.lbSkadiLogSize;
	}
	
	private JLabel getLbStreamLogSize() {
		if (this.lbStreamLogSize == null) {
			this.lbStreamLogSize = new JLabel("Stream log size:");
		}
		return this.lbStreamLogSize;
	}
	
	private JLabel getLbChatLogSize() {
		if (this.lbChatLogSize == null) {
			this.lbChatLogSize = new JLabel("Chat log size:");
		}
		return this.lbChatLogSize;
	}
	
	private JButton getBtnClearLogs() {
		if (this.btnClearLogs == null) {
			this.btnClearLogs = new JButton("clear log files");
			this.btnClearLogs.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					SkadiLogging.clearLogFiles();
					SettingsDialog.this.getLbSkadiLogValue().setText(SkadiLogging.getSkadiLogSize() + " kB");
					SettingsDialog.this.getLbChatLogValue().setText(SkadiLogging.getChatLogSize() + " kB");
					SettingsDialog.this.getLbStreamLogValue().setText(SkadiLogging.getStreamLogSize() + " kB");
				}
			});
		}
		return this.btnClearLogs;
	}
	
	private JLabel getLbSkadiLogValue() {
		if (this.lbSkadiLogValue == null) {
			this.lbSkadiLogValue = new JLabel(SkadiLogging.getSkadiLogSize() + " kB");
		}
		return this.lbSkadiLogValue;
	}
	
	private JLabel getLbStreamLogValue() {
		if (this.lbStreamLogValue == null) {
			this.lbStreamLogValue = new JLabel(SkadiLogging.getStreamLogSize() + " kB");
		}
		return this.lbStreamLogValue;
	}
	
	private JLabel getLbChatLogValue() {
		if (this.lbChatLogValue == null) {
			this.lbChatLogValue = new JLabel(SkadiLogging.getChatLogSize() + " kB");
		}
		return this.lbChatLogValue;
	}
	
	private JButton getBtnOpenSkadiDir() {
		if (this.btnOpenSkadiDir == null) {
			this.btnOpenSkadiDir = new JButton("Open directory");
			this.btnOpenSkadiDir.setEnabled(false);
			if (Desktop.isDesktopSupported()) {
				this.btnOpenSkadiDir.setEnabled(true);
				this.btnOpenSkadiDir.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent event) {
						try {
							Desktop.getDesktop().open(new File(PersistenceManager.PERSISTENCE_DIRECTORY));
						} catch (final IOException e) {
							SkadiLogging.log(e);
						}
					}
				});
			}
		}
		return this.btnOpenSkadiDir;
	}
	
}
