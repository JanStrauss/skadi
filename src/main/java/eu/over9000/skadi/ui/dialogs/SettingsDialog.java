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

import eu.over9000.skadi.io.PersistenceHandler;
import eu.over9000.skadi.model.StateContainer;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;

public class SettingsDialog extends Dialog<StateContainer> {

	public static final String LIVESTREAMER_CONFIG_PATH_WIN = System.getenv("APPDATA") + "\\livestreamer\\";
	private static final Logger LOGGER = LoggerFactory.getLogger(SettingsDialog.class);

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

		final Button btLivestreamerCfg = new Button("Open configuration folder");
		btLivestreamerCfg.setOnAction(event -> {
			try {
				Desktop.getDesktop().open(new File(LIVESTREAMER_CONFIG_PATH_WIN));
			} catch (final Exception e) {
				LOGGER.error("settings dialog: open config folder failed: ", e);
			}
		});

		final Label lbChrome = new Label("Chrome executable");
		final TextField tfChrome = new TextField(state.getExecutableChrome());
		tfChrome.setPrefColumnCount(25);

		final GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);

		grid.add(lbLivestreamer, 0, 0);
		grid.add(tfLivestreamer, 1, 0);
		if (SystemUtils.IS_OS_WINDOWS) {
			grid.add(btLivestreamerCfg, 2, 0);
		}
		grid.add(lbChrome, 0, 1);
		grid.add(tfChrome, 1, 1);

		final CheckBox cbShowNotifications = new CheckBox("Show notifications");
		cbShowNotifications.setSelected(state.isDisplayNotifications());
		final CheckBox cbMinimizeToTray = new CheckBox("Minimize to tray");
		cbMinimizeToTray.setSelected(state.isMinimizeToTray());
		final CheckBox cbDarkTheme = new CheckBox("Use dark Theme");
		cbDarkTheme.setSelected(state.isUseDarkTheme());

		final VBox boxCheckboxes = new VBox(10, cbShowNotifications, cbMinimizeToTray, cbDarkTheme);

		final Button btSkadiLog = new Button("Open Skadi log");
		btSkadiLog.setOnAction(event -> {
			try {
				Desktop.getDesktop().open(new File(PersistenceHandler.PERSISTENCE_DIRECTORY + "skadi.log"));
			} catch (final Exception e) {
				LOGGER.error("settings dialog: open log failed: ", e);
			}
		});

		final VBox boxSkadiLog = new VBox(10, btSkadiLog);

		final VBox boxContent = new VBox(10, grid, new Separator(), boxCheckboxes, new Separator(), boxSkadiLog);

		getDialogPane().setContent(boxContent);

		setResultConverter(btn -> {
			if (btn == saveButtonType) {
				state.setExecutableLivestreamer(tfLivestreamer.getText());
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
