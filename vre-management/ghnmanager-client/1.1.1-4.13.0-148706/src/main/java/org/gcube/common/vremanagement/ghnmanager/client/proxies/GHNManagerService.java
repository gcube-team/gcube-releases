package org.gcube.common.vremanagement.ghnmanager.client.proxies;

import static org.gcube.common.vremanagement.ghnmanager.client.fws.Types.*;

import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;

/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public interface GHNManagerService {
	
	public boolean addScope(AddScopeInputParams params) throws Exception;
	
	public boolean removeScope(String scope) throws Exception;
	
	public boolean addRIToScope(ScopeRIParams params) throws Exception;
	
	public boolean activateRI(RIData params) throws Exception;
	
	public boolean deactivateRI(RIData params) throws Exception;
	
	public boolean removeRIFromScope(ScopeRIParams params) throws Exception;
	
	public void shutdown(ShutdownOptions options) throws Exception;
}
