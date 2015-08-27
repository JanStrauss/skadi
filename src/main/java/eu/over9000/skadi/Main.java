/*
 * Copyright (c) 2014-2015 s1mpl3x <jan[at]over9000.eu>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package eu.over9000.skadi;

import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.util.Arrays;

import javafx.application.Application;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.over9000.skadi.lock.SingleInstanceLock;
import eu.over9000.skadi.remote.VersionRetriever;
import eu.over9000.skadi.ui.MainWindow;
import eu.over9000.skadi.util.JavaVersionUtil;

public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	public static void main(final String[] args) throws Exception {
		System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");

		printStartupInfo(args);

		if (!JavaVersionUtil.checkRequiredVersionIsPresent()) {
			System.err.println("Skadi requires Java " + JavaVersionUtil.REQUIRED_VERSION + ", exiting");
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

	private static void printStartupInfo(final String[] args) {
		LOGGER.info("################################################################################");
		LOGGER.info("TIME:    " + LocalDateTime.now().toString());
		LOGGER.info("OS:      " + SystemUtils.OS_NAME + " " + SystemUtils.OS_VERSION + " " + SystemUtils.OS_ARCH);
		LOGGER.info("JAVA:    " + SystemUtils.JAVA_VERSION);
		LOGGER.info("         " + SystemUtils.JAVA_RUNTIME_NAME + " <build " + SystemUtils.JAVA_RUNTIME_VERSION + ">");
		LOGGER.info("VM:      " + SystemUtils.JAVA_VM_NAME + " <build" + SystemUtils.JAVA_VM_VERSION + ", " + SystemUtils.JAVA_VM_INFO + ">");
		LOGGER.info("VM-ARGS: " + ManagementFactory.getRuntimeMXBean().getInputArguments());
		if (VersionRetriever.isLocalInfoAvailable()) {
			LOGGER.info("SKADI:   " + VersionRetriever.getLocalVersion() + " " + VersionRetriever.getLocalBuild() + " " + VersionRetriever.getLocalTimestamp());
		} else {
			LOGGER.info("SKADI:   " + "No local version info available");
		}
		LOGGER.info("ARGS:    " + Arrays.asList(args));
		LOGGER.info("################################################################################");
	}
}