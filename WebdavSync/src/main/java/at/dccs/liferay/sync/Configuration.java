package at.dccs.liferay.sync;

import java.util.List;

/*
 * Helper class for JSON Configuration
 */
public class Configuration {
	List<Server> server;

	public List<Server> getServer() {
		return server;
	}

	public void setServer(List<Server> server) {
		this.server = server;
	}	
}
