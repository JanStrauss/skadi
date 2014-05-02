package eu.over9000.skadi.gui;

import javax.swing.table.AbstractTableModel;

import eu.over9000.skadi.SkadiMain;
import eu.over9000.skadi.channel.ChannelInstance;
import eu.over9000.skadi.util.StringUtil;
import eu.over9000.skadi.util.TimeUtil;

public class ChannelDataTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 5979714625340610316L;
	private static final String[] columnNames = new String[] { "Status", "Channel", "Status", "Game", "Viewers",
	        "Uptime" };
	
	@Override
	public int getColumnCount() {
		return ChannelDataTableModel.columnNames.length;
	}
	
	@Override
	public String getColumnName(final int column) {
		return ChannelDataTableModel.columnNames[column];
	}
	
	@Override
	public boolean isCellEditable(final int row, final int column) {
		return false;
	}
	
	@Override
	public int getRowCount() {
		return SkadiMain.getInstance().getChannels().size();
	}
	
	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		return String.class;
	}
	
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		final ChannelInstance rowChannel = SkadiMain.getInstance().getChannels().values()
		        .toArray(new ChannelInstance[0])[rowIndex];
		
		if (rowChannel.getMetadata() == null) {
			switch (columnIndex) {
				case 0:
					return null;
				case 1:
					return StringUtil.extractChannelName(rowChannel.getURL());
				case 2:
					return "-";
				case 3:
					return "-";
				case 4:
					return "-";
				case 5:
					return "-";
				default:
					return "ERROR";
			}
		} else {
			switch (columnIndex) {
				case 0:
					return rowChannel.getMetadata().isOnline();
				case 1:
					return rowChannel.getMetadata().getStreamer();
				case 2:
					return rowChannel.getMetadata().getTitle();
				case 3:
					return rowChannel.getMetadata().getGame();
				case 4:
					return Integer.toString(rowChannel.getMetadata().getViewers());
				case 5:
					return TimeUtil.getDurationBreakdown(rowChannel.getMetadata().getUptime());
				default:
					return "ERROR";
			}
		}
		
	}
	
	public void handleUpdate() {
		this.fireTableDataChanged();
	}
	
	public ChannelInstance getChannelAt(final int row) {
		return SkadiMain.getInstance().getChannels().values().toArray(new ChannelInstance[0])[row];
	}
}
