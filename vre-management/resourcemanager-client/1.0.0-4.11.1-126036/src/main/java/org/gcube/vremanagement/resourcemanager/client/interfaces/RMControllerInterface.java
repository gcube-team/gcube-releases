package org.gcube.vremanagement.resourcemanager.client.interfaces;

import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;
import org.gcube.vremanagement.resourcemanager.client.exceptions.InvalidOptionsException;
import org.gcube.vremanagement.resourcemanager.client.exceptions.InvalidScopeException;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.CreateScopeParameters;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.OptionsParameters;

/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public interface RMControllerInterface {
	
	public Empty changeScopeOptions(OptionsParameters options) throws InvalidScopeException, InvalidOptionsException;
	
	public String disposeScope(String scope) throws InvalidScopeException;
	
	public Empty createScope(CreateScopeParameters params) throws InvalidScopeException, InvalidOptionsException; 
	

}
