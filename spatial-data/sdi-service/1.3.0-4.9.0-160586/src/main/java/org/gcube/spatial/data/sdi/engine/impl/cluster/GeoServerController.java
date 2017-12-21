package org.gcube.spatial.data.sdi.engine.impl.cluster;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.spatial.data.geonetwork.utils.ScopeUtils;
import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.impl.faults.InvalidServiceEndpointException;
import org.gcube.spatial.data.sdi.engine.impl.faults.OutdatedServiceEndpointException;
import org.gcube.spatial.data.sdi.engine.impl.is.ISUtils;
import org.gcube.spatial.data.sdi.model.credentials.AccessType;
import org.gcube.spatial.data.sdi.model.credentials.Credentials;
import org.gcube.spatial.data.sdi.model.service.GeoServerDescriptor;

import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.manager.GeoServerRESTStoreManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeoServerController extends GeoServiceController<GeoServerDescriptor>{



	//CACHED INFO 
	private HashMap<String,HashSet<String>> dataStores=null;
	private HashSet<String> workspaces=null;
	private HashSet<String> styles;
	private Long hostedLayerCount=0l;


	public GeoServerController(ServiceEndpoint serviceEndpoint) throws InvalidServiceEndpointException {
		super(serviceEndpoint);
	}

	@Override
	public GeoServerDescriptor getLiveDescriptor() {
		GeoServerDescriptor toReturn=new GeoServerDescriptor();
		toReturn.setBaseEndpoint(baseURL);
		toReturn.setVersion(version);
		
		String scopeName=ScopeUtils.getCurrentScopeName();

		Map<String,Property> pointProperties=accessPoint.propertyMap();
		for(AccessType toLookForType:AccessType.values()) {
			String userNameProperty=toLookForType+"_u_"+scopeName;
			String passwordProperty=toLookForType+"_u_"+scopeName;
			if(pointProperties.containsKey(userNameProperty)) {
				String user=pointProperties.get(userNameProperty).value();
				String password=ISUtils.decryptString(pointProperties.get(passwordProperty).value());
				toReturn.getAccessibleCredentials().add(new Credentials(user,password,toLookForType));
			}
		}

		toReturn.getAccessibleCredentials().add(adminAccount);
		
		//Getting scope data spaces
		String confidentialProperty="confidential_"+scopeName;
		if(pointProperties.containsKey(confidentialProperty))
			toReturn.setConfidentialWorkspace(pointProperties.get(confidentialProperty).value());
		String contextProperty="context_"+scopeName;
		if(pointProperties.containsKey(contextProperty))
			toReturn.setContextVisibilityWorkspace(pointProperties.get(contextProperty).value());	
		String sharedProperty="shared_"+scopeName;
		if(pointProperties.containsKey(sharedProperty))
			toReturn.setSharedWorkspace(pointProperties.get(sharedProperty).value());
		String publicProperty="public_"+scopeName;
		if(pointProperties.containsKey(publicProperty))
			toReturn.setPublicWorkspace(pointProperties.get(publicProperty).value());

		toReturn.setHostedLayersCount(getHostedLayersCount());
		
		return toReturn;
	}

	@Override
	protected AccessPoint getTheRightAccessPoint(ServiceEndpoint endpoint) {
		for(AccessPoint declaredPoint:endpoint.profile().accessPoints().asCollection()) {
			if(declaredPoint.name().equals(LocalConfiguration.getProperty(LocalConfiguration.GEOSERVER_SE_ENDPOINT_NAME))) {
				return declaredPoint;				
			}
		}		
		return null;
	}




	// Controller logic

	
	@Override
	protected void initServiceEndpoint() throws OutdatedServiceEndpointException {
		// TODO Auto-generated method stub	
	}
	


	private long lastDatastoreUpdate=0l;
	private long lastWorkspaceUpdate=0l;
	private long lastStylesUpdate=0l;
	private long lastLayerCountUpdate=0l;


	public GeoServerRESTReader getReader() throws MalformedURLException{
		return getManager().getReader();
	}

	public GeoServerRESTStoreManager getDataStoreManager() throws IllegalArgumentException, MalformedURLException{
		return getManager().getStoreManager();
	}

	public GeoServerRESTPublisher getPublisher() throws IllegalArgumentException, MalformedURLException{
		return getManager().getPublisher();
	}

	protected GeoServerRESTManager getManager() throws IllegalArgumentException, MalformedURLException{
		return new GeoServerRESTManager(new URL(baseURL), adminAccount.getUsername(), adminAccount.getPassword());
	}


	public synchronized Set<String> getDatastores(String workspace){
		try {
			if(dataStores==null || (System.currentTimeMillis()-lastDatastoreUpdate>LocalConfiguration.getTTL(LocalConfiguration.GEOSERVER_DATASTORE_TTL))){
				log.trace("Loading datastores for {} ",baseURL);
				HashMap<String,HashSet<String>> toSet=new HashMap<>();
				for(String ws: getWorkspaces()){
					HashSet<String> currentWsDatastores=new HashSet<>(getLiveDatastores(ws));
					log.debug("Found {} ds in {} ws ",currentWsDatastores.size(),ws);
					toSet.put(ws, currentWsDatastores);
				}			
				dataStores=toSet;
				lastDatastoreUpdate=System.currentTimeMillis();
			}
		}catch(Throwable t) {
			log.warn("Unable to get Datastores for {} ",baseURL,t);
		}
		return dataStores.get(workspace);
	}

	public synchronized Long getHostedLayersCount(){
		try{
			if(System.currentTimeMillis()-lastLayerCountUpdate>LocalConfiguration.getTTL(LocalConfiguration.GEOSERVER_HOSTED_LAYERS_TTL)){
				log.trace("Loading layer count for {} ",baseURL);
				hostedLayerCount=getLiveHostedLayersCount();
				log.debug("Found {} layers ",hostedLayerCount);
				lastLayerCountUpdate=System.currentTimeMillis();
			}
		}catch(Throwable t){
			log.warn("Unable to get layer count for {} ",baseURL,t);
		}
		return hostedLayerCount;
	}


	public synchronized Set<String> getStyles(){
		try {
			if(styles==null||(System.currentTimeMillis()-lastStylesUpdate>LocalConfiguration.getTTL(LocalConfiguration.GEOSERVER_STYLES_TTL))){
				log.trace("Loading styles for {} ",baseURL);
				styles=new HashSet<>(getLiveStyles());			
				log.debug("Found {} styles ",styles.size());
				lastStylesUpdate=System.currentTimeMillis();
			}
		}catch(Throwable t) {
			log.warn("Unable to get Styles for {} ",baseURL,t);
		}
		return styles;
	}


	public synchronized Set<String> getWorkspaces() {
		try {
		if(workspaces==null||(System.currentTimeMillis()-lastWorkspaceUpdate>LocalConfiguration.getTTL(LocalConfiguration.GEOSERVER_WORKSPACE_TTL))){
			log.trace("Loading workspaces for {} ",baseURL);
			workspaces=new HashSet<String>(getLiveWorkspaces());
			log.debug("Found {} workspaces",workspaces.size());
			lastWorkspaceUpdate=0l;
		}
		}catch(Throwable t) {
			log.warn("Unable to get Workspaces for {} ",baseURL,t);
		}
		return workspaces;
	}


	public void invalidateWorkspacesCache(){
		lastWorkspaceUpdate=0l;
	}

	public void invalidateDatastoresCache(){
		lastDatastoreUpdate=0l;
	}

	public void invalidateStylesCache(){
		lastStylesUpdate=0l;
	}

	public void invalidateHostedLayersCountCache(){
		lastLayerCountUpdate=0l;
	}

	public void onChangedDataStores() {
		invalidateDatastoresCache();
	}
	public void onChangedLayers() {
		invalidateHostedLayersCountCache();
	}
	public void onChangedStyles() {
		invalidateStylesCache();
	}
	public void onChangedWorkspaces() {
		invalidateWorkspacesCache();
		invalidateDatastoresCache();
	}



	public Set<String> getLiveDatastores(String workspace) throws MalformedURLException {
		return new HashSet<String>(getReader().getDatastores(workspace).getNames());
	}


	public Long getLiveHostedLayersCount() throws MalformedURLException {
		return new Long(getReader().getLayers().size());
	}


	public Set<String> getLiveStyles() throws MalformedURLException {
		return new HashSet<String>(getReader().getStyles().getNames());
	}


	public Set<String> getLiveWorkspaces() throws MalformedURLException {
		return new HashSet<String>(getReader().getWorkspaceNames());
	}
}
