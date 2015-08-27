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

package eu.over9000.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Border;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Created by Jan on 27.08.2015.
 */
public class DebugSortedBug extends Application {

	private static final int NUM_CHANNELS = 25;
	private final ObservableList<DummyChannel> channels = FXCollections.observableArrayList(c -> new Observable[]{c.nameProperty(), c.onlineProperty(), c.viewerProperty()});
	private final Map<DummyChannel, DummyChannelUpdateService> serviceMap = new HashMap<>();

	public static void main(final String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(final Stage stage) throws Exception {
		
		for (int i = 0; i < NUM_CHANNELS; i++) {
			final DummyChannel channel = new DummyChannel("Dummy Channel " + i);
			channels.add(channel);
			final DummyChannelUpdateService updateService = new DummyChannelUpdateService(channel);
			serviceMap.put(channel, updateService);
			updateService.start();
		}


		final TableView<DummyChannel> table = new TableView<>();
		table.setBorder(Border.EMPTY);
		table.setPadding(Insets.EMPTY);

		final TableColumn<DummyChannel, Boolean> liveCol = new TableColumn<>("Live");
		liveCol.setCellValueFactory(p -> p.getValue().onlineProperty());
		liveCol.setSortType(TableColumn.SortType.DESCENDING);
		//liveCol.setCellFactory(p -> new LiveCell());
		//		liveCol.setComparator((o1, o2) -> {
		//
		//			if (o1 == null && o2 == null) {
		//				return 0;
		//			} else if (o1 != null && o2 == null) {
		//				return 1;
		//			} else if (o1 == null && o2 != null) {
		//				return -1;
		//			} else {
		//				return Boolean.compare(o1, o2);
		//			}
		//		});

		final TableColumn<DummyChannel, String> nameCol = new TableColumn<>("Channel");
		nameCol.setCellValueFactory(p -> p.getValue().nameProperty());
		//		nameCol.setComparator((o1, o2) -> {
		//			if (o1 == null && o2 == null) {
		//				return 0;
		//			} else if (o1 != null && o2 == null) {
		//				return 1;
		//			} else if (o1 == null && o2 != null) {
		//				return -1;
		//			} else {
		//				return o1.compareTo(o2);
		//			}
		//		});
		
		
		final TableColumn<DummyChannel, Integer> viewerCol = new TableColumn<>("Viewer");
		viewerCol.setCellValueFactory(p -> p.getValue().viewerProperty().asObject());
		viewerCol.setSortType(TableColumn.SortType.DESCENDING);
		//viewerCol.setCellFactory(p -> new RightAlignedCell<>());


		table.getColumns().add(liveCol);
		table.getColumns().add(nameCol);
		table.getColumns().add(viewerCol);

		table.getSortOrder().add(liveCol);
		table.getSortOrder().add(viewerCol);
		table.getSortOrder().add(nameCol);

		final SortedList<DummyChannel> sortedChannelList = new SortedList<>(channels);
		final FilteredList<DummyChannel> filteredList = channels.filtered(DummyChannel::getOnline);
		sortedChannelList.comparatorProperty().bind(table.comparatorProperty());

		table.setItems(filteredList);
		final Scene scene = new Scene(table, 400, 300);

		stage.setTitle("My JavaFX Application");
		stage.setScene(scene);
		stage.show();
	}

	private class DummyChannelUpdateService extends ScheduledService<Void> {

		private final Random RND = new Random();

		public DummyChannelUpdateService(final DummyChannel toUpdate) {
			setPeriod(Duration.seconds(1));
			setOnSucceeded(event -> {

				boolean online = RND.nextBoolean();
				toUpdate.setOnline(online);
				toUpdate.setViewer(online ? RND.nextInt(20000) : 0);
			});
		}

		@Override
		protected Task<Void> createTask() {
			return new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					Thread.sleep(RND.nextInt(100));
					return null;
				}
			};
		}
	}

	private class DummyChannel {
		private final StringProperty name;
		private final BooleanProperty online;
		private final IntegerProperty viewer;

		public DummyChannel(final String channelName) {
			name = new SimpleStringProperty(channelName);
			online = new SimpleBooleanProperty(false);
			viewer = new SimpleIntegerProperty(0);
		}

		public String getName() {
			return name.get();
		}

		public void setName(final String name) {
			this.name.set(name);
		}

		public StringProperty nameProperty() {
			return name;
		}

		public boolean getOnline() {
			return online.get();
		}

		public void setOnline(final boolean online) {
			this.online.set(online);
		}

		public BooleanProperty onlineProperty() {
			return online;
		}

		public int getViewer() {
			return viewer.get();
		}

		public void setViewer(final int viewer) {
			this.viewer.set(viewer);
		}

		public IntegerProperty viewerProperty() {
			return viewer;
		}

		@Override
		public boolean equals(final Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			final DummyChannel that = (DummyChannel) o;
			return Objects.equals(name, that.name);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name);
		}
	}
}
