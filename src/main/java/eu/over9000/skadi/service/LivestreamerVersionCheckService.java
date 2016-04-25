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

package eu.over9000.skadi.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import org.controlsfx.control.StatusBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.over9000.skadi.model.StateContainer;

public class LivestreamerVersionCheckService extends Service<String> {

	private static final String VERSION_IN_BRACKETS = "\\(((\\d|\\.)+)\\)";
	private static final Pattern NEW_VERSION = Pattern.compile("A new version of Livestreamer " + VERSION_IN_BRACKETS + " is available!");

	private static final String FAILED_MESSAGE = "failed to check livestreamer version";
	private static final String PREFIX = "[cli][info] ";

	private static final Logger LOGGER = LoggerFactory.getLogger(LivestreamerVersionCheckService.class);

	public LivestreamerVersionCheckService(final StatusBar sb) {
		setOnSucceeded(event -> {
			final String message = (String) event.getSource().getValue();

			if (message != null) {
				sb.setText(message);
			}
		});
	}

	@Override
	protected Task<String> createTask() {
		return new Task<String>() {

			@Override
			protected String call() throws Exception {
				final String livestreamerExec = StateContainer.getInstance().getExecutableLivestreamer();

				try {
					final Process process = new ProcessBuilder(livestreamerExec, "--version-check").redirectErrorStream(true).start();

					final BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String line = br.readLine();
					br.close();
					process.waitFor();

					if (line == null) {
						return FAILED_MESSAGE;
					}

					line = line.replace(PREFIX, "");

					LOGGER.info("Livestreamer version check result: " + line);

					return NEW_VERSION.matcher(line).matches() ? line : null;

				} catch (final IOException e) {
					LOGGER.error(FAILED_MESSAGE, e);
					return FAILED_MESSAGE;
				}
			}
		};
	}
}