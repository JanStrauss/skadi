package eu.over9000.skadi.gui;

import javax.swing.table.AbstractTableModel;

import eu.over9000.skadi.SkadiMain;
import eu.over9000.skadi.channel.Channel;
import eu.over9000.skadi.util.StringUtil;

public class ChannelDataTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 5979714625340610316L;
	private static final String[] columnNames = new String[] { "Live", "Channel", "Status", "Game", "Viewers", "Uptime" };
	
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
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		final Channel rowChannel = SkadiMain.getInstance().getChannels().get(rowIndex);
		
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
					return 0L;
				case 5:
					return 0L;
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
					return new Long(rowChannel.getMetadata().getViewers());
				case 5:
					return rowChannel.getMetadata().getUptime();
				default:
					return "ERROR";
			}
		}
		
	}
	
	public void handleUpdate(final Channel channel) {
		this.fireTableRowsUpdated(0, SkadiMain.getInstance().getChannels().size() - 1);
	}
	
	public void handleDelete(final Channel channel) {
		final int index = SkadiMain.getInstance().getChannels().indexOf(channel);
		this.fireTableRowsDeleted(index, index);
		SkadiGUI.getInstance().applyPrefWidth();
	}
	
	public void handleAdd(final Channel channel) {
		final int index = SkadiMain.getInstance().getChannels().indexOf(channel);
		this.fireTableRowsInserted(index, index);
	}
}
