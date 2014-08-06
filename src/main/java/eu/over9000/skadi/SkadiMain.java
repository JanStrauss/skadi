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
package eu.over9000.skadi;

import eu.over9000.skadi.channel.Channel;
import eu.over9000.skadi.channel.ChannelManager;
import eu.over9000.skadi.channel.ChannelUpdater;
import eu.over9000.skadi.gui.SkadiGUI;
import eu.over9000.skadi.io.PersistenceManager;
import eu.over9000.skadi.logging.SkadiLogging;
import eu.over9000.skadi.notification.NotificationManager;
import eu.over9000.skadi.util.SkadiVersionChecker;

/**
 * Singleton class storing configuration values and providing the main method.
 * 
 * @author Jan Strauß
 * 
 */
public class SkadiMain {
	
	public static final String CLIENT_ID = "i2uu9j43ure9x7n4ojpgg4hvcnw6y91";
	
	private static SkadiMain instance;
	
	public String chrome_exec = "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe";
	public String livestreamer_exec = "C:\\Program Files (x86)\\Livestreamer\\livestreamer.exe";
	public String vlc_exec = "C:\\Program Files (x86)\\VideoLAN\\VLC\\vlc.exe";
	
	public boolean use_livestreamer = true;
	public boolean display_notifications = true;
	
	public static SkadiMain getInstance() {
		if (SkadiMain.instance == null) {
			SkadiMain.instance = new SkadiMain();
		}
		return SkadiMain.instance;
	}
	
	public static void main(final String[] args) {
		SkadiMain.getInstance().runInit(args);
	}
	
	private void runInit(final String[] args) {
		this.addShutdownHook();
		
		ChannelManager.getInstance();
		ChannelUpdater.getInstance();
		
		PersistenceManager.getInstance().loadData();
		
		SkadiGUI.createInstance();
		
		NotificationManager.getInstance();
		
		SkadiVersionChecker.checkVersion();
		
	}
	
	private void addShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			
			@Override
			public void run() {
				SkadiLogging.log("STOPPING UPDATER");
				ChannelUpdater.getInstance().stopUpdater();
				SkadiLogging.log("KILLING STREAMS/CHATS..");
				for (final Channel channel : ChannelManager.getInstance().getChannels()) {
					channel.closeStreamAndChat();
				}
				SkadiLogging.log("SAVING DATA..");
				PersistenceManager.getInstance().saveData();
				SkadiLogging.log("SHUTDOWN COMPLETE");
				
			}
		}));
	}
	
}
