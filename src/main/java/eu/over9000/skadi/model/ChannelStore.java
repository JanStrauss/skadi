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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import org.controlsfx.control.StatusBar;

import eu.over9000.skadi.io.PersistenceHandler;
import eu.over9000.skadi.remote.ChannelDataRetriever;
import eu.over9000.skadi.service.ChannelUpdateService;
import eu.over9000.skadi.util.StringUtil;

public class ChannelStore {

	private final ObservableList<Channel> channels = FXCollections.observableArrayList(c -> new Observable[]{c.titleProperty(), c.nameProperty(), c.uptimeProperty(), c.onlineProperty(), c.viewerProperty(), c.gameProperty()});

	private final ObservableMap<Channel, ChannelUpdateService> channelUpdater = FXCollections.observableHashMap();

	public ChannelStore(final PersistenceHandler persistenceHandler) {
		final StateContainer state = StateContainer.getInstance();

		final List<Channel> emptyChannels = state.getChannels().stream().map(Channel::new).collect(Collectors.toList());

		this.channels.addListener((final ListChangeListener.Change<? extends Channel> c) -> {
			boolean updateState = false;
			while (c.next()) {
				if (c.wasAdded()) {
					updateState = true;
					for (final Channel channel : c.getAddedSubList()) {
						final ChannelUpdateService service = new ChannelUpdateService(channel);
						service.start();
						ChannelStore.this.channelUpdater.put(channel, service);
					}
				} else if (c.wasRemoved()) {
					updateState = true;
					for (final Channel channel : c.getRemoved()) {
						final ChannelUpdateService service = ChannelStore.this.channelUpdater.remove(channel);
						service.cancel();
					}
				}
			}

			if (updateState) {
				final List<String> channelnames = ChannelStore.this.getChannelNames();
				if (channelnames.containsAll(state.getChannels()) && state.getChannels().containsAll(channelnames)) {
					return;
				}
				state.getChannels().clear();
				state.getChannels().addAll(channelnames);
				persistenceHandler.saveState(state);
			}
		});

		this.channels.addAll(emptyChannels);
	}

	public ObservableList<Channel> getChannels() {
		return this.channels;
	}

	public List<String> getChannelNames() {
		return this.channels.stream().flatMap(c -> Stream.of(c.getName())).sorted().collect(Collectors.toList());
	}

	private Channel buildDummyChannel(final String name) {
		return new Channel(name);
	}

	public boolean addChannel(final String name, final StatusBar sb) {
		if (!this.checkPattern(name)) {
			sb.setText(name + " is no vaild channelname");
			return false;
		}
		if (this.checkContains(name)) {
			sb.setText("channel " + name + " is already added");
			return false;
		}
		if (!this.checkExists(name)) {
			sb.setText("channel " + name + " does not exist");
			return false;
		}

		final Channel newChannel = this.buildDummyChannel(name);
		this.channels.add(newChannel);
		sb.setText("added channel " + name);
		return true;
	}

	public void addChannels(final Set<String> result, final StatusBar sb) {
		final Set<Channel> dummys = new HashSet<>();
		result.forEach(c -> {
			if (this.checkPattern(c) && !this.checkContains(c)) {
				dummys.add(this.buildDummyChannel(c));
			}
		});
		this.channels.addAll(dummys);
		sb.setText("added " + dummys.size() + " channels");
	}

	private boolean checkPattern(final String channel) {
		return Pattern.matches(StringUtil.USERNAME_REGEX, channel);
	}

	private boolean checkContains(final String channel) {
		return StringUtil.containsIgnoreCase(this.getChannelNames(), channel);
	}

	private boolean checkExists(final String channel) {
		return ChannelDataRetriever.checkIfChannelExists(channel);
	}

}
