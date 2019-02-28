package org.gcube.spatial.data.geonetwork.configuration;

import java.util.Set;

import org.gcube.spatial.data.geonetwork.extension.ServerAccess.Version;
import org.gcube.spatial.data.geonetwork.model.Account;
import org.gcube.spatial.data.geonetwork.model.ScopeConfiguration;
import org.gcube.spatial.data.geonetwork.model.faults.EncryptionException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingConfigurationException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingServiceEndpointException;

public interface Configuration {

	public String getGeoNetworkEndpoint()throws MissingServiceEndpointException;	
	
	public Version getGeoNetworkVersion()throws MissingServiceEndpointException;
	
	/**
	 * 
	 * @return current scope configuration
	 * @throws MissingConfigurationException
	 * @throws EncryptionException
	 */
	public ScopeConfiguration getScopeConfiguration() throws MissingServiceEndpointException,MissingConfigurationException;
	
	/**
	 * Assign an available configuration to the current scope and returns it
	 * @throws MissingConfigurationException if no available configuration is found
	 * @return
	 */
	public ScopeConfiguration acquireConfiguration() throws MissingServiceEndpointException,MissingConfigurationException;
	public Account getAdminAccount() throws MissingServiceEndpointException;
		
	public Set<ScopeConfiguration> getExistingConfigurations()throws MissingServiceEndpointException;
	
	public Set<ScopeConfiguration> getParentScopesConfiguration()throws MissingServiceEndpointException;
	/**
	 * Store the passed configuration
	 * 
	 * @param toCreate
	 * @throws MissingConfigurationException 
	 * @throws EncryptionException
	 */
	public void createScopeConfiguration(ScopeConfiguration toCreate) throws MissingServiceEndpointException ;
}
