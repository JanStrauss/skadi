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

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DownloadService extends Service<File> {
	private final static Logger LOGGER = LoggerFactory.getLogger(DownloadService.class);

	private static final int BUFFER_SIZE = 1024 * 8;
	private static final int NANOS_IN_SECOND = 1_000_000_000;

	private final HttpClient httpClient;
	private final String remoteUrl;
	private final File localFile;

	public DownloadService(final String remoteUrl, final File localFile) {
		httpClient = HttpClientBuilder.create().setSSLHostnameVerifier(new DefaultHostnameVerifier()).build();

		this.remoteUrl = remoteUrl;
		this.localFile = localFile;
	}

	@Override
	protected Task<File> createTask() {
		return new Task<File>() {
			protected File call() throws Exception {
				LOGGER.info(String.format("Downloading file %s to %s", remoteUrl, localFile.getAbsolutePath()));

				final HttpGet httpGet = new HttpGet(remoteUrl);
				final HttpResponse response = httpClient.execute(httpGet);
				OutputStream localFileStream = null;

				try (InputStream remoteContentStream = response.getEntity().getContent()) {
					final long fileSize = response.getEntity().getContentLength();
					LOGGER.debug(String.format("Size of file to download is %s", fileSize));

					localFileStream = new FileOutputStream(localFile);
					final byte[] buffer = new byte[BUFFER_SIZE];
					int sizeOfChunk;
					int amountComplete = 0;
					final long startTime = System.nanoTime();
					while ((sizeOfChunk = remoteContentStream.read(buffer)) != -1) {
						if (isCancelled()) {
							updateMessage("Download cancelled");
							return null;
						}

						localFileStream.write(buffer, 0, sizeOfChunk);

						amountComplete += sizeOfChunk;
						updateProgress(amountComplete, fileSize);


						final long bytesec = (long) ((double) amountComplete / (System.nanoTime() - startTime) * NANOS_IN_SECOND);

						updateMessage(String.format("Downloaded %s of %s kB (%d%% @%s/s)", FileUtils.byteCountToDisplaySize(amountComplete), FileUtils.byteCountToDisplaySize(fileSize), (int) ((double) amountComplete / (double) fileSize * 100.0), FileUtils.byteCountToDisplaySize(bytesec)));

					}
					updateMessage("Download completed");
					return localFile;
				} finally {
					if (localFileStream != null) {
						localFileStream.close();
					}
				}
			}
		};
	}

}

