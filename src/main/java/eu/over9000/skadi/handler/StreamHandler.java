package eu.over9000.skadi.handler;

import java.io.File;
import java.io.IOException;

import eu.over9000.skadi.SkadiMain;
import eu.over9000.skadi.channel.Channel;
import eu.over9000.skadi.io.PersistenceManager;
import eu.over9000.skadi.stream.StreamDataset;

public class StreamHandler extends Thread {
	
	private final Process process;
	private final Channel channel;
	
	public static StreamHandler createHandler(final Channel channel, final StreamDataset streamDataset) {
		try {
			return new StreamHandler(channel, streamDataset);
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private StreamHandler(final Channel channel, final StreamDataset stream) throws IOException {
		this.channel = channel;
		
		this.setName("StreamHandler Thread for " + channel.getURL());
		
		final File logFile = new File(PersistenceManager.STREAM_LOG_FILE);
		
		this.process = new ProcessBuilder(SkadiMain.getInstance().vlc_exec, "--play-and-exit", "--no-video-title-show",
		        stream.getHighestQuality().getUrl()).redirectError(logFile).redirectOutput(logFile).start();
		
		this.start();
	}
	
	@Override
	public void run() {
		try {
			this.process.waitFor();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		
		this.channel.streamClosedCallback();
	}
	
	public void closeStream() {
		this.process.destroy();
		System.out.println("destroyed");
		this.interrupt();
	}
}
