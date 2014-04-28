package eu.over9000.skadi.handler;

import java.io.IOException;

import eu.over9000.skadi.channel.ChannelInstance;

public class StreamHandler extends Thread {
	
	private final Process process;
	private final ChannelInstance instance;
	
	public static StreamHandler createHandler(final ChannelInstance instance, final String url, final String quality) {
		try {
			return new StreamHandler(instance, url, quality);
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private StreamHandler(final ChannelInstance instance, final String url, final String quality) throws IOException {
		this.instance = instance;
		
		this.setName("StreamHandler Thread for " + url);
		
		final ProcessBuilder builder = new ProcessBuilder("livestreamer", url, quality);
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
		
		this.instance.streamClosedCallback();
	}
	
	public void closeStream() {
		this.process.destroy();
		System.out.println("destroyed");
		this.interrupt();
	}
}
