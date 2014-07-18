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

/**
 * The handler for the chat process.
 * 
 * @author Jan Strauß
 * 
 */
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
