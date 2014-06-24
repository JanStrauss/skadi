package eu.over9000.skadi.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import eu.over9000.skadi.SkadiMain;
import eu.over9000.skadi.channel.Channel;
import eu.over9000.skadi.logging.SkadiLogging;
import eu.over9000.skadi.stream.StreamDataset;
import eu.over9000.skadi.stream.StreamRetriever;

public class StreamHandler extends Thread {
	
	private final Process process;
	private final Channel channel;
	
	public static StreamHandler createHandler(final Channel channel) {
		try {
			return new StreamHandler(channel);
		} catch (final IOException e) {
			SkadiLogging.log(e);
			return null;
		}
	}
	
	private StreamHandler(final Channel channel) throws IOException {
		this.channel = channel;
		
		this.setName("StreamHandler Thread for " + channel.getURL());
		
		if (SkadiMain.getInstance().use_livestreamer) {
			this.process = new ProcessBuilder(SkadiMain.getInstance().livestreamer_exec, channel.getURL(), "best",
			        "-p " + SkadiMain.getInstance().vlc_exec, "-a --play-and-exit {filename}")
			        .redirectErrorStream(true).start();
		} else {
			final StreamDataset stream = StreamRetriever.getStreams(channel);
			this.process = new ProcessBuilder(SkadiMain.getInstance().vlc_exec, "--play-and-exit",
			        "--no-video-title-show", "--network-caching=2500", "--repeat", stream.getHighestQuality().getUrl())
			        .redirectErrorStream(true).start();
		}
		
		this.start();
	}
	
	@Override
	public void run() {
		try {
			final BufferedReader br = new BufferedReader(new InputStreamReader(this.process.getInputStream()));
			
			String line = null;
			while ((line = br.readLine()) != null) {
				SkadiLogging.logStreamOutput(this.channel, line);
			}
			
			this.process.waitFor();
		} catch (final InterruptedException | IOException e) {
			SkadiLogging.log(e);
		}
		
		this.channel.streamClosedCallback();
	}
	
	public void closeStream() {
		this.process.destroy();
		SkadiLogging.log("destroyed");
		this.interrupt();
	}
}
