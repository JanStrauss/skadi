package eu.over9000.skadi.handler;

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
import eu.over9000.skadi.io.StateContainer;
import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.remote.ChannelDataRetriever;
import eu.over9000.skadi.service.ChannelUpdateService;
import eu.over9000.skadi.util.StringUtil;

public class ChannelHandler {

	private final ObservableList<Channel> channels = FXCollections.observableArrayList(c -> new Observable[] {
	        c.titleProperty(), c.nameProperty(), c.uptimeProperty(), c.onlineProperty(), c.viewerProperty(),
	        c.gameProperty() });

	private final ObservableMap<Channel, ChannelUpdateService> channelUpdater = FXCollections.observableHashMap();
	
	public ChannelHandler(final PersistenceHandler persistenceHandler, final StateContainer state) {
		final List<Channel> emptyChannels = state.getChannels().stream().map(c -> new Channel(c, "-", "-", 0, 0))
		        .collect(Collectors.toList());

		this.channels.addListener((final ListChangeListener.Change<? extends Channel> c) -> {
			boolean updateState = false;
			while (c.next()) {
				if (c.wasAdded()) {
					updateState = true;
					for (final Channel channel : c.getAddedSubList()) {
						final ChannelUpdateService service = new ChannelUpdateService(channel);
						service.start();
						ChannelHandler.this.channelUpdater.put(channel, service);
					}
				} else if (c.wasRemoved()) {
					updateState = true;
					for (final Channel channel : c.getRemoved()) {
						final ChannelUpdateService service = ChannelHandler.this.channelUpdater.remove(channel);
						service.cancel();
					}
				}
			}
			
			if (updateState) {
				final List<String> channelnames = ChannelHandler.this.getChannelNames();
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
		final List<String> result = this.channels.stream().flatMap(c -> Stream.of(c.getName())).sorted()
		        .collect(Collectors.toList());
		return result;
	}
	
	private Channel buildDummyChannel(final String name) {
		return new Channel(name, "-", "-", 0, 0);
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

	private boolean checkPattern(final String c) {
		return Pattern.matches("\\w+{2,25}", c);
	}

	private boolean checkContains(final String c) {
		return StringUtil.containsIgnoreCase(this.getChannelNames(), c);
	}

	private boolean checkExists(final String c) {
		return ChannelDataRetriever.checkIfChannelExists(c);
	}
	
}
