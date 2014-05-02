package eu.over9000.skadi.gui;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import eu.over9000.skadi.channel.Platform;

public class ChannelDataCellRenderer implements TableCellRenderer {
	
	private final ChannelStatusDisplayer displayerStatus = new ChannelStatusDisplayer();
	private final ChannelStringDisplayer displayerString = new ChannelStringDisplayer();
	private final ChannelNumberDisplayer displayerNumber = new ChannelNumberDisplayer();
	
	private class ChannelStatusDisplayer extends JPanel {
		
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = 3574128645388761068L;
		
		private final ImageIcon icon_unknown = new ImageIcon(this.getClass().getResource("/stream_unknown.png"));
		private final ImageIcon icon_offline = new ImageIcon(this.getClass().getResource("/stream_down.png"));
		private final ImageIcon icon_online = new ImageIcon(this.getClass().getResource("/stream_up.png"));
		
		private final JLabel labelStatus;
		
		public ChannelStatusDisplayer() {
			this.labelStatus = new JLabel();
			
			this.add(this.labelStatus);
		}
		
		public Component renderFor(final JTable table, final Boolean online, final boolean isSelected) {
			if (online == null) {
				this.labelStatus.setIcon(this.icon_unknown);
			} else if (online) {
				this.labelStatus.setIcon(this.icon_online);
			} else {
				this.labelStatus.setIcon(this.icon_offline);
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
	
	private class ChannelStringDisplayer extends JLabel {
		
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
	
	private class ChannelNumberDisplayer extends JLabel {
		
		private static final long serialVersionUID = 978453739962831117L;
		
		public ChannelNumberDisplayer() {
			this.setHorizontalAlignment(SwingConstants.RIGHT);
			this.setOpaque(true);
		}
		
		public Component renderFor(final JTable table, final String text, final boolean isSelected) {
			
			this.setText(text + "   ");
			
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
	
	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
	        final boolean hasFocus, final int row, final int column) {
		switch (column) {
			case 0:
				return this.displayerStatus.renderFor(table, (Boolean) value, isSelected);
			case 1:
				return this.displayerString.renderFor(table, (String) value, isSelected);
			case 2:
				return this.displayerString.renderFor(table, (String) value, isSelected);
			case 3:
				return this.displayerString.renderFor(table, (String) value, isSelected);
			case 4:
				return this.displayerNumber.renderFor(table, (String) value, isSelected);
			case 5:
				return this.displayerNumber.renderFor(table, (String) value, isSelected);
			case 6:
				return this.displayerString.renderFor(table, ((Platform) value).name(), isSelected);
			case 7:
				return this.displayerString.renderFor(table, (String) value, isSelected);
			default:
				break;
		}
		return new JLabel("ERROR");
	}
}
