package eu.over9000.skadi.handler;

import java.io.File;
import java.io.IOException;

import eu.over9000.skadi.SkadiMain;
import eu.over9000.skadi.channel.Channel;
import eu.over9000.skadi.io.PersistenceManager;
import eu.over9000.skadi.stream.StreamDataset;
import eu.over9000.skadi.stream.StreamRetriever;

public class StreamHandler extends Thread {
	
	private static final boolean USE_LIVESTREAMER = true;
	private final Process process;
	private final Channel channel;
	
	public static StreamHandler createHandler(final Channel channel) {
		try {
			return new StreamHandler(channel);
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private StreamHandler(final Channel channel) throws IOException {
		this.channel = channel;
		
		this.setName("StreamHandler Thread for " + channel.getURL());
		
		final File logFile = new File(PersistenceManager.STREAM_LOG_FILE);
		
		if (StreamHandler.USE_LIVESTREAMER) {
			this.process = new ProcessBuilder(SkadiMain.getInstance().livestreamer_exec, channel.getURL(), "best",
			        "-a --play-and-exit {filename}").redirectError(logFile).redirectOutput(logFile).start();
		} else {
			final StreamDataset stream = StreamRetriever.getStreams(channel);
			this.process = new ProcessBuilder(SkadiMain.getInstance().vlc_exec, "--play-and-exit",
			        "--no-video-title-show", stream.getHighestQuality().getUrl()).redirectError(logFile)
			        .redirectOutput(logFile).start();
		}
		
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
