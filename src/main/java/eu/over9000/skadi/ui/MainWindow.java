/*
 * The MIT License (MIT)
 *
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
package eu.over9000.skadi.ui;

import java.util.Optional;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.StatusBar;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import eu.over9000.skadi.handler.ChannelHandler;
import eu.over9000.skadi.handler.ChatHandler;
import eu.over9000.skadi.handler.StreamHandler;
import eu.over9000.skadi.io.PersistenceHandler;
import eu.over9000.skadi.lock.LockWakeupReceiver;
import eu.over9000.skadi.lock.SingleInstanceLock;
import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.model.StateContainer;
import eu.over9000.skadi.service.ForcedChannelUpdateService;
import eu.over9000.skadi.service.ImportFollowedService;
import eu.over9000.skadi.service.VersionCheckerService;
import eu.over9000.skadi.ui.cells.LiveCell;
import eu.over9000.skadi.ui.cells.RightAlignedCell;
import eu.over9000.skadi.ui.cells.UptimeCell;
import eu.over9000.skadi.ui.tray.Tray;
import eu.over9000.skadi.util.JavaFXUtil;
import eu.over9000.skadi.util.NotificationUtil;
import eu.over9000.skadi.util.StringUtil;

public class MainWindow extends Application implements LockWakeupReceiver {

	private ChannelHandler channelHandler;
	private ChatHandler chatHandler;
	private StreamHandler streamHandler;
	private PersistenceHandler persistenceHandler;
	private StateContainer currentState;

	private ObjectProperty<Channel> detailChannel;

	private BorderPane bp;
	private SplitPane sp;
	private StatusBar sb;

	private ChannelDetailPane detailPane;
	private TableView<Channel> table;
	private TableColumn<Channel, Boolean> liveCol;
	private TableColumn<Channel, String> nameCol;
	private TableColumn<Channel, String> titleCol;
	private TableColumn<Channel, String> gameCol;
	private TableColumn<Channel, Integer> viewerCol;
	private TableColumn<Channel, Long> uptimeCol;
	private SortedList<Channel> sortedChannelList;
	private FilteredList<Channel> filteredChannelList;
	private Button add;
	private TextField addName;
	private Button imprt;
	private Button details;
	private Button remove;
	private Button refresh;
	private Button settings;
	private ToggleButton onlineOnly;
	private ToolBar tb;
	private TextField filterText;
	private HandlerControlButton chatAndStreamButton;

	private Stage stage;

	private Tray tray;

	@Override
	public void init() throws Exception {
		this.persistenceHandler = new PersistenceHandler();
		this.currentState = this.persistenceHandler.loadState();
		this.channelHandler = new ChannelHandler(this.persistenceHandler, this.currentState);
		this.chatHandler = new ChatHandler(this.currentState);
		this.streamHandler = new StreamHandler(this.currentState, this.channelHandler);

		this.detailChannel = new SimpleObjectProperty<>();
	}

	@Override
	public void start(final Stage stage) throws Exception {
		Platform.setImplicitExit(false);

		this.stage = stage;

		this.detailPane = new ChannelDetailPane(this);

		this.bp = new BorderPane();
		this.sp = new SplitPane();
		this.sb = new StatusBar();

		this.setupTable();
		this.setupToolbar(stage);

		this.sp.getItems().add(this.table);

		this.bp.setTop(this.tb);
		this.bp.setCenter(this.sp);
		this.bp.setBottom(this.sb);

		final Scene scene = new Scene(this.bp, 1280, 720);
		scene.getStylesheets().add(this.getClass().getResource("/styles/copyable-label.css").toExternalForm());
		scene.setOnDragOver(event -> {
			final Dragboard d = event.getDragboard();
			if (d.hasUrl() || d.hasString()) {
				event.acceptTransferModes(TransferMode.COPY);
			} else {
				event.consume();
			}
		});
		scene.setOnDragDropped(event -> {
			final Dragboard d = event.getDragboard();
			boolean success = false;
			if (d.hasUrl()) {
				final String user = StringUtil.extractUsernameFromURL(d.getUrl());
				if (user != null) {
					success = this.channelHandler.addChannel(user, this.sb);
				} else {
					this.sb.setText("dragged url is no twitch stream");
				}
			} else if (d.hasString()) {
				success = this.channelHandler.addChannel(d.getString(), this.sb);
			}
			event.setDropCompleted(success);
			event.consume();

		});

		stage.setTitle("Skadi");
		stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/icons/skadi.png")));
		stage.setScene(scene);
		stage.show();

		stage.iconifiedProperty().addListener((obs, oldV, newV) -> {
			if (this.currentState.isMinimizeToTray()) {
				if (newV) {
					stage.hide();
				}
			}
		});
		stage.setOnCloseRequest(event -> {
			Platform.exit();
		});

		this.tray = new Tray(stage, this.currentState);

		this.bindColumnWidths();

		final VersionCheckerService versionCheckerService = new VersionCheckerService(stage, this.sb);
		versionCheckerService.start();

		SingleInstanceLock.addReceiver(this);

	}

	@Override
	public void stop() throws Exception {
		super.stop();

		this.streamHandler.onShutdown();
		this.tray.onShutdown();
		ForcedChannelUpdateService.onShutdown();
		NotificationUtil.onShutdown();
	}

	private void setupToolbar(final Stage stage) {

		this.add = GlyphsDude.createIconButton(FontAwesomeIcons.PLUS);
		this.addName = new TextField();
		this.addName.setOnAction(event -> this.add.fire());

		this.add.setOnAction(event -> {
			final String name = this.addName.getText().trim();

			if (name.isEmpty()) {
				return;
			}

			final boolean result = this.channelHandler.addChannel(name, this.sb);

			if (result) {
				this.addName.clear();
			}

		});

		this.imprt = GlyphsDude.createIconButton(FontAwesomeIcons.DOWNLOAD);
		this.imprt.setOnAction(event -> {
			final TextInputDialog dialog = new TextInputDialog();
			dialog.initModality(Modality.APPLICATION_MODAL);
			dialog.initOwner(stage);
			dialog.setTitle("Import followed channels");
			dialog.setHeaderText("Import followed channels from Twitch");
			dialog.setGraphic(null);
			dialog.setContentText("Twitch username:");

			dialog.showAndWait().ifPresent(name -> {
				final ImportFollowedService ifs = new ImportFollowedService(this.channelHandler, name, this.sb);
				ifs.start();
			});
		});

		this.details = GlyphsDude.createIconButton(FontAwesomeIcons.INFO);
		this.details.setDisable(true);
		this.details.setOnAction(event -> {
			this.detailChannel.set(this.table.getSelectionModel().getSelectedItem());
			if (!this.sp.getItems().contains(this.detailPane)) {
				this.sp.getItems().add(this.detailPane);
				this.doDetailSlide(true);
			}
		});
		this.details.setTooltip(new Tooltip("Show channel information"));

		this.remove = GlyphsDude.createIconButton(FontAwesomeIcons.TRASH);
		this.remove.setDisable(true);
		this.remove.setOnAction(event -> {
			final Channel candidate = this.table.getSelectionModel().getSelectedItem();

			final Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.initModality(Modality.APPLICATION_MODAL);
			alert.initOwner(stage);
			alert.setTitle("Delete channel");
			alert.setHeaderText("Delete " + candidate.getName());
			alert.setContentText("Do you really want to delete " + candidate.getName() + "?");

			final Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				this.channelHandler.getChannels().remove(candidate);
				this.sb.setText("Removed channel " + candidate.getName());
			}
		});

		this.refresh = GlyphsDude.createIconButton(FontAwesomeIcons.REFRESH);
		this.refresh.setTooltip(new Tooltip("Refresh all channels"));
		this.refresh.setOnAction(event -> {
			this.refresh.setDisable(true);
			final ForcedChannelUpdateService service = new ForcedChannelUpdateService(this.channelHandler, this.sb,
					this.refresh);
			service.start();
		});

		this.settings = GlyphsDude.createIconButton(FontAwesomeIcons.COG);
		this.settings.setTooltip(new Tooltip("Settings"));
		this.settings.setOnAction(event -> {
			final SettingsDialog dialog = new SettingsDialog(this.currentState);
			dialog.initModality(Modality.APPLICATION_MODAL);
			dialog.initOwner(stage);
			final Optional<StateContainer> result = dialog.showAndWait();
			if (result.isPresent()) {
				this.persistenceHandler.saveState(result.get());
			}
		});

		this.onlineOnly = new ToggleButton("Live", GlyphsDude.createIcon(FontAwesomeIcons.FILTER));
		this.onlineOnly.setSelected(this.currentState.isOnlineFilterActive());

		this.onlineOnly.setOnAction(event -> {
			this.currentState.setOnlineFilterActive(this.onlineOnly.isSelected());
			this.persistenceHandler.saveState(this.currentState);
			this.updateFilterPredicate();
		});

		this.filterText = new TextField();
		this.filterText.textProperty().addListener((obs, oldV, newV) -> this.updateFilterPredicate());
		this.filterText.setTooltip(new Tooltip("Filter channels by name, status and game"));

		this.tb = new ToolBar();
		this.tb.getItems().addAll(this.addName, this.add, this.imprt, new Separator(), this.refresh, this.settings,
				new Separator(), this.onlineOnly, this.filterText, new Separator(), this.details, this.remove);

		this.chatAndStreamButton = new HandlerControlButton(this.chatHandler, this.streamHandler, this.table, this.tb,
				this.sb);

		this.updateFilterPredicate();
	}

	private void updateFilterPredicate() {
		this.filteredChannelList.setPredicate(channel -> {

			boolean isOnlineResult;
			boolean containsTextResult;

			// isOnline returns a Boolean, can be null
			isOnlineResult = !this.onlineOnly.isSelected() || Boolean.TRUE.equals(channel.isOnline());

			final String filter = this.filterText.getText().trim();
			if (filter.isEmpty()) {
				containsTextResult = true;
			} else {
				final boolean nameContains = StringUtils.containsIgnoreCase(channel.getName(), filter);
				final boolean gameContains = StringUtils.containsIgnoreCase(channel.getGame(), filter);
				final boolean titleContains = StringUtils.containsIgnoreCase(channel.getTitle(), filter);
				containsTextResult = nameContains || gameContains || titleContains;
			}

			return isOnlineResult && containsTextResult;
		});
	}

	private void setupTable() {
		this.table = new TableView<>();

		this.liveCol = new TableColumn<>("Live");
		this.liveCol.setCellValueFactory(p -> p.getValue().onlineProperty());
		this.liveCol.setSortType(SortType.DESCENDING);
		this.liveCol.setCellFactory(p -> new LiveCell());

		this.nameCol = new TableColumn<>("Channel");
		this.nameCol.setCellValueFactory(p -> p.getValue().nameProperty());

		this.titleCol = new TableColumn<>("Status");
		this.titleCol.setCellValueFactory(p -> p.getValue().titleProperty());

		this.gameCol = new TableColumn<Channel, String>("Game");
		this.gameCol.setCellValueFactory(p -> p.getValue().gameProperty());

		this.viewerCol = new TableColumn<>("Viewer");
		this.viewerCol.setCellValueFactory(p -> p.getValue().viewerProperty().asObject());
		this.viewerCol.setSortType(SortType.DESCENDING);
		this.viewerCol.setCellFactory(p -> new RightAlignedCell<Integer>());

		this.uptimeCol = new TableColumn<>("Uptime");
		this.uptimeCol.setCellValueFactory((p) -> p.getValue().uptimeProperty().asObject());
		this.uptimeCol.setCellFactory(p -> new UptimeCell());

		this.table.setPlaceholder(new Label("no channels added/matching the filters"));

		this.table.getColumns().add(this.liveCol);
		this.table.getColumns().add(this.nameCol);
		this.table.getColumns().add(this.titleCol);
		this.table.getColumns().add(this.gameCol);
		this.table.getColumns().add(this.viewerCol);
		this.table.getColumns().add(this.uptimeCol);

		this.table.getSortOrder().add(this.liveCol);
		this.table.getSortOrder().add(this.viewerCol);
		this.table.getSortOrder().add(this.nameCol);

		this.filteredChannelList = new FilteredList<Channel>(this.channelHandler.getChannels());
		this.sortedChannelList = new SortedList<>(this.filteredChannelList);
		this.sortedChannelList.comparatorProperty().bind(this.table.comparatorProperty());

		this.table.setItems(this.sortedChannelList);

		this.table.setRowFactory(tv -> {
			final TableRow<Channel> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if ((event.getButton() == MouseButton.PRIMARY) && (event.getClickCount() == 2) && !row.isEmpty()) {
					this.detailChannel.set(row.getItem());
					if (!this.sp.getItems().contains(this.detailPane)) {
						this.sp.getItems().add(this.detailPane);
						this.doDetailSlide(true);
					}
				}
			});
			return row;
		});
		this.table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {

			this.details.setDisable(newV == null);
			this.remove.setDisable(newV == null);
			this.chatAndStreamButton.setDisable(newV == null);
			this.chatAndStreamButton.resetQualities();
			if ((newV == null) && this.sp.getItems().contains(this.detailPane)) {
				this.doDetailSlide(false);
			}

		});
	}

	private void bindColumnWidths() {
		final ScrollBar tsb = JavaFXUtil.getVerticalScrollbar(this.table);
		final ReadOnlyDoubleProperty sbw = tsb.widthProperty();
		final DoubleBinding tcw = this.table.widthProperty().subtract(sbw);

		this.liveCol.prefWidthProperty().bind(tcw.multiply(0.05));
		this.nameCol.prefWidthProperty().bind(tcw.multiply(0.15));
		this.titleCol.prefWidthProperty().bind(tcw.multiply(0.4));
		this.gameCol.prefWidthProperty().bind(tcw.multiply(0.2));
		this.viewerCol.prefWidthProperty().bind(tcw.multiply(0.075));
		this.uptimeCol.prefWidthProperty().bind(tcw.multiply(0.125));
	}

	public void doDetailSlide(final boolean doOpen) {

		final KeyValue positionKeyValue = new KeyValue(this.sp.getDividers().get(0).positionProperty(), doOpen ? 0.15
				: 1);
		final KeyValue opacityKeyValue = new KeyValue(this.detailPane.opacityProperty(), doOpen ? 1 : 0);
		final KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.1), positionKeyValue, opacityKeyValue);
		final Timeline timeline = new Timeline(keyFrame);
		timeline.setOnFinished(evt -> {
			if (!doOpen) {
				MainWindow.this.sp.getItems().remove(MainWindow.this.detailPane);
				MainWindow.this.detailPane.setOpacity(1);
			}
		});
		timeline.play();
	}

	public StateContainer getCurrentState() {
		return this.currentState;
	}

	public ObjectProperty<Channel> getDetailChannel() {
		return this.detailChannel;
	}

	public HandlerControlButton getChatAndStreamButton() {
		return this.chatAndStreamButton;
	}

	@Override
	public void onWakeupReceived() {
		Platform.runLater(() -> {
			this.sb.setText("Wakeup received");
			this.stage.show();
			this.stage.setIconified(false);
			this.stage.toFront();
		});
	}
}
