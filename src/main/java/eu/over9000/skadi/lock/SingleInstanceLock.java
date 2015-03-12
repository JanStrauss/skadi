package eu.over9000.skadi.lock;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleInstanceLock {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SingleInstanceLock.class);
	
	private static final int SKADI_LOCKING_PORT = 37973;
	private static final byte[] WAKEUP_SIGNATURE = "SKADI".getBytes();
	
	private static DatagramSocket lockingSocket;
	private static Thread wakeupReceiverThread;

	private static Set<LockWakeupReceiver> receivers = new HashSet<>();
	
	public static boolean startSocketLock() {
		try {
			SingleInstanceLock.lockingSocket = new DatagramSocket(SingleInstanceLock.SKADI_LOCKING_PORT,
			        InetAddress.getLoopbackAddress());
			SingleInstanceLock.wakeupReceiverThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					while ((SingleInstanceLock.lockingSocket != null) && !SingleInstanceLock.lockingSocket.isClosed()) {
						final byte[] buffer = new byte[SingleInstanceLock.WAKEUP_SIGNATURE.length];
						
						final DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
						try {
							SingleInstanceLock.lockingSocket.receive(incoming);
							
							if (Arrays.equals(SingleInstanceLock.WAKEUP_SIGNATURE, incoming.getData())) {
								SingleInstanceLock.LOGGER.info("received wakeup on locking socket");
								SingleInstanceLock.receivers.forEach(receiver -> receiver.onWakeupReceived());
							}
							
						} catch (final IOException e) {
							if ((SingleInstanceLock.lockingSocket == null)
							        || SingleInstanceLock.lockingSocket.isClosed()) {
								return;
							}
							SingleInstanceLock.LOGGER.error("error handling locking socket", e);
						}
					}
					
				}
			}, "SkadiWakeupReceiver");
			
			SingleInstanceLock.wakeupReceiverThread.start();
			
		} catch (final SocketException e) {
			try {
				final DatagramSocket sendWakeupSocket = new DatagramSocket(0, InetAddress.getLoopbackAddress());
				
				final DatagramPacket sendWakeupPacket = new DatagramPacket(SingleInstanceLock.WAKEUP_SIGNATURE,
				        SingleInstanceLock.WAKEUP_SIGNATURE.length, InetAddress.getLoopbackAddress(),
				        SingleInstanceLock.SKADI_LOCKING_PORT);
				sendWakeupSocket.send(sendWakeupPacket);
				sendWakeupSocket.close();

			} catch (final IOException e1) {
				SingleInstanceLock.LOGGER.error("error handling locking socket", e);
			}
		}
		
		return SingleInstanceLock.lockingSocket != null;
	}
	
	public static void addReceiver(final LockWakeupReceiver receiver) {
		SingleInstanceLock.receivers.add(receiver);
	}

	public static void stopSocketLock() {
		SingleInstanceLock.lockingSocket.close();
	}
}
