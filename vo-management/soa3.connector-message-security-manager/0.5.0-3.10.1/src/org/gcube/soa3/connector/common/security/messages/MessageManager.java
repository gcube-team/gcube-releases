package org.gcube.soa3.connector.common.security.messages;

import org.gcube.soa3.connector.common.security.Credentials;

/**
 * 
 * Message manager class: it manages different type of messages basing on the actual
 * implementation
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public interface MessageManager 
{
	/** 
	 * Sets the credentials
	 * @param credentials
	 */
	public void setCredentials (Credentials credentials);
	
}
