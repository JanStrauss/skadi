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
