package eu.over9000.skadi.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import eu.over9000.skadi.util.TimeUtil;

public class ChannelNumberDisplayer extends JLabel {
	
	private static final long serialVersionUID = 978453739962831117L;
	
	public ChannelNumberDisplayer() {
		this.setHorizontalAlignment(SwingConstants.RIGHT);
		this.setOpaque(true);
	}
	
	public Component renderFor(final JTable table, final Long number, final boolean isSelected,
	        final boolean isUptimeColumn) {
		
		if (isUptimeColumn) {
			this.setText(TimeUtil.getDurationBreakdown(number) + "   ");
		} else {
			this.setText(number + "   ");
		}
		
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