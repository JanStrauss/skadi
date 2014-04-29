package eu.over9000.skadi.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import eu.over9000.skadi.channel.ChannelInstance;

public class ChannelDataListRenderer implements ListCellRenderer<ChannelInstance> {
	
	private final ChannelDataListDisplayer displayer = new ChannelDataListDisplayer();
	
	@Override
	public Component getListCellRendererComponent(final JList<? extends ChannelInstance> list,
	        final ChannelInstance value, final int index, final boolean isSelected, final boolean cellHasFocus) {
		return this.displayer.renderFor(list, value, isSelected);
	}
	
	private class ChannelDataListDisplayer extends JPanel {
		
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = 3574128645388761068L;
		
		private final JLabel labelURL;
		
		public ChannelDataListDisplayer() {
			this.labelURL = new JLabel();
			
			this.add(this.labelURL);
		}
		
		public Component renderFor(final JList<? extends ChannelInstance> list, final ChannelInstance value,
		        final boolean isSelected) {
			this.labelURL.setText(value.getURL());
			
			if (isSelected) {
				this.setBackground(list.getSelectionBackground());
				this.setForeground(list.getSelectionForeground());
			} else {
				this.setBackground(list.getBackground());
				this.setForeground(list.getForeground());
			}
			return this;
		}
		
	}
}
