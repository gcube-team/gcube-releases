package gr.cite.geoanalytics.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataStoreConfig {

	private static Logger log = LoggerFactory.getLogger(DataStoreConfig.class);

	private String host = null;
	private int port;
	private String description = null;
	private String databaseName = null;
	private String dataStoreName = null;
	private String password = null;
	private String user = null;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getDataStoreName() {
		return dataStoreName;
	}

	public void setDataStoreName(String dataStoreName) {
		log.trace("Setting datastore name: " + dataStoreName);
		this.dataStoreName = dataStoreName;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		log.trace("Setting datastore host: " + host);
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		log.trace("Setting datastore description: " + description);
		this.description = description;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		log.trace("Setting database name: " + databaseName);
		this.databaseName = databaseName;
	}
}
