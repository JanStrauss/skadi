package eu.over9000.skadi.handler;

import java.io.File;
import java.io.IOException;

import eu.over9000.skadi.SkadiMain;
import eu.over9000.skadi.channel.Channel;
import eu.over9000.skadi.io.PersistenceManager;

public class ChatHandler extends Thread {
	
	private final Process process;
	private final Channel instance;
	
	public static ChatHandler createHandler(final Channel instance, final String url) {
		try {
			return new ChatHandler(instance, url);
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private ChatHandler(final Channel instance, final String url) throws IOException {
		this.instance = instance;
		this.setName("ChatHandler Thread for " + url);
		
		final File logFile = new File(PersistenceManager.CHAT_LOG_FILE);
		this.process = new ProcessBuilder(SkadiMain.getInstance().chrome_exec, "--app=" + url + "chat?popout=true",
		        "--window-size=350,720").redirectError(logFile).redirectOutput(logFile).start();
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
