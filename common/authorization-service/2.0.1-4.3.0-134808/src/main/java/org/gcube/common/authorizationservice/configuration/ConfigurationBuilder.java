package org.gcube.common.authorizationservice.configuration;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationBuilder {

	
	List<AuthorizationRule> rules = new ArrayList<AuthorizationRule>();
	
	List<String> ips = new ArrayList<String>();
		
	public static ConfigurationBuilder getBuilder(){
		return new ConfigurationBuilder();
	}
	
	public ConfigurationBuilder rule(AuthorizationRule rule){
		this.rules.add(rule);
		return this;
	}
	
	public ConfigurationBuilder autoConfirmedIp(String ip){
		this.ips.add(ip);
		return this;
	}
	
	public AuthorizationConfiguration build(){
		AuthorizationConfiguration configuration =  new AuthorizationConfiguration();
		configuration.setAuthorizationRules(rules);
		configuration.setAllowedContainerIps(ips);
		return configuration;
	}
	
	
}
