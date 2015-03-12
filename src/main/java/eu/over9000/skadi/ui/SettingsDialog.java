package eu.over9000.skadi.ui;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import eu.over9000.skadi.model.StateContainer;

public class SettingsDialog extends Dialog<StateContainer> {
	
	public SettingsDialog(final StateContainer state) {

		this.setTitle("Skadi settings");
		this.setHeaderText(null);
		this.setGraphic(null);
		
		final ButtonType saveButtonType = new ButtonType("Save", ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

		final Label lbLivestreamer = new Label("Livestreamer executable");
		final TextField tfLivestreamer = new TextField(state.getExecutableLivestreamer());
		tfLivestreamer.setPrefColumnCount(25);

		final Label lbLVC = new Label("VLC executable");
		final TextField tfVLC = new TextField(state.getExecutableVLC());
		tfVLC.setPrefColumnCount(25);

		final Label lbChrome = new Label("Chrome executable");
		final TextField tfChrome = new TextField(state.getExecutableChrome());
		tfChrome.setPrefColumnCount(25);

		final CheckBox cbShowNotifications = new CheckBox("Show notifications");
		cbShowNotifications.setSelected(state.isDisplayNotifications());
		final CheckBox cbMinimizeToTray = new CheckBox("Minimize to tray");
		cbMinimizeToTray.setSelected(state.isMinimizeToTray());

		final GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		
		grid.add(lbLivestreamer, 0, 0);
		grid.add(tfLivestreamer, 1, 0);
		grid.add(lbLVC, 0, 1);
		grid.add(tfVLC, 1, 1);
		grid.add(lbChrome, 0, 2);
		grid.add(tfChrome, 1, 2);

		final VBox boxCheckboxes = new VBox(10, cbShowNotifications, cbMinimizeToTray);
		
		final VBox boxContent = new VBox(10, grid, new Separator(), boxCheckboxes);
		
		this.getDialogPane().setContent(boxContent);
		
		this.setResultConverter(btn -> {
			if (btn == saveButtonType) {
				state.setExecutableLivestreamer(tfLivestreamer.getText());
				state.setExecutableVLC(tfVLC.getText());
				state.setExecutableChrome(tfChrome.getText());

				state.setDisplayNotifications(cbShowNotifications.isSelected());
				state.setMinimizeToTray(cbMinimizeToTray.isSelected());
				return state;
			}
			return null;
		});
	}
}
