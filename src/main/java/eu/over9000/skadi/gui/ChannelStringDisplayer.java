package eu.over9000.skadi.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

public class ChannelStringDisplayer extends JLabel {
	
	private static final long serialVersionUID = 978453739962831117L;
	
	public ChannelStringDisplayer() {
		this.setHorizontalAlignment(SwingConstants.LEFT);
		this.setOpaque(true);
	}
	
	public Component renderFor(final JTable table, final String text, final boolean isSelected) {
		
		this.setText("   " + text);
		
		if (isSelected) {
			this.setBackground(table.getSelectionBackground());
			this.setForeground(table.getSelectionForeground());
		} else {
			this.setBackground(table.getBackground());
			this.setForeground(table.getForeground());
		}
		return this;
	}
	
}