package eu.over9000.skadi.ui.cells;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.util.TimeUtil;

public class UptimeCell extends TableCell<Channel, Long> {

	public UptimeCell() {
		this.setAlignment(Pos.CENTER_RIGHT);
	}

	@Override
	protected void updateItem(final Long item, final boolean empty) {
		super.updateItem(item, empty);
		
		if (empty || item == null) {
			this.setText(null);
			this.setGraphic(null);
		} else {
			this.setText(TimeUtil.getDurationBreakdown(item, false));
		}
		
	}
}