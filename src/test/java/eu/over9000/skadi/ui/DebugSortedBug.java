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
			final DummyChannel channel = new DummyChannel("DummyChannel" + i);
			channels.add(channel);
			final DummyChannelUpdateService updateService = new DummyChannelUpdateService(channel);
			serviceMap.put(channel, updateService);
			updateService.start();
		}

		final TableView<DummyChannel> table = new TableView<>();
		table.setBorder(Border.EMPTY);
		table.setPadding(Insets.EMPTY);

		final TableColumn<DummyChannel, DummyChannel.StreamState> onlineColumn = new TableColumn<>("Live");
		onlineColumn.setCellValueFactory(p -> p.getValue().onlineProperty());
		onlineColumn.setSortType(TableColumn.SortType.DESCENDING);

		final TableColumn<DummyChannel, String> nameColumn = new TableColumn<>("Channel");
		nameColumn.setCellValueFactory(p -> p.getValue().nameProperty());
		
		final TableColumn<DummyChannel, Integer> viewerColumn = new TableColumn<>("Viewer");
		viewerColumn.setCellValueFactory(p -> p.getValue().viewerProperty().asObject());
		viewerColumn.setSortType(TableColumn.SortType.DESCENDING);

		table.getColumns().add(onlineColumn);
		table.getColumns().add(nameColumn);
		table.getColumns().add(viewerColumn);
		
		table.getSortOrder().add(onlineColumn);
		table.getSortOrder().add(viewerColumn);
		table.getSortOrder().add(nameColumn);

		final FilteredList<DummyChannel> filteredList = channels.filtered(c -> DummyChannel.StreamState.ONLINE == c.getOnline());
		final SortedList<DummyChannel> sortedList = new SortedList<>(filteredList);
		sortedList.comparatorProperty().bind(table.comparatorProperty());
		table.setItems(sortedList);

		final Scene scene = new Scene(table, 800, 600);
		stage.setScene(scene);
		stage.show();
	}

	private static class DummyChannel {

		private final StringProperty name;
		private final ObjectProperty<StreamState> online;
		private final IntegerProperty viewer;

		public DummyChannel(final String channelName) {
			name = new ReadOnlyStringWrapper(channelName);
			online = new SimpleObjectProperty<>(StreamState.UNKNOWN);
			viewer = new SimpleIntegerProperty(0);
		}

		public String getName() {
			return name.get();
		}

		public StringProperty nameProperty() {
			return name;
		}

		public StreamState getOnline() {
			return online.get();
		}

		public void setOnline(final StreamState online) {
			this.online.set(online);
		}

		public ObjectProperty<StreamState> onlineProperty() {
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

		public enum StreamState {UNKNOWN, OFFLINE, ONLINE}
	}

	private class DummyChannelUpdateService extends ScheduledService<Void> {

		private final Random RND = new Random();

		public DummyChannelUpdateService(final DummyChannel toUpdate) {
			setPeriod(Duration.seconds(1));
			setOnSucceeded(event -> {

				boolean online = RND.nextBoolean();
				toUpdate.setOnline(online ? DummyChannel.StreamState.ONLINE : DummyChannel.StreamState.OFFLINE);
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
}
