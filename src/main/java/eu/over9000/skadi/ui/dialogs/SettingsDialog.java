/*
 * Copyright (c) 2014-2015 s1mpl3x <jan[at]over9000.eu>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package eu.over9000.skadi.ui.dialogs;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import eu.over9000.skadi.model.StateContainer;

public class SettingsDialog extends Dialog<StateContainer> {

	public SettingsDialog() {
		final StateContainer state = StateContainer.getInstance();

		setTitle("Skadi settings");
		setHeaderText(null);
		setGraphic(null);

		final ButtonType saveButtonType = new ButtonType("Save", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

		final Label lbLivestreamer = new Label("Livestreamer executable");
		final TextField tfLivestreamer = new TextField(state.getExecutableLivestreamer());
		tfLivestreamer.setPrefColumnCount(25);

		final Label lbVideoplayer = new Label("Videoplayer executable");
		final TextField tfVideoplayer = new TextField(state.getExecutableVideoplayer());
		tfVideoplayer.setPrefColumnCount(25);

		final Label lbChrome = new Label("Chrome executable");
		final TextField tfChrome = new TextField(state.getExecutableChrome());
		tfChrome.setPrefColumnCount(25);

		final GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);

		grid.add(lbLivestreamer, 0, 0);
		grid.add(tfLivestreamer, 1, 0);
		grid.add(lbVideoplayer, 0, 1);
		grid.add(tfVideoplayer, 1, 1);
		grid.add(lbChrome, 0, 2);
		grid.add(tfChrome, 1, 2);

		final CheckBox cbShowNotifications = new CheckBox("Show notifications");
		cbShowNotifications.setSelected(state.isDisplayNotifications());
		final CheckBox cbMinimizeToTray = new CheckBox("Minimize to tray");
		cbMinimizeToTray.setSelected(state.isMinimizeToTray());
		final CheckBox cbDarkTheme = new CheckBox("Use dark Theme");
		cbDarkTheme.setSelected(state.isUseDarkTheme());

		final VBox boxCheckboxes = new VBox(10, cbShowNotifications, cbMinimizeToTray, cbDarkTheme);

		final VBox boxContent = new VBox(10, grid, new Separator(), boxCheckboxes);

		getDialogPane().setContent(boxContent);

		setResultConverter(btn -> {
			if (btn == saveButtonType) {
				state.setExecutableLivestreamer(tfLivestreamer.getText());
				state.setExecutableVideoplayer(tfVideoplayer.getText());
				state.setExecutableChrome(tfChrome.getText());

				state.setDisplayNotifications(cbShowNotifications.isSelected());
				state.setMinimizeToTray(cbMinimizeToTray.isSelected());
				state.setUseDarkTheme(cbDarkTheme.isSelected());
				return state;
			}
			return null;
		});
	}
}
