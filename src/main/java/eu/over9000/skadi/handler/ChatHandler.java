package eu.over9000.skadi.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import eu.over9000.skadi.SkadiMain;
import eu.over9000.skadi.channel.Channel;
import eu.over9000.skadi.logging.SkadiLogging;

public class ChatHandler extends Thread {
	
	private final Process process;
	private final Channel channel;
	
	public static ChatHandler createHandler(final Channel channel, final String url) {
		try {
			return new ChatHandler(channel, url);
		} catch (final IOException e) {
			SkadiLogging.log(e);
			return null;
		}
	}
	
	private ChatHandler(final Channel channel, final String url) throws IOException {
		this.channel = channel;
		this.setName("ChatHandler Thread for " + url);
		
		this.process = new ProcessBuilder(SkadiMain.getInstance().chrome_exec, "--app=" + url + "chat?popout=true",
		        "--window-size=350,720").redirectErrorStream(true).start();
		this.start();
	}
	
	@Override
	public void run() {
		try {
			final BufferedReader br = new BufferedReader(new InputStreamReader(this.process.getInputStream()));
			
			String line = null;
			while ((line = br.readLine()) != null) {
				SkadiLogging.logChatOutput(this.channel, line);
			}
			
			this.process.waitFor();
		} catch (final InterruptedException | IOException e) {
			SkadiLogging.log(e);
		}
		
		this.channel.chatClosedCallback();
	}
	
	public void closeChat() {
		this.process.destroy();
		SkadiLogging.log("destroyed");
		this.interrupt();
	}
	
}
