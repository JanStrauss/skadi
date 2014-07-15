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
import javax.swing.SwingConstants;

import eu.over9000.skadi.util.TimeUtil;

/**
 * Used by the {@link ChannelDataCellRenderer} to render number columns.
 * 
 * @author Jan Strauß
 * 
 */
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
