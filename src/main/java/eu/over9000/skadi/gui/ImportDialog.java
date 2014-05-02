package eu.over9000.skadi.gui;

import java.awt.BorderLayout;
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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import eu.over9000.skadi.SkadiMain;

public class ImportDialog extends JDialog {
	public ImportDialog() {
		this.initialize();
	}
	
	private void initialize() {
		this.setResizable(false);
		this.setTitle("Import followed channels from twitch.tv");
		this.setModal(true);
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		this.getContentPane().add(this.getPnInput(), BorderLayout.CENTER);
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
	private JLabel lbStatus;
	
	private JPanel getPnInput() {
		if (this.pnInput == null) {
			this.pnInput = new JPanel();
			this.pnInput.setBorder(new CompoundBorder(new EmptyBorder(5, 5, 5, 5), new TitledBorder(UIManager
			        .getBorder("TitledBorder.border"), "Import", TitledBorder.LEADING, TitledBorder.TOP, null, null)));
			final GridBagLayout gbl_pnInput = new GridBagLayout();
			gbl_pnInput.columnWidths = new int[] { 0, 0, 0 };
			gbl_pnInput.rowHeights = new int[] { 0, 0, 0, 0 };
			gbl_pnInput.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
			gbl_pnInput.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
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
			final GridBagConstraints gbc_lbStatus = new GridBagConstraints();
			gbc_lbStatus.gridwidth = 2;
			gbc_lbStatus.insets = new Insets(0, 0, 0, 5);
			gbc_lbStatus.gridx = 0;
			gbc_lbStatus.gridy = 2;
			this.pnInput.add(this.getLbStatus(), gbc_lbStatus);
		}
		return this.pnInput;
	}
	
	private JLabel getLblNewLabel() {
		if (this.lblNewLabel == null) {
			this.lblNewLabel = new JLabel("Twitch.tv username:");
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
					
					final int count = SkadiMain.getInstance().importFollowedChannelsFromTwitch(
					        ImportDialog.this.getTextField().getText().trim());
					
					ImportDialog.this.setVisible(false);
					
					JOptionPane.showMessageDialog(SkadiGUI.getInstance(), "imported " + count + " channels.");
				}
			});
		}
		return this.btnImport;
	}
	
	private JLabel getLbStatus() {
		if (this.lbStatus == null) {
			this.lbStatus = new JLabel("...");
			this.lbStatus.setHorizontalAlignment(SwingConstants.LEFT);
		}
		return this.lbStatus;
	}
}
