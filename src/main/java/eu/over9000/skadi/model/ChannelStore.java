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

package eu.over9000.skadi.model;

import eu.over9000.skadi.io.PersistenceHandler;
import eu.over9000.skadi.remote.ChannelDataRetriever;
import eu.over9000.skadi.service.ChannelUpdateService;
import eu.over9000.skadi.ui.StatusBarWrapper;
import eu.over9000.skadi.util.StringUtil;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChannelStore {

	private final ObservableList<Channel> channels = FXCollections.observableArrayList(c -> new Observable[]{c.titleProperty(), c.nameProperty(), c.uptimeProperty(), c.onlineProperty(), c.viewerProperty(), c.gameProperty()});

	private final ObservableMap<Channel, ChannelUpdateService> channelUpdater = FXCollections.observableHashMap();

	public ChannelStore(final PersistenceHandler persistenceHandler) {
		final StateContainer state = StateContainer.getInstance();

		final List<Channel> emptyChannels = state.getChannels().stream().map(Channel::new).collect(Collectors.toList());

		channels.addListener((final ListChangeListener.Change<? extends Channel> c) -> {
			boolean updateState = false;
			while (c.next()) {
				if (c.wasAdded()) {
					updateState = true;
					for (final Channel channel : c.getAddedSubList()) {
						final ChannelUpdateService service = new ChannelUpdateService(channel);
						service.start();
						channelUpdater.put(channel, service);
					}
				} else if (c.wasRemoved()) {
					updateState = true;
					for (final Channel channel : c.getRemoved()) {
						final ChannelUpdateService service = channelUpdater.remove(channel);
						service.cancel();
					}
				}
			}

			if (updateState) {
				final List<String> channelnames = getChannelNames();
				if (channelnames.containsAll(state.getChannels()) && state.getChannels().containsAll(channelnames)) {
					return;
				}
				state.getChannels().clear();
				state.getChannels().addAll(channelnames);
				persistenceHandler.saveState(state);
			}
		});

		channels.addAll(emptyChannels);
	}

	public ObservableList<Channel> getChannels() {
		return channels;
	}

	public List<String> getChannelNames() {
		return channels.stream().flatMap(c -> Stream.of(c.getName())).sorted().collect(Collectors.toList());
	}

	private Channel buildDummyChannel(final String name) {
		return new Channel(name);
	}

	public boolean addChannel(final String name, final StatusBarWrapper sb) {
		if (!checkPattern(name)) {
			sb.updateStatusText(name + " is no vaild channelname");
			return false;
		}
		if (checkContains(name)) {
			sb.updateStatusText("channel " + name + " is already added");
			return false;
		}
		if (!checkExists(name)) {
			sb.updateStatusText("channel " + name + " does not exist");
			return false;
		}

		final Channel newChannel = buildDummyChannel(name);
		channels.add(newChannel);
		sb.updateStatusText("added channel " + name);
		return true;
	}

	public void addChannels(final Set<String> result, final StatusBarWrapper sb) {
		final Set<Channel> dummys = new HashSet<>();
		result.forEach(c -> {
			if (checkPattern(c) && !checkContains(c)) {
				dummys.add(buildDummyChannel(c));
			}
		});
		channels.addAll(dummys);
		sb.updateStatusText("added " + dummys.size() + " channels");
	}

	private boolean checkPattern(final String channel) {
		return Pattern.matches(StringUtil.USERNAME_REGEX, channel);
	}

	private boolean checkContains(final String channel) {
		return StringUtil.containsIgnoreCase(getChannelNames(), channel);
	}

	private boolean checkExists(final String channel) {
		return ChannelDataRetriever.checkIfChannelExists(channel);
	}

}
