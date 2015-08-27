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

package eu.over9000.skadi.model;

import java.time.LocalTime;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;

import eu.over9000.skadi.remote.data.ChannelMetadata;
import eu.over9000.skadi.util.NotificationUtil;

public class Channel {
	private static final String DEFAULT_CHANNEL_LOGO = "http://static-cdn.jtvnw.net/jtv_user_pictures/xarth/404_user_150x150.png";
	private final StringProperty name;
	private final StringProperty title;
	private final IntegerProperty viewer;
	private final LongProperty uptime;
	private final ObjectProperty<Boolean> online;
	private final ObjectProperty<Boolean> wasOnline;
	private final StringProperty game;
	private final ListProperty<XYChart.Data<Number, Number>> viewerHistory;
	private final IntegerProperty viewerHistoryAverage;
	private final StringProperty logoURL;
	private final ObjectProperty<LocalTime> lastUpdated;
	private final IntegerProperty followers;
	private final IntegerProperty views;
	private final ObjectProperty<Boolean> partner;
	private final ObjectProperty<Image> preview;

	public Channel(final String name) {
		this.name = new SimpleStringProperty(name);
		title = new SimpleStringProperty("-");
		viewer = new SimpleIntegerProperty(0);
		uptime = new SimpleLongProperty(0);
		online = new SimpleObjectProperty<>();
		wasOnline = new SimpleObjectProperty<>();
		game = new SimpleStringProperty("-");
		viewerHistory = new SimpleListProperty<>(FXCollections.observableArrayList());
		viewerHistoryAverage = new SimpleIntegerProperty();
		viewerHistoryAverage.bind(Bindings.createIntegerBinding(buildAvgFunc(), viewerHistory));
		logoURL = new SimpleStringProperty(DEFAULT_CHANNEL_LOGO);
		lastUpdated = new SimpleObjectProperty<>(LocalTime.now());
		followers = new SimpleIntegerProperty();
		views = new SimpleIntegerProperty();
		partner = new SimpleObjectProperty<>();
		preview = new SimpleObjectProperty<>();
	}

	public void updateFrom(final ChannelMetadata u) {
		if (u.hasTitle()) {
			setTitle(u.getTitle());
		}
		if (u.hasOnline()) {
			updateOnline(u.getOnline());
		}
		if (u.hasViewer()) {
			updateViewer(u.getViewer());
		}
		if (u.hasGame()) {
			setGame(u.getGame());
		}
		if (u.hasUptime()) {
			setUptime(u.getUptime());
		}
		if (u.hasLogoURL()) {
			setLogoURL(u.getLogoURL());
		}
		if (u.hasFollowers()) {
			setFollowers(u.getFollowers());
		}
		if (u.hasViews()) {
			setViews(u.getViews());
		}
		if (u.hasPartner()) {
			setPartner(u.getPartner());
		}
		if (u.hasPreview()) {
			setPreview(u.getPreview());
		}

		setLastUpdated(LocalTime.now());

		if (cameOnline()) {
			NotificationUtil.showOnlineNotification(this);
		}
	}

	private boolean cameOnline() {
		final boolean wasNotOnline = (getWasOnline() != null) && !getWasOnline();
		final boolean isNowOnline = (isOnline() != null) && isOnline();

		return wasNotOnline && isNowOnline;
	}

	private Callable<Integer> buildAvgFunc() {
		return () -> viewerHistory.isEmpty() ? 0 : (int) viewerHistory.stream().flatMapToInt(data -> IntStream.of(data.getYValue().intValue())).average().getAsDouble();
	}

	private void updateViewer(final int viewer) {
		setViewer(viewer);
		getViewerHistory().add(new XYChart.Data<>(System.currentTimeMillis(), viewer));
	}

	private void updateOnline(final Boolean online) {
		wasOnline.set(isOnline());
		setOnline(online);
	}

	@Override
	public String toString() {
		return "Channel [name=" + getName() + "]";
	}

	public String buildURL() {
		return "http://www.twitch.tv/" + getName() + "/";
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final Channel channel = (Channel) o;
		return Objects.equals(name, channel.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	public final StringProperty nameProperty() {
		return name;
	}

	public final String getName() {
		return nameProperty().get();
	}

	public final StringProperty titleProperty() {
		return title;
	}

	public final String getTitle() {
		return titleProperty().get();
	}

	public final void setTitle(final String title) {
		titleProperty().set(title);
	}

	public final IntegerProperty viewerProperty() {
		return viewer;
	}

	public final int getViewer() {
		return viewerProperty().get();
	}

	public final void setViewer(final int viewer) {
		viewerProperty().set(viewer);
	}

	public final LongProperty uptimeProperty() {
		return uptime;
	}

	public final long getUptime() {
		return uptimeProperty().get();
	}

	public final void setUptime(final long uptime) {
		uptimeProperty().set(uptime);
	}

	public final ObjectProperty<Boolean> onlineProperty() {
		return online;
	}

	public final Boolean isOnline() {
		return onlineProperty().get();
	}

	public final void setOnline(final Boolean online) {
		onlineProperty().set(online);
	}

	public final StringProperty gameProperty() {
		return game;
	}

	public final String getGame() {
		return gameProperty().get();
	}

	public final void setGame(final String game) {
		gameProperty().set(game);
	}

	public final ListProperty<XYChart.Data<Number, Number>> viewerHistoryProperty() {
		return viewerHistory;
	}

	public final javafx.collections.ObservableList<javafx.scene.chart.XYChart.Data<Number, Number>> getViewerHistory() {
		return viewerHistoryProperty().get();
	}

	public final IntegerProperty viewerHistoryAverageProperty() {
		return viewerHistoryAverage;
	}

	public final int getViewerHistoryAverage() {
		return viewerHistoryAverageProperty().get();
	}

	public final StringProperty logoURLProperty() {
		return logoURL;
	}

	public final String getLogoURL() {
		return logoURLProperty().get();
	}

	public final void setLogoURL(final String logoURL) {
		logoURLProperty().set(logoURL);
	}

	public final ObjectProperty<LocalTime> lastUpdatedProperty() {
		return lastUpdated;
	}

	public final LocalTime getLastUpdated() {
		return lastUpdatedProperty().get();
	}

	public final void setLastUpdated(final LocalTime lastUpdated) {
		lastUpdatedProperty().set(lastUpdated);
	}

	public final ObjectProperty<Boolean> wasOnlineProperty() {
		return wasOnline;
	}

	public final Boolean getWasOnline() {
		return wasOnlineProperty().get();
	}

	public final IntegerProperty followersProperty() {
		return followers;
	}

	public final int getFollowers() {
		return followersProperty().get();
	}

	public final void setFollowers(final int followers) {
		followersProperty().set(followers);
	}

	public final IntegerProperty viewsProperty() {
		return views;
	}

	public final int getViews() {
		return viewsProperty().get();
	}

	public final void setViews(final int views) {
		viewsProperty().set(views);
	}

	public final ObjectProperty<Boolean> partnerProperty() {
		return partner;
	}

	public final Boolean getPartner() {
		return partnerProperty().get();
	}

	public final void setPartner(final Boolean partner) {
		partnerProperty().set(partner);
	}

	public Image getPreview() {
		return preview.get();
	}

	public void setPreview(final Image preview) {
		this.preview.set(preview);
	}
	
	public ObjectProperty<Image> previewProperty() {
		return preview;
	}
}
