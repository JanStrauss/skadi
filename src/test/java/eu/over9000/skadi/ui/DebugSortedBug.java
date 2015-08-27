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

package eu.over9000.skadi.ui;

import java.util.Objects;
import java.util.Random;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
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

	private static final int NUM_ENTRIES = 10;
	private final ObservableList<TestEntry> entryList = FXCollections.observableArrayList(c -> new Observable[]{c.nameProperty(), c.onlineProperty()});

	public static void main(final String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(final Stage stage) throws Exception {
		
		for (int i = 0; i < NUM_ENTRIES; i++) {
			final TestEntry entry = new TestEntry("Entry" + i);
			entryList.add(entry);
			final EntryUpdateService updateService = new EntryUpdateService(entry);
			updateService.start();
		}

		final TableView<TestEntry> table = new TableView<>();
		table.setBorder(Border.EMPTY);
		table.setPadding(Insets.EMPTY);

		final TableColumn<TestEntry, TestEntry.EntryState> onlineColumn = new TableColumn<>("State");
		onlineColumn.setCellValueFactory(p -> p.getValue().onlineProperty());

		final TableColumn<TestEntry, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(p -> p.getValue().nameProperty());

		table.getColumns().add(onlineColumn);
		table.getColumns().add(nameColumn);
		
		table.getSortOrder().add(onlineColumn);  // if commented out the bug disappears
		table.getSortOrder().add(nameColumn);

		final FilteredList<TestEntry> filteredList = entryList.filtered(c -> TestEntry.EntryState.ONLINE == c.getOnline());
		final SortedList<TestEntry> sortedList = new SortedList<>(filteredList);
		sortedList.comparatorProperty().bind(table.comparatorProperty());
		table.setItems(sortedList);

		final Scene scene = new Scene(table, 800, 600);
		stage.setScene(scene);
		stage.show();
	}

	private static class TestEntry {

		private final StringProperty name;
		private final ObjectProperty<EntryState> online;

		public TestEntry(final String channelName) {
			name = new ReadOnlyStringWrapper(channelName);
			online = new SimpleObjectProperty<>(EntryState.UNKNOWN);
		}

		public String getName() {
			return name.get();
		}

		public StringProperty nameProperty() {
			return name;
		}

		public EntryState getOnline() {
			return online.get();
		}

		public void setOnline(final EntryState online) {
			this.online.set(online);
		}

		public ObjectProperty<EntryState> onlineProperty() {
			return online;
		}

		@Override
		public boolean equals(final Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			final TestEntry that = (TestEntry) o;
			return Objects.equals(name, that.name);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name);
		}

		public enum EntryState {UNKNOWN, OFFLINE, ONLINE}

	}

	private class EntryUpdateService extends ScheduledService<Void> {

		private final Random RND = new Random();

		public EntryUpdateService(final TestEntry toUpdate) {
			setPeriod(Duration.seconds(1));
			setOnSucceeded(event -> {
				final TestEntry.EntryState newState = TestEntry.EntryState.values()[RND.nextInt(TestEntry.EntryState.values().length)];
				toUpdate.setOnline(newState);
			});
		}

		@Override
		protected Task<Void> createTask() {
			return new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					Thread.sleep(RND.nextInt(100)); // simulate REST request
					return null;
				}
			};
		}
	}
}
