/*******************************************************************************
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
 ******************************************************************************/
package eu.over9000.skadi.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.controlsfx.control.StatusBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.jcabi.manifests.Manifests;

import eu.over9000.skadi.service.helper.VersionCheckResult;
import eu.over9000.skadi.util.DesktopUtil;

/**
 * This class provides a method used to check the local version against the latest version on github.
 *
 * @author Jan Strauß
 */
public class VersionCheckerService extends Service<VersionCheckResult> {

	private static final String SKADI_BUILD = "Skadi-Build";

	private static final String SKADI_VERSION = "Skadi-Version";

	private final static String SKADI_RELEASES_URL = "https://github.com/s1mpl3x/skadi/releases/";

	private static final Logger LOGGER = LoggerFactory.getLogger(VersionCheckerService.class);

	private final VersionRetriever versionRetriever;

	public VersionCheckerService(final Stage window, final StatusBar sb) {
		this.versionRetriever = new VersionRetriever();
		this.setOnSucceeded(event -> {

			final VersionCheckResult result = (VersionCheckResult) event.getSource().getValue();

			final String remoteVersion = result.getRemoteVersion();
			final String localVersion = result.getLocalVersion();
			final String localBuild = result.getLocalBuild();

			VersionCheckerService.LOGGER.info("version: " + localVersion);
			VersionCheckerService.LOGGER.info("build: " + localBuild);

			switch (result.getCompareResult()) {
				case LOCAL_IS_LATEST:
					sb.setText("This is the latest version.");
					break;

				case LOCAL_IS_NEWER:
					sb.setText("This version (" + localVersion + ") is newer than the lastest public release version" +
							" " +
							"(" + remoteVersion + ") - use with caution");
					break;

				case LOCAL_IS_OLDER:
					final Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Update available");
					alert.setHeaderText(remoteVersion + " is available");

					final Label text = new Label("There is a newer version (" + remoteVersion + ") of Skadi " +
							"available" +
							"." +
							" You can download it from: ");
					final Hyperlink link = new Hyperlink(VersionCheckerService.SKADI_RELEASES_URL);
					link.setOnAction(e -> DesktopUtil.openWebpage(VersionCheckerService.SKADI_RELEASES_URL));

					alert.getDialogPane().setContent(new VBox(text, link));
					alert.initModality(Modality.APPLICATION_MODAL);
					alert.initOwner(window);
					alert.showAndWait();
					break;

				default:
					throw new IllegalStateException();
			}
		});
		this.setOnFailed(event -> {
			sb.setText("could not find local version, will skip version check");
			VersionCheckerService.LOGGER.info("could not find local version, will skip version check");
		});
	}

	@Override
	protected Task<VersionCheckResult> createTask() {
		return new Task<VersionCheckResult>() {

			@Override
			protected VersionCheckResult call() throws Exception {

				if (!Manifests.exists(VersionCheckerService.SKADI_VERSION) || !Manifests.exists(VersionCheckerService
						.SKADI_BUILD)) {
					throw new RuntimeException();
				}

				final String localVersionString = Manifests.read(VersionCheckerService.SKADI_VERSION);
				final String localBuildString = Manifests.read(VersionCheckerService.SKADI_BUILD);

				final String remoteVersionString = VersionCheckerService.this.versionRetriever.getLatestVersion();

				final DefaultArtifactVersion remoteVersion = new DefaultArtifactVersion(remoteVersionString);
				final DefaultArtifactVersion localVersion = new DefaultArtifactVersion(localVersionString);

				final int result = localVersion.compareTo(remoteVersion);

				return new VersionCheckResult(result, localVersionString, localBuildString, remoteVersionString);
			}
		};
	}

	private static class VersionRetriever {
		private final HttpClient httpClient = HttpClients.createMinimal();

		private final JsonParser parser = new JsonParser();

		private final String API_URL = "https://api.github.com/repos/s1mpl3x/skadi/releases";

		public String getLatestVersion() {
			try {
				final URI URL = new URI(this.API_URL);
				final HttpResponse response = this.httpClient.execute(new HttpGet(URL));

				final String responseString = new BasicResponseHandler().handleResponse(response);

				final JsonArray tagsArray = this.parser.parse(responseString).getAsJsonArray();

				return tagsArray.get(0).getAsJsonObject().get("tag_name").getAsString();
			} catch (URISyntaxException | IOException e) {
				VersionCheckerService.LOGGER.error("VersionRetriever exception", e);
			}
			return "";
		}
	}

}
