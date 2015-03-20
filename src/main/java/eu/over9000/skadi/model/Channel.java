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

package eu.over9000.skadi.model;

import java.time.LocalTime;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

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

	public Channel(final String name) {
		this.name = new SimpleStringProperty(name);
		this.title = new SimpleStringProperty("-");
		this.viewer = new SimpleIntegerProperty(0);
		this.uptime = new SimpleLongProperty(0);
		this.online = new SimpleObjectProperty<>();
		this.wasOnline = new SimpleObjectProperty<>();
		this.game = new SimpleStringProperty("-");
		this.viewerHistory = new SimpleListProperty<>(FXCollections.observableArrayList());
		this.viewerHistoryAverage = new SimpleIntegerProperty();
		this.viewerHistoryAverage.bind(Bindings.createIntegerBinding(this.buildAvgFunc(), this.viewerHistory));
		this.logoURL = new SimpleStringProperty(DEFAULT_CHANNEL_LOGO);
		this.lastUpdated = new SimpleObjectProperty<>(LocalTime.now());
		this.followers = new SimpleIntegerProperty();
		this.views = new SimpleIntegerProperty();
		this.partner = new SimpleObjectProperty<>();
	}

	public void updateFrom(final ChannelMetadata u) {
		if (u.hasTitle()) {
			this.setTitle(u.getTitle());
		}
		if (u.hasOnline()) {
			this.updateOnline(u.getOnline());
		}
		if (u.hasViewer()) {
			this.updateViewer(u.getViewer());
		}
		if (u.hasGame()) {
			this.setGame(u.getGame());
		}
		if (u.hasUptime()) {
			this.setUptime(u.getUptime());
		}
		if (u.hasLogoURL()) {
			this.setLogoURL(u.getLogoURL());
		}
		if (u.hasFollowers()) {
			this.setFollowers(u.getFollowers());
		}
		if (u.hasViews()) {
			this.setViews(u.getViews());
		}
		if (u.hasPartner()) {
			this.setPartner(u.getPartner());
		}

		this.setLastUpdated(LocalTime.now());

		if (this.cameOnline()) {
			NotificationUtil.showOnlineNotification(this);
		}
	}

	private boolean cameOnline() {
		final boolean wasNotOnline = (this.getWasOnline() != null) && !this.getWasOnline();
		final boolean isNowOnline = (this.isOnline() != null) && this.isOnline();

		return wasNotOnline && isNowOnline;
	}

	private Callable<Integer> buildAvgFunc() {
		return () -> this.viewerHistory.isEmpty() ? 0 : (int) this.viewerHistory.stream().flatMapToInt(data -> IntStream.of(data.getYValue().intValue())).average().getAsDouble();
	}

	private void updateViewer(final int viewer) {
		this.setViewer(viewer);
		this.getViewerHistory().add(new XYChart.Data<>(System.currentTimeMillis(), viewer));
	}

	private void updateOnline(final Boolean online) {
		this.wasOnline.set(this.isOnline());
		this.setOnline(online);
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
		result = (prime * result) + (this.name == null ? 0 : this.name.hashCode());
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

	public final String getName() {
		return this.nameProperty().get();
	}

	public final void setName(final String name) {
		this.nameProperty().set(name);
	}

	public final StringProperty titleProperty() {
		return this.title;
	}

	public final String getTitle() {
		return this.titleProperty().get();
	}

	public final void setTitle(final String title) {
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

	public final String getGame() {
		return this.gameProperty().get();
	}

	public final void setGame(final String game) {
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

	public final String getLogoURL() {
		return this.logoURLProperty().get();
	}

	public final void setLogoURL(final String logoURL) {
		this.logoURLProperty().set(logoURL);
	}

	public final ObjectProperty<LocalTime> lastUpdatedProperty() {
		return this.lastUpdated;
	}

	public final LocalTime getLastUpdated() {
		return this.lastUpdatedProperty().get();
	}

	public final void setLastUpdated(final LocalTime lastUpdated) {
		this.lastUpdatedProperty().set(lastUpdated);
	}

	public final ObjectProperty<Boolean> wasOnlineProperty() {
		return this.wasOnline;
	}

	public final Boolean getWasOnline() {
		return this.wasOnlineProperty().get();
	}

	public final void setWasOnline(final Boolean wasOnline) {
		this.wasOnlineProperty().set(wasOnline);
	}

	public final IntegerProperty followersProperty() {
		return this.followers;
	}

	public final int getFollowers() {
		return this.followersProperty().get();
	}

	public final void setFollowers(final int followers) {
		this.followersProperty().set(followers);
	}

	public final IntegerProperty viewsProperty() {
		return this.views;
	}

	public final int getViews() {
		return this.viewsProperty().get();
	}

	public final void setViews(final int views) {
		this.viewsProperty().set(views);
	}

	public final ObjectProperty<Boolean> partnerProperty() {
		return this.partner;
	}

	public final Boolean getPartner() {
		return this.partnerProperty().get();
	}

	public final void setPartner(final Boolean partner) {
		this.partnerProperty().set(partner);
	}

}
