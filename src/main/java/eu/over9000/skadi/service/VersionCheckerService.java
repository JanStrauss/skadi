/*
 * The MIT License (MIT)
 *
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

package eu.over9000.skadi.service;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.controlsfx.control.StatusBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcabi.manifests.Manifests;

import eu.over9000.skadi.lock.SingleInstanceLock;
import eu.over9000.skadi.remote.VersionRetriever;
import eu.over9000.skadi.service.helper.RemoteVersionResult;
import eu.over9000.skadi.service.helper.VersionCheckResult;
import eu.over9000.skadi.ui.dialogs.PerformUpdateDialog;
import eu.over9000.skadi.ui.dialogs.UpdateAvailableDialog;

/**
 * This class provides a method used to check the local version against the latest version on github.
 *
 * @author Jan Strauß
 */
public class VersionCheckerService extends Service<VersionCheckResult> {

	private static final String SKADI_BUILD = "Skadi-Build";
	private static final String SKADI_VERSION = "Skadi-Version";
	private static final String SKADI_TIMESTAMP = "Skadi-Timestamp";

	private final static String SKADI_RELEASES_URL = "https://github.com/s1mpl3x/skadi/releases/";

	private static final Logger LOGGER = LoggerFactory.getLogger(VersionCheckerService.class);

	public VersionCheckerService(final Stage window, final StatusBar sb) {
		this.setOnSucceeded(event -> {

			final VersionCheckResult result = (VersionCheckResult) event.getSource().getValue();

			final String remoteVersion = result.getRemoteResult().getVersion();
			final String localVersion = result.getLocalVersion();
			final String localBuild = result.getLocalBuild();
			final String localTimestamp = result.getLocalTimestamp();
			final String latestDownloadLink = result.getRemoteResult().getDownloadURL();
			final int downloadSize = result.getRemoteResult().getSize();


			LOGGER.info("version: " + localVersion);
			LOGGER.info("build: " + localBuild);
			LOGGER.info("timestamp: " + Instant.ofEpochMilli(Long.valueOf(localTimestamp)));


			switch (result.getCompareResult()) {
				case LOCAL_IS_LATEST:
					sb.setText("This is the latest version.");
					break;

				case LOCAL_IS_NEWER:
					sb.setText("This version (" + localVersion + ") is newer than the latest public release version (" + remoteVersion + ") - use with caution");
					break;

				case LOCAL_IS_OLDER:
					sb.setText(remoteVersion + " is available");

					UpdateAvailableDialog dialog = new UpdateAvailableDialog(result.getRemoteResult());
					dialog.initModality(Modality.APPLICATION_MODAL);
					dialog.initOwner(window);
					final Optional<ButtonType> doDownload = dialog.showAndWait();
					if (doDownload.get() == UpdateAvailableDialog.UPDATE_BUTTON_TYPE) {
						window.hide();

						PerformUpdateDialog doDialog = new PerformUpdateDialog(result.getRemoteResult());
						doDialog.initModality(Modality.APPLICATION_MODAL);
						doDialog.initOwner(window);
						final Optional<File> newJar = doDialog.showAndWait();

						if (newJar.isPresent()) {
							LOGGER.info("closing socket..");
							SingleInstanceLock.stopSocketLock();
							try {
								LOGGER.info("starting new jar..");
								Runtime.getRuntime().exec("java -jar " + newJar.get().getAbsolutePath());
							} catch (IOException e) {
								LOGGER.error("error starting updated version", e);
							}
							LOGGER.info("begin shutdown");
							Platform.exit();
						} else {
							LOGGER.info("no jar given");
						}
					}


					break;

				default:
					throw new IllegalStateException();
			}
		});
		this.setOnFailed(event -> {
			sb.setText("could not find local version, will skip version check");
			LOGGER.info("could not find local version, will skip version check");
		});
	}

	@Override
	protected Task<VersionCheckResult> createTask() {
		return new Task<VersionCheckResult>() {

			@Override
			protected VersionCheckResult call() throws Exception {

				if (!Manifests.exists(SKADI_VERSION) || !Manifests.exists(SKADI_BUILD) || !Manifests.exists(SKADI_TIMESTAMP)) {
					//final RemoteVersionResult remoteResult = VersionRetriever.getLatestVersion();
					//return new VersionCheckResult(remoteResult, "1426850855799", "1532d69", "Skadi-2.0.0", -1);
					throw new RuntimeException();
				}

				final String localVersionString = Manifests.read(SKADI_VERSION);
				final String localBuildString = Manifests.read(SKADI_BUILD);
				final String localTimestampString = Manifests.read(SKADI_TIMESTAMP);

				final RemoteVersionResult remoteResult = VersionRetriever.getLatestVersion();


				final DefaultArtifactVersion remoteVersion = new DefaultArtifactVersion(remoteResult.getVersion());
				final DefaultArtifactVersion localVersion = new DefaultArtifactVersion(localVersionString);

				final int result = localVersion.compareTo(remoteVersion);

				return new VersionCheckResult(remoteResult, localTimestampString, localBuildString, localVersionString, result);
			}
		};
	}

}
