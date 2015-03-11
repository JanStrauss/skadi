package eu.over9000.skadi.ui.cells;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import eu.over9000.skadi.model.Channel;

public class LiveCell extends TableCell<Channel, Boolean> {

	private final ImageView unknown = new ImageView(new Image(this.getClass().getResourceAsStream("/icons/stream_unknown.png")));
	private final ImageView up = new ImageView(new Image(this.getClass().getResourceAsStream("/icons/stream_up.png")));
	private final ImageView down = new ImageView(new Image(this.getClass().getResourceAsStream("/icons/stream_down.png")));
	
	public LiveCell() {
		this.setAlignment(Pos.CENTER);
	}

	@Override
	protected void updateItem(final Boolean item, final boolean empty) {
		super.updateItem(item, empty);
		
		if (empty) {
			this.setText(null);
			this.setGraphic(null);
		} else if (item == null) {
			this.setGraphic(this.unknown);
		} else {
			this.setGraphic(item ? this.up : this.down);
		}
		
	}
}