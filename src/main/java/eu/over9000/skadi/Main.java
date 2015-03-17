package eu.over9000.skadi;

import javafx.application.Application;
import eu.over9000.skadi.lock.SingleInstanceLock;
import eu.over9000.skadi.ui.MainWindow;
import eu.over9000.skadi.util.VersionUtil;

public class Main {
	
	public static void main(final String[] args) throws Exception {
		System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");

		if (!VersionUtil.checkRequiredVersionIsPresent()) {
			System.err.println("Skadi requires Java " + VersionUtil.REQUIRED_VERSION + ", exiting");
			return;
		}
		
		if (!SingleInstanceLock.startSocketLock()) {
			System.err.println("another instance is up, exiting");
			return;
		}

		Application.launch(MainWindow.class, args);

		SingleInstanceLock.stopSocketLock();

		System.exit(0);
	}
}