package eu.over9000.skadi.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ChannelDataCellRenderer implements TableCellRenderer {
	
	private final ChannelStatusDisplayer displayerStatus = new ChannelStatusDisplayer();
	private final ChannelStringDisplayer displayerString = new ChannelStringDisplayer();
	private final ChannelNumberDisplayer displayerNumber = new ChannelNumberDisplayer();
	
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
				return this.displayerNumber.renderFor(table, (Long) value, isSelected, false);
			case 5:
				return this.displayerNumber.renderFor(table, (Long) value, isSelected, true);
			default:
				break;
		}
		return new JLabel("ERROR");
	}
}
