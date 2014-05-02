package eu.over9000.skadi.gui;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

public class ChannelStatusDisplayer extends JLabel {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3574128645388761068L;
	
	private final ImageIcon icon_unknown = new ImageIcon(this.getClass().getResource("/stream_unknown.png"));
	private final ImageIcon icon_offline = new ImageIcon(this.getClass().getResource("/stream_down.png"));
	private final ImageIcon icon_online = new ImageIcon(this.getClass().getResource("/stream_up.png"));
	
	public ChannelStatusDisplayer() {
		this.setOpaque(true);
		this.setHorizontalAlignment(SwingConstants.CENTER);
	}
	
	public Component renderFor(final JTable table, final Boolean online, final boolean isSelected) {
		if (online == null) {
			this.setIcon(this.icon_unknown);
		} else if (online) {
			this.setIcon(this.icon_online);
		} else {
			this.setIcon(this.icon_offline);
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