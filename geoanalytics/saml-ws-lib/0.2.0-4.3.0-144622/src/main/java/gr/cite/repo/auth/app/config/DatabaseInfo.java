package gr.cite.repo.auth.app.config;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DatabaseInfo {

	@NotNull
	@Valid
	@JsonProperty(value = "database")
	private String database;

	@NotNull
	@Valid
	@JsonProperty(value = "username")
	private String username;

	@NotNull
	@Valid
	@JsonProperty(value = "password")
	private String password;

	@NotNull
	@Valid
	@JsonProperty(value = "databaseName")
	private String databaseName;

	@Valid
	@JsonProperty(value = "serverPort")
	private String serverPort;

	@Valid
	@JsonProperty(value = "serverName")
	private String serverName;

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
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

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getServerPort() {
		return serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

}