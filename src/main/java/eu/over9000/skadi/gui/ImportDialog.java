package eu.over9000.skadi.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import eu.over9000.skadi.SkadiMain;

public class ImportDialog extends JDialog {
	
	private final SkadiGUI gui;
	
	public ImportDialog(final SkadiGUI gui) {
		this.gui = gui;
		this.initialize();
	}
	
	private void initialize() {
		this.setLocationRelativeTo(this.gui);
		this.setResizable(false);
		this.setTitle("Import followed channels from twitch.tv");
		this.setModal(true);
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		this.getContentPane().add(this.getPnInput(), BorderLayout.CENTER);
		this.getRootPane().setDefaultButton(this.getBtnImport());
		this.pack();
		this.setVisible(true);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5418981501993305218L;
	private JPanel pnInput;
	private JLabel lblNewLabel;
	private JTextField textField;
	private JButton btnImport;
	private JProgressBar pbImport;
	private JLabel lbProgress;
	private JLabel lbStatus;
	
	private JPanel getPnInput() {
		if (this.pnInput == null) {
			this.pnInput = new JPanel();
			this.pnInput.setBorder(new EmptyBorder(5, 5, 5, 5));
			final GridBagLayout gbl_pnInput = new GridBagLayout();
			gbl_pnInput.columnWidths = new int[] { 0, 0, 0 };
			gbl_pnInput.rowHeights = new int[] { 0, 0, 0, 0, 0 };
			gbl_pnInput.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
			gbl_pnInput.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
			this.pnInput.setLayout(gbl_pnInput);
			final GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
			gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
			gbc_lblNewLabel.gridx = 0;
			gbc_lblNewLabel.gridy = 0;
			this.pnInput.add(this.getLblNewLabel(), gbc_lblNewLabel);
			final GridBagConstraints gbc_textField = new GridBagConstraints();
			gbc_textField.insets = new Insets(0, 0, 5, 0);
			gbc_textField.fill = GridBagConstraints.HORIZONTAL;
			gbc_textField.gridx = 1;
			gbc_textField.gridy = 0;
			this.pnInput.add(this.getTextField(), gbc_textField);
			final GridBagConstraints gbc_btnImport = new GridBagConstraints();
			gbc_btnImport.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnImport.insets = new Insets(0, 0, 5, 0);
			gbc_btnImport.gridx = 1;
			gbc_btnImport.gridy = 1;
			this.pnInput.add(this.getBtnImport(), gbc_btnImport);
			final GridBagConstraints gbc_lbProgress = new GridBagConstraints();
			gbc_lbProgress.insets = new Insets(0, 0, 5, 5);
			gbc_lbProgress.fill = GridBagConstraints.HORIZONTAL;
			gbc_lbProgress.gridx = 0;
			gbc_lbProgress.gridy = 2;
			this.pnInput.add(this.getLbProgress(), gbc_lbProgress);
			final GridBagConstraints gbc_pbImport = new GridBagConstraints();
			gbc_pbImport.insets = new Insets(0, 0, 5, 0);
			gbc_pbImport.fill = GridBagConstraints.HORIZONTAL;
			gbc_pbImport.gridx = 1;
			gbc_pbImport.gridy = 2;
			this.pnInput.add(this.getPbImport(), gbc_pbImport);
			final GridBagConstraints gbc_lbStatus = new GridBagConstraints();
			gbc_lbStatus.fill = GridBagConstraints.HORIZONTAL;
			gbc_lbStatus.gridx = 1;
			gbc_lbStatus.gridy = 3;
			this.pnInput.add(this.getLbStatus(), gbc_lbStatus);
		}
		return this.pnInput;
	}
	
	private JLabel getLblNewLabel() {
		if (this.lblNewLabel == null) {
			this.lblNewLabel = new JLabel("Twitch username:");
		}
		return this.lblNewLabel;
	}
	
	private JTextField getTextField() {
		if (this.textField == null) {
			this.textField = new JTextField();
			this.textField.setColumns(25);
		}
		return this.textField;
	}
	
	private JButton getBtnImport() {
		if (this.btnImport == null) {
			this.btnImport = new JButton("Import");
			this.btnImport.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(final ActionEvent e) {
					ImportDialog.this.btnImport.setEnabled(false);
					
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							final String username = ImportDialog.this.getTextField().getText().trim();
							
							final String result = SkadiMain.getInstance().importFollowedChannelsFromTwitch(username,
							        ImportDialog.this);
							
							SwingUtilities.invokeLater(new Runnable() {
								
								@Override
								public void run() {
									ImportDialog.this.setVisible(false);
									
								}
							});
							
							JOptionPane.showMessageDialog(ImportDialog.this.gui, result);
							
						}
					}).start();
					
				}
			});
		}
		return this.btnImport;
	}
	
	private JProgressBar getPbImport() {
		if (this.pbImport == null) {
			this.pbImport = new JProgressBar();
			this.pbImport.setPreferredSize(new Dimension(146, 24));
		}
		return this.pbImport;
	}
	
	private JLabel getLbProgress() {
		if (this.lbProgress == null) {
			this.lbProgress = new JLabel("Progress:");
		}
		return this.lbProgress;
	}
	
	private JLabel getLbStatus() {
		if (this.lbStatus == null) {
			this.lbStatus = new JLabel(" ");
		}
		return this.lbStatus;
	}
	
	public void setIndeterminate(final boolean value) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				ImportDialog.this.pbImport.setIndeterminate(value);
				
			}
		});
	}
	
	public void updateProgress(final int max, final int loaded, final String status) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				ImportDialog.this.pbImport.setIndeterminate(false);
				ImportDialog.this.pbImport.setMaximum(max * 2);
				ImportDialog.this.pbImport.setValue(loaded);
				ImportDialog.this.lbStatus.setText(status);
				
			}
		});
	}
}
