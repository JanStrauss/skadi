package eu.over9000.skadi.handler;

import java.io.File;
import java.io.IOException;

import eu.over9000.skadi.SkadiMain;
import eu.over9000.skadi.channel.Channel;
import eu.over9000.skadi.io.PersistenceManager;

public class StreamHandler extends Thread {
	
	private final Process process;
	private final Channel instance;
	
	public static StreamHandler createHandler(final Channel instance, final String url, final String quality) {
		try {
			return new StreamHandler(instance, url, quality);
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private StreamHandler(final Channel instance, final String url, final String quality) throws IOException {
		this.instance = instance;
		
		this.setName("StreamHandler Thread for " + url);
		
		final File logFile = new File(PersistenceManager.STREAM_LOG_FILE);
		
		this.process = new ProcessBuilder(SkadiMain.getInstance().livestreamer_exec, url, quality,
		        "-a --play-and-exit {filename}").redirectError(logFile).redirectOutput(logFile).start();
		
		this.start();
	}
	
	@Override
	public void run() {
		try {
			this.process.waitFor();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		
		this.instance.streamClosedCallback();
	}
	
	public void closeStream() {
		this.process.destroy();
		System.out.println("destroyed");
		this.interrupt();
	}
}
