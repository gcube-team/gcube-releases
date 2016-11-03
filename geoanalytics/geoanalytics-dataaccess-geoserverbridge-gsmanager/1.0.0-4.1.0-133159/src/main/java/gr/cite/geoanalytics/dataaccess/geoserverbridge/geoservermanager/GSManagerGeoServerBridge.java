package gr.cite.geoanalytics.dataaccess.geoserverbridge.geoservermanager;

import gr.cite.geoanalytics.context.GeoServerBridgeConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.SystemPresentationConfig;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.GeoServerBridge;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.DataStore;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.FeatureType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Layer;
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
public class GSManagerGeoServerBridge implements GeoServerBridge
{
	private static Logger logger = LoggerFactory.getLogger(GSManagerGeoServerBridge.class);

	private GeoServerRESTReader reader = null;
	private GeoServerRESTPublisher publisher = null;
	private GeoServerBridgeConfig configuration = null;
	private GeoServerRESTStoreManager storeManager = null;

	@Inject
	public void setConfiguration(GeoServerBridgeConfig configuration) {
		this.configuration = configuration;
	}
	
	@PostConstruct
	private void initialize() throws MalformedURLException {
		this.reader = new GeoServerRESTReader(configuration.getGeoServerBridgeUrl(),
				configuration.getGeoServerBridgeUser(),
				configuration.getGeoServerBridgePassword());
		this.publisher = new GeoServerRESTPublisher(
				configuration.getGeoServerBridgeUrl(),
				configuration.getGeoServerBridgeUser(),
				configuration.getGeoServerBridgePassword());
		this.storeManager = new GeoServerRESTStoreManager(
				new URL(configuration.getGeoServerBridgeUrl()),
				configuration.getGeoServerBridgeUser(),
				configuration.getGeoServerBridgePassword());
	}

	@Override
	public List<String> listDataStores() throws GeoServerBridgeException
	{
		try
		{
			RESTDataStoreList datastores = reader.getDatastores(configuration.getGeoServerBridgeWorkspace());
			if(datastores == null)
				return null;
			return datastores.getNames();
		} catch (Exception e)
		{
			logger.error("Erorr while listing data stores", e);
			throw new GeoServerBridgeException(
					"Error while listing data stores", e);
		}
	}

	@Override
	public DataStore getDataStore(String name) throws GeoServerBridgeException
	{
		RESTDataStore dsr = null;
		try
		{
			dsr = reader.getDatastore(configuration.getGeoServerBridgeWorkspace(), name);
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
		return ds;
	}
	
	@Override
	public Boolean dataStoreExists(String workspaceName, String dataStoreName) throws GeoServerBridgeException{
		
		Boolean exists = false;
		try{
			exists = (reader.getDatastore(workspaceName, dataStoreName) == null) ? false : true;
		}catch(Exception e){
			logger.debug("Error while retrieveing data store", e);
			throw new GeoServerBridgeException("Error while retrieveing data store", e);
		}
		
		return exists;
	}

	@Override
	public List<String> listLayers() throws GeoServerBridgeException
	{
		List<Layer> all = getLayers();
		List<String> ls = new ArrayList<String>();
		for (Layer l : all)
			ls.add(l.getName());
		return ls;
	}

	private List<String> listLayersOfDataStores(List<String> datastores) throws GeoServerBridgeException
	{
		List<Layer> all = getLayers();
		List<String> res = new ArrayList<String>();

		for (Layer l : all)
		{
			for (String ds : datastores)
			{
				if (l.getDatastore().equals(ds))
				{
					res.add(l.getName());
					break;
				}
			}
		}
		return res;
	}

	@Override
	public List<String> listLayersOfDataStore(String datastore) throws GeoServerBridgeException
	{
		List<String> ds = new ArrayList<String>();
		ds.add(datastore);
		return listLayersOfDataStores(ds);
	}

	@Override
	public Layer getLayer(String name) throws GeoServerBridgeException
	{
		Layer l = null;

		try
		{
			RESTLayer layer = reader.getLayer(configuration.getGeoServerBridgeWorkspace(), name);
			if(layer == null) return null;
			RESTFeatureType ft = reader.getFeatureType(layer);
			
			if(ft == null) return null;
			l = new Layer();
			l.setDatastore(ft.getStoreName());
			l.setEnabled(true);
			l.setWorkspace(configuration.getGeoServerBridgeWorkspace());
			l.setName(layer.getName());
			l.setDefaultStyle(layer.getDefaultStyle());
			l.setFeatureTypeLink(layer.getResourceUrl());
			l.setType(layer.getType().toString());
			l.setTitle(layer.getTitle());
		} catch (Exception e)
		{
			logger.error("Error while retrieving layer", e);
			throw new GeoServerBridgeException("Error while retrieving layer",
					e);
		}

		return l;
	}

	@Override
	public List<Layer> getLayers() throws GeoServerBridgeException
	{
		return getLayersOfDataStores(listDataStores());
	}

	private List<Layer> getLayersOfDataStores(List<String> datastores) throws GeoServerBridgeException
	{
		datastores = datastores.stream().
				map(x -> configuration.getGeoServerBridgeWorkspace() + ":" + x).
				collect(Collectors.toList());
		
		List<Layer> ls = new ArrayList<Layer>();
		try
		{
			List<String> all = reader.getLayers().getNames();
			for (String lname : all)
			{
				RESTLayer layer = reader.getLayer(configuration.getGeoServerBridgeWorkspace(), lname);
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
					Layer l = new Layer();
					l.setDatastore(ft.getStoreName());
					l.setEnabled(true);
					l.setWorkspace(configuration.getGeoServerBridgeWorkspace());
					l.setName(layer.getName());
					l.setDefaultStyle(layer.getDefaultStyle());
					l.setFeatureTypeLink(layer.getResourceUrl());
					l.setType(layer.getType().toString());
					l.setTitle(layer.getTitle());
					ls.add(l);
				}
			}
		} catch (Exception e)
		{
			logger.error("Error while retrieving layer info", e);
			throw new GeoServerBridgeException(
					"Error while retrieving layer info", e);
		}
		return ls;
	}
	
