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
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.GridView;
import org.controlsfx.control.SegmentedButton;
import org.controlsfx.control.StatusBar;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import eu.over9000.skadi.handler.ChatHandler;
import eu.over9000.skadi.handler.StreamHandler;
import eu.over9000.skadi.io.PersistenceHandler;
import eu.over9000.skadi.lock.LockWakeupReceiver;
import eu.over9000.skadi.lock.SingleInstanceLock;
import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.model.ChannelStore;
import eu.over9000.skadi.model.StateContainer;
import eu.over9000.skadi.model.StreamQuality;
import eu.over9000.skadi.service.ForcedChannelUpdateService;
import eu.over9000.skadi.service.ImportFollowedService;
import eu.over9000.skadi.service.LivestreamerVersionCheckService;
import eu.over9000.skadi.service.VersionCheckerService;
import eu.over9000.skadi.ui.cells.ChannelGridCell;
import eu.over9000.skadi.ui.cells.LiveCell;
import eu.over9000.skadi.ui.cells.RightAlignedCell;
import eu.over9000.skadi.ui.cells.UptimeCell;
import eu.over9000.skadi.ui.dialogs.SettingsDialog;
import eu.over9000.skadi.ui.tray.Tray;
import eu.over9000.skadi.util.ExecutorServiceAccess;
import eu.over9000.skadi.util.JavaFXUtil;
import eu.over9000.skadi.util.NotificationUtil;
import eu.over9000.skadi.util.StringUtil;

public class MainWindow extends Application implements LockWakeupReceiver {

	public static final int TOOLBAR_HEIGHT = 32;
	private final String darkCSS = getClass().getResource("/styles/dark.css").toExternalForm();
	private ChannelStore channelStore;
	private ChatHandler chatHandler;
	private StreamHandler streamHandler;
	private PersistenceHandler persistenceHandler;
	private StateContainer currentState;
	private ObjectProperty<Channel> detailChannel;
	private SplitPane splitPane;
	private StackPane stackPane;
	private StatusBar statusBar;
	private ChannelDetailPane detailPane;
	private TableView<Channel> table;
	private GridView<Channel> grid;
	private TableColumn<Channel, Boolean> liveCol;
	private TableColumn<Channel, String> nameCol;
	private TableColumn<Channel, String> titleCol;
	private TableColumn<Channel, String> gameCol;
	private TableColumn<Channel, Integer> viewerCol;
	private TableColumn<Channel, Long> uptimeCol;
	private FilteredList<Channel> filteredChannelList;
	private Button add;
	private TextField addName;
	private Button details;
	private Button remove;
	private Button refresh;
	private ToggleButton onlineOnly;
	private ToolBar toolBarL;
	private ToolBar toolBarR;
	private TextField filterText;
	private HandlerControlButton chatAndStreamButton;
	private Stage stage;
	private Tray tray;
	private Scene scene;

	@Override
	public void init() throws Exception {
		persistenceHandler = new PersistenceHandler();
		currentState = persistenceHandler.loadState();
		channelStore = new ChannelStore(persistenceHandler);
		chatHandler = new ChatHandler();
		streamHandler = new StreamHandler(this, channelStore);

		detailChannel = new SimpleObjectProperty<>();
	}

