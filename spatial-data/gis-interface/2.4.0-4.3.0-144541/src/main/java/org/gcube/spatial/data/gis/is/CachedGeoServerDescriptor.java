package org.gcube.spatial.data.gis.is;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.gcube.spatial.data.gis.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CachedGeoServerDescriptor extends LiveGeoServerDescriptor {
	

	
	
	private long lastDatastoreUpdate=0l;
	private long lastWorkspaceUpdate=0l;
	private long lastStylesUpdate=0l;
	private long lastLayerCountUpdate=0l;
	
	private HashMap<String,HashSet<String>> dataStores=null;
	private HashSet<String> workspaces=null;
	private HashSet<String> styles;
	private Long hostedLayerCount=0l;
	
	
	public CachedGeoServerDescriptor(String url, String user, String password) {
		super(url, user, password);
		// TODO Auto-generated constructor stub
	}


	@Override
	public synchronized Set<String> getDatastores(String workspace) throws MalformedURLException {
		if(dataStores==null || (System.currentTimeMillis()-lastDatastoreUpdate>Configuration.getTTL(Configuration.GEOSERVER_DATASTORE_TTL))){
			log.trace("Loading datastores for {} ",getUrl());
			HashMap<String,HashSet<String>> toSet=new HashMap<>();
			for(String ws: getWorkspaces()){
				HashSet<String> currentWsDatastores=new HashSet<>(super.getDatastores(ws));
				log.debug("Found {} ds in {} ws ",currentWsDatastores.size(),ws);
				toSet.put(ws, currentWsDatastores);
			}			
			dataStores=toSet;
			lastDatastoreUpdate=System.currentTimeMillis();
		}
		return dataStores.get(workspace);
	}
	
	@Override
	public synchronized Long getHostedLayersCount() throws MalformedURLException {
		if(System.currentTimeMillis()-lastLayerCountUpdate>Configuration.getTTL(Configuration.GEOSERVER_HOSTED_LAYERS_TTL)){
			log.trace("Loading layer count for {} ",getUrl());
			hostedLayerCount=super.getHostedLayersCount();
			log.debug("Found {} layers ",hostedLayerCount);
			lastLayerCountUpdate=System.currentTimeMillis();
		}
		return hostedLayerCount;
	}
	
	
	@Override
	public synchronized Set<String> getStyles() throws MalformedURLException {
		if(styles==null||(System.currentTimeMillis()-lastStylesUpdate>Configuration.getTTL(Configuration.GEOSERVER_STYLES_TTL))){
			log.trace("Loading styles for {} ",getUrl());
			styles=new HashSet<>(super.getStyles());			
			log.debug("Found {} styles ",styles.size());
			lastStylesUpdate=System.currentTimeMillis();
		}
		return styles;
	}
	
	
	@Override
	public synchronized Set<String> getWorkspaces() throws MalformedURLException {
		if(workspaces==null||(System.currentTimeMillis()-lastWorkspaceUpdate>Configuration.getTTL(Configuration.GEOSERVER_WORKSPACE_TTL))){
			log.trace("Loading workspaces for {} ",getUrl());
			workspaces=new HashSet<String>(super.getWorkspaces());
			log.debug("Found {} workspaces",workspaces.size());
			lastWorkspaceUpdate=0l;
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
	
	@Override
	public void onChangedDataStores() {
		invalidateDatastoresCache();
	}
	@Override
	public void onChangedLayers() {
		invalidateHostedLayersCountCache();
	}
	@Override
	public void onChangedStyles() {
		invalidateStylesCache();
	}
	@Override
	public void onChangedWorkspaces() {
		invalidateWorkspacesCache();
		invalidateDatastoresCache();
	}

	
}
