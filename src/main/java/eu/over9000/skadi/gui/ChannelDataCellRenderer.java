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

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * {@link TableCellRenderer} for the Channel table.
 * 
 * @author Jan Strauß
 * 
 */
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
