package org.gcube.spatial.data.sdi.engine.impl;

import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.spatial.data.geonetwork.utils.ScopeUtils;
import org.gcube.spatial.data.sdi.engine.GISManager;
import org.gcube.spatial.data.sdi.engine.GeoNetworkManager;
import org.gcube.spatial.data.sdi.engine.SDIManager;
import org.gcube.spatial.data.sdi.engine.ThreddsManager;
import org.gcube.spatial.data.sdi.model.ScopeConfiguration;
import org.gcube.spatial.data.sdi.model.credentials.AccessType;
import org.gcube.spatial.data.sdi.model.credentials.Credentials;
import org.gcube.spatial.data.sdi.model.service.GeoNetworkConfiguration;
import org.gcube.spatial.data.sdi.model.service.GeoServerClusterConfiguration;
import org.gcube.spatial.data.sdi.model.service.GeoServerConfiguration;
import org.gcube.spatial.data.sdi.model.service.ThreddsConfiguration;
import org.gcube.spatial.data.sdi.model.service.Version;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Singleton
public class SDIManagerImpl implements SDIManager {

//	@Inject
	GeoNetworkManager geonetworkManager;
//	@Inject
	ThreddsManager threddsManager;
//	@Inject
	GISManager gisManager;
	
	
	
	
	@Inject
	public SDIManagerImpl(GeoNetworkManager geonetworkManager, ThreddsManager threddsManager, GISManager gisManager) {
		super();
		this.geonetworkManager = geonetworkManager;
		this.threddsManager = threddsManager;
		this.gisManager = gisManager;
	}





	@Override
	public ScopeConfiguration getContextConfiguration() {
				
		// TODO filter info by user role
		
		ScopeConfiguration toReturn=new ScopeConfiguration();
		toReturn.setContextName(ScopeUtils.getCurrentScopeName());
		try{
			toReturn.setGeonetworkConfiguration(geonetworkManager.getConfiguration());			
		}catch(Exception e){
			log.warn("Scope is not well configured. Missing GeoNetwork. ",e);
		}
		
		try{
			toReturn.setThreddsConfiguration(threddsManager.getConfiguration());
		}catch(Exception e){
			log.warn("THREDDS not found in current scope {} ",ScopeUtils.getCurrentScope());
		}
		
		try{
			toReturn.setGeoserverClusterConfiguration(gisManager.getConfiguration());
		}catch(Exception e){
			log.warn("GeoServer not found in current scope {} ",ScopeUtils.getCurrentScope());
		}
		
		return toReturn;
	}
	
}
