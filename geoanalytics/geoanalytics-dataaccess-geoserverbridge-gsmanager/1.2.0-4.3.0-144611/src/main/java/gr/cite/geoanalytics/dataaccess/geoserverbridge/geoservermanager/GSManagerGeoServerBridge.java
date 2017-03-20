package gr.cite.geoanalytics.dataaccess.geoserverbridge.geoservermanager;

import gr.cite.geoanalytics.context.GeoServerBridgeConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.SystemPresentationConfig;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.GeoServerBridge;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.DataStore;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.FeatureType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.GeoserverLayer;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.exception.GeoServerBridgeException;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.util.ScaledStyleCreator;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.decoder.RESTDataStore;
import it.geosolutions.geoserver.rest.decoder.RESTDataStoreList;
import it.geosolutions.geoserver.rest.decoder.RESTFeatureType;
import it.geosolutions.geoserver.rest.decoder.RESTLayer;
import it.geosolutions.geoserver.rest.decoder.RESTStyle;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder.ProjectionPolicy;
import it.geosolutions.geoserver.rest.encoder.datastore.GSPostGISDatastoreEncoder;
import it.geosolutions.geoserver.rest.encoder.feature.GSFeatureTypeEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSFeatureDimensionInfoEncoder;
import it.geosolutions.geoserver.rest.manager.GeoServerRESTStoreManager;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GSManagerGeoServerBridge implements GeoServerBridge {
	private static Logger logger = LoggerFactory.getLogger(GSManagerGeoServerBridge.class);

	private GeoServerRESTReader reader;
	private GeoServerRESTPublisher publisher;
	private GeoServerBridgeConfig geoServerBridgeConfig;
	private GeoServerRESTStoreManager storeManager;	
	
	public GSManagerGeoServerBridge(GeoServerBridgeConfig geoServerBridgeConfig){
		this.geoServerBridgeConfig = geoServerBridgeConfig;
	}
	
	@Inject
	public void setGeoServerBridgeConfig(GeoServerBridgeConfig geoServerBridgeConfig) {
		logger.trace("Setting configuration: "+geoServerBridgeConfig);
		this.geoServerBridgeConfig = geoServerBridgeConfig;
	}
	
	@PostConstruct
	private void initialize(){
		logger.debug("Initializing GSManagerGeoServerBridge...");
		try {
			this.reader = new GeoServerRESTReader(geoServerBridgeConfig.getGeoServerBridgeUrl(),
					geoServerBridgeConfig.getGeoServerBridgeUser(),
					geoServerBridgeConfig.getGeoServerBridgePassword());
		} catch (MalformedURLException e) {
			logger.error("Problem while initializng GeoServerRESTReader: "+e);
		}
		this.publisher = new GeoServerRESTPublisher(
				geoServerBridgeConfig.getGeoServerBridgeUrl(),
				geoServerBridgeConfig.getGeoServerBridgeUser(),
				geoServerBridgeConfig.getGeoServerBridgePassword());
		try {
			this.storeManager = new GeoServerRESTStoreManager(
					new URL(geoServerBridgeConfig.getGeoServerBridgeUrl()),
					geoServerBridgeConfig.getGeoServerBridgeUser(),
					geoServerBridgeConfig.getGeoServerBridgePassword());
		} catch (IllegalArgumentException | MalformedURLException e) {
			logger.error("Problem while initializng GeoServerRESTStoreManager: "+e);
		}
		logger.debug("Initialized GSManagerGeoServerBridge");
	}

	@Override
	public List<String> listDataStores() throws GeoServerBridgeException
	{
		logger.debug("Listing Datastores...");
		try
		{
			RESTDataStoreList datastores = reader.getDatastores(geoServerBridgeConfig.getGeoServerBridgeWorkspace());
			if(datastores == null){
				logger.debug("List of Datastores is null");
				return null;
			}
			logger.debug("List of Datastores will be returned");
			return datastores.getNames();
		} catch (Exception e)
		{
			logger.error("Error while listing data stores", e);
			throw new GeoServerBridgeException(
					"Error while listing data stores", e);
		}
	}

	@Override
	public DataStore getDataStore(String name) throws GeoServerBridgeException
	{
		logger.debug("Getting Datastore with name: "+ name);
		
		RESTDataStore dsr = null;
		try
		{
			dsr = reader.getDatastore(geoServerBridgeConfig.getGeoServerBridgeWorkspace(), name);
		} catch (Exception e)
		{
			logger.error("Error while retrieving data store info", e);
			throw new GeoServerBridgeException("Error while retrieving data store info", e);
		}
		if(dsr == null) return null;
		DataStore ds = new DataStore();
		ds.setDataStoreName(dsr.getName());
		ds.setWorkspace(ds.getWorkspace());
		ds.setDbType(dsr.getType().toString());
		logger.debug("Got Datastore with name: "+ name);
		return ds;
	}
	
	@Override
	public Boolean dataStoreExists(String workspaceName, String dataStoreName) throws GeoServerBridgeException{
		
		logger.debug("Checking if Datastore with name: "+ dataStoreName +" in workspace "+workspaceName+" exists");
		
		Boolean exists = false;
		try{
			exists = (reader.getDatastore(workspaceName, dataStoreName) == null) ? false : true;
		}catch(Exception e){
			logger.debug("Error while retrieveing data store", e);
			throw new GeoServerBridgeException("Error while retrieveing data store", e);
		}
		logger.debug("Datastore with name: "+ dataStoreName +" in workspace "+workspaceName+" exists: "+ exists);
		return exists;
	}

	@Override
	public List<String> listLayers() throws GeoServerBridgeException
	{
		logger.debug("Listing Layers...");

		List<GeoserverLayer> all = getGeoserverLayers();
		List<String> ls = new ArrayList<String>();
		for (GeoserverLayer l : all)
			ls.add(l.getId());
		logger.debug("List of Layers will be returned");
		return ls;
	}

	private List<String> listLayersOfDataStores(List<String> datastores) throws GeoServerBridgeException
	{
		logger.debug("Listing Layers of Datastores: "+ datastores);
		
		List<GeoserverLayer> all;
		try {
			all = getGeoserverLayers();
		} catch (GeoServerBridgeException e) {
			logger.error("Error while listing Layers: "+e);
			throw new GeoServerBridgeException("Error while listing Layers: ", e);
		}
		List<String> res = new ArrayList<String>();

		for (GeoserverLayer l : all)
		{
			for (String ds : datastores)
			{
				if (l.getDatastore().equals(ds))
				{
					res.add(l.getId());
					break;
				}
			}
		}
		logger.debug("Layers of Datastores: "+ datastores+" will be returned");
		return res;
	}

	@Override
	public List<String> listLayersOfDataStore(String datastore) throws GeoServerBridgeException
	{
		logger.debug("Listing Layers of Datastore: "+ datastore);
		
		List<String> ds = new ArrayList<String>();
		ds.add(datastore);
		List<String> listOfDatastores = null;
		try {
			listOfDatastores = listLayersOfDataStores(ds);
		} catch (GeoServerBridgeException e) {
			logger.error("Error while listing Layers of Datastores: "+e);
			throw new GeoServerBridgeException("Error while listing Layers of Datastores: ", e);
		}
		logger.debug("Layers of Datastore: "+ datastore+" will be returned");
		return listOfDatastores;
	}

	@Override
	public GeoserverLayer getGeoserverLayer(String id) throws GeoServerBridgeException
	{
		logger.debug("Getting Layer with id: "+ id);
		
		GeoserverLayer l = null;

		try
		{
			RESTLayer layer = reader.getLayer(geoServerBridgeConfig.getGeoServerBridgeWorkspace(), id);
			if(layer == null) return null;
			RESTFeatureType ft = reader.getFeatureType(layer);
			
			if(ft == null) return null;
			l = new GeoserverLayer();
			l.setDatastore(ft.getStoreName());
			l.setEnabled(true);
			l.setWorkspace(geoServerBridgeConfig.getGeoServerBridgeWorkspace());
			l.setId(layer.getName());
			l.setDefaultStyle(layer.getDefaultStyle());
			l.setFeatureTypeLink(layer.getResourceUrl());
			l.setType(layer.getType().toString());
			l.setTitle(layer.getTitle());
		} catch (Exception e)
		{
			logger.error("Error while retrieving layer", e);
			throw new GeoServerBridgeException("Error while retrieving layer",e);
		}
		logger.debug("Got Layer with id: "+ id);
		return l;
	}

	@Override
	public List<GeoserverLayer> getGeoserverLayers() throws GeoServerBridgeException 
	{
		logger.debug("Getting all Layers...");
		
		List<GeoserverLayer> geoserverLayersOfDatastores = null;
		try {
			geoserverLayersOfDatastores = getGeoserverLayersOfDataStores(listDataStores());
		} catch (GeoServerBridgeException e) {
			logger.error("Error while getting Layers", e);
			throw new GeoServerBridgeException("Error while getting Layers",e);
		}
		
		logger.debug("Got all Layers");
		return geoserverLayersOfDatastores;
	}

	private List<GeoserverLayer> getGeoserverLayersOfDataStores(List<String> datastores) throws GeoServerBridgeException
	{
		
		logger.debug("Getting all Layers of datastores: "+ datastores);
		
		datastores = datastores.stream().
				map(x -> geoServerBridgeConfig.getGeoServerBridgeWorkspace() + ":" + x).
				collect(Collectors.toList());
		
		List<GeoserverLayer> ls = new ArrayList<GeoserverLayer>();
		try
		{
			List<String> all = reader.getLayers().getNames();
			for (String lname : all)
			{
				RESTLayer layer = reader.getLayer(geoServerBridgeConfig.getGeoServerBridgeWorkspace(), lname);
				RESTFeatureType ft = null;
				if(layer != null)
					ft = reader.getFeatureType(layer);
				if(layer == null || ft == null)
				{
					logger.warn("Skipping layer with no returned data: " + lname);
					continue;
				}
				if(datastores.contains(ft.getStoreName()))
				{
					logger.debug("Creating object Layer...");
					GeoserverLayer l = new GeoserverLayer();
					l.setDatastore(ft.getStoreName());
					l.setEnabled(true);
					l.setWorkspace(geoServerBridgeConfig.getGeoServerBridgeWorkspace());
					l.setId(layer.getName());
					l.setDefaultStyle(layer.getDefaultStyle());
					l.setFeatureTypeLink(layer.getResourceUrl());
					l.setType(layer.getType().toString());
					l.setTitle(layer.getTitle());
					ls.add(l);
					logger.debug("Object Layer has been created");
				}
			}
		} catch (Exception e)
		{
			logger.error("Error while retrieving layer info", e);
			throw new GeoServerBridgeException(
					"Error while retrieving layer info", e);
		}
		logger.debug("Got all Layers of datastores");
		return ls;
	}
	
	@Override
	public List<GeoserverLayer> getGeoserverLayersOfDataStore(String datastore) throws GeoServerBridgeException
	{
		logger.debug("Getting Layers of Datastore: "+ datastore);
		List<String> datastores = new ArrayList<String>();
		datastores.add(datastore);
		
		List<GeoserverLayer> geoserverLayersOfDatastores = getGeoserverLayersOfDataStores(datastores);
		if(geoserverLayersOfDatastores == null || geoserverLayersOfDatastores.isEmpty()) {
			logger.warn("List of Layers is null or empty");
		}
		logger.debug("Got Layers of Datastore: "+ datastore);
		return geoserverLayersOfDatastores;
	}
	
	@Override
	public void addWorkspace(String name, String uri) throws GeoServerBridgeException{
		logger.debug("Adding workspace with name: "+ name + " and url: "+uri);
		if (!name.isEmpty() && !uri.isEmpty()){
			try{
				URI u = URI.create(uri);
				publisher.createWorkspace(name, u);
				logger.debug("Workspace created succesfully");
			}catch(Exception e){
				logger.error("Error while creating workspace");
				throw new GeoServerBridgeException("Error while creating workspace", e);
			}
		}else{
			logger.warn("Empty String for workspace name or empty uri");
		}
	}
	
	@Override
	public void addWorkspace(String name, URI uri) throws GeoServerBridgeException{
		logger.debug("Adding workspace with name: "+ name + " and url: "+uri);
		if (!name.isEmpty() && uri != null){
			try{
				publisher.createWorkspace(name, uri);
				logger.debug("Workspace created succesfully");
			}catch(Exception e){
				logger.error("Error while creating workspace");
				throw new GeoServerBridgeException("Error while creating workspace", e);
			}
			
		}else{
			logger.warn("Empty String for workspace name or null URI");
		}
	}
	
	@Override
	public void addWorkspace(String name) throws GeoServerBridgeException{
		logger.debug("Adding workspace with name: "+ name);
		if (!name.isEmpty()){
			try{
				publisher.createWorkspace(name);
				logger.debug("Workspace created succesfully");
			}catch(Exception e){
				logger.error("Error while creating workspace");
				throw new GeoServerBridgeException("Error while creating workspace", e);
			}
		}else{
			logger.warn("Empty String for workspace name");
		}
	}
	
	@Override
	public void addDataStore(DataStore dataStore) throws GeoServerBridgeException{
		logger.debug("Adding datastore: "+ dataStore.getDataStoreName());
		try{
			GSPostGISDatastoreEncoder dataStoreEncoder = new GSPostGISDatastoreEncoder(dataStore.getDataStoreName());
			
			dataStoreEncoder.setDescription(dataStore.getDescription());
			dataStoreEncoder.setEnabled(dataStore.isEnabled());
			dataStoreEncoder.setUser(dataStore.getUser());
			dataStoreEncoder.setPassword(dataStore.getPassword());
			dataStoreEncoder.setDatabase(dataStore.getDatabase());
			dataStoreEncoder.setPort(dataStore.getPort());
			dataStoreEncoder.setHost(dataStore.getHost());
			
			storeManager.create(dataStore.getWorkspace(), dataStoreEncoder);
			logger.debug("Data store, created successfully");
		}catch(Exception e){
			logger.error("Error while creating datastore", e);
			throw new GeoServerBridgeException("Error while creating data store", e);
		}
	}
	 
	@Override
	public Boolean workspaceExists(String workSpaceName) throws GeoServerBridgeException{
		logger.debug("Check if workspace with name: "+ workSpaceName + " exists...");
		List<String> workSpacesNames = new ArrayList<String>();
		Boolean exists = false;
		
		try{
			workSpacesNames = reader.getWorkspaceNames();
			logger.debug("WorkSpace name retrieved successfully");
		}catch(Exception e){
			logger.error("Error while retrieving workspace names", e);
			throw new GeoServerBridgeException("Error while retrieving workspace names", e);
		}
		
		if (workSpacesNames.contains(workSpaceName)){
			exists = true;
		}
		logger.debug("Workspace with name: "+ workSpaceName + " exists: "+exists);
		return exists;
	}

	@Override
	public void addGeoserverLayer(GeoserverLayer geoserverLayer, FeatureType ft, Map<String, String> slds, Integer minScale, Integer maxScale) throws GeoServerBridgeException {
		logger.info("Starting publishing layer with name \""+ geoserverLayer.getId() + "\" to GeoServer");

		if((minScale != null && minScale < 0) || (maxScale != null && maxScale < 0)) {
			throw new GeoServerBridgeException("Illegal minScale/maxScale");
		}
		
		if(minScale != null && minScale != 0 && maxScale != null && maxScale != 0 && minScale > maxScale) {
			throw new GeoServerBridgeException("minScale should be less than maxScale");
		}
		
		logger.debug("Creating and setting GSFeatureTypeEncoder...");
		
		GSFeatureTypeEncoder fte = new GSFeatureTypeEncoder();
		fte.setEnabled(true);
		
		if(ft.getName() != null){
			fte.setName(ft.getName());
		} else {
			fte.setName(geoserverLayer.getId());
		}
		
		if(ft.getSrs() != null){
			fte.setSRS(ft.getSrs());
		}
		
		if(ft.getTitle() != null) {
			fte.setTitle(ft.getTitle());
		}		

		ProjectionPolicy pp = null;
		
		switch(ft.getProjectionPolicy()){
			case FORCE_DECLARED:
				pp = ProjectionPolicy.FORCE_DECLARED;
				break;
			case REPROJECT_TO_DECLARED:
				pp = ProjectionPolicy.REPROJECT_TO_DECLARED;
				break;
			case NONE:
				pp = ProjectionPolicy.NONE;
				break;
		}
		
		fte.setProjectionPolicy(pp);
		
		logger.debug("GSFeatureTypeEncoder has been created and set");		
		logger.debug("Creating and setting Bounds...");
		
		Bounds bb = ft.getLatLonBoundingBox();
		
		if(bb != null){
			fte.setLatLonBoundingBox(bb.getMinx(), bb.getMiny(), bb.getMaxx(), bb.getMaxy(), bb.getCrs());
		}
		
		bb = ft.getNativeBoundingBox();
		
		if(bb != null){
			fte.setLatLonBoundingBox(bb.getMinx(), bb.getMiny(), bb.getMaxx(), bb.getMaxy(), bb.getCrs());
		}
		
		logger.debug("GSFeatureTypeEncoder have been created and set");
		
		if(ft.getMetadata() != null) {
			//TODO test if this correspondence is correct
			for(Map.Entry<String, String> m : ft.getMetadata().entrySet()) {
				GSDimensionInfoEncoder fdie = new GSFeatureDimensionInfoEncoder(m.getKey());
				fdie.setEnabled(true);
			}
		}
		
		logger.debug("Setting style of layer...");
		
		GSLayerEncoder le = new GSLayerEncoder();
		List<String> styles = new ArrayList<String>();
		ArrayList<String> scaledStyles = new ArrayList<String>();
		
		if(geoserverLayer.getStyles() != null){
			styles.addAll(geoserverLayer.getStyles());
		}
		
		if(geoserverLayer.getDefaultStyle() != null){
			styles.add(geoserverLayer.getDefaultStyle());
		} else {
			geoserverLayer.setDefaultStyle(SystemPresentationConfig.DEFAULT_STYLE);
			styles.add(SystemPresentationConfig.DEFAULT_STYLE);
		}
		
		for(String style : styles) {
			if(minScale != null || maxScale != null) {
				String scaledName = ScaledStyleCreator.getScaledName(style, minScale, maxScale);
				try {
					if(getStyle(scaledName) == null) {
						addStyle(scaledName, ScaledStyleCreator.createScaled(slds.get(style), minScale, maxScale));
					}
				} catch (GeoServerBridgeException e) {
					logger.error("Error while getting or adding or creating scaled", e);
				}
				if(!style.equals(geoserverLayer.getDefaultStyle())) {
					scaledStyles.add(scaledName);
				}
			} else {
				scaledStyles.add(style);
			}
		}
		
		if(geoserverLayer.getDefaultStyle() != null) {
			if(minScale != null || maxScale != null) {
				le.setDefaultStyle(ScaledStyleCreator.getScaledName(geoserverLayer.getDefaultStyle(), minScale, maxScale));
			} else {
				le.setDefaultStyle(geoserverLayer.getDefaultStyle());
			}
		}else {
			if(minScale != null || maxScale != null) {
				le.setDefaultStyle(ScaledStyleCreator.getScaledName(SystemPresentationConfig.DEFAULT_STYLE, minScale, maxScale));
			} else {
				le.setDefaultStyle(SystemPresentationConfig.DEFAULT_STYLE);
			}
		}
		
		for(String st : scaledStyles){
			le.addStyle(st);
		}
		
		le.setEnabled(true);
		
		logger.debug("Style of layer has been set");
		
		boolean status = false;
		try {
			logger.info("Publishing layer to geoserver...");
			status = publisher.publishDBLayer(geoServerBridgeConfig.getGeoServerBridgeWorkspace(), geoServerBridgeConfig.getDataStoreConfig().getDataStoreName(), fte, le);
		} catch(Exception e) {
			logger.error("Error while adding layer", e);
			throw new GeoServerBridgeException("Error while adding layer", e);
		}
		 
		 if(!status){
			 logger.error("An error originating from the underlying "
					 + "bridge provider occurred while adding a new layer");
			 throw new GeoServerBridgeException("An error originating from the underlying "
					 + "bridge provider occurred while adding a new layer");
		 }
		 logger.info("Layer with name \""+ geoserverLayer.getId() +"\" has been published");	
	}
	
	@Override
	public void addGeoserverLayer(GeoserverLayer geoserverLayer, FeatureType ft, Map<String, String> slds) throws GeoServerBridgeException
	{
		logger.debug("Adding layer with id: "+ geoserverLayer.getId() + "and null min and max scale...");
		addGeoserverLayer(geoserverLayer, ft, slds, null, null);
		logger.debug("Layer with name: "+ geoserverLayer.getId() + "and null min and max scale has been added");
	}

	@Override
	public void deleteLayer(String name) throws GeoServerBridgeException
	{
		logger.debug("Deleting layer with name: "+ name + "...");
		boolean removed;
		try
		{
			removed = publisher.unpublishFeatureType(geoServerBridgeConfig.getGeoServerBridgeWorkspace(), geoServerBridgeConfig.getDataStoreConfig().getDataStoreName(), URLEncoder.encode(name, "UTF-8"));
			if(removed) publisher.removeLayer(geoServerBridgeConfig.getGeoServerBridgeWorkspace(), URLEncoder.encode(name, "UTF-8"));
		} catch (Exception e)
		{
			logger.error("Error while removing layer", e);
			throw new GeoServerBridgeException("Error while removing layer", e);
		}
		
		if(removed == false)
		{
			logger.error("An error originating from the underlying bridge provider occurred while deleting layer " + name);
			throw new GeoServerBridgeException("An error originating from the underlying bridge provider occurred while deleting layer " + name);
		}
		logger.debug("Layer with name: "+ name + " has been deleted");
	}

	@Override
	public FeatureType getFeatureType(String name) throws GeoServerBridgeException
	{
		logger.debug("Getting feature type from layer, with name: "+name+"...");
		try
		{
			RESTLayer layer = reader.getLayer(geoServerBridgeConfig.getGeoServerBridgeWorkspace(), name);
			if(layer == null){
				logger.warn("Layer is null");
				return null;
			}
			RESTFeatureType ftr = reader.getFeatureType(layer);
			
			if(ftr == null) {
				logger.error("A layer for feature type " + name + " was found, but the corresponding feature type could not be retrieved");
				throw new GeoServerBridgeException("A layer for feature type " + name + " was found, but the corresponding feature type could not be retrieved");
			}
			
			FeatureType featureType = new FeatureType();
			featureType.setDatastore(ftr.getStoreName());
			featureType.setEnabled(true);
			//featureType.setMaxFeatures(ftr.getMaxFeatures());
			//featureType.setMetadata(ftr.getMetadata());
			featureType.setName(ftr.getName());
			featureType.setNativeCRS(ftr.getCRS());
			featureType.setNativeName(ftr.getNativeName());
			//featureType.setNumDecimals(ftr.getNumDecimals());
			//featureType.setProjectionPolicy(ftr.getProjectionPolicy());
			//featureType.setSrs(ftr.getSrs());
			featureType.setTitle(ftr.getTitle());
			featureType.setWorkspace(geoServerBridgeConfig.getGeoServerBridgeWorkspace());
			
			Bounds b = new Bounds();
			b.setMaxx(ftr.getMaxX());
			b.setMaxy(ftr.getMaxY());
			b.setMinx(ftr.getMinX());
			b.setMiny(ftr.getMinY());
			featureType.setLatLonBoundingBox(b);
			
			logger.debug("Got Feature type from layer, with name: "+name);
			return featureType;
		} catch (Exception e)
		{
			logger.error("Error while retrieving feature type", e);
			throw new GeoServerBridgeException("Error while retrieving feature type", e);
		}
	}
	
	@Override
	public String getStyle(String name) throws GeoServerBridgeException
	{
		logger.debug("Getting style with name: "+name+"...");
		String sld = reader.getSLD(name);
		if(sld ==null || sld.isEmpty()) {
			logger.warn("SLD is null or empty");
		}
		logger.debug("Got style with name: "+name);
		return sld;
	}
	
	@Override
	public void addStyle(String name, String sld) throws GeoServerBridgeException
	{
		logger.debug("Adding style: " +name+" and sld: "+sld);
		RESTStyle s = reader.getStyle(name);
		if(s != null) {
			logger.error("Style " + name + " already exists");
			throw new GeoServerBridgeException("Style " + name + " already exists");
		}
		boolean published = publisher.publishStyle(sld, name);
		if(published == false){
			logger.error("An error has occurred during publication of style " + name);
			throw new GeoServerBridgeException("An error has occurred during publication of style " + name);
		}
		logger.debug("Style: " +name+" and sld: "+sld+" has been added");
	}
	
	@Override
	public void addStyle(String name, String sld, Integer minScale, Integer maxScale) throws GeoServerBridgeException
	{
		logger.debug("Adding style: " +name+" and sld: "+sld+" with min and max scale...");
		String s = reader.getSLD(ScaledStyleCreator.getScaledName(name, minScale, maxScale));
		if(s != null){
			logger.error("Style " + name + " already exists");
			throw new GeoServerBridgeException("Style " + name + " already exists");
		}
		boolean published = publisher.publishStyle(ScaledStyleCreator.createScaled(sld, minScale, maxScale), ScaledStyleCreator.getScaledName(name, minScale, maxScale));
		if(published == false){
			logger.error("An error has occurred during publication of style " + name);
			throw new GeoServerBridgeException("An error has occurred during publication of style " + name);
		}
		logger.debug("Style: " +name+" and sld: "+sld+" with min and max scale has been added");
	}
	
	@Override
	public void removeStyle(String name, Integer minScale, Integer maxScale) throws GeoServerBridgeException
	{
		logger.debug("Removing style: " +name+"...");
		String sn = (minScale == null && maxScale == null) ? name : ScaledStyleCreator.getScaledName(name, minScale, maxScale);
		String s = reader.getSLD(sn);
		if(s == null){
			logger.warn("SLD is null");
			return;
		}
		boolean removed = publisher.removeStyle(sn);
		if(removed == false){
			logger.error("An error has occured during removal of style "  + sn);
			throw new GeoServerBridgeException("An error has occured during removal of style "  + sn);
		}
		logger.debug("Style: " +name+" has been removed");
	}

	@Override
	public void removeStyle(String name) throws GeoServerBridgeException
	{
		logger.debug("Removing style: " +name+" with null min and max scale...");
		removeStyle(name, null, null);
		logger.debug("Style: " +name+" with null min and max scale has been removed");
	}
	
	@Override
	public void setDefaultLayerStyle(String layerName, String styleName, String sld, Integer minScale, Integer maxScale) throws GeoServerBridgeException
	{
		logger.debug("Setting default style to layer: "+layerName+"...");
		String scaledName = styleName;
		if(minScale != null || maxScale != null)
			scaledName = ScaledStyleCreator.getScaledName(styleName, minScale, maxScale);
		
		if(getStyle(scaledName) == null)
			addStyle(scaledName, ScaledStyleCreator.createScaled(sld, minScale, maxScale));
		
		GSLayerEncoder le = new GSLayerEncoder();
		le.setDefaultStyle(scaledName);
		boolean res = publisher.configureLayer(geoServerBridgeConfig.getGeoServerBridgeWorkspace(), layerName, le);
		if(res == false)
		{
			logger.error("An error has occurred while configuring layer " + layerName + " with style " + scaledName);
			throw new GeoServerBridgeException("An error has occurred while configuring layer " + layerName + " with style " + scaledName);
		}
		logger.debug("Default style to layer: "+layerName+" has been set");
	}
	
	@Override
	public void setDefaultLayerStyle(String layerName, String styleName, String sld) throws GeoServerBridgeException
	{
		logger.debug("Setting default style to layer: "+layerName+" with null min and max scale...");
		setDefaultLayerStyle(layerName, styleName, sld, null, null);
		logger.debug("Default style to layer: "+layerName+" with null min and max scale has been set");
	}
	
	@Override
	public void addLayerStyle(String layerName, String styleName, String sld, Integer minScale, Integer maxScale) throws GeoServerBridgeException 
	{
		logger.debug("Adding style: "+styleName+" to layer: "+layerName+"...");
		String scaledName = styleName;
		if(minScale != null || maxScale != null)
			scaledName = ScaledStyleCreator.getScaledName(styleName, minScale, maxScale);
		
		if(getStyle(scaledName) == null)
			addStyle(scaledName, ScaledStyleCreator.createScaled(sld, minScale, maxScale));
		
		//TODO cannot currently check if style already exists
		GSLayerEncoder le = new GSLayerEncoder();
		le.addStyle(scaledName);
		boolean res = publisher.configureLayer(geoServerBridgeConfig.getGeoServerBridgeWorkspace(), layerName, le);
		if(res == false)
		{
			logger.error("An error has occurred while adding style " + styleName + " to layer " + layerName);
			throw new GeoServerBridgeException("An error has occurred while adding style " + styleName + " to layer " + layerName);
		}
		logger.debug("Style: "+styleName+" to layer: "+layerName+" has been added");
	}

	@Override
	public void addLayerStyle(String layerName, String styleName, String sld) throws GeoServerBridgeException 
	{
		logger.debug("Adding style: "+styleName+" to layer: "+layerName+" with min and max scale...");
		addLayerStyle(layerName, styleName, sld, null, null);
		logger.debug("Style: "+styleName+" to layer: "+layerName+" with min and max scale has been added...");
	}

	@Override
	public void removeLayerStyle(String layerName, String styleName, Integer minScale, Integer maxScale) throws GeoServerBridgeException 
	{
		logger.debug("Removing style: "+styleName+" from layer: "+layerName+"...");
		String scaledName = styleName;
		if(minScale != null || maxScale != null)
			scaledName = ScaledStyleCreator.getScaledName(styleName, minScale, maxScale);
		
		//TODO cannot currently check if style does not exist
		GSLayerEncoder le = new GSLayerEncoder();
		le.delStyle(scaledName);
		boolean res = publisher.configureLayer(geoServerBridgeConfig.getGeoServerBridgeWorkspace(), layerName, le);
		if(res == false)
		{
			logger.error("An error has occurred while removing style " + styleName + " from layer " + layerName);
			throw new GeoServerBridgeException("An error has occurred while removing style " + styleName + " from layer " + layerName);
		}
		logger.debug("Style: "+styleName+" from layer: "+layerName+" has been removed");
		
	}

	@Override
	public void removeLayerStyle(String layerName, String styleName) throws GeoServerBridgeException 
	{
		logger.debug("Removing style: "+styleName+" from layer: "+layerName+" with null min and max scale...");
		removeLayerStyle(layerName, styleName, null, null);
		logger.debug("Style: "+styleName+" from layer: "+layerName+" with null min and max scale has been removed");
	}
}
