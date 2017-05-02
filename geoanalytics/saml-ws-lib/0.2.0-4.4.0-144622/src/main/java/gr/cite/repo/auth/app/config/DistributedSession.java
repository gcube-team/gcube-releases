package gr.cite.repo.auth.app.config;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DistributedSession {

	@NotNull
	@NotEmpty
	@JsonProperty
	private String workerName;

	@NotNull
	@JsonProperty
	private DatabaseInfo databaseInfo;

	@JsonProperty
	private Boolean simpleSessionManager;

	public String getWorkerName() {
		return workerName;
	}

	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}

	public DatabaseInfo getDatabaseInfo() {
		return databaseInfo;
	}

	public void setDatabaseInfo(DatabaseInfo databaseInfo) {
		this.databaseInfo = databaseInfo;
	}

	public Boolean getSimpleSessionManager() {
		return simpleSessionManager;
	}

	public void setSimpleSessionManager(Boolean simpleSessionManager) {
		this.simpleSessionManager = simpleSessionManager;
	}

}