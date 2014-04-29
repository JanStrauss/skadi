package eu.over9000.skadi.gui;

import javax.swing.AbstractListModel;

import eu.over9000.skadi.SkadiMain;
import eu.over9000.skadi.channel.ChannelInstance;

public class ChannelDataListModel extends AbstractListModel<ChannelInstance> {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 7576558662916716790L;
	
	@Override
	public int getSize() {
		return SkadiMain.getInstance().getChannels().size();
	}
	
	@Override
	public ChannelInstance getElementAt(final int index) {
		return SkadiMain.getInstance().getChannels().values().toArray(new ChannelInstance[0])[index];
	}
	
	public void handleUpdate() {
		this.fireContentsChanged(this, 0, this.getSize());
	}
	
}
