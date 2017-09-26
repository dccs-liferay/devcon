package at.dccs.liferay.sync;

/*
 *  * Helper class for JSON Configuration
 *   */
public class Folder {
	String remotePath;
	String syncFolder;	
	String localPath;
	
	public String getRemotePath() {
		return remotePath;
	}
	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}
	public String getSyncFolder() {
		return syncFolder;
	}
	public void setSyncFolder(String syncFolder) {
		this.syncFolder = syncFolder;
	}
	public String getLocalPath() {
		return localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
}
