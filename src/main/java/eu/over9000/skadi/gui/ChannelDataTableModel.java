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

import javax.swing.table.AbstractTableModel;

import eu.over9000.skadi.channel.Channel;
import eu.over9000.skadi.channel.ChannelManager;
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
		return ChannelManager.getInstance().getChannels().size();
	}
	
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		final Channel rowChannel = ChannelManager.getInstance().getChannels().get(rowIndex);
		
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
		this.fireTableRowsUpdated(0, ChannelManager.getInstance().getChannels().size() - 1);
	}
	
	public void handleDelete(final Channel channel) {
		this.fireTableRowsDeleted(0, ChannelManager.getInstance().getChannels().size() - 1);
	}
	
	public void handleAdd(final Channel channel) {
		final int index = ChannelManager.getInstance().getChannels().indexOf(channel);
		this.fireTableRowsInserted(index, index);
		
	}
}
