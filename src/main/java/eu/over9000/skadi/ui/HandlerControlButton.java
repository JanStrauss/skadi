package eu.over9000.skadi.ui;

import java.util.List;

import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;

import org.controlsfx.control.StatusBar;

import eu.over9000.skadi.handler.ChatHandler;
import eu.over9000.skadi.handler.StreamHandler;
import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.model.StreamQuality;
import eu.over9000.skadi.service.QualityRetrievalService;

public class HandlerControlButton {
	private final SplitMenuButton openStream;
	private final Button openChat;

	private final TableView<Channel> table;
	private final StreamHandler streamHandler;
	private final ChatHandler chatHandler;
	private final StatusBar sb;
	private final MenuItem worstItem;
	
	public HandlerControlButton(final ChatHandler chatHandler, final StreamHandler streamHandler,
	        final TableView<Channel> table, final ToolBar tb, final StatusBar sb) {
		this.streamHandler = streamHandler;
		this.chatHandler = chatHandler;
		this.table = table;
		this.sb = sb;

		this.openStream = new SplitMenuButton();
		this.openStream.setText("Stream: best");
		
		this.worstItem = new MenuItem("Stream: worst");
		this.worstItem.setOnAction(event -> {
			this.openStreamWithQuality(StreamQuality.getWorstQuality());
		});

		this.openStream.getItems().add(this.worstItem);
		this.openChat = new Button("Chat");
		
		this.openStream.setDisable(true);
		this.openChat.setDisable(true);
		
		this.openStream.setOnAction(event -> {
			this.openStreamWithQuality(StreamQuality.getBestQuality());
			
		});

		this.openChat.setOnAction(event -> {
			final Channel candidate = this.table.getSelectionModel().getSelectedItem();
			
			sb.setText("opening chat of " + candidate.getName());
			this.chatHandler.openChat(candidate);
			
		});

		tb.getItems().add(this.openStream);
		tb.getItems().add(this.openChat);
	}
	
	private void openStreamWithQuality(final StreamQuality quality) {
		final Channel candidate = this.table.getSelectionModel().getSelectedItem();
		this.sb.setText("opening '" + quality.getQuality() + "' stream of " + candidate.getName());
		this.streamHandler.openStream(candidate, quality);
	}

	public void setDisable(final boolean b) {
		this.openStream.setDisable(b);
		this.openChat.setDisable(b);
	}
	
	public void resetQualities() {
		this.openStream.getItems().clear();
		this.openStream.getItems().add(this.worstItem);
		
		final Channel candidate = this.table.getSelectionModel().getSelectedItem();

		if ((candidate != null) && candidate.isOnline()) {
			final QualityRetrievalService service = new QualityRetrievalService(
			        quality -> this.openStreamWithQuality(quality), candidate);
			service.setOnSucceeded(event -> {
				if (candidate.equals(this.table.getSelectionModel().getSelectedItem())) {
					this.openStream.getItems().clear();
					this.openStream.getItems().addAll((List<MenuItem>) event.getSource().getValue());
					this.openStream.getItems().add(new SeparatorMenuItem());
					this.openStream.getItems().add(this.worstItem);
				}
			});
			service.start();
			
		}
	}
}
