package eu.over9000.skadi;

import javafx.application.Application;
import eu.over9000.skadi.lock.SingleInstanceLock;
import eu.over9000.skadi.ui.MainWindow;

public class Main {

	public static void main(final String[] args) throws Exception {
		if (!SingleInstanceLock.startSocketLock()) {
			return;
		}

		Application.launch(MainWindow.class, args);
		
		SingleInstanceLock.stopSocketLock();
	}

}