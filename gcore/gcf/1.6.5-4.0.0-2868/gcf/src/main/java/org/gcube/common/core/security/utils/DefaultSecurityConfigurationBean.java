package org.gcube.common.core.security.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * An utility bean to store the default security configuration for every service
 * 
 * @author Ciro Formisano
 *
 */
class DefaultSecurityConfigurationBean 
{
	private String 	auth_method;
	private Set <String> protection_level;
	private boolean enabled;
	private boolean override;

	public static final String 	GSI_SECURE_CONVERSATION = "GSISecureConversation",
								GSI_SECURE_TRANSPORT = "GSITransport",
								PL_PRIVACY = "privacy",
								PL_INTEGRITY = "integrity",
								NONE= "none";
	
	public DefaultSecurityConfigurationBean() 
	{
		this.protection_level = new HashSet<String>();;
		this.enabled = false;
		this.override = false;
	}
	
	
	
	public boolean isEnabled() 
	{
		return enabled;
	}



	public void setEnabled(boolean enabled) 
	{
		this.enabled = enabled;
	}

	public boolean isOverride() 
	{
		return override;
	}

	public void setOverride(String override) 
	{
		if (override != null && override.equalsIgnoreCase("true")) this.override = true;
		else this.override = false;
	}


	public String getIn_auth_method() 
	{
		return auth_method;
	}

	public void setAuth_method(String auth_method) throws Exception
	{
		this.auth_method = parseAuthMethod(auth_method);

	}
	
	private String parseAuthMethod (String authMethod) throws Exception 
	{
		String response = null;
		
		if (authMethod.equalsIgnoreCase(GSI_SECURE_CONVERSATION)) response = GSI_SECURE_CONVERSATION;
		else if (authMethod.equalsIgnoreCase(GSI_SECURE_TRANSPORT))response = GSI_SECURE_TRANSPORT;
		else if (authMethod.equalsIgnoreCase(NONE)) response = NONE;
		else throw new Exception("Invalid auth method "+authMethod);
		
		return response;
	}
	

	public Set<String> getIn_protection_levels() 
	{
		return protection_level;
	}

	public void addProtection_level(String protection_level) throws Exception
	{
		if (this.protection_level.size() == 2) throw new Exception("Too many prot level");
		else this.protection_level.add(parseProtectionLevel(protection_level));

	}
	
	private String parseProtectionLevel (String protLevel) throws Exception 
	{
		protLevel = protLevel.toLowerCase();
		String response = null;
		
		if (protLevel.equals(PL_PRIVACY) || protLevel.equals(PL_INTEGRITY)) response = protLevel;
		else throw new Exception("Invalid protection level "+protLevel);
		
		return response;
	}	

}
