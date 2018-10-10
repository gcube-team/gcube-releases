package org.gcube.spatial.data.sdi.engine.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.spatial.data.geonetwork.utils.ScopeUtils;
import org.gcube.spatial.data.sdi.engine.GISManager;
import org.gcube.spatial.data.sdi.engine.GeoNetworkManager;
import org.gcube.spatial.data.sdi.engine.SDIManager;
import org.gcube.spatial.data.sdi.engine.ThreddsManager;
import org.gcube.spatial.data.sdi.engine.impl.faults.InvalidServiceDefinitionException;
import org.gcube.spatial.data.sdi.engine.impl.faults.ServiceRegistrationException;
import org.gcube.spatial.data.sdi.model.ScopeConfiguration;
import org.gcube.spatial.data.sdi.model.health.HealthReport;
import org.gcube.spatial.data.sdi.model.health.Level;
import org.gcube.spatial.data.sdi.model.health.ServiceHealthReport;
import org.gcube.spatial.data.sdi.model.services.GeoNetworkServiceDefinition;
import org.gcube.spatial.data.sdi.model.services.GeoServerDefinition;
import org.gcube.spatial.data.sdi.model.services.ServiceDefinition;
import org.gcube.spatial.data.sdi.model.services.ThreddsDefinition;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Singleton
public class SDIManagerImpl implements SDIManager {

	GeoNetworkManager geonetworkManager;
	ThreddsManager threddsManager;
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
			toReturn.setGeonetworkConfiguration(geonetworkManager.getSuggestedInstances());			
		}catch(Exception e){
			log.warn("Scope is not well configured. Missing GeoNetwork. ",e);
		}

		try{
			toReturn.setThreddsConfiguration(threddsManager.getSuggestedInstances());
		}catch(Exception e){
			log.warn("THREDDS not found in current scope {} ",ScopeUtils.getCurrentScope());
		}

		try{
			toReturn.setGeoserverClusterConfiguration(gisManager.getSuggestedInstances());
		}catch(Exception e){
			log.warn("GeoServer not found in current scope {} ",ScopeUtils.getCurrentScope());
		}

		return toReturn;
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	@Override
	public HealthReport getHealthReport() {
		HealthReport report=new HealthReport();
		report.setContext(ScopeUtils.getCurrentScope());
		ServiceHealthReport threddsReport=threddsManager.getHealthReport();
		report.setThredds(threddsReport);
		ServiceHealthReport gnReport=geonetworkManager.getHealthReport();
		report.setGeonetwork(gnReport);
		ServiceHealthReport gsReport=gisManager.getHealthReport();
		report.setGeoserverCluster(gsReport);

		Level overall=Level.OK;
		if(threddsReport.getOverallStatus().equals(Level.ERROR)||
				gnReport.getOverallStatus().equals(Level.ERROR)||
				gsReport.getOverallStatus().equals(Level.ERROR)) overall=Level.ERROR;
		else if(threddsReport.getOverallStatus().equals(Level.WARNING)||
				gnReport.getOverallStatus().equals(Level.WARNING)||
				gsReport.getOverallStatus().equals(Level.WARNING)) overall=Level.WARNING;

		report.setOverallStatus(overall);
		log.debug("Returning report : {} ",report);
		return report;
	}


	@Override
	public String registerService(ServiceDefinition definition) throws ServiceRegistrationException{
		try {

			switch(definition.getType()) {
			case GEONETWORK :
				return geonetworkManager.registerService((GeoNetworkServiceDefinition)definition);

			case GEOSERVER :
				return gisManager.registerService((GeoServerDefinition)definition);

			case THREDDS :
				return threddsManager.registerService((ThreddsDefinition)definition);

			default : throw new InvalidServiceDefinitionException("Unable to register. Invalid service type. Definition was "+definition); 
			}		
		}catch(ClassCastException e) {
			throw new InvalidServiceDefinitionException("Unable to register. Incoherent service type. Definition was "+definition);
		}
	}
	
	@Override
	public String importService(String sourceToken, String host, ServiceDefinition.Type expectedType) throws ServiceRegistrationException {
		switch(expectedType) {
			case GEONETWORK :
				return geonetworkManager.importHostFromToken(sourceToken, host);

			case GEOSERVER :
				return gisManager.importHostFromToken(sourceToken, host);

			case THREDDS :
				return threddsManager.importHostFromToken(sourceToken, host);

			default : throw new InvalidServiceDefinitionException("Unable to register. Invalid service type "+expectedType); 
			}		
		}
	
	
	@Override
	public GeoNetworkManager getGeoNetworkManager() {
		return geonetworkManager;
	}
	
	@Override
	public GISManager getGeoServerManager() {
		return gisManager;
	}
	
	@Override
	public ThreddsManager getThreddsManager() {
		return threddsManager;
	}
}
