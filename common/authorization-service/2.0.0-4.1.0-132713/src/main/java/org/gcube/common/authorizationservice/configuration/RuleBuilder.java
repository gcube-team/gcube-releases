package org.gcube.common.authorizationservice.configuration;

import java.util.ArrayList;
import java.util.List;


public class RuleBuilder {

	private String servletPath;
	
	private List<AllowedEntity> entities = new ArrayList<AllowedEntity>();
		
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

	public RuleBuilder needsToken(boolean needsToken){
		this.requiresToken= needsToken;
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
		return new AuthorizationRule(servletPath, entities, requiresToken);
	}
}