/*
 * Copyright (c) 2014-2016 Jan Strauß <jan[at]over9000.eu>
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

import eu.over9000.cathode.data.RootBox;
import eu.over9000.skadi.io.PersistenceHandler;
import eu.over9000.skadi.model.StateContainer;
import eu.over9000.skadi.service.CheckAuthService;
import eu.over9000.skadi.util.TwitchUtil;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.util.Optional;

public class SettingsDialog extends Dialog<StateContainer> {

	public static final String STREAMLINK_CONFIG_PATH_WIN = System.getenv("APPDATA") + "\\streamlink\\";
	private static final Logger LOGGER = LoggerFactory.getLogger(SettingsDialog.class);

	private final StateContainer state;
	private final PersistenceHandler persistenceHandler;

	private CheckBox cbShowNotifications;
	private CheckBox cbMinimizeToTray;
	private CheckBox cbDarkTheme;
	private Label lbStreamlink;
	private Label lbChrome;
	private Label lbAuthUser;
	private Label valueAuthUser;
	private TextField tfStreamlink;
	private TextField tfChrome;
	private Button btStreamlinkCfg;
	private Button btSkadiLog;
	private Button btChangeAuth;
	private VBox boxSkadiLog;
	private ProgressIndicator pi;
	private GridPane contentPane;

	public SettingsDialog(final StateContainer state, final PersistenceHandler persistenceHandler) {
		this.persistenceHandler = persistenceHandler;
		this.state = state;

		setTitle("Skadi settings");
		setHeaderText(null);
		setGraphic(null);

		final ButtonType saveButtonType = new ButtonType("Save", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

		getDialogPane().setContent(getContentPane());

		setResultConverter(btn -> {
			if (btn == saveButtonType) {
				state.setExecutableStreamlink(tfStreamlink.getText());
				state.setExecutableChrome(tfChrome.getText());

				state.setDisplayNotifications(cbShowNotifications.isSelected());
				state.setMinimizeToTray(cbMinimizeToTray.isSelected());
				state.setUseDarkTheme(cbDarkTheme.isSelected());
				return state;
			}
			return null;
		});
	}

	public CheckBox getCbShowNotifications() {
		if (cbShowNotifications == null) {
			cbShowNotifications = new CheckBox("Show notifications");
			cbShowNotifications.setSelected(state.isDisplayNotifications());
		}
		return cbShowNotifications;
	}

	public CheckBox getCbMinimizeToTray() {
		if (cbMinimizeToTray == null) {
			cbMinimizeToTray = new CheckBox("Minimize to tray");
			cbMinimizeToTray.setSelected(state.isMinimizeToTray());
		}
		return cbMinimizeToTray;
	}

	public CheckBox getCbDarkTheme() {
		if (cbDarkTheme == null) {
			cbDarkTheme = new CheckBox("Use dark Theme");
			cbDarkTheme.setSelected(state.isUseDarkTheme());
		}
		return cbDarkTheme;
	}

	public Label getLbStreamlink() {
		if (lbStreamlink == null) {
			lbStreamlink = new Label("streamlink executable");
		}
		return lbStreamlink;
	}

	public TextField getTfStreamlink() {
		if (tfStreamlink == null) {
			tfStreamlink = new TextField(state.getExecutableStreamlink());
			tfStreamlink.setPrefColumnCount(25);
		}
		return tfStreamlink;
	}

	public Button getBtStreamlinkCfg() {
		if (btStreamlinkCfg == null) {
			btStreamlinkCfg = new Button("Open configuration folder");
			btStreamlinkCfg.setOnAction(event -> {
				try {
					Desktop.getDesktop().open(new File(STREAMLINK_CONFIG_PATH_WIN));
				} catch (final Exception e) {
					LOGGER.error("settings dialog: open config folder failed: ", e);
				}
			});
		}
		return btStreamlinkCfg;
	}

	public Label getLbChrome() {
		if (lbChrome == null) {
			lbChrome = new Label("Chrome executable");
		}
		return lbChrome;
	}

	public TextField getTfChrome() {
		if (tfChrome == null) {
			tfChrome = new TextField(state.getExecutableChrome());
			tfChrome.setPrefColumnCount(25);
		}
		return tfChrome;
	}

	public Button getBtSkadiLog() {
		if (btSkadiLog == null) {
			btSkadiLog = new Button("Open Skadi log");
			btSkadiLog.setOnAction(event -> {
				try {
					Desktop.getDesktop().open(new File(PersistenceHandler.PERSISTENCE_DIRECTORY + "skadi.log"));
				} catch (final Exception e) {
					LOGGER.error("settings dialog: open log failed: ", e);
				}
			});
		}
		return btSkadiLog;
	}

	public VBox getBoxSkadiLog() {
		if (boxSkadiLog == null) {
			boxSkadiLog = new VBox(10, getBtSkadiLog());
		}
		return boxSkadiLog;
	}

	public Label getLbAuthUser() {
		if (lbAuthUser == null) {
			lbAuthUser = new Label("Authorized for user: ");
		}
		return lbAuthUser;
	}

	public ProgressIndicator getPi() {
		if (pi == null) {
			pi = new ProgressIndicator();

			pi.setPrefSize(16, 16);
			pi.setMaxSize(16, 16);
			pi.setMinSize(16, 16);
		}
		return pi;
	}

	public Label getValueAuthUser() {
		if (valueAuthUser == null) {
			valueAuthUser = new Label("", getPi());

			runCheckAuthService();
		}
		return valueAuthUser;
	}

	private void runCheckAuthService() {
		final CheckAuthService service = new CheckAuthService();

		service.setOnSucceeded(event -> {
			final RootBox result = (RootBox) event.getSource().getValue();
			if (result.getToken().isValid()) {
				valueAuthUser.setText(result.getToken().getUserName());

			} else {
				LOGGER.warn("invalid token response: " + result.toString());
				valueAuthUser.setText("none/invalid token");
			}
			valueAuthUser.setGraphic(null);
		});
		service.setOnFailed(event -> {
			valueAuthUser.setText("failed to check auth. see log for details");
			valueAuthUser.setGraphic(null);
		});
		service.start();
	}

	public Button getBtChangeAuth() {
		if (btChangeAuth == null) {
			btChangeAuth = new Button("Change Authorization");
			btChangeAuth.setOnAction(event -> {
				final AuthorizationDialog authDialog = new AuthorizationDialog();

				authDialog.initModality(Modality.APPLICATION_MODAL);
				authDialog.initOwner(getOwner());

				final Optional<String> result = authDialog.showAndWait();
				if (result.isPresent()) {

					if (result.get() != null && !result.get().isEmpty()) {
						state.setAuthToken(result.get());
						persistenceHandler.saveState(state);
						TwitchUtil.init(result.get());

						runCheckAuthService();
					}
				}
			});
		}
		return btChangeAuth;
	}


	public GridPane getContentPane() {
		if (contentPane == null) {
			contentPane = new GridPane();
			contentPane.setHgap(10);
			contentPane.setVgap(10);

			contentPane.add(getLbStreamlink(), 0, 0);
			contentPane.add(getTfStreamlink(), 1, 0);
			if (SystemUtils.IS_OS_WINDOWS) {
				contentPane.add(getBtStreamlinkCfg(), 2, 0);
			}
			contentPane.add(getLbChrome(), 0, 1);
			contentPane.add(getTfChrome(), 1, 1);

			contentPane.add(new Separator(), 0, 2, 3, 1);

			contentPane.add(getCbShowNotifications(), 0, 3);
			contentPane.add(getCbMinimizeToTray(), 0, 4);
			contentPane.add(getCbDarkTheme(), 0, 5);

			contentPane.add(new Separator(), 0, 6, 3, 1);

			contentPane.add(getLbAuthUser(), 0, 7);
			contentPane.add(getValueAuthUser(), 1, 7);
			contentPane.add(getBtChangeAuth(), 2, 7);

			contentPane.add(new Separator(), 0, 8, 3, 1);

			contentPane.add(getBoxSkadiLog(), 0, 9);


		}
		return contentPane;
	}
}
