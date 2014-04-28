package eu.over9000.skadi.handler;

import java.io.IOException;

import eu.over9000.skadi.channel.ChannelInstance;

public class ChatHandler extends Thread {
	
	private final Process process;
	private final ChannelInstance instance;
	
	public static ChatHandler createHandler(final ChannelInstance instance, final String url) {
		try {
			return new ChatHandler(instance, url);
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private ChatHandler(final ChannelInstance instance, final String url) throws IOException {
		this.instance = instance;
		this.setName("ChatHandler Thread for " + url);
		
		final ProcessBuilder builder = new ProcessBuilder(
		        "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe",
		        "--app=" + url + "chat?popout=true", "--window-size=350,720");
		
		builder.inheritIO();
		this.process = builder.start();
		
		this.start();
	}
	
	@Override
	public void run() {
		try {
			this.process.waitFor();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		
		this.instance.chatClosedCallback();
	}
	
	public void closeChat() {
		this.process.destroy();
		System.out.println("destroyed");
		this.interrupt();
	}
	
}
