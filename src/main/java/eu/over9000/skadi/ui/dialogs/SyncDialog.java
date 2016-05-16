/*
 * Copyright (c) 2014-2016 s1mpl3x <jan[at]over9000.eu>
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

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import eu.over9000.cathode.data.RootBox;
import eu.over9000.skadi.model.ChannelStore;
import eu.over9000.skadi.service.CheckAuthService;
import eu.over9000.skadi.service.PutFollowedService;
import eu.over9000.skadi.service.RetrieveFollowedService;
import eu.over9000.skadi.ui.StatusBarWrapper;
import eu.over9000.skadi.ui.cells.SyncListCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SyncDialog extends Dialog<Boolean> {


	private final StatusBarWrapper sb;
	private final ChannelStore channelStore;

	private final ObservableList<String> localOnStart = FXCollections.observableArrayList();
	private final ObservableList<String> remoteOnStart = FXCollections.observableArrayList();

	private final ObservableList<String> localWorking = FXCollections.observableArrayList();
	private final ObservableList<String> remoteWorking = FXCollections.observableArrayList();

	private final ListView<String> localChannelView = new ListView<>(new SortedList<>(localWorking, String::compareTo));
	private final ListView<String> remoteChannelView = new ListView<>(new SortedList<>(remoteWorking, String::compareTo));
	private final BorderPane localPane = new BorderPane(new ProgressIndicator());
	private final BorderPane remotePane = new BorderPane(new ProgressIndicator());
	private GridPane contentPane;
	private String user;
	private VBox buttonBar;
	private Button btSelectedToRemote;
	private Button btSelectedToLocal;

	private boolean localChanges = false;

	public SyncDialog(final ChannelStore channelStore, final StatusBarWrapper sb) {
		this.channelStore = channelStore;
		this.sb = sb;

		setTitle("Followed Channels synchronization");
		setHeaderText(null);
		setGraphic(null);

		localChannelView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		remoteChannelView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		BorderPane.setMargin(localChannelView, new Insets(5, 0, 5, 0));
		BorderPane.setMargin(remoteChannelView, new Insets(5, 0, 5, 0));

		localPane.setTop(new Label("Skadi followed channels"));
		remotePane.setTop(new Label("Twitch followed channels"));

		localPane.setBottom(genListButtonBar(localWorking, localOnStart, remoteChannelView));
		remotePane.setBottom(genListButtonBar(remoteWorking, remoteOnStart, localChannelView));

		localPane.setMinSize(250, 600);
		remotePane.setMinSize(250, 600);

		getDialogPane().setContent(getContentPane());

		localChannelView.setCellFactory(param -> new SyncListCell(remoteWorking));
		remoteChannelView.setCellFactory(param -> new SyncListCell(localWorking));

		localChannelView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			btSelectedToRemote.setDisable(newValue == null);
		});

		remoteChannelView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			btSelectedToLocal.setDisable(newValue == null);
		});

		final ButtonType close = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(close, ButtonType.CANCEL);

		setupAuthService();

		setupLocalList();

		setResultConverter(param -> localChanges);

		final Button btOk = (Button) getDialogPane().lookupButton(close);
		btOk.addEventFilter(ActionEvent.ACTION, event -> {

			final List<String> diffLocal = localWorking.stream().filter(c -> !localOnStart.contains(c)).collect(Collectors.toList());
			final List<String> diffRemote = remoteWorking.stream().filter(c -> !remoteOnStart.contains(c)).collect(Collectors.toList());

			final Alert confirmChanges = new Alert(Alert.AlertType.CONFIRMATION, "Add " + diffLocal.size() + " channels to Skadi's list and add " + diffRemote.size() + " to your twitch follows?");

			final Optional<ButtonType> result = confirmChanges.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				performSync(diffLocal, diffRemote);
			} else {
				event.consume();
			}
		});
	}

	private void performSync(final List<String> diffLocal, final List<String> diffRemote) {

		// local
		if (!diffLocal.isEmpty()) {
			channelStore.addChannels(diffLocal, sb);
			localChanges = true;
		}

		//remote
		if (!diffRemote.isEmpty()) {
			final PutFollowedService service = new PutFollowedService(user, diffRemote, sb);
			service.start();
		}

	}

	private HBox genListButtonBar(final ObservableList<String> working, final ObservableList<String> onStart, final ListView<String> otherView) {

		final Button btReset = new Button("Reset");
		btReset.setOnAction(event -> {
			working.clear();
			working.addAll(onStart);
			forceListRefreshOn(otherView);
		});

		final HBox result = new HBox(5, btReset);
		result.setAlignment(Pos.CENTER_RIGHT);
		return result;
	}

	private void setupLocalList() {
		localOnStart.addAll(channelStore.getChannelNames());
		localWorking.addAll(localOnStart);
		localPane.setCenter(localChannelView);
	}

	private void setupRemoteList(final Set<String> channels) {
		remoteOnStart.addAll(channels);
		remoteWorking.addAll(remoteOnStart);
		remotePane.setCenter(remoteChannelView);
		forceListRefreshOn(localChannelView);
	}

	private void setupAuthService() {
		final CheckAuthService authService = new CheckAuthService();
		authService.setOnSucceeded(event -> {
			final RootBox result = (RootBox) event.getSource().getValue();
			if (result.getToken().isValid()) {

				user = result.getToken().getUserName();

				setupRemoteService();

			} else {
				sb.updateStatusText("failed to retrieve username, stopping");
				close();
			}
		});
		authService.setOnFailed(event -> {
			sb.updateStatusText("failed to retrieve username, stopping");
			close();
		});
		authService.start();
	}

	private void setupRemoteService() {
		final RetrieveFollowedService followedService = new RetrieveFollowedService(user);
		followedService.setOnSucceeded(event -> {
			final Set<String> result = followedService.getValue();
			setupRemoteList(result);
		});
		followedService.setOnFailed(event -> {
			sb.updateStatusText("failed to retrieve followed channels from twitch, stopping");
			close();
		});
		followedService.start();
	}


	public GridPane getContentPane() {
		if (contentPane == null) {
			contentPane = new GridPane();

			contentPane.setHgap(10);
			contentPane.setVgap(10);

			getContentPane().add(localPane, 0, 0);
			getContentPane().add(getButtonBar(), 1, 0);
			getContentPane().add(remotePane, 2, 0);
		}
		return contentPane;
	}

	public VBox getButtonBar() {
		if (buttonBar == null) {

			btSelectedToRemote = GlyphsDude.createIconButton(FontAwesomeIcon.ANGLE_RIGHT);
			btSelectedToLocal = GlyphsDude.createIconButton(FontAwesomeIcon.ANGLE_LEFT);
			final Button btAllToRemote = GlyphsDude.createIconButton(FontAwesomeIcon.ANGLE_DOUBLE_RIGHT);
			final Button btAllToLocal = GlyphsDude.createIconButton(FontAwesomeIcon.ANGLE_DOUBLE_LEFT);

			btSelectedToRemote.setDisable(true);
			btSelectedToLocal.setDisable(true);

			btSelectedToRemote.setOnAction(event -> {
				final ObservableList<String> selected = localChannelView.getSelectionModel().getSelectedItems();
				selected.stream().filter(c -> !remoteWorking.contains(c)).forEach(remoteWorking::add);
				forceListRefreshOn(localChannelView);
			});

			btSelectedToLocal.setOnAction(event -> {
				final ObservableList<String> selected = remoteChannelView.getSelectionModel().getSelectedItems();
				selected.stream().filter(c -> !localWorking.contains(c)).forEach(localWorking::add);
				forceListRefreshOn(remoteChannelView);
			});

			btAllToRemote.setOnAction(event -> {
				localWorking.stream().filter(c -> !remoteWorking.contains(c)).forEach(remoteWorking::add);
				forceListRefreshOn(localChannelView);
			});

			btAllToLocal.setOnAction(event -> {
				remoteWorking.stream().filter(c -> !localWorking.contains(c)).forEach(localWorking::add);
				forceListRefreshOn(remoteChannelView);
			});

			btSelectedToRemote.setMinSize(32, 32);
			btSelectedToLocal.setMinSize(32, 32);
			btAllToRemote.setMinSize(32, 32);
			btAllToLocal.setMinSize(32, 32);

			buttonBar = new VBox(10, btSelectedToRemote, btSelectedToLocal, btAllToRemote, btAllToLocal);
			buttonBar.setAlignment(Pos.CENTER);
		}
		return buttonBar;
	}

	private <T> void forceListRefreshOn(final ListView<T> listView) {
		final ObservableList<T> items = listView.<T>getItems();
		listView.<T>setItems(null);
		listView.<T>setItems(items);
	}
}