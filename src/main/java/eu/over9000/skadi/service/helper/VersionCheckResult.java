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

package eu.over9000.skadi.service.helper;

public class VersionCheckResult {
	private final VersionCompareResult compareResult;
	private final String localVersion;
	private final String localBuild;
	private final String localTimestamp;
	private final RemoteVersionResult remoteResult;

	public VersionCheckResult(final RemoteVersionResult remoteResult, final String localTimestamp, final String localBuild, final String localVersion, final int compareResult) {
		this.localTimestamp = localTimestamp;
		this.localBuild = localBuild;
		this.localVersion = localVersion;
		this.remoteResult = remoteResult;
		this.compareResult = VersionCompareResult.fromInt(compareResult);
	}

	public VersionCompareResult getCompareResult() {
		return this.compareResult;
	}

	public String getLocalVersion() {
		return this.localVersion;
	}

	public String getLocalBuild() {
		return this.localBuild;
	}

	public String getLocalTimestamp() {
		return this.localTimestamp;
	}

	public RemoteVersionResult getRemoteResult() {
		return this.remoteResult;
	}

	public enum VersionCompareResult {
		LOCAL_IS_LATEST, LOCAL_IS_NEWER, LOCAL_IS_OLDER;

		private static VersionCompareResult fromInt(final int result) {
			if (result < 0) {
				return LOCAL_IS_OLDER;
			} else if (result == 0) {
				return LOCAL_IS_LATEST;
			} else {
				return LOCAL_IS_NEWER;
			}
		}
	}

}
