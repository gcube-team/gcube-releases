package org.gcube.vremanagement.resourcemanager.client.interfaces;

import org.gcube.vremanagement.resourcemanager.client.exceptions.InvalidScopeException;
import org.gcube.vremanagement.resourcemanager.client.exceptions.ResourcesCreationException;
import org.gcube.vremanagement.resourcemanager.client.exceptions.ResourcesRemovalException;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.AddResourcesParameters;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.RemoveResourcesParameters;

/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public interface RMBinderInterface {
	
	public String addResources(AddResourcesParameters params) throws ResourcesCreationException, InvalidScopeException;

	public String removeResources(RemoveResourcesParameters params) throws ResourcesRemovalException, InvalidScopeException;
}
