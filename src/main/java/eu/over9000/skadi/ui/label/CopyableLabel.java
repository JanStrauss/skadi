package eu.over9000.skadi.ui.label;

import javafx.geometry.Insets;
import javafx.scene.control.TextField;

public class CopyableLabel extends TextField {
	public CopyableLabel() {
		this.setEditable(false);
		this.getStyleClass().add("copyable-label");
		this.setPadding(new Insets(0, 0, 0, 5));
	}
}