	@Override
	public List<Layer> getLayersOfDataStore(String datastore) throws GeoServerBridgeException
	{
		List<String> datastores = new ArrayList<String>();
		datastores.add(datastore);
		return getLayersOfDataStores(datastores);
	}
	
	@Override
	public void addWorkspace(String name, String uri) throws GeoServerBridgeException{
		if (!name.isEmpty() && !uri.isEmpty()){
			try{
				URI u = URI.create(uri);
				publisher.createWorkspace(name, u);
				logger.debug("Worksapce created succesfully");
			}catch(Exception e){
				logger.debug("Error while creating workspace");
				throw new GeoServerBridgeException("Error while creating workspace", e);
			}
		}else{
			logger.debug("Empty String for workspace name or empty uri");
		}
	}
	
	@Override
	public void addWorkspace(String name, URI uri) throws GeoServerBridgeException{
		if (!name.isEmpty() && uri != null){
			try{
				publisher.createWorkspace(name, uri);
				logger.debug("Worksapce created succesfully");
			}catch(Exception e){
				logger.debug("Error while creating workspace");
				throw new GeoServerBridgeException("Error while creating workspace", e);
			}
			
		}else{
			logger.debug("Empty String for workspace name or null URI");
		}
	}
	
	@Override
	public void addWorkspace(String name) throws GeoServerBridgeException{
		if (!name.isEmpty()){
			try{
				publisher.createWorkspace(name);
				logger.debug("Worksapce created succesfully");
			}catch(Exception e){
				logger.debug("Error while creating workspace");
				throw new GeoServerBridgeException("Error while creating workspace", e);
			}
		}else{
			logger.debug("Empty String for workspace name");
		}
	}
	
