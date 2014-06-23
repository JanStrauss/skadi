package eu.over9000.skadi.logging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import eu.over9000.skadi.channel.Channel;
import eu.over9000.skadi.gui.SkadiGUI;
import eu.over9000.skadi.io.PersistenceManager;

public class SkadiLogging {
	
	private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	private static SkadiLogging instance;
	
	private PrintWriter chatLog;
	private PrintWriter streamLog;
	private PrintWriter skadiLog;
	
	private static SkadiLogging getInstance() {
		if (SkadiLogging.instance == null) {
			SkadiLogging.instance = new SkadiLogging();
		}
		return SkadiLogging.instance;
	}
	
	private SkadiLogging() {
		try {
			this.streamLog = new PrintWriter(new BufferedWriter(
			        new FileWriter(PersistenceManager.STREAM_LOG_FILE, true)));
			this.chatLog = new PrintWriter(new BufferedWriter(new FileWriter(PersistenceManager.CHAT_LOG_FILE, true)));
			this.skadiLog = new PrintWriter(new BufferedWriter(new FileWriter(PersistenceManager.SKADI_LOG_FILE, true)));
		} catch (final IOException e) {
			SkadiLogging.log(e);
		}
	}
	
	public static void log(final String message) {
		final StackTraceElement[] st = Thread.currentThread().getStackTrace();
		final String className = st[2].getClassName() + "#" + st[2].getMethodName() + ":" + st[2].getLineNumber();
		
		final String logEntry = SkadiLogging.currentTimestamp() + " | " + className + " | " + message;
		System.out.println(logEntry);
		
		SkadiGUI.getInstance().appendLog(logEntry);
		
		final PrintWriter log = SkadiLogging.getInstance().skadiLog;
		
		synchronized (log) {
			log.println(logEntry);
			log.flush();
		}
	}
	
	public static void log(final Exception e) {
		final StackTraceElement[] st = Thread.currentThread().getStackTrace();
		final String className = st[2].getClassName() + "#" + st[2].getMethodName() + ":" + st[2].getLineNumber();
		
		final String logEntry = SkadiLogging.currentTimestamp() + " | " + className + " | EXCEPTION: " + e.getMessage();
		System.out.println(logEntry);
		SkadiGUI.getInstance().appendLog(logEntry);
		
		e.printStackTrace();
		final PrintWriter log = SkadiLogging.getInstance().skadiLog;
		synchronized (log) {
			log.println(logEntry);
			e.printStackTrace(log);
			log.flush();
		}
	}
	
	public static void logStreamOutput(final Channel channel, final String line) {
		final String logEntry = SkadiLogging.currentTimestamp() + " | STREAM | " + channel.getURL() + " | " + line;
		System.out.println(logEntry);
		SkadiGUI.getInstance().appendLog(logEntry);
		
		final PrintWriter log = SkadiLogging.getInstance().streamLog;
		
		synchronized (log) {
			log.println(logEntry);
			log.flush();
		}
	}
	
	public static void logChatOutput(final Channel channel, final String line) {
		final String logEntry = SkadiLogging.currentTimestamp() + " | CHAT | " + channel.getURL() + " | " + line;
		System.out.println(logEntry);
		SkadiGUI.getInstance().appendLog(logEntry);
		
		final PrintWriter log = SkadiLogging.getInstance().chatLog;
		
		synchronized (log) {
			log.println(logEntry);
			log.flush();
		}
	}
	
	private static String currentTimestamp() {
		return SkadiLogging.TIMESTAMP_FORMAT.format(new Date());
	}
}
