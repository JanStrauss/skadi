package eu.over9000.skadi.model;

import java.time.LocalTime;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public class Channel {
	private final StringProperty name;
	private final StringProperty title;
	private final IntegerProperty viewer;
	private final LongProperty uptime;
	private final ObjectProperty<Boolean> online;
	private final StringProperty game;
	private final ListProperty<XYChart.Data<Number, Number>> viewerHistory;
	private final IntegerProperty viewerHistoryAverage;
	private final StringProperty logoURL;
	private final ObjectProperty<LocalTime> lastUpdated;
	
	public Channel(final String name, final String title, final String game, final int viewer, final long uptime) {
		this.name = new SimpleStringProperty(name);
		this.title = new SimpleStringProperty(title);
		this.viewer = new SimpleIntegerProperty(viewer);
		this.uptime = new SimpleLongProperty(uptime);
		this.online = new SimpleObjectProperty<>();
		this.game = new SimpleStringProperty(game);
		this.viewerHistory = new SimpleListProperty<XYChart.Data<Number, Number>>(FXCollections.observableArrayList());
		this.viewerHistoryAverage = new SimpleIntegerProperty();
		this.viewerHistoryAverage.bind(Bindings.createIntegerBinding(this.buildAvgFunc(), this.viewerHistory));
		this.logoURL = new SimpleStringProperty("http://static-cdn.jtvnw.net/jtv_user_pictures/xarth/404_user_150x150.png");
		this.lastUpdated = new SimpleObjectProperty<LocalTime>(LocalTime.now());
	}

	public void updateFrom(final Channel updated) {
		this.setOnline(updated.isOnline());
		this.updateViewer(updated.getViewer());
		this.setUptime(updated.getUptime());
		this.setTitle(updated.getTitle());
		this.setGame(updated.getGame());
		this.setLogoURL(updated.getLogoURL());
		this.setLastUpdated(LocalTime.now());
		
		/*
		 * to keep refreshing the preview, we have to trigger the recompute of the lastUpdatedProperty. If this is not done, no
		 * new invalidation events will be fired and the binding will not be updated.
		 */
		this.getLastUpdated();
	}

	private Callable<Integer> buildAvgFunc() {
		// @formatter:off
		return () -> this.viewerHistory.isEmpty() ? 0 : (int) this.viewerHistory.stream()
				.flatMapToInt(data -> IntStream.of(data.getYValue().intValue()))
				.average()
				.getAsDouble();
		// @formatter:on
	}
	
	public void updateViewer(final int viewer) {
		this.setViewer(viewer);
		this.getViewerHistory().add(new XYChart.Data<Number, Number>(System.currentTimeMillis(), viewer));
	}
	
	@Override
	public String toString() {
		return "Channel [name=" + this.getName() + "]";
	}
	
	public String buildURL() {
		return "http://www.twitch.tv/" + this.getName() + "/";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.name == null ? 0 : this.name.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Channel)) {
			return false;
		}
		final Channel other = (Channel) obj;
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	public final StringProperty nameProperty() {
		return this.name;
	}
	
	public final java.lang.String getName() {
		return this.nameProperty().get();
	}
	
	public final void setName(final java.lang.String name) {
		this.nameProperty().set(name);
	}
	
	public final StringProperty titleProperty() {
		return this.title;
	}
	
	public final java.lang.String getTitle() {
		return this.titleProperty().get();
	}
	
	public final void setTitle(final java.lang.String title) {
		this.titleProperty().set(title);
	}
	
	public final IntegerProperty viewerProperty() {
		return this.viewer;
	}
	
	public final int getViewer() {
		return this.viewerProperty().get();
	}
	
	public final void setViewer(final int viewer) {
		this.viewerProperty().set(viewer);
	}
	
	public final LongProperty uptimeProperty() {
		return this.uptime;
	}
	
	public final long getUptime() {
		return this.uptimeProperty().get();
	}
	
	public final void setUptime(final long uptime) {
		this.uptimeProperty().set(uptime);
	}
	
	public final ObjectProperty<Boolean> onlineProperty() {
		return this.online;
	}
	
	public final Boolean isOnline() {
		return this.onlineProperty().get();
	}
	
	public final void setOnline(final Boolean online) {
		this.onlineProperty().set(online);
	}
	
	public final StringProperty gameProperty() {
		return this.game;
	}
	
	public final java.lang.String getGame() {
		return this.gameProperty().get();
	}
	
	public final void setGame(final java.lang.String game) {
		this.gameProperty().set(game);
	}

	public final ListProperty<XYChart.Data<Number, Number>> viewerHistoryProperty() {
		return this.viewerHistory;
	}
	
	public final javafx.collections.ObservableList<javafx.scene.chart.XYChart.Data<Number, Number>> getViewerHistory() {
		return this.viewerHistoryProperty().get();
	}
	
	public final void setViewerHistory(final ObservableList<XYChart.Data<Number, Number>> viewerHistory) {
		this.viewerHistoryProperty().set(viewerHistory);
	}

	public final IntegerProperty viewerHistoryAverageProperty() {
		return this.viewerHistoryAverage;
	}

	public final int getViewerHistoryAverage() {
		return this.viewerHistoryAverageProperty().get();
	}

	public final void setViewerHistoryAverage(final int viewerHistoryAverage) {
		this.viewerHistoryAverageProperty().set(viewerHistoryAverage);
	}
	
	public final StringProperty logoURLProperty() {
		return this.logoURL;
	}
	
	public final java.lang.String getLogoURL() {
		return this.logoURLProperty().get();
	}
	
	public final void setLogoURL(final java.lang.String logoURL) {
		this.logoURLProperty().set(logoURL);
	}
	
	public final ObjectProperty<LocalTime> lastUpdatedProperty() {
		return this.lastUpdated;
	}
	
	public final java.time.LocalTime getLastUpdated() {
		return this.lastUpdatedProperty().get();
	}
	
	public final void setLastUpdated(final java.time.LocalTime lastUpdated) {
		this.lastUpdatedProperty().set(lastUpdated);
	}

}
