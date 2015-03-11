package eu.over9000.skadi.ui;

import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;

import org.controlsfx.control.SegmentedButton;

import eu.over9000.skadi.handler.ChatHandler;
import eu.over9000.skadi.handler.StreamHandler;
import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.model.StreamQuality;

public class HandlerControlButton extends SegmentedButton {
	private final ToggleButton openBoth;
	private final ToggleButton openStream;
	private final ToggleButton openChat;
	
	private final TableView<Channel> table;
	private final StreamHandler streamHandler;
	private final ChatHandler chatHandler;

	private boolean openBothChanging = false;
	private boolean externalChange = false;
	
	public HandlerControlButton(final ChatHandler chatHandler, final StreamHandler streamHandler, final TableView<Channel> table) {
		this.streamHandler = streamHandler;
		this.chatHandler = chatHandler;
		this.table = table;
		this.openBoth = new ToggleButton("Stream & Chat");
		this.openStream = new ToggleButton("Stream");
		this.openChat = new ToggleButton("Chat");

		this.openBoth.selectedProperty().addListener((obs, oldV, newV) -> {
			this.openBothChanging = true;
			// System.out.println("both changed to " + newV);
				
				if (!this.externalChange) {
					this.openStream.setSelected(newV);
					this.openChat.setSelected(newV);
				}
				
				this.openBothChanging = false;
			});
		
		this.openStream.selectedProperty().addListener((obs, oldV, newV) -> {
			final Channel candidate = this.table.getSelectionModel().getSelectedItem();
			// System.out.println("stream changed to " + newV + ", sel=" + candidate.getName());
				
				if (!this.externalChange) {
					if (newV) {
						this.streamHandler.openStream(candidate, StreamQuality.getBestQuality());
					} else {
						this.streamHandler.closeStream(candidate);
					}
				}
				
				if (!this.openBothChanging) {
					this.openBoth.setSelected(newV && this.openChat.isSelected());
				}
				
			});
		
		this.openChat.selectedProperty().addListener((obs, oldV, newV) -> {
			final Channel candidate = this.table.getSelectionModel().getSelectedItem();
			// System.out.println("chat changed to " + newV + ", sel=" + candidate.getName());
				
				if (!this.externalChange) {
					if (newV) {
						this.chatHandler.openChat(candidate);
						
					} else {
						this.chatHandler.closeChat(candidate);
					}
				}
				
				if (!this.openBothChanging) {
					this.openBoth.setSelected(newV && this.openStream.isSelected());
				}
			});
		
		this.getButtons().add(this.openBoth);
		this.getButtons().add(this.openStream);
		this.getButtons().add(this.openChat);
		this.setToggleGroup(null);
		this.setDisable(true);

	}
	
	public void handleChatClosed(final Channel channel) {
		if (!channel.equals(this.table.getSelectionModel().getSelectedItem())) {
			return;
		}

		this.externalChange = true;

		this.openChat.setSelected(false);

		this.externalChange = false;
	}
	
	public void handleStreamClosed(final Channel channel) {
		if (!channel.equals(this.table.getSelectionModel().getSelectedItem())) {
			return;
		}

		this.externalChange = true;

		this.openStream.setSelected(false);

		this.externalChange = false;
	}

	public void handleChannelSelectionChange(final boolean stream, final boolean chat) {
		// System.out.println("selection change:" + stream + " " + chat);
		this.externalChange = true;

		this.openStream.setSelected(stream);
		this.openChat.setSelected(chat);

		this.externalChange = false;
	}
}
