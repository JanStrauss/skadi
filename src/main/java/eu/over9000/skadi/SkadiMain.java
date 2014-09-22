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

import java.awt.Frame;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

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
	private static final int SKADI_LOCKING_PORT = 37973;
	
	private DatagramSocket lockingSocket;
	
	private static SkadiMain instance;
	
	private static final byte[] WAKEUP_SIGNATURE = "SKADI".getBytes();
	
	private final boolean isLinux = System.getProperty("os.name").equals("Linux");
	
	public String chrome_exec = this.isLinux ? "chromium-browser"
	        : "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe";
	public String livestreamer_exec = this.isLinux ? "livestreamer"
	        : "C:\\Program Files (x86)\\Livestreamer\\livestreamer.exe";
	public String vlc_exec = this.isLinux ? "vlc" : "C:\\Program Files (x86)\\VideoLAN\\VLC\\vlc.exe";
	
	public boolean use_livestreamer = true;
	public boolean display_notifications = true;
	public boolean minimize_to_tray = false;
	private Thread wakeupReceiverThread;
	
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
		
		if (!this.createSocketLock()) {
			System.out.println("could not create socket on port " + SkadiMain.SKADI_LOCKING_PORT
			        + " (Skadi already running?), exiting.");
			return;
		}
		
		this.addShutdownHook();
		
		ChannelManager.getInstance();
		ChannelUpdater.getInstance();
		
		PersistenceManager.getInstance().loadData();
		
		SkadiGUI.createInstance();
		
		NotificationManager.getInstance();
		
		SkadiVersionChecker.checkVersion();
		
	}
	
	private boolean createSocketLock() {
		try {
			this.lockingSocket = new DatagramSocket(SkadiMain.SKADI_LOCKING_PORT, InetAddress.getLoopbackAddress());
			this.wakeupReceiverThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					while ((SkadiMain.this.lockingSocket != null) && !SkadiMain.this.lockingSocket.isClosed()) {
						final byte[] buffer = new byte[SkadiMain.WAKEUP_SIGNATURE.length];
						
						final DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
						try {
							SkadiMain.this.lockingSocket.receive(incoming);
							
							if (Arrays.equals(SkadiMain.WAKEUP_SIGNATURE, incoming.getData())) {
								SkadiGUI.getInstance().setVisible(true);
								SkadiGUI.getInstance().setState(Frame.NORMAL);
								SkadiGUI.getInstance().toFront();
								SkadiLogging.log("received wakeup on locking socket");
							}
							
						} catch (final IOException e) {
							if ((SkadiMain.this.lockingSocket == null) || SkadiMain.this.lockingSocket.isClosed()) {
								return;
							}
							e.printStackTrace();
						}
					}
					
				}
			}, "SkadiWakeupReceiver");
			
			this.wakeupReceiverThread.start();
		} catch (final SocketException e) {
			try {
				final DatagramSocket sendWakeupSocket = new DatagramSocket(0, InetAddress.getLoopbackAddress());
				
				final DatagramPacket sendWakeupPacket = new DatagramPacket(SkadiMain.WAKEUP_SIGNATURE,
				        SkadiMain.WAKEUP_SIGNATURE.length, InetAddress.getLoopbackAddress(),
				        SkadiMain.SKADI_LOCKING_PORT);
				sendWakeupSocket.send(sendWakeupPacket);
				sendWakeupSocket.close();
			} catch (final IOException e1) {
				e1.printStackTrace();
			}
		}
		
		return this.lockingSocket != null;
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
				
				if (SkadiMain.this.lockingSocket != null) {
					SkadiLogging.log("RELEASING LOCK..");
					SkadiMain.this.wakeupReceiverThread.interrupt();
					SkadiMain.this.lockingSocket.close();
				}
				
				SkadiLogging.log("SHUTDOWN COMPLETE");
			}
		}));
	}
	
}
