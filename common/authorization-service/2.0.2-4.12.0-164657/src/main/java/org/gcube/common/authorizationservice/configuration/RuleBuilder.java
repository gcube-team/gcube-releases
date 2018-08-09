package org.gcube.common.authorizationservice.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.common.authorization.library.ClientType;


public class RuleBuilder {

	private String servletPath;
	
	private List<AllowedEntity> entities = new ArrayList<AllowedEntity>();
	
	private List<ClientType> acceptedClients = new ArrayList<ClientType>();
		
	private boolean requiresToken= true;
	
	public RuleBuilder(){}
	
	public RuleBuilder path(String servletPath){
		this.servletPath = servletPath;
		return this;
	}
	
	public RuleBuilder entity(AllowedEntity entity){
		this.entities.add(entity);
		return this;
	}

	public RuleBuilder needsToken(ClientType ... typesAccepted){
		this.requiresToken= true;
		this.acceptedClients = Arrays.asList(typesAccepted);
		return this;
	}
	
	protected String getServletPath() {
		return servletPath;
	}

	protected List<AllowedEntity> getEntities() {
		return entities;
	}
	
	protected boolean isRequiresToken() {
		return requiresToken;
	}
	
	public AuthorizationRule build(){
		return new AuthorizationRule(servletPath, entities, requiresToken, acceptedClients);
	}
}