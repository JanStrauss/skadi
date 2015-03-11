package eu.over9000.skadi.handler;

/*******************************************************************************
 * Copyright (c) 2014 Jan Strauß
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import eu.over9000.skadi.io.StateContainer;
import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.ui.MainWindow;

/**
 * The handler for the chat process.
 *
 * @author Jan Strauß
 *
 */
public class ChatHandler {

	private final StateContainer state;
	private final ObservableMap<Channel, ChatProcessHandler> handlers = FXCollections.observableHashMap();
	private final MainWindow ui;
	
	public ChatHandler(final StateContainer state, final ChannelHandler channelHandler, final MainWindow ui) {
		this.state = state;
		this.ui = ui;
		
		channelHandler.getChannels().addListener((final ListChangeListener.Change<? extends Channel> c) -> {
			while (c.next()) {
				if (c.wasRemoved()) {
					for (final Channel channel : c.getRemoved()) {
						if (this.handlers.containsKey(channel)) {
							final ChatProcessHandler cph = this.handlers.remove(channel);
							cph.closeChat();
						}
					}
				}
			}
		});
		
		this.handlers.addListener(new MapChangeListener<Channel, ChatProcessHandler>() {

			@Override
			public void onChanged(final javafx.collections.MapChangeListener.Change<? extends Channel, ? extends ChatProcessHandler> c) {
				if (c.wasAdded()) {
					System.out.println("added: " + c.getKey() + " " + c.getValueAdded());
				}
				if (c.wasRemoved()) {
					System.out.println("removed: " + c.getKey() + " " + c.getValueRemoved());
				}
			}
			
		});
	}
	
	public void openChat(final Channel channel) {

		if (this.handlers.containsKey(channel)) {
			return;
		}
		
		System.out.println("open chat for " + channel);

		try {
			final ChatProcessHandler cph = new ChatProcessHandler(channel);
			this.handlers.put(channel, cph);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeChat(final Channel channel) {
		final ChatProcessHandler cph = this.handlers.remove(channel);
		if (cph != null) {
			cph.closeChat();
		}
	}
	
	public boolean isOpen(final Channel channel) {
		return this.handlers.containsKey(channel);
	}
	
	public void onShutdown() {
		for (final ChatProcessHandler cph : this.handlers.values()) {
			cph.closeChat();
		}
	}

	private class ChatProcessHandler implements Runnable {
		private final Process process;
		private final Channel channel;
		private final Thread thread;
		private boolean internalKill = false;
		
		private ChatProcessHandler(final Channel channel) throws IOException {
			this.thread = new Thread(this);
			this.channel = channel;
			this.thread.setName("ChatHandler Thread for " + channel.getName());
			this.process = new ProcessBuilder(ChatHandler.this.state.getExecutableChrome(), "--app=" + channel.buildURL() + "chat?popout=true", "--window-size=350,758").redirectErrorStream(true)
					.start();
			this.thread.start();
		}

		@Override
		public void run() {
			try {
				final BufferedReader br = new BufferedReader(new InputStreamReader(this.process.getInputStream()));

				String line = null;
				while ((line = br.readLine()) != null) {
					System.out.println(line);
				}
				this.process.waitFor();
			} catch (final InterruptedException | IOException e) {
				e.printStackTrace();
			}

			System.out.println(">>>>>>> removing " + this.channel);
			ChatHandler.this.handlers.remove(this.channel);

			if (!this.internalKill) {
				Platform.runLater(() -> ChatHandler.this.ui.getChatAndStreamButton().handleChatClosed(this.channel));
			}
		}

		public void closeChat() {
			System.out.println("killing process for" + this.channel);
			
			this.internalKill = true;
			this.process.destroy();
		}
	}
}