	@Override
	public void addDataStore(DataStore dataStore) throws GeoServerBridgeException{
		
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
			logger.debug("Error while creating datastore", e);
			throw new GeoServerBridgeException("Error while creating data store", e);
		}
	}
	 
	@Override
	public Boolean workspaceExists(String workSpaceName) throws GeoServerBridgeException{
		
		List<String> workSpacesNames = new ArrayList<String>();
		Boolean exists = false;
		
		try{
			workSpacesNames = reader.getWorkspaceNames();
			logger.debug("WorkSpace name retrieved successfully");
		}catch(Exception e){
			logger.debug("Error while retrieving workspace names", e);
			throw new GeoServerBridgeException("Error while retrieving workspace names", e);
		}
		
		if (workSpacesNames.contains(workSpaceName)){
			exists = true;
		}
		
		return exists;
	}

	@Override
	public void addLayer(Layer layer, FeatureType ft, Map<String, String> slds, Integer minScale, Integer maxScale) throws GeoServerBridgeException
	{
		if((minScale != null && minScale < 0) || (maxScale != null && maxScale < 0)) throw new GeoServerBridgeException("Illegal minScale/maxScale");
		if(minScale != null && minScale != 0 && maxScale != null && maxScale != 0 && minScale > maxScale) throw new GeoServerBridgeException("minScale should be less than maxScale");
		
		GSFeatureTypeEncoder fte = new GSFeatureTypeEncoder();
		fte.setEnabled(true);
		if(ft.getName() != null) fte.setName(ft.getName());
		else ft.setName(layer.getName());
		if(ft.getSrs() != null) fte.setSRS(ft.getSrs());
		if(ft.getTitle() != null) fte.setTitle(ft.getTitle());
		ProjectionPolicy pp = null;
		switch(ft.getProjectionPolicy())
		{
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
		Bounds bb = ft.getLatLonBoundingBox();
		if(bb != null) fte.setLatLonBoundingBox(bb.getMinx(), bb.getMiny(), bb.getMaxx(), bb.getMaxy(), bb.getCrs());
		bb = ft.getNativeBoundingBox();
		if(bb != null) fte.setLatLonBoundingBox(bb.getMinx(), bb.getMiny(), bb.getMaxx(), bb.getMaxy(), bb.getCrs());
		
		if(ft.getMetadata() != null)
		{
			//TODO test if this correspondence is correct
			for(Map.Entry<String, String> m : ft.getMetadata().entrySet())
			{
				GSDimensionInfoEncoder fdie = new GSFeatureDimensionInfoEncoder(m.getKey());
				fdie.setEnabled(true);
			}
		}
		
		GSLayerEncoder le = new GSLayerEncoder();
		List<String> styles = new ArrayList<String>();
		ArrayList<String> scaledStyles = new ArrayList<String>();
		if(layer.getStyles() != null) styles.addAll(layer.getStyles());
		if(layer.getDefaultStyle() != null) styles.add(layer.getDefaultStyle());
		else
		{
			layer.setDefaultStyle(SystemPresentationConfig.DEFAULT_STYLE);
			styles.add(SystemPresentationConfig.DEFAULT_STYLE);
		}
		
		for(String style : styles)
		{
			if(minScale != null || maxScale != null)
			{
				String scaledName = ScaledStyleCreator.getScaledName(style, minScale, maxScale);
				if(getStyle(scaledName) == null)
				{
					addStyle(scaledName, ScaledStyleCreator.createScaled(slds.get(style), minScale, maxScale));
				}
				if(!style.equals(layer.getDefaultStyle()))
					scaledStyles.add(scaledName);
			}else
				scaledStyles.add(style);
		}
		
		if(layer.getDefaultStyle() != null)
		{
			if(minScale != null || maxScale != null)
				le.setDefaultStyle(ScaledStyleCreator.getScaledName(layer.getDefaultStyle(), minScale, maxScale));
			else
				le.setDefaultStyle(layer.getDefaultStyle());
		}else
		{
			if(minScale != null || maxScale != null)
				le.setDefaultStyle(ScaledStyleCreator.getScaledName(SystemPresentationConfig.DEFAULT_STYLE, minScale, maxScale));
			else
				le.setDefaultStyle(SystemPresentationConfig.DEFAULT_STYLE);
		}
		for(String st : scaledStyles)
			le.addStyle(st);
		
		le.setEnabled(true);
		
		boolean status = false;
		 try
		 {
			 status = publisher.publishDBLayer(configuration.getGeoServerBridgeWorkspace(), configuration.getDataStoreConfig().getDataStoreName(), fte, le);
		 }catch(Exception e)
		 {
			 logger.error("Error while adding layer", e);
			 throw new GeoServerBridgeException("Error while adding layer", e);
		 }
		 
		 if(!status) throw new GeoServerBridgeException("An error originating from the underlying "
				 + "bridge provider occurred while adding a new layer");
	}
	
	@Override
	public void addLayer(Layer layer, FeatureType ft, Map<String, String> slds) throws GeoServerBridgeException
	{
		addLayer(layer, ft, slds, null, null);
	}

	@Override
	public void deleteLayer(String name) throws GeoServerBridgeException
	{
		boolean removed;
		try
		{
			removed = publisher.unpublishFeatureType(configuration.getGeoServerBridgeWorkspace(), configuration.getDataStoreConfig().getDataStoreName(), URLEncoder.encode(name, "UTF-8"));
			if(removed) publisher.removeLayer(configuration.getGeoServerBridgeWorkspace(), URLEncoder.encode(name, "UTF-8"));
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
	}

	@Override
	public FeatureType getFeatureType(String name) throws GeoServerBridgeException
	{
		try
		{
			RESTLayer layer = reader.getLayer(configuration.getGeoServerBridgeWorkspace(), name);
			if(layer == null)
				return null;
			RESTFeatureType ftr = reader.getFeatureType(layer);
			
			if(ftr == null)
				throw new GeoServerBridgeException("A layer for feature type " + name + " was found, but the corresponding feature type could not be retrieved");
			
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
			featureType.setWorkspace(configuration.getGeoServerBridgeWorkspace());
			
			Bounds b = new Bounds();
			b.setMaxx(ftr.getMaxX());
			b.setMaxy(ftr.getMaxY());
			b.setMinx(ftr.getMinX());
			b.setMiny(ftr.getMinY());
			featureType.setLatLonBoundingBox(b);
			
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
		return reader.getSLD(name);
	}
	
	@Override
	public void addStyle(String name, String sld) throws GeoServerBridgeException
	{
		RESTStyle s = reader.getStyle(name);
		if(s != null) throw new GeoServerBridgeException("Style " + name + " already exists");
		boolean published = publisher.publishStyle(sld, name);
		if(published == false) throw new GeoServerBridgeException("An error has occurred during publication of style " + name);
	}
	
	@Override
	public void addStyle(String name, String sld, Integer minScale, Integer maxScale) throws GeoServerBridgeException
	{
		String s = reader.getSLD(ScaledStyleCreator.getScaledName(name, minScale, maxScale));
		if(s != null) throw new GeoServerBridgeException("Style " + name + " already exists");
		boolean published = publisher.publishStyle(ScaledStyleCreator.createScaled(sld, minScale, maxScale), ScaledStyleCreator.getScaledName(name, minScale, maxScale));
		if(published == false) throw new GeoServerBridgeException("An error has occurred during publication of style " + name);
	}
	
	@Override
	public void removeStyle(String name, Integer minScale, Integer maxScale) throws GeoServerBridgeException
	{
		String sn = (minScale == null && maxScale == null) ? name : ScaledStyleCreator.getScaledName(name, minScale, maxScale);
		String s = reader.getSLD(sn);
		if(s == null) return;
		boolean removed = publisher.removeStyle(sn);
		if(removed == false) throw new GeoServerBridgeException("An error has occured during removal of style "  + sn);
	}

	@Override
	public void removeStyle(String name) throws GeoServerBridgeException
	{
		removeStyle(name, null, null);
	}
	
	@Override
	public void setDefaultLayerStyle(String layerName, String styleName, String sld, Integer minScale, Integer maxScale) throws GeoServerBridgeException
	{
		String scaledName = styleName;
		if(minScale != null || maxScale != null)
			scaledName = ScaledStyleCreator.getScaledName(styleName, minScale, maxScale);
		
		if(getStyle(scaledName) == null)
			addStyle(scaledName, ScaledStyleCreator.createScaled(sld, minScale, maxScale));
		
		GSLayerEncoder le = new GSLayerEncoder();
		le.setDefaultStyle(scaledName);
		boolean res = publisher.configureLayer(configuration.getGeoServerBridgeWorkspace(), layerName, le);
		if(res == false)
		{
			logger.error("An error has occurred while configuring layer " + layerName + " with style " + scaledName);
			throw new GeoServerBridgeException("An error has occurred while configuring layer " + layerName + " with style " + scaledName);
		}
	}
	
	@Override
	public void setDefaultLayerStyle(String layerName, String styleName, String sld) throws GeoServerBridgeException
	{
		setDefaultLayerStyle(layerName, styleName, sld, null, null);
	}
	
	@Override
	public void addLayerStyle(String layerName, String styleName, String sld, Integer minScale, Integer maxScale) throws GeoServerBridgeException 
	{
		String scaledName = styleName;
		if(minScale != null || maxScale != null)
			scaledName = ScaledStyleCreator.getScaledName(styleName, minScale, maxScale);
		
		if(getStyle(scaledName) == null)
			addStyle(scaledName, ScaledStyleCreator.createScaled(sld, minScale, maxScale));
		
		//TODO cannot currently check if style already exists
		GSLayerEncoder le = new GSLayerEncoder();
		le.addStyle(scaledName);
		boolean res = publisher.configureLayer(configuration.getGeoServerBridgeWorkspace(), layerName, le);
		if(res == false)
		{
			logger.error("An error has occurred while adding style " + styleName + " to layer " + layerName);
			throw new GeoServerBridgeException("An error has occurred while adding style " + styleName + " to layer " + layerName);
		}
	}

	@Override
	public void addLayerStyle(String layerName, String styleName, String sld) throws GeoServerBridgeException 
	{
		addLayerStyle(layerName, styleName, sld, null, null);
	}

	@Override
	public void removeLayerStyle(String layerName, String styleName, Integer minScale, Integer maxScale) throws GeoServerBridgeException 
	{
		String scaledName = styleName;
		if(minScale != null || maxScale != null)
			scaledName = ScaledStyleCreator.getScaledName(styleName, minScale, maxScale);
		
		//TODO cannot currently check if style does not exist
		GSLayerEncoder le = new GSLayerEncoder();
		le.delStyle(scaledName);
		boolean res = publisher.configureLayer(configuration.getGeoServerBridgeWorkspace(), layerName, le);
		if(res == false)
		{
			logger.error("An error has occurred while removing style " + styleName + " from layer " + layerName);
			throw new GeoServerBridgeException("An error has occurred while removing style " + styleName + " from layer " + layerName);
		}
		
	}

	@Override
	public void removeLayerStyle(String layerName, String styleName) throws GeoServerBridgeException 
	{
		removeLayerStyle(layerName, styleName, null, null);
	}
}
