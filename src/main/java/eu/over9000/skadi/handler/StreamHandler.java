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
	
	public static StreamHandler createHandler(final Channel channel, final String quality) {
		try {
			return new StreamHandler(channel, quality);
		} catch (final IOException e) {
			SkadiLogging.log(e);
			return null;
		}
	}
	
	private StreamHandler(final Channel channel, final String quality) throws IOException {
		this.channel = channel;
		
		this.setName("StreamHandler Thread for " + channel.getURL());
		
		if (SkadiMain.getInstance().use_livestreamer) {
			this.process = new ProcessBuilder(SkadiMain.getInstance().livestreamer_exec, channel.getURL(), quality,
			        "-p " + SkadiMain.getInstance().vlc_exec, "-a --play-and-exit {filename}")
			        .redirectErrorStream(true).start();
		} else {
			StreamDataset stream = channel.getStreamDataset();
			
			if (stream == null) {
				SkadiLogging.log("will retrieve stream data for " + channel.getURL()
				        + " now, as no stream information available currently..");
				
				stream = StreamRetriever.getStreams(channel);
				channel.updateStreamdata(stream);
			}
			
			this.process = new ProcessBuilder(SkadiMain.getInstance().vlc_exec, "--play-and-exit",
			        "--no-video-title-show", "--network-caching=2500", "--repeat", stream.getQuality(quality).getUrl())
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
