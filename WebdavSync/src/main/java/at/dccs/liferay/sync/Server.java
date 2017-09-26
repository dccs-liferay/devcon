package at.dccs.liferay.sync;

import java.util.List;

/*
 *  * Helper class for JSON Configuration
 *   */
public class Server {
	boolean enabled;
	boolean upload;
	String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	String url;
	String username;
	String password;
	List<Folder> folders;
	List<String> blacklist;
	
	public List<String> getBlacklist() {
		return blacklist;
	}
	public void setBlacklist(List<String> blacklist) {
		this.blacklist = blacklist;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public boolean isUpload() {
		return upload;
	}
	public void setUpload(boolean upload) {
		this.upload = upload;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<Folder> getFolders() {
		return folders;
	}
	public void setFolders(List<Folder> path) {
		this.folders = path;
	}
	public boolean isBlacklisted(String filename) {
		if(blacklist == null || blacklist.isEmpty()) return false;
		return blacklist.contains(filename);
	}
}