	@Override
	public void start(final Stage stage) throws Exception {
		Platform.setImplicitExit(false);


		this.stage = stage;

		detailPane = new ChannelDetailPane(this);

		final BorderPane bp = new BorderPane();
		splitPane = new SplitPane();
		splitPane.setBorder(Border.EMPTY);
		splitPane.setPadding(Insets.EMPTY);
		stackPane = new StackPane();
		stackPane.setBorder(Border.EMPTY);
		stackPane.setPadding(Insets.EMPTY);
		statusBar = new StatusBar();
		statusBar.setBorder(null);

		filteredChannelList = new FilteredList<>(channelStore.getChannels());

		setupTable();
		setupGrid();

		stackPane.getChildren().add(grid);
		stackPane.getChildren().add(table);

		setupToolbarLeft(stage);
		setupToolbarRight();

		splitPane.getItems().add(stackPane);

		final BorderPane toolbarPane = new BorderPane();

		toolbarPane.setCenter(toolBarL);
		toolbarPane.setRight(toolBarR);

		bp.setTop(toolbarPane);
		bp.setCenter(splitPane);
		bp.setBottom(statusBar);

		scene = new Scene(bp, 1280, 720);
		scene.getStylesheets().add(getClass().getResource("/styles/copyable-label.css").toExternalForm());
		scene.getStylesheets().add(getClass().getResource("/styles/common.css").toExternalForm());

		if (currentState.isUseDarkTheme()) {
			scene.getStylesheets().add(darkCSS);
		}

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
					success = channelStore.addChannel(user, statusBar);
				} else {
					statusBar.setText("dragged url is no twitch stream");
				}
			} else if (d.hasString()) {
				success = channelStore.addChannel(d.getString(), statusBar);
			}
			event.setDropCompleted(success);
			event.consume();

		});

		tray = new Tray(stage);
		NotificationUtil.init();

		stage.setTitle("Skadi");
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/skadi.png")));
		stage.setScene(scene);
		stage.show();

		stage.iconifiedProperty().addListener((obs, oldV, newV) -> {
			if (currentState.isMinimizeToTray()) {
				if (newV) {
					stage.hide();
				}
			}
		});
		stage.setOnCloseRequest(event -> Platform.exit());

		bindColumnWidths();

		final VersionCheckerService versionCheckerService = new VersionCheckerService(stage, statusBar);
		versionCheckerService.start();

		final LivestreamerVersionCheckService livestreamerVersionCheckService = new LivestreamerVersionCheckService(statusBar);
		livestreamerVersionCheckService.start();

		SingleInstanceLock.addReceiver(this);

	}
	
	private void setupGrid() {
		grid = new GridView<>();
		grid.setBorder(Border.EMPTY);
		grid.setPadding(Insets.EMPTY);
		grid.setCellFactory(gridView -> new ChannelGridCell());
		grid.setCellHeight(200);
		grid.setCellWidth(200);
		grid.setHorizontalCellSpacing(5);
		grid.setVerticalCellSpacing(5);

		final SortedList<Channel> sortedChannelList = new SortedList<>(filteredChannelList);
		sortedChannelList.setComparator((channel1, channel2) -> Integer.compare(channel2.getViewer(), channel1.getViewer()));

		grid.setItems(sortedChannelList);

		// TODO grid view with selection model

	}
	
	private void setupToolbarRight() {
		final ToggleButton tbTable = GlyphsDude.createIconToggleButton(FontAwesomeIcon.TABLE, null, null, ContentDisplay.GRAPHIC_ONLY);
		final ToggleButton tbGrid = GlyphsDude.createIconToggleButton(FontAwesomeIcon.TH, null, null, ContentDisplay.GRAPHIC_ONLY);

		tbTable.setTooltip(new Tooltip("Table view"));
		tbGrid.setTooltip(new Tooltip("Grid view"));

		tbTable.setOnAction(event -> {
			table.toFront();
			currentState.setShowGrid(false);
			persistenceHandler.saveState(currentState);
		});

		tbGrid.setOnAction(event -> {
			grid.toFront();
			currentState.setShowGrid(true);
			persistenceHandler.saveState(currentState);
		});

		final SegmentedButton segmentedButton = new SegmentedButton(tbTable, tbGrid);
		final PersistentButtonToggleGroup toggleGroup = new PersistentButtonToggleGroup();
		segmentedButton.setToggleGroup(toggleGroup);
		toolBarR = new ToolBar(new Separator(), segmentedButton);
		toolBarR.setPrefHeight(TOOLBAR_HEIGHT);
		toolBarR.setMinHeight(TOOLBAR_HEIGHT);

		if (currentState.isShowGrid()) {
			tbGrid.setSelected(true);
			grid.toFront();
		} else {
			tbTable.setSelected(true);
			table.toFront();
		}
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		tray.onShutdown();
		ExecutorServiceAccess.onShutdown();
		NotificationUtil.onShutdown();
	}

	private void setupToolbarLeft(final Stage stage) {

		add = GlyphsDude.createIconButton(FontAwesomeIcon.PLUS);
		addName = new TextField();
		addName.setOnAction(event -> add.fire());

		add.setOnAction(event -> {
			final String name = addName.getText().trim();

			if (name.isEmpty()) {
				return;
			}

			final boolean result = channelStore.addChannel(name, statusBar);

			if (result) {
				addName.clear();
			}

		});

		final Button imprt = GlyphsDude.createIconButton(FontAwesomeIcon.DOWNLOAD);
		imprt.setOnAction(event -> {
			final TextInputDialog dialog = new TextInputDialog();
			dialog.initModality(Modality.APPLICATION_MODAL);
			dialog.initOwner(stage);
			dialog.setTitle("Import followed channels");
			dialog.setHeaderText("Import followed channels from Twitch");
			dialog.setGraphic(null);
			dialog.setContentText("Twitch username:");

			dialog.showAndWait().ifPresent(name -> {
				final ImportFollowedService ifs = new ImportFollowedService(channelStore, name, statusBar);
				ifs.start();
			});
		});

		details = GlyphsDude.createIconButton(FontAwesomeIcon.INFO);
		details.setDisable(true);
		details.setOnAction(event -> {
			detailChannel.set(table.getSelectionModel().getSelectedItem());
			if (!splitPane.getItems().contains(detailPane)) {
				splitPane.getItems().add(detailPane);
				doDetailSlide(true);
			}
		});
		details.setTooltip(new Tooltip("Show channel information"));

		remove = GlyphsDude.createIconButton(FontAwesomeIcon.TRASH);
		remove.setDisable(true);
		remove.setOnAction(event -> {
			final Channel candidate = table.getSelectionModel().getSelectedItem();

			final Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.initModality(Modality.APPLICATION_MODAL);
			alert.initOwner(stage);
			alert.setTitle("Delete channel");
			alert.setHeaderText("Delete " + candidate.getName());
			alert.setContentText("Do you really want to delete " + candidate.getName() + "?");

			final Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				channelStore.getChannels().remove(candidate);
				statusBar.setText("Removed channel " + candidate.getName());
			}
		});

		refresh = GlyphsDude.createIconButton(FontAwesomeIcon.REFRESH);
		refresh.setTooltip(new Tooltip("Refresh all channels"));
		refresh.setOnAction(event -> {
			refresh.setDisable(true);
			final ForcedChannelUpdateService service = new ForcedChannelUpdateService(channelStore, statusBar, refresh);
			service.start();
		});

		final Button settings = GlyphsDude.createIconButton(FontAwesomeIcon.COG);
		settings.setTooltip(new Tooltip("Settings"));
		settings.setOnAction(event -> {
			final SettingsDialog dialog = new SettingsDialog();
			dialog.initModality(Modality.APPLICATION_MODAL);
			dialog.initOwner(stage);
			final Optional<StateContainer> result = dialog.showAndWait();
			if (result.isPresent()) {
				persistenceHandler.saveState(result.get());
				checkThemeChange();
			}
		});

		onlineOnly = new ToggleButton(null, GlyphsDude.createIcon(FontAwesomeIcon.VIDEO_CAMERA));
		onlineOnly.setSelected(currentState.isOnlineFilterActive());
		onlineOnly.setTooltip(new Tooltip("Show only live channels"));

		onlineOnly.setOnAction(event -> {
			currentState.setOnlineFilterActive(onlineOnly.isSelected());
			persistenceHandler.saveState(currentState);
			updateFilterPredicate();
		});

		filterText = new TextField();
		filterText.textProperty().addListener((obs, oldV, newV) -> updateFilterPredicate());
		filterText.setTooltip(new Tooltip("Filter channels by name, status and game"));

		toolBarL = new ToolBar();
		toolBarL.getItems().addAll(addName, add, imprt, new Separator(), refresh, settings, new Separator(), onlineOnly, filterText, new Separator(), details, remove);
		toolBarL.setPrefHeight(TOOLBAR_HEIGHT);
		toolBarL.setMinHeight(TOOLBAR_HEIGHT);

		chatAndStreamButton = new HandlerControlButton(chatHandler, streamHandler, table, toolBarL, statusBar);

		updateFilterPredicate();
	}
	
	private void checkThemeChange() {
		final boolean useDark = currentState.isUseDarkTheme();
		final boolean isPresent = scene.getStylesheets().contains(darkCSS);

		if (useDark == isPresent) {
			return;
		}

		if (useDark) {
			scene.getStylesheets().add(darkCSS);
		} else {
			scene.getStylesheets().remove(darkCSS);
		}

	}
	
	private void updateFilterPredicate() {
		filteredChannelList.setPredicate(channel -> {

			boolean isOnlineResult;
			boolean containsTextResult;

			// isOnline returns a Boolean, can be null
			isOnlineResult = !onlineOnly.isSelected() || Boolean.TRUE.equals(channel.isOnline());

			final String filter = filterText.getText().trim();
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
		table = new TableView<>();
		table.setBorder(Border.EMPTY);
		table.setPadding(Insets.EMPTY);

		liveCol = new TableColumn<>("Live");
		liveCol.setCellValueFactory(p -> p.getValue().onlineProperty());
		liveCol.setSortType(SortType.DESCENDING);
		liveCol.setCellFactory(p -> new LiveCell());

		nameCol = new TableColumn<>("Channel");
		nameCol.setCellValueFactory(p -> p.getValue().nameProperty());

		titleCol = new TableColumn<>("Status");
		titleCol.setCellValueFactory(p -> p.getValue().titleProperty());

		gameCol = new TableColumn<>("Game");
		gameCol.setCellValueFactory(p -> p.getValue().gameProperty());

		viewerCol = new TableColumn<>("Viewer");
		viewerCol.setCellValueFactory(p -> p.getValue().viewerProperty().asObject());
		viewerCol.setSortType(SortType.DESCENDING);
		viewerCol.setCellFactory(p -> new RightAlignedCell<>());

		uptimeCol = new TableColumn<>("Uptime");
		uptimeCol.setCellValueFactory((p) -> p.getValue().uptimeProperty().asObject());
		uptimeCol.setCellFactory(p -> new UptimeCell());

		table.setPlaceholder(new Label("no channels added/matching the filters"));

		table.getColumns().add(liveCol);
		table.getColumns().add(nameCol);
		table.getColumns().add(titleCol);
		table.getColumns().add(gameCol);
		table.getColumns().add(viewerCol);
		table.getColumns().add(uptimeCol);

		table.getSortOrder().add(liveCol);
		table.getSortOrder().add(viewerCol);
		table.getSortOrder().add(nameCol);


		final SortedList<Channel> sortedChannelList = new SortedList<>(filteredChannelList);
		sortedChannelList.comparatorProperty().bind(table.comparatorProperty());

		table.setItems(sortedChannelList);

		table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
			details.setDisable(newV == null);
			remove.setDisable(newV == null);
			chatAndStreamButton.setDisable(newV == null);
			chatAndStreamButton.resetQualities();
			if ((newV == null) && splitPane.getItems().contains(detailPane)) {
				doDetailSlide(false);
			}

		});

		table.setOnMousePressed(event -> {
			if (table.getSelectionModel().getSelectedItem() == null) {
				return;
			}

			if (event.isMiddleButtonDown()) {
				streamHandler.openStream(table.getSelectionModel().getSelectedItem(), StreamQuality.getBestQuality());

			} else if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
				detailChannel.set(table.getSelectionModel().getSelectedItem());
				if (!splitPane.getItems().contains(detailPane)) {
					splitPane.getItems().add(detailPane);
					doDetailSlide(true);
				}

			}
		});
	}

	private void bindColumnWidths() {
		final ScrollBar tsb = JavaFXUtil.getVerticalScrollbar(table);
		final ReadOnlyDoubleProperty sbw = tsb.widthProperty();
		final DoubleBinding tcw = table.widthProperty().subtract(sbw);

		liveCol.prefWidthProperty().bind(tcw.multiply(0.05));
		nameCol.prefWidthProperty().bind(tcw.multiply(0.15));
		titleCol.prefWidthProperty().bind(tcw.multiply(0.4));
		gameCol.prefWidthProperty().bind(tcw.multiply(0.2));
		viewerCol.prefWidthProperty().bind(tcw.multiply(0.075));
		uptimeCol.prefWidthProperty().bind(tcw.multiply(0.125));

	}

	public void doDetailSlide(final boolean doOpen) {

		final KeyValue positionKeyValue = new KeyValue(splitPane.getDividers().get(0).positionProperty(), doOpen ? 0.15 : 1);
		final KeyValue opacityKeyValue = new KeyValue(detailPane.opacityProperty(), doOpen ? 1 : 0);
		final KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.1), positionKeyValue, opacityKeyValue);
		final Timeline timeline = new Timeline(keyFrame);
		timeline.setOnFinished(evt -> {
			if (!doOpen) {
				splitPane.getItems().remove(detailPane);
				detailPane.setOpacity(1);
			}
		});
		timeline.play();
	}

	public ObjectProperty<Channel> getDetailChannel() {
		return detailChannel;
	}

	@Override
	public void onWakeupReceived() {
		Platform.runLater(() -> {
			statusBar.setText("Wakeup received");
			stage.show();
			stage.setIconified(false);
			stage.toFront();
		});
	}

	public void updateStatusText(final String text) {
		statusBar.setText(text);
	}
}
