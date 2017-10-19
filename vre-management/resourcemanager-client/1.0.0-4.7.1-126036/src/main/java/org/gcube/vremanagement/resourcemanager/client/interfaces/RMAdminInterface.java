package org.gcube.vremanagement.resourcemanager.client.interfaces;

import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;
import org.gcube.vremanagement.resourcemanager.client.exceptions.InvalidScopeException;

/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public interface RMAdminInterface {
	
	public Empty cleanSoftwareState() throws InvalidScopeException;

}
