/*
 * Copyright (c) 2014-2016 s1mpl3x <jan[at]over9000.eu>
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

import eu.over9000.skadi.lock.SingleInstanceLock;
import eu.over9000.skadi.remote.VersionRetriever;
import eu.over9000.skadi.service.helper.RemoteVersionResult;
import eu.over9000.skadi.service.helper.VersionCheckResult;
import eu.over9000.skadi.ui.StatusBarWrapper;
import eu.over9000.skadi.ui.dialogs.PerformUpdateDialog;
import eu.over9000.skadi.ui.dialogs.UpdateAvailableDialog;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * This class provides a method used to check the local version against the latest version on github.
 */
public class VersionCheckerService extends AbstractSkadiService<VersionCheckResult> {

	private static final Logger LOGGER = LoggerFactory.getLogger(VersionCheckerService.class);

	public VersionCheckerService(final Stage window, final StatusBarWrapper sb) {
		setOnSucceeded(event -> {

			final VersionCheckResult result = (VersionCheckResult) event.getSource().getValue();

			if (result == null) {
				LOGGER.error("version check could not be completed.");
				return;
			}

			final String remoteVersion = result.getRemoteResult().getVersion();
			final String localVersion = result.getLocalVersion();

			switch (result.getCompareResult()) {
				case LOCAL_IS_LATEST:
					final String msg_latest = "This is the latest version.";
					sb.updateStatusText(msg_latest);
					LOGGER.info(msg_latest);
					break;

				case LOCAL_IS_NEWER:
					final String msg_newer = "This version (" + localVersion + ") is newer than the latest public release version (" + remoteVersion + ") - use with caution";
					LOGGER.info(msg_newer);
					sb.updateStatusText(msg_newer);
					break;

				case LOCAL_IS_OLDER:
					final String msg_older = remoteVersion + " is available";
					LOGGER.info(msg_older);
					sb.updateStatusText(msg_older);

					final UpdateAvailableDialog dialog = new UpdateAvailableDialog(result.getRemoteResult());
					dialog.initModality(Modality.APPLICATION_MODAL);
					dialog.initOwner(window);
					final Optional<ButtonType> doDownload = dialog.showAndWait();
					if (doDownload.isPresent() && doDownload.get() == UpdateAvailableDialog.UPDATE_BUTTON_TYPE) {
						window.hide();

						final PerformUpdateDialog doDialog = new PerformUpdateDialog(result.getRemoteResult());
						doDialog.initModality(Modality.APPLICATION_MODAL);
						doDialog.initOwner(window);
						final Optional<File> newJar = doDialog.showAndWait();

						if (newJar.isPresent()) {
							LOGGER.info("closing socket..");
							SingleInstanceLock.stopSocketLock();
							try {
								LOGGER.info("starting new jar..");
								Runtime.getRuntime().exec("java -jar " + newJar.get().getAbsolutePath());
							} catch (final IOException e) {
								LOGGER.error("error starting updated version", e);
							}
							LOGGER.info("begin shutdown");
							Platform.exit();
						} else {
							LOGGER.info("no jar given, showing ui again");
							window.show();
						}
					}
					break;

				default:
					throw new IllegalStateException();
			}
		});
		setOnFailed(event -> sb.updateStatusText("could not find local version, will skip version check"));
	}

	@Override
	protected Task<VersionCheckResult> createTask() {
		return new Task<VersionCheckResult>() {

			@Override
			protected VersionCheckResult call() throws Exception {

				if (!VersionRetriever.isLocalInfoAvailable()) {
					LOGGER.error("could not find local version/build/timestamp");
					return null;
				}

				final String localVersionString = VersionRetriever.getLocalVersion();
				final String localBuildString = VersionRetriever.getLocalBuild();
				final String localTimestampString = VersionRetriever.getLocalTimestamp();

				final RemoteVersionResult remoteResult = VersionRetriever.getLatestVersion();

				if (remoteResult == null) {
					LOGGER.error("could not retrieve remote version");
					return null;
				}

				final DefaultArtifactVersion remoteVersion = new DefaultArtifactVersion(remoteResult.getVersion());
				final DefaultArtifactVersion localVersion = new DefaultArtifactVersion(localVersionString);

				final int result = localVersion.compareTo(remoteVersion);

				return new VersionCheckResult(remoteResult, localTimestampString, localBuildString, localVersionString, result);
			}
		};
	}

}
