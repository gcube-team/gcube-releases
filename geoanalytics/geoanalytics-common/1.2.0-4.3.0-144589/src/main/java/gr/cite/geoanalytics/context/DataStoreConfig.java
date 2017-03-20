package gr.cite.geoanalytics.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DataStoreConfig {
	
	private static Logger log = LoggerFactory.getLogger(DataStoreConfig.class);
	
	private static final String dataStoreDefault = "geoanalytics";

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

	@Value("${gr.cite.geoanalytics.dataaccess.dbPass}")
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getUser() {
		return user;
	}

	@Value("${gr.cite.geoanalytics.dataaccess.dbUser}")
	public void setUser(String user) {
		this.user = user;
	}

	public String getDataStoreName() {
		return dataStoreName;
	}

	@Value("${gr.cite.geoanalytics.dataaccess.geoServerBridge.datastore:" + dataStoreDefault + "}")
	public void setDataStoreName(String dataStoreName) {
		log.trace("Setting datastore name: " + dataStoreName);
		this.dataStoreName = dataStoreName;
	}

	public String getHost() {
		return host;
	}
	
	@Value("${gr.cite.geoanalytics.dataaccess.geoServerBridge.datastore.host}")
	public void setHost(String host) {
		log.trace("Setting datastore host: " + host);
		this.host = host;
	}
	
	public int getPort() {
		return port;
	}

	@Value("${gr.cite.geoanalytics.dataaccess.geoServerBridge.datastore.port}")
	public void setPort(int port) {
		this.port = port;
	}

	public String getDescription() {
		return description;
	}
	
	@Value("${gr.cite.geoanalytics.dataaccess.geoServerBridge.datastore.description}")
	public void setDescription(String description) {
		log.trace("Setting datastore description: " + description);
		this.description = description;
	}
	
	public String getDatabaseName() {
		return databaseName;
	}
	
	@Value("${gr.cite.geoanalytics.dataaccess.geoServerBridge.datastore.database}")
	public void setDatabaseName(String databaseName) {
		log.trace("Setting database name: " + databaseName);
		this.databaseName = databaseName;
	}
	
}
