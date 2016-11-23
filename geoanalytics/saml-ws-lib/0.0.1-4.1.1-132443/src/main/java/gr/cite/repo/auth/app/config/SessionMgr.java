package gr.cite.repo.auth.app.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("sessionManager")
public class SessionMgr {

	@JsonProperty
	private Boolean simpleSessionManager;
	
	@JsonProperty
	private DistributedSession distributedSession;

	public Boolean getSimpleSessionManager() {
		return simpleSessionManager;
	}

	public void setSimpleSessionManager(Boolean simpleSessionManager) {
		this.simpleSessionManager = simpleSessionManager;
	}

	public DistributedSession getDistributedSession() {
		return distributedSession;
	}

	public void setDistributedSession(DistributedSession distributedSession) {
		this.distributedSession = distributedSession;
	}
	
}