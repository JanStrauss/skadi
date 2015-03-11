package eu.over9000.skadi.ui;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import eu.over9000.skadi.model.Channel;

public class RightAlignedCell<T> extends TableCell<Channel, T> {
	public RightAlignedCell() {
		this.setAlignment(Pos.CENTER_RIGHT);
	}

	@Override
	protected void updateItem(final T item, final boolean empty) {
		super.updateItem(item, empty);
		
		if (empty || item == null) {
			this.setText(null);
			this.setGraphic(null);
		} else {
			this.setText(item.toString());
		}
		
	}
}