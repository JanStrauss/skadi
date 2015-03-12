package eu.over9000.skadi.ui;

import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;

import org.controlsfx.control.StatusBar;

import eu.over9000.skadi.handler.ChatHandler;
import eu.over9000.skadi.handler.StreamHandler;
import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.model.StreamQuality;

public class HandlerControlButton {
	private final Button openBoth;
	private final ToggleButton openStream;
	private final Button openChat;
	
	private final TableView<Channel> table;
	private final StreamHandler streamHandler;
	private final ChatHandler chatHandler;

	private final StatusBar sb;

	private boolean externalChange = false;
	
	public HandlerControlButton(final ChatHandler chatHandler, final StreamHandler streamHandler,
			final TableView<Channel> table, final ToolBar tb, final StatusBar sb) {
		this.sb = sb;
		this.streamHandler = streamHandler;
		this.chatHandler = chatHandler;
		this.table = table;
		
		this.openBoth = new Button("Stream & Chat");
		this.openStream = new ToggleButton("Stream");
		this.openChat = new Button("Chat");

		this.openBoth.setDisable(true);
		this.openStream.setDisable(true);
		this.openChat.setDisable(true);

		this.openBoth.setOnAction(event -> {

			if (!this.externalChange) {
				this.openStream.setSelected(true);
				this.openChat.fire();
			}
		});
		
		this.openStream.selectedProperty().addListener((obs, oldV, newV) -> {
			final Channel candidate = this.table.getSelectionModel().getSelectedItem();

			if (!this.externalChange) {
				if (newV) {
					sb.setText("opening stream of " + candidate.getName());
					this.streamHandler.openStream(candidate, StreamQuality.getBestQuality());
				} else {
					sb.setText("closing stream of " + candidate.getName());
					this.streamHandler.closeStream(candidate);
				}
			}

		});
		
		this.openChat.setOnAction(event -> {
			final Channel candidate = this.table.getSelectionModel().getSelectedItem();

			if (!this.externalChange) {
				sb.setText("opening chat of " + candidate.getName());
				this.chatHandler.openChat(candidate);

			}

		});
		
		tb.getItems().add(this.openBoth);
		tb.getItems().add(this.openStream);
		tb.getItems().add(this.openChat);
	}
	
	public void handleStreamClosed(final Channel channel) {
		if (!channel.equals(this.table.getSelectionModel().getSelectedItem())) {
			return;
		}

		this.externalChange = true;

		this.openStream.setSelected(false);

		this.externalChange = false;
	}

	public void handleChannelSelectionChange(final boolean stream) {
		this.externalChange = true;

		this.openStream.setSelected(stream);

		this.externalChange = false;
	}
	
	public void setDisable(final boolean b) {
		this.openBoth.setDisable(b);
		this.openStream.setDisable(b);
		this.openChat.setDisable(b);
	}
}
