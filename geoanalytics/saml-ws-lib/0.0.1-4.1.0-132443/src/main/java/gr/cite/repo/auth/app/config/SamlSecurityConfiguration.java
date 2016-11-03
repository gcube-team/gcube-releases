package gr.cite.repo.auth.app.config;


import io.dropwizard.Configuration;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SamlSecurityConfiguration extends Configuration {
	
    @Valid
    @JsonProperty
    private Security security;

    @Valid
    @JsonProperty
    private SessionMgr sessionManager;

	public Security getSecurity() {
		return security;
	}

	public void setSecurity(Security security) {
		this.security = security;
	}

	public SessionMgr getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(SessionMgr sessionManager) {
		this.sessionManager = sessionManager;
	}
	
}



