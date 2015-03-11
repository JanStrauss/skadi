package eu.over9000.skadi.service.helper;

public class VersionCheckResult {
	public enum VersionCompareResult {
		LOCAL_IS_LATEST, LOCAL_IS_NEWER, LOCAL_IS_OLDER;

		private static VersionCompareResult fromInt(final int result) {
			if (result < 0) {
				return VersionCompareResult.LOCAL_IS_OLDER;
			} else if (result == 0) {
				return LOCAL_IS_LATEST;
			} else {
				return LOCAL_IS_NEWER;
			}
		}
	}
	
	private final VersionCompareResult compareResult;
	private final String localVersion;
	private final String localBuild;
	private final String remoteVersion;

	public VersionCheckResult(final int result, final String localVersion, final String localBuild, final String remoteVersion) {
		this.compareResult = VersionCompareResult.fromInt(result);
		this.localVersion = localVersion;
		this.localBuild = localBuild;
		this.remoteVersion = remoteVersion;
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

	public String getRemoteVersion() {
		return this.remoteVersion;
	}

}