/*
 * Copyright (c) 2014-2016 Jan Strauß <jan[at]over9000.eu>
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

package eu.over9000.skadi.service.helper;

public class RemoteVersionResult {

	private final String version;
	private final String downloadURL;
	private final String published;
	private final String changeLog;
	private final int size;

	public RemoteVersionResult(final String version, final String published, final String downloadURL, final String changelog, final int size) {
		this.version = version;
		this.downloadURL = downloadURL;
		this.published = published;
		changeLog = changelog;
		this.size = size;
	}

	@Override
	public String toString() {
		return "RemoteVersionResult{" +
				"version='" + version + '\'' +
				", downloadURL='" + downloadURL + '\'' +
				", published='" + published + '\'' +
				", changeLog='" + changeLog + '\'' +
				", size=" + size +
				'}';
	}

	public String getVersion() {
		return version;
	}

	public String getDownloadURL() {
		return downloadURL;
	}

	public int getSize() {
		return size;
	}

	public String getPublished() {
		return published;
	}

	public String getChangeLog() {
		return changeLog;
	}
}
