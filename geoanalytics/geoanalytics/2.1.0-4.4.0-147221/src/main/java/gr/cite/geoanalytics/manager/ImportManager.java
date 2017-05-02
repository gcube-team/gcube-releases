package gr.cite.geoanalytics.manager;

import gr.cite.clustermanager.layers.DataCreatorGeoanalytics;
import gr.cite.clustermanager.layers.DataMonitor;
import gr.cite.clustermanager.model.GosDefinition;
import gr.cite.clustermanager.model.ZNodeData.ZNodeStatus;
import gr.cite.clustermanager.trafficshaping.TrafficShaper;
import gr.cite.gaap.datatransferobjects.AttributeInfo;
import gr.cite.gaap.datatransferobjects.LayerMessenger;
import gr.cite.gaap.datatransferobjects.LayerMessengerForAdminPortlet;
import gr.cite.gaap.datatransferobjects.LayerStyleMessenger;
import gr.cite.gaap.datatransferobjects.ShapefileImportProperties;
import gr.cite.gaap.datatransferobjects.TsvImportProperties;
import gr.cite.gaap.datatransferobjects.WfsRequestLayer;
import gr.cite.gaap.datatransferobjects.ShapeImportInfo;
//import gr.cite.gaap.datatransferobjects.ImportMetadata;
import gr.cite.gaap.datatransferobjects.WfsRequestMessenger;
import gr.cite.gaap.datatransferobjects.WfsShapeInfo;
import gr.cite.gaap.geospatialbackend.exceptions.NoAvailableGos;
import gr.cite.gaap.datatransferobjects.request.ImportMetadata;
import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.GeographyHierarchy;
import gr.cite.gaap.servicelayer.GeospatialBackendClustered;
import gr.cite.gaap.servicelayer.Toolbox;
import gr.cite.gaap.viewbuilders.PostGISMaterializedViewBuilder;
import gr.cite.gaap.servicelayer.GeocodeManager;
import gr.cite.geoanalytics.common.ShapeAttributeDataType;
import gr.cite.geoanalytics.common.ViewBuilder;
import gr.cite.geoanalytics.context.Configuration;
import gr.cite.geoanalytics.context.GeoServerBridgeConfig;
import gr.cite.geoanalytics.dataaccess.entities.geocode.Geocode;
import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
import gr.cite.geoanalytics.dataaccess.entities.layer.LayerImport;
import gr.cite.geoanalytics.dataaccess.entities.layer.LayerTag;
import gr.cite.geoanalytics.dataaccess.entities.layer.LayerTenant;
import gr.cite.geoanalytics.dataaccess.entities.layer.dao.LayerTagDao;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerBounds;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.SystemPresentationConfig;
import gr.cite.geoanalytics.dataaccess.entities.tag.Tag;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.GeoNetworkBridge;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.FeatureType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.GeoserverLayer;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.exception.GeoNetworkBridgeException;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.exception.GeoServerBridgeException;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.geoservermanager.GSManagerGeoNetworkBridge;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.elements.MetaDataForm;
import gr.cite.geoanalytics.security.SecurityContextAccessor;
import gr.cite.gos.client.GeoserverManagement;
import gr.cite.gos.client.ShapeManagement;
import gr.cite.geoanalytics.util.http.CustomException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.postgresql.util.PSQLException;
import org.apache.log4j.varia.LevelMatchFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.common.io.Files;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

@Service
public class ImportManager {

	public static Logger logger = LoggerFactory.getLogger(ImportManager.class);

	@Autowired private ViewBuilder builder;
//	@Autowired private GeospatialBackend shapeManager;
	@Autowired private GeocodeManager geocodeManager;
	@Autowired private ConfigurationManager configurationManager;
	@Autowired private SecurityContextAccessor securityContextAccessor;
//	@Autowired private GeoServerBridge geoServerBridge;
	@Autowired private Configuration configuration;
	@Autowired private LayerManager layerManager;
	@Autowired private TenantManager tenantManager;
	@Autowired private LayerTagDao layerTagDao;
	private final static Object geoNetworkLock = new Object();
	private final static Object databaseLock = new Object();

	//the following is part of the client to exchange information with the gos nodes
	@Autowired private GeospatialBackendClustered geospatialBackendClustered;
	//these two are part of the Zookeeper Cluster management (monitoring and editing) 
	@Autowired private DataMonitor dataMonitor;
	@Autowired private DataCreatorGeoanalytics dataCreatorGeoanalytics;
	//this is for managing the geoserver instances
	@Autowired private GeoserverManagement geoserverManagement;
	//this is for traffic shaping
	@Autowired private TrafficShaper trafficShaper;

	public ImportManager() {
	}

	@Transactional(readOnly = true)
	public Map<String, String> analyzeAttributes(String filename, String charset) throws Exception {
		boolean inferTypes = true;
		return new Toolbox().analyzeAttributesOfShapeFile(filename, charset, inferTypes);
	}

	@Transactional(readOnly = true)
	public Set<String> getAttributeValues(String filename, String charset, String attribute) throws Exception {
		return new Toolbox().getAttributeValuesFromShapeFile(filename, charset, attribute);
	}

	@Async("importPool")
	public void importTsvLayer(LayerImport layerImport, ImportMetadata metadata, String tsvData, String tenantName) throws NoAvailableGos, Exception{
		String layerName = layerImport.getName();
		String geocodeSystemName = layerImport.getGeocodeSystem();
		String description = metadata.getDescription();
		List<String> keywords = metadata.getKeywords();

		// decide in which gos it will be added
		GosDefinition gosDefinition = trafficShaper.getGosForNewLayer();

		Layer layer = null;

		try {
			GeocodeSystem geocodeSystem = this.geocodeManager.findGeocodeSystemByName(geocodeSystemName, false);
			Layer templateLayer = this.layerManager.findTemplateLayerByGeocodeSystem(geocodeSystem);

			layer = this.createLayerInDatabase(layerImport, geocodeSystem, tenantName, description);
			Collection<Shape> shapesOfLayer = this.tsvParsing(templateLayer, layer, tsvData);

			this.createTagsOfLayer(layer, keywords);
			this.createShapesOfLayer(gosDefinition, shapesOfLayer);
			this.createDataBaseView(gosDefinition, new ArrayList<>(shapesOfLayer), layer.getId().toString());
			this.publishLayerToGeoServer(gosDefinition, templateLayer, layer);
			this.publishLayerToGeoNetworkAsync(30000, layer.getId().toString(), tenantName, layerName, metadata);
			this.updateImportStatusToSuccess(layerImport, layer);
		} catch (Exception e) {
			try {
				if (layer != null) {
					this.removeLayer(gosDefinition, layer);
				}
			} catch (Exception ex) {
				logger.error("Error while rolling back layer and database view", ex);
			} finally {
				this.updateImportStatusToFailure(layerImport);
			}

			logger.error(null, e);
		}
		
		logger.info("Layer \"" + layer.getName() + " ( " + layer.getId() + " ) has been imported successfully!");
	}

	@Async("importPool")
	public void importWfsLayer(LayerImport layerImport, WfsRequestLayer layerInfo, String tenantName, String pathName, WfsRequestMessenger reqM) throws Exception {
		String layerName = layerInfo.getLayerName();
		String layerDescription = layerInfo.getLayerDescription();
		
		//decide in which gos it will be added
		GosDefinition gosDefinition = trafficShaper.getGosForNewLayer();
		logger.info("Importing of layer "+ layerImport.getName() + " will be submitted to GOS: "+gosDefinition.getGosEndpoint());

		Layer layer = null;
		try {

			WfsShapeInfo wfsShapeInfo = null;
			Map<String, String> attrs = null;
			ImportMetadata metadata = null;
			
			synchronized (this) {
				attrs = this.analyzeAttributes(pathName, "UTF-8");
				List<AttributeInfo> attrInfo = new ArrayList<AttributeInfo>();
				for (Map.Entry<String, String> attrEntry : attrs.entrySet()) {
					AttributeInfo aI = new AttributeInfo(attrEntry.getKey(), attrEntry.getValue(), null, null, true, true);
					attrInfo.add(aI);
				}
				// Get Metadata from wfs call
				metadata = this.getCapabilitiesForPublishingToGeonetwork(reqM, tenantName);
				layer = this.createLayerInDatabase(layerImport, null, tenantName, layerDescription);
				Map<String, Map<String, AttributeInfo>> attrInfoM = createAttributeInfoMap(attrInfo);
				Principal principal = layerImport.getCreator();
				wfsShapeInfo = geospatialBackendClustered.getShapesFromShapefile(pathName, layer.getId().toString(), 4326, "UTF-8", true, attrInfoM, principal, true);
			}
			this.createShapesOfLayer(gosDefinition, wfsShapeInfo.getListShape());
			this.createDataBaseView(gosDefinition, wfsShapeInfo.getListShape(), layer.getId().toString());
			this.publishWfsLayerToGeoServer(gosDefinition, layer, wfsShapeInfo.getBounds());
			this.publishLayerToGeoNetworkAsync(30000, layer.getId().toString(), tenantName, layerName, metadata);
			this.updateImportStatusToSuccess(layerImport, layer);
			
		} catch (Exception e) {
			try {
				if (layer != null) {
					this.removeLayer(gosDefinition, layer);
				}
			} catch (Exception ex) {
				logger.error("Error while rolling back layer and database view", ex);
			} finally {
				this.updateImportStatusToFailure(layerImport);
			}

			logger.error(null, e);
		}
	}

	@Async("importPool")
	public void importShapeFileLayer(LayerImport layerImport, ShapefileImportProperties properties, ImportMetadata metadata, String tenantName, String pathName) throws Exception {
		Layer layer = null;
		String layerName = layerImport.getName();
		String layerDescription = metadata.getDescription();
		Principal principal = layerImport.getCreator();

		//decide in which gos it will be added
		GosDefinition gosDefinition = trafficShaper.getGosForNewLayer();

		Map<String, String> attrs = this.analyzeAttributes(pathName, "UTF-8");

		try {
			WfsShapeInfo wfsShapeInfo = null;

			List<AttributeInfo> attrInfo = new ArrayList<AttributeInfo>();
			for (Map.Entry<String, String> attrEntry : attrs.entrySet()) {
				AttributeInfo aI = new AttributeInfo(attrEntry.getKey(), attrEntry.getValue(), null, null, true, true);
				attrInfo.add(aI);
			}

			layer = this.createLayerInDatabase(layerImport, null, tenantName, layerDescription);
			Map<String, Map<String, AttributeInfo>> attrInfoM = createAttributeInfoMap(attrInfo);
			wfsShapeInfo = geospatialBackendClustered.getShapesFromShapefile(pathName, layer.getId().toString(), 4326, "UTF-8", true, attrInfoM, principal, true);			
			this.createShapesOfLayer(gosDefinition, wfsShapeInfo.getListShape());
			this.createDataBaseView(gosDefinition, wfsShapeInfo.getListShape(), layer.getId().toString());
			this.publishWfsLayerToGeoServer(gosDefinition, layer, wfsShapeInfo.getBounds());
			this.publishLayerToGeoNetworkAsync(30000, layer.getId().toString(), tenantName, layerName, metadata);
			this.updateLayerIfTemplate(layer, wfsShapeInfo.getListShape(), properties);
			this.updateImportStatusToSuccess(layerImport, layer);
		} catch (Exception e) {
			try {
				if (layer != null) {
					this.removeLayer(gosDefinition, layer);
				}
			} catch (Exception ex) {
				logger.error("Error while rolling back layer and database view", ex);
			} finally {
				this.updateImportStatusToFailure(layerImport);
			}

			logger.error(null, e);
		}
	}

	private Layer createLayerInDatabase(LayerImport layerImport, GeocodeSystem geocodeSystem, String tenantName, String description) throws Exception {
		logger.info("Creating Layer \"" + layerImport.getName() + "\" in Database");

		Principal principal = layerImport.getCreator();
		String name = layerImport.getName();

		Layer layer = new Layer();
		layer.setCreator(principal);
		layer.setName(name);
		layer.setGeocodeSystem(geocodeSystem);
		layer.setDescription(description);
		layer.setExtraData("<extraData geographic = \"true\" />");
		layerManager.createLayer(layer);

		Tenant tenant = tenantManager.findByName(tenantName);

		LayerTenant layerTenant = new LayerTenant();
		layerTenant.setLayer(layer);
		layerTenant.setTenant(tenant);
		layerManager.createLayerTenant(layerTenant);

		logger.info("Layer with name: \"" + name + "\" and id: \"" + layer.getId() + "\" has been created successfully");

		return layer;
	}

	private void createShapesOfLayer(GosDefinition gosDefinition, Collection<Shape> shapesOfLayer) throws Exception{	
		logger.info("Inserting " + shapesOfLayer.size() + " shapes into database (through GOS)");		
		
		List<String> gosEndpoints = geospatialBackendClustered.createShapesOfLayer(gosDefinition.getGosEndpoint(), shapesOfLayer);

		logger.info(shapesOfLayer.size() + " shapes have been inserted successfully on gos endpoints: "+gosEndpoints);
	}

	private void createTagsOfLayer(Layer layer, List<String> keywords) throws Exception {
		if (keywords != null && keywords.size() > 0) {
			Principal principal = securityContextAccessor.getPrincipal();
			List<Tag> tags = keywords.stream().map(name -> new Tag().withName(name).withCreator(principal)).collect(Collectors.toList());

			logger.info("Inserting " + tags.size() + " tags of layer into database");

			layerManager.createTagsOfLayer(layer, tags);

			logger.info(tags.size() + " tags have been inserted successfully!");
		}
	}
	
	public boolean deleteLayerFromInfra(String layerID){
		
		Layer layer = new Layer();
		layer.setId(UUID.fromString(layerID));
		try{
			layerManager.deleteLayer(layer);
			geospatialBackendClustered.deleteShapesOfLayer(layer.getId());
			
			Set<GosDefinition> layersGos = dataMonitor.getAvailableGosFor(layerID);
			layersGos.addAll(dataMonitor.getNotAvailableGosFor(layerID));
			
			Set<Boolean> results = layersGos.parallelStream()
					 .map(gosDef -> {
						 try{
							 this.deleteDataBaseView(gosDef.getGosEndpoint(), layer.getId().toString());
							 this.geoserverManagement.deleteGeoserverLayer(gosDef.getGosEndpoint(), layerID);
							 this.dataCreatorGeoanalytics.deleteLayer(layerID, gosDef.getGosEndpoint());
							 return new HashSet<Boolean>(Arrays.asList(true));
						 } 
						 catch(Exception ex){
							 logger.error("Could not delete database view and/or geoserver layer of layerID: "+layerID +" on gos: "+gosDef.getGosEndpoint());
							 return new HashSet<Boolean>(Arrays.asList(false));
						 }
					 })
					 .reduce((s1, s2) -> {HashSet<Boolean> s = new HashSet<Boolean>(); s.addAll(s2); s.addAll(s2); return s;})
					 .get();
			
			if(results.contains(false))
				return false;
			
			return true;
		}
		catch(Exception ex){
			logger.error("Could not delete layer "+layerID+" from infrastructure... -> Exception: "+ex.getMessage());
			return false;
		}
		
	}
	
	
	
	public LayerImport createTsvLayerImport(TsvImportProperties properties, String description) throws Exception {
		Principal creator = securityContextAccessor.getPrincipal();

		LayerImport layerImport = new LayerImport();
		layerImport.setStatus((short) 0);
		layerImport.setType(LayerImport.TYPE_TSV);
		layerImport.setCreator(creator);
		layerImport.setName(properties.getLayerName());
		layerImport.setGeocodeSystem(properties.getGeocodeSystem());
		layerImport.setFileName(properties.getFileName());
		layerImport.setDescription(description);

		layerManager.createLayerImport(layerImport);

		return layerImport;
	}
	
	public LayerImport createWfsLayerImport(WfsRequestLayer layerInfo, String sourceURL) throws Exception {
		Principal creator = securityContextAccessor.getPrincipal();

		LayerImport layerImport = new LayerImport();
		layerImport.setStatus((short) 0);
		layerImport.setType(LayerImport.TYPE_WFS);
		layerImport.setCreator(creator);
		layerImport.setName(layerInfo.getLayerName());
		layerImport.setGeocodeSystem(null);
		layerImport.setFileName(sourceURL);
		layerImport.setDescription(layerInfo.getLayerDescription());

		layerManager.createLayerImport(layerImport);

		return layerImport;
	}
	
	public LayerImport createShapeFileLayerImport(ShapefileImportProperties properties, String filename) throws Exception {
		Principal creator = securityContextAccessor.getPrincipal();

		LayerImport layerImport = new LayerImport();
		layerImport.setStatus((short) 0);
		layerImport.setType(LayerImport.TYPE_SHAPEFILE);
		layerImport.setCreator(creator);
		layerImport.setName(properties.getNewLayerName());
		layerImport.setFileName(filename);
		layerImport.setDescription(null);
		
		if(properties.isTemplate()){			
			GeocodeSystem geocodeSystem = geocodeManager.findGeocodeSystemByName(properties.getGeocodeSystem(), false);

			if (geocodeSystem != null) {
				throw new CustomException(HttpStatus.BAD_REQUEST, "Geocode System " + properties.getGeocodeSystem() + " already exists");						
			}
			layerImport.setGeocodeSystem(properties.getGeocodeSystem());
		}
		
		layerManager.createLayerImport(layerImport);

		return layerImport;
	}
	
	private void updateLayerIfTemplate(Layer layer, List<Shape> shapes, ShapefileImportProperties properties) throws Exception{
		if (properties.isTemplate()) {
			Principal creator = layer.getCreator();
			String geocodeSystemName = properties.getGeocodeSystem();
			String geocodeMapping = properties.getGeocodeMapping();

			GeocodeSystem geocodeSystem = this.geocodeManager.createGeocodeSystem(creator, geocodeSystemName);
			layer.setGeocodeSystem(geocodeSystem);
			layer.setIsTemplate((short) 1);
			this.layerManager.updateLayer(layer);
			this.geocodeManager.createGeocodesOfTemplateLayer(layer, shapes, geocodeMapping);
		}
	}
	
	private void updateImportStatusToSuccess(LayerImport layerImport, Layer layer) throws Exception {
		layerImport.setLayer(layer);
		layerImport.setStatus((short) 1);
		layerManager.updateLayerImport(layerImport);
	}

	private void updateImportStatusToFailure(LayerImport layerImport) throws Exception {
		layerImport.setLayer(null);
		layerImport.setStatus((short) -1);
		layerManager.updateLayerImport(layerImport);
	}

	private void publishLayerToGeoServer(GosDefinition gosDefinition, Layer templateLayer, Layer newLayer) throws Exception{ 
		try {
			String style = "line";
			LayerConfig templateLayerConfig = configurationManager.getLayerConfig(templateLayer.getId());
			Bounds boundingBox = new Bounds(templateLayerConfig.getBoundingBox());

			LayerConfig layerConfig = new LayerConfig();
			layerConfig.setName(newLayer.getName());
			layerConfig.setTermId(newLayer.getId().toString());
			layerConfig.setStyle(style);

			LayerBounds layerBounds = new LayerBounds();
			layerBounds.setMinX(boundingBox.getMinx());
			layerBounds.setMinY(boundingBox.getMiny());
			layerBounds.setMaxX(boundingBox.getMaxx());
			layerBounds.setMaxY(boundingBox.getMaxy());
			layerConfig.setBoundingBox(layerBounds);

			newLayerFromImportedData(gosDefinition, newLayer.getId().toString(), layerConfig, boundingBox.getCrs(), style);			

			configurationManager.addLayerConfig(layerConfig);
		} catch (Exception e) {
			throw new Exception("Error occured while publishing Layer to GeoServer", e);
		}
	}

	private void publishWfsLayerToGeoServer(GosDefinition gosDefinition, Layer newLayer, Bounds bounds) throws Exception{ 
		logger.info("Adding layer entry "+newLayer.getId().toString() +" to geoserver of gos: "+gosDefinition.getGosEndpoint());
		try {
			String style = "line";
			Bounds boundingBox = bounds;

			LayerConfig layerConfig = new LayerConfig();
			layerConfig.setName(newLayer.getName());
			layerConfig.setTermId(newLayer.getId().toString());
			layerConfig.setStyle(style);

			LayerBounds layerBounds = new LayerBounds();
			layerBounds.setMinX(boundingBox.getMinx());
			layerBounds.setMinY(boundingBox.getMiny());
			layerBounds.setMaxX(boundingBox.getMaxx());
			layerBounds.setMaxY(boundingBox.getMaxy());
			layerConfig.setBoundingBox(layerBounds);

			newLayerFromImportedData(gosDefinition, newLayer.getId().toString(), layerConfig, boundingBox.getCrs(), style);

			configurationManager.addLayerConfig(layerConfig);
			
			logger.info("Added layer entry "+newLayer.getId().toString() +" to geoserver of gos: "+gosDefinition.getGosEndpoint());
		} catch (Exception e) {
			throw new Exception("Could not publish layer to GeoServer", e);
		}
	}	
	
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = { Exception.class })
	private void createDataBaseView(GosDefinition gosDefinition, List<Shape> layerShapes, String identity) throws Exception {
		synchronized (databaseLock) {
			logger.info("Creating Materialized View of \"" + identity + "\"");

			builder.forIdentity(identity).forShapes(layerShapes).createViewStatement().execute(gosDefinition.getGosEndpoint());

			logger.info("Materialized View of \"" + identity + "\" has been created successfully!");
		}
	}

	private void deleteDataBaseView(String gosEndpoint, String identity) throws Exception {
		logger.info("Dropping Materialized View of \"" + identity + "\"");
		builder.forIdentity(identity).removeViewStatement().execute(gosEndpoint);
		logger.info("Materialized View of \"" + identity + "\" was dropped successfully!");
	}

	private void publishLayerToGeoNetworkAsync(long zookeeperTimeoutMillis, String layerID, String tenant, String layerName, ImportMetadata importMetadata) throws Exception {		
		
		//note that the data monitor might have not been already notified about the layer, since it was just before inserted on geoservers, 
		//so we need to wait till the datamonitor is notified.
		
		//spawn a new thread to handle this.
		new Thread(){
			public void run(){
				logger.info("Async publishing to geonetwork is spawned... might take some time");
				long elapsedTime = 0, stepping = 100;
				Set<GosDefinition> availableGosForLayer = dataMonitor.getAvailableGosFor(layerID);
				while((availableGosForLayer==null || availableGosForLayer.isEmpty()) && elapsedTime<=zookeeperTimeoutMillis){
					availableGosForLayer = dataMonitor.getAvailableGosFor(layerID);
					try{Thread.sleep(stepping);}catch(InterruptedException ex){}
					elapsedTime += stepping;
				}
				if(availableGosForLayer==null || availableGosForLayer.isEmpty()){
					logger.info("Could not publish metadata to geonetwork for layer \"" + layerName + "\" . Could not get information from any gos geoserver. Is it inserted in any geoserver?" );
					return;
				}
				GosDefinition gosDefinition = new ArrayList<GosDefinition>(availableGosForLayer).get(0);
				try{
					publishLayerToGeoNetwork(gosDefinition, tenant, layerName, layerID ,importMetadata);
				}
				catch(Exception ex){
					logger.info("Publishing metadata of layer \""+layerName+"\" to geonetwork has failed!!!");
				}
			}
		}.start();


	}
	
	
	private void publishLayerToGeoNetwork(GosDefinition gosDefinition, String tenant, String layerName, String layerId, ImportMetadata importMetadata) throws Exception {
		logger.info("Publishing metadata to geonetwork for layer \"" + layerName + "\"");
		
		MetaDataForm meta = new MetaDataForm(importMetadata.getUser(), importMetadata.getTitle(), new Date());
		meta.setAbstractField(importMetadata.getDescription());
		meta.setPurpose(importMetadata.getPurpose());
		meta.setKeywords(importMetadata.getKeywords());
		meta.setUserLimitation(importMetadata.getLimitation());
		meta.setDistributorOrganisationName(importMetadata.getDistributorOrganisationName());
		meta.setDistributorIndividualName(importMetadata.getDistributorIndividualName());
		meta.setDistributorSite(importMetadata.getDistributorOnlineResource());
		meta.setProviderIndividualName(importMetadata.getProviderIndividualName());
		meta.setProviderSite(importMetadata.getProviderOnlineResource());
		meta.setProviderOrganisationName(importMetadata.getProviderOrganisationName());

		GeoserverLayer geoserverLayer = geoserverManagement.getGeoserverLayer(gosDefinition.getGosEndpoint(), layerId);
		if (geoserverLayer != null) {
			FeatureType featureType = geoserverManagement.getFeatureType(gosDefinition.getGosEndpoint(), layerId);
			featureType.setSrs("EPSG:4326");
			meta.setGraphicOverviewFromGeoserverLayer(geoserverLayer, featureType, gosDefinition.getGeoserverEndpoint());
		}

		synchronized (geoNetworkLock) {
			try {
				GeoNetworkBridge geo = new GSManagerGeoNetworkBridge();
				geo.publishGeonetwork(tenant, meta);
				logger.info("Metadata for layer \"" + layerName + "\" have been imported successfully");
			} catch (GeoNetworkBridgeException e) {
				throw new GeoNetworkBridgeException("Could not publish layer metadata to GeoNetwork", e);
			}
		}

		logger.info("Metadata for layer \"" + layerName + "\" have been published to geonetwork successfully");
	}

	public Collection<Shape> tsvParsing(Layer templateLayer, Layer newLayer, String tsvData) throws Exception {
		Map<String, Shape> newShapes = new HashMap<>();

		try {
			UUID newLayerID = newLayer.getId();
			Principal creator = securityContextAccessor.getPrincipal();

			Map<String, Shape> sourceShapes = geospatialBackendClustered.getShapesOfLayer(templateLayer);	

			if (sourceShapes == null) {
				throw new Exception("Template layer " + templateLayer.getName() + " does not have shapes");
			}

			CSVParser csvParser = CSVParser.parse(tsvData, CSVFormat.TDF.withRecordSeparator('\n'));
			CSVRecord csvRecordHeader = null;
			int recordIndex = 0;
			int geoPos = -1;

			for (CSVRecord record : csvParser) {
				if (recordIndex != 0) {
					String[] recordsAsArray;
					String attributeTag = null;
					String geoAttributeValue = null;
					String prefixOfAttribute = null;
					String extraTags = "";
					int valueIndex = 0;

					Shape sourceShape = null, targetShape = null;

					for (String value : record) {
						if (valueIndex == 0) {
							recordsAsArray = value.split(",");
							List<String> recordsAsList = new ArrayList<String>(Arrays.asList(recordsAsArray));
							geoAttributeValue = recordsAsList.remove((geoPos));
							geoAttributeValue = geoAttributeValue.substring(0, 1).toUpperCase() + geoAttributeValue.substring(1).toLowerCase();

							if (recordsAsList.isEmpty()) {
								prefixOfAttribute = String.join("_", newLayer.getName());
							} else {
								prefixOfAttribute = String.join("_", recordsAsList);
							}

							sourceShape = sourceShapes.get(geoAttributeValue);

							if (sourceShape == null) {
								break;
							}

							if (!newShapes.containsKey(geoAttributeValue)) {
								targetShape = this.newShapeBasedOnOld(creator, newLayerID, sourceShape);
								newShapes.put(geoAttributeValue, targetShape);
							} else {
								targetShape = newShapes.get(geoAttributeValue);
							}
						} else {
							attributeTag = prefixOfAttribute + "_" + csvRecordHeader.get(valueIndex);
							extraTags = addExtraTag(extraTags, attributeTag, value);
						}
						valueIndex++;

						if (valueIndex == record.size()) {
							String extraData = addShapeAttributes(targetShape.getExtraData(), extraTags);
							targetShape.setExtraData(extraData);
						}
					}
				} else {
					csvRecordHeader = record;
					String firstPartOfheader = "";
					try {
						String[] headers = record.get(0).split("\\\\");
						firstPartOfheader = Arrays.asList(headers).get(0);
					} catch (Exception e) {
						throw new RuntimeException("Error TSV is does not have proper formation", e);
					}
					String[] firstPartOfHeaderArrayCommaSeparated = firstPartOfheader.split(",");
					List<String> firstPartOfHeaderListCommaSeparated = Arrays.asList(firstPartOfHeaderArrayCommaSeparated);
					geoPos = firstPartOfHeaderListCommaSeparated.indexOf("geo");
				}
				recordIndex++;
			}

		} catch (Exception e) {
			throw new Exception("Error during parsing of Tsv File", e);
		}

		logger.info("Tsv Parsing finished.");

		return newShapes.values();
	}

	public String addExtraTag(String extraTags, String attribute, String value) {
		String openingTag = "<" + attribute + " type=\"" + ShapeAttributeDataType.STRING.toString().toUpperCase() + "\">";
		String closingTag = "</" + attribute + ">";
		return extraTags + openingTag + value + closingTag;
	}

	public String addShapeAttributes(String extraData, String extraTags) throws Exception {
		int index = extraData.lastIndexOf("</extraData>");
		String prefixOfExtraData = extraData.substring(0, index);
		String suffixOfExtraData = extraData.substring(index, extraData.length());
		return prefixOfExtraData + extraTags + suffixOfExtraData;
	}

	private Shape newShapeBasedOnOld(Principal principal, UUID layerID, Shape sourceShape) throws Exception {
		Shape targetShape = new Shape();
		targetShape.setCode(sourceShape.getCode());
		targetShape.setCreatorID(principal.getId());
		targetShape.setGeography(sourceShape.getGeography());
		targetShape.setName(sourceShape.getName());
		targetShape.setExtraData(sourceShape.getExtraData());
		targetShape.setLayerID(layerID);
		return targetShape;
	}

	
//	public ShapeImportInfo importLayerFromShapeFile(String filename, Layer layer, Geocode boundaryTerm,
//			int srid, String charset, boolean forceLonLat, boolean newLayer, List<AttributeInfo> attrInfo,
//			Principal principal, boolean overwriteMappings, String style, GeographyHierarchy hierarchy)
//			throws Exception {
//
//		//decide in which gos will be inserted
//		GosDefinition gosDefinition = trafficShaper.getGosForNewLayer();
//
//		
//		ShapeImportInfo info = importLayerFromShapeFileToDataBase(gosDefinition, filename, layer, boundaryTerm, srid, charset,
//				forceLonLat, newLayer, attrInfo, principal, hierarchy);
//
//		
//		try {
//			if(newLayer){
//				this.setUpNewLayer(gosDefinition, layer, boundaryTerm, null, info.getBoundingBox());
//			}else{
//				this.setUpUpdatedLayer(gosDefinition, layer, boundaryTerm, null, null, info.getBoundingBox());
//			}
//			securityContextAccessor.updateLayers(); // update accessible layers (only for current user, which is
//													// acceptable because layers are added by a single user with
//													// administrative rights)
//		} catch (PSQLException p) {
//			logger.error("Not a valid name for layer", p);
//	//		this.removeLayer(tt); teleiws na ta sxoliasw h mhpws apla na ele3w ti kaleitai wste na mhn pesw panw se null?
//	//		removeDataBaseView(tt);
//			p.printStackTrace();
//			throw p;
//		} catch (Exception e) {  // TODO: prin valw to psql exception apo panw
//			//ti ekane? Phgaine na kanei remove alla den uphrxe!! K eskage
//			logger.error("Error while importing data from shape file", e);
//			this.removeLayer(gosDefinition, layer);
//			e.printStackTrace();
//			return null;
//		}
//		return info;
//	}
//
//
//	@Transactional(rollbackFor = { Exception.class })
//	void removeDataBaseView(Layer layer, List<Shape> listOfShapes) throws Exception {
//		this.builder.forIdentity(layer.getId().toString()).forShapes(listOfShapes).removeViewIfExists();
//	}
//
//
//	@Transactional(rollbackFor = { GeoServerBridgeException.class, Exception.class })
//	private ShapeImportInfo importLayerFromShapeFileToDataBase(GosDefinition gosDefinition, String filename, Layer layer,
//			Geocode boundaryTerm, int srid, String charset, boolean forceLonLat, boolean newLayer,
//			List<AttributeInfo> attrInfo, Principal principal, GeographyHierarchy hierarchy) throws Exception {
//
//		ShapeImportInfo info = null;
//		boolean removed = false;
//		GeoserverLayer l = null;
//		FeatureType ft = null;
//		LayerConfig lcfg = configurationManager.getLayerConfig(layer.getId());
//		Map<String, String> layerStyles = null;
//		String layerDefaultStyle = null; // default style for existing layer
//
//		try {
//			if (lcfg != null && newLayer == true) {
//				// defensive actions
//				l = geoserverManagement.getGeoserverLayer(gosDefinition.getGosEndpoint(), layer.getId().toString()); // TODO could maybe create new l and ft from lcfg (style?)
//				layerStyles = configurationManager.getLayerStyles();
//				layerDefaultStyle = configurationManager.getDefaultTermStyle(layer.getId().toString());
//				if (l != null) {
//					ft = geoserverManagement.getFeatureType(gosDefinition.getGosEndpoint(), layer.getName());
//				}
//				removeLayer(gosDefinition, layer);
//				removed = true;
//			}
//
//			LayerConfig layerConfig = new LayerConfig();
//
//			info = newLayerFromShapeFile(gosDefinition, filename, layer, boundaryTerm, srid, charset, forceLonLat, newLayer, attrInfo,
//					principal, true, layerConfig, layerDefaultStyle, hierarchy); // TODO expose overwriteMappings
//
////			 setUpNewOrUpdatedLayer(tt, boundaryTerm, newLayer, layerConfig, style, info.getBoundingBox());
//
//			securityContextAccessor.updateLayers(); // update accessible layers (only for current user, which is
//													// acceptable because layers are added by a single user with
//													// administrative rights)
//			return info;
//		} catch (Exception e) {
//			handleLayerUpdateException(gosDefinition, layer, newLayer, removed, ft, lcfg, layerStyles, e);
//			return null;
//		}
//	}
//
//	private void handleLayerUpdateException(GosDefinition gosDefinition, Layer layer, boolean newLayer, boolean removed, FeatureType ft,
//			LayerConfig lcfg, Map<String, String> layerStyles, Exception e) throws Exception {
//
//		GeoserverLayer l;
//		logger.error("Error while importing layer", e);
//		if (newLayer == false && removed == true) {
//			logger.info("Attempting to recover from layer removal");
//			try {
//				if ((l = geoserverManagement.getGeoserverLayer(gosDefinition.getGosEndpoint(), layer.getName())) == null) {
//
//					geoserverManagement.addGeoserverLayer(gosDefinition.getGosEndpoint(), l, ft, layerStyles, lcfg.getMinScale(), lcfg.getMaxScale());
//				}
//				throw e;
//			} catch (GeoServerBridgeException gbe) {
//				logger.error("Unable to recover from layer removal", gbe);
//				throw new Exception("Unable to recover from layer removal", gbe);
//			}
//		} else {
//			throw e;
//		}
//
//	}
//
//	@Transactional(rollbackFor = { Exception.class })
//	public ShapeImportInfo newLayerFromShapeFile(GosDefinition gosDefinition, String filename, Layer layer, Geocode boundaryTerm, int srid,
//			String charset, boolean forceLonLat, boolean newLayer, List<AttributeInfo> attrInfo, Principal principal,
//			boolean overwriteMappings, LayerConfig layerConfig, String style, GeographyHierarchy hierarchy)
//			throws Exception {
//
//		Map<String, Map<String, AttributeInfo>> attrInfoM = createAttributeInfoMap(attrInfo);
//		ShapeImportInfo info = geospatialBackendClustered.getShapeManagement().fromShapefile(filename, layer.getId().toString(), srid, charset,
//				forceLonLat, attrInfoM, principal, overwriteMappings);
////		geospatialBackendClustered.generateShapesOfImport(gosEndpoint, layer.getId(), attrInfoM, info.getValueMappingValues(), info.getImportId(), layer.getId().toString(), hierarchy, principal);
//		if (boundaryTerm != null)
//			geospatialBackendClustered.generateShapeBoundary(layer.getId(), layer.getName(), boundaryTerm, principal);
//
//		String layerName = newLayer ? layer.getName() : layerConfig.getName();
//		info.setLayerName(layerName);
//
//		if (!newLayer) {
//			info.setBoundingBox(
//					new Bounds(layerConfig.getBoundingBox().getMinX(), layerConfig.getBoundingBox().getMinY(),
//							layerConfig.getBoundingBox().getMaxX(), layerConfig.getBoundingBox().getMaxY(), null));
//		}
//		projectManager.updateAllProjectAttributes();
//
//		return info;
//	}
//	
//	@Transactional(rollbackFor = { Exception.class })
//	public void setUpNewLayer(GosDefinition gosDefinition, Layer layer, Geocode boundaryTerm, String style, Bounds boundingBox) throws Exception {
//		LayerConfig layerConfig = new LayerConfig() ;
//		layerConfig.setName(layer.getName());
//		layerConfig.setTermId(layer.getId().toString());
//		
//		if (style == null) {
//			style = configurationManager.getDefaultTermStyle(layer.getId().toString());
//		}
//		
//		if (boundaryTerm != null){
//			layerConfig.setBoundaryTermId(boundaryTerm.getId().toString());
//		}
//
//		LayerBounds b = new LayerBounds();
//		b.setMinX(boundingBox.getMinx());
//		b.setMinY(boundingBox.getMiny());
//		b.setMaxX(boundingBox.getMaxx());
//		b.setMaxY(boundingBox.getMaxy());
//		layerConfig.setBoundingBox(b);
//		
////		this.createDataBaseView(gosDefinition, layer.getId().toString());
//		
//		//Publish Layer to Geoserver
//		newLayerFromImportedData(gosDefinition, layer.getId().toString(), layerConfig, boundingBox.getCrs(), style);
//
//		configurationManager.addLayerConfig(layerConfig);		
//	}
//	
//	@Transactional(rollbackFor = { Exception.class })
//	public void setUpUpdatedLayer(GosDefinition gosDefinition, Layer layer, Geocode boundaryTerm,
//			LayerConfig layerConfig, String style, Bounds boundingBox) throws Exception {
//
//		layerConfig = (layerConfig == null) ? new LayerConfig() : layerConfig;
//
//		if (style == null) {
//			style = configurationManager.getDefaultTermStyle(layer.getId().toString());
//		}
//
//		// TODO modify zoom levels, add style?
////		this.createDataBaseView(layer.getId().toString());
//
//
//		LayerConfig lcfg = configurationManager.getLayerConfig(layer.getId());
//
//		LayerBounds b = new LayerBounds();
//		b.setMinX(boundingBox.getMinx());
//		b.setMinY(boundingBox.getMiny());
//		b.setMaxX(boundingBox.getMaxx());
//		b.setMaxY(boundingBox.getMaxy());
//		lcfg.getBoundingBox().mergeWith(b);
//		
//		if (boundaryTerm != null){
//			lcfg.setBoundaryTermId(boundaryTerm.getId().toString());
//		}
//
//		configureLayer(layer, SystemPresentationConfig.DEFAULT_THEME, style, lcfg.getMinScale(), lcfg.getMaxScale());
//		configurationManager.updateLayerConfig(lcfg);		
//	}


	private Map<String, Map<String, AttributeInfo>> createAttributeInfoMap(List<AttributeInfo> attrInfo) {
		Map<String, Map<String, AttributeInfo>> attrInfoM = new HashMap<String, Map<String, AttributeInfo>>();

		for (AttributeInfo ai : attrInfo) {
			if (attrInfoM.get(ai.getName()) == null)
				attrInfoM.put(ai.getName(), new HashMap<String, AttributeInfo>());
			if (ai.getValue() == null)
				attrInfoM.get(ai.getName()).put("", ai);
			else
				attrInfoM.get(ai.getName()).put(ai.getValue(), ai);
		}
		return attrInfoM;
	}

	/* if inputStream is enabled, doTheRequest returns InputStream (--needed for outputFormat=shape-zip purposes) */
	public Object doTheRequest(String url, Map<String, String> parameters, boolean inputStream) throws Exception {
		Client client = Client.create();
		WebResource webResource = null;

		MultivaluedMap<String, String> nameValuePairs = new MultivaluedMapImpl();
		for (Map.Entry<String, String> params : parameters.entrySet()) {
			if (params.getKey() != null && !params.getKey().isEmpty() && params.getValue() != null && !params.getValue().isEmpty())
				nameValuePairs.add(params.getKey(), params.getValue());
		}
		webResource = client.resource(url).queryParams(nameValuePairs);
		ClientResponse response = webResource.get(ClientResponse.class);

		if (response.getStatus() == 201 || response.getStatus() == 200) {
			try {
				if (inputStream)
					return response.getEntity(InputStream.class);
				return response.getEntity(String.class);

			} catch (Exception e) {
				System.err.println("Exception occured!");
			}
		}
		return null;
	}

	public ImportMetadata getCapabilitiesForPublishingToGeonetwork(WfsRequestMessenger reqM, String tenant) throws Exception {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("service", "wfs");
		parameters.put("version", "1.0.0");
		parameters.put("request", "GetCapabilities");

		String body = (String) doTheRequest(reqM.getUrl(), parameters, false);

		if (body == null)
			return null;

		return parseGetCapabilitiesForPublishingToGeonetwork(body, tenant);
	}

	public ImportMetadata parseGetCapabilitiesForPublishingToGeonetwork(String body, String tenant) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(body));

		Document document = db.parse(is);

		XPath xPath = XPathFactory.newInstance().newXPath();

		String name = (String) xPath.compile("/WFS_Capabilities/Service/Name/text()").evaluate(document, XPathConstants.STRING);
		String title = (String) xPath.compile("/WFS_Capabilities/Service/Title/text()").evaluate(document, XPathConstants.STRING);
		String abstractText = (String) xPath.compile("/WFS_Capabilities/Service/Abstract/text()").evaluate(document, XPathConstants.STRING);
		String keywords = (String) xPath.compile("/WFS_Capabilities/Service/Keywords/text()").evaluate(document, XPathConstants.STRING);
		String onlineResource = (String) xPath.compile("/WFS_Capabilities/Service/OnlineResource/text()").evaluate(document, XPathConstants.STRING);

		ImportMetadata wfsImportMetadata = new ImportMetadata();
		wfsImportMetadata.setAbstractField(abstractText);

		List<String> keyWs = new ArrayList<String>();
		String[] pieces = keywords.split(",");
		for (String k : pieces)
			keyWs.add(k);
		wfsImportMetadata.setKeywords(keyWs);
		wfsImportMetadata.setProviderIndividualName(name);
		wfsImportMetadata.setProviderOnlineResource(onlineResource);
		wfsImportMetadata.setProviderOrganisationName(name);
		wfsImportMetadata.setTitle(title);

		return wfsImportMetadata;
	}

	public List<LayerMessenger> getCapabilities(WfsRequestMessenger reqM, String tenant, boolean doPublish) throws Exception {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("service", "wfs");
		parameters.put("version", "1.0.0");
		parameters.put("request", "GetCapabilities");

		String body = (String) doTheRequest(reqM.getUrl(), parameters, false);

		if (body == null)
			return null;

		parseGetCapabilitiesForService(body, tenant, doPublish);

		return parseGetFeatureTypes(body);
	}

	public void parseGetCapabilitiesForService(String body, String tenant, boolean doPublish) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(body));

		Document document = db.parse(is);

		XPath xPath = XPathFactory.newInstance().newXPath();

		String name = (String) xPath.compile("/WFS_Capabilities/Service/Name/text()").evaluate(document, XPathConstants.STRING);
		String title = (String) xPath.compile("/WFS_Capabilities/Service/Title/text()").evaluate(document, XPathConstants.STRING);
		String abstractText = (String) xPath.compile("/WFS_Capabilities/Service/Abstract/text()").evaluate(document, XPathConstants.STRING);
		String keywords = (String) xPath.compile("/WFS_Capabilities/Service/Keywords/text()").evaluate(document, XPathConstants.STRING);
		String onlineResource = (String) xPath.compile("/WFS_Capabilities/Service/OnlineResource/text()").evaluate(document, XPathConstants.STRING);

		ImportMetadata wfsImportMetadata = new ImportMetadata();
		wfsImportMetadata.setAbstractField(abstractText);

		List<String> keyWs = new ArrayList<String>();
		String[] pieces = keywords.split(",");
		for (String k : pieces)
			keyWs.add(k);
		wfsImportMetadata.setKeywords(keyWs);
		wfsImportMetadata.setProviderIndividualName(name);
		wfsImportMetadata.setProviderOnlineResource(onlineResource);
		wfsImportMetadata.setProviderOrganisationName(name);
		wfsImportMetadata.setTitle(title);

//		if (doPublish)
//			publishLayerToGeoNetworkAsync(30000, tenant, name, wfsImportMetadata);
		
	}

	public List<LayerMessenger> parseGetFeatureTypes(String body) throws Exception {
		NodeList featureTypes = null;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(body));

		Document document = db.parse(is);
		document.getDocumentElement().normalize();

		XPath xPath = XPathFactory.newInstance().newXPath();
		featureTypes = (NodeList) xPath.compile("/WFS_Capabilities/FeatureTypeList/FeatureType").evaluate(document, XPathConstants.NODESET);

		List<LayerMessenger> featureTypesToReturn = new ArrayList<LayerMessenger>();

		for (int i = 0; i < featureTypes.getLength(); i++) {
			Node node = featureTypes.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				LayerMessenger lM = new LayerMessenger();

				Element elem = (Element) node;

				if (elem.getElementsByTagName("Title").item(0).getFirstChild() != null)
					lM.setTitle(elem.getElementsByTagName("Title").item(0).getFirstChild().getNodeValue());
				if (elem.getElementsByTagName("Abstract").item(0).getFirstChild() != null)
					lM.setAbstractText(elem.getElementsByTagName("Abstract").item(0).getFirstChild().getNodeValue());
				if (elem.getElementsByTagName("SRS").item(0).getFirstChild() != null)
					lM.setSrs(elem.getElementsByTagName("SRS").item(0).getFirstChild().getNodeValue());
				if (elem.getElementsByTagName("Keywords").item(0).getFirstChild() != null)
					lM.setKeywords(elem.getElementsByTagName("Keywords").item(0).getFirstChild().getNodeValue());
				if (elem.getElementsByTagName("LatLongBoundingBox").item(0).getFirstChild() != null)
					lM.setArea(((Element) elem.getElementsByTagName("LatLongBoundingBox").item(0)).getAttribute("minx").concat(" | ")
							.concat(((Element) elem.getElementsByTagName("LatLongBoundingBox").item(0)).getAttribute("miny")).concat(" | ")
							.concat(((Element) elem.getElementsByTagName("LatLongBoundingBox").item(0)).getAttribute("maxx")).concat(" | ")
							.concat(((Element) elem.getElementsByTagName("LatLongBoundingBox").item(0)).getAttribute("maxy")));
				if (elem.getElementsByTagName("Name").item(0).getFirstChild() != null)
					lM.setName(elem.getElementsByTagName("Name").item(0).getFirstChild().getNodeValue());

				featureTypesToReturn.add(lM);
			}
		}
		return featureTypesToReturn;
	}

	public List<LayerMessenger> doCapabilities(WfsRequestMessenger reqM, String tenant, boolean doPublish) throws Exception {
		return getCapabilities(reqM, tenant, doPublish);
	}

	public Map<String, InputStream> doWfsCall(WfsRequestMessenger reqM, String featureType) throws Exception {
		Map<String, InputStream> map = new HashMap<String, InputStream>();

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("service", "wfs");
		parameters.put("version", "1.0.0");
		parameters.put("request", "GetFeature");
		parameters.put("typeName", featureType);
		parameters.put("outputFormat", "SHAPE-ZIP");

		try {
			InputStream inputStream = (InputStream) doTheRequest(reqM.getUrl(), parameters, true);
			if (inputStream == null)
				return null;

			File ff = Files.createTempDir();
			File f = new File(ff.getAbsolutePath() + "/" + featureType + ".zip");

			FileOutputStream fos = new FileOutputStream(f);
			int length;
			byte[] bytes = new byte[1024];
			while ((length = inputStream.read(bytes)) >= 0) {
				fos.write(bytes, 0, length);
			}
			fos.close();

			ZipFile zipFile = new ZipFile(f);

			Enumeration<?> enu = zipFile.entries();
			while (enu.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) enu.nextElement();

				String name = zipEntry.getName();
				long size = zipEntry.getSize();
				long compressedSize = zipEntry.getCompressedSize();
				logger.info(String.format("name: %-20s | size: %6d | compressed size: %6d", name, size, compressedSize));
				InputStream is = zipFile.getInputStream(zipEntry);
				map.put(name, is);
			}
		} catch (Exception e) {
			return null;
		}
		return map;
	}

	public Map<String, InputStream> getShapefilesFromZip(String fileName, InputStream inputStream) throws Exception {
		Map<String, InputStream> map = new HashMap<String, InputStream>();

		try {

			if (inputStream == null)
				return null;

			File ff = Files.createTempDir();
			File f = new File(ff.getAbsolutePath() + "/" + fileName + ".zip");

			FileOutputStream fos = new FileOutputStream(f);
			int length;
			byte[] bytes = new byte[1024];
			while ((length = inputStream.read(bytes)) >= 0) {
				fos.write(bytes, 0, length);
			}
			fos.close();

			ZipFile zipFile = new ZipFile(f);

			Enumeration<?> enu = zipFile.entries();
			while (enu.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) enu.nextElement();

				String name = zipEntry.getName();
				long size = zipEntry.getSize();
				long compressedSize = zipEntry.getCompressedSize();
				logger.info(String.format("name: %-20s | size: %6d | compressed size: %6d", name, size, compressedSize));
				InputStream is = zipFile.getInputStream(zipEntry);
				map.put(name, is);
			}
		} catch (Exception e) {
			throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "No valid shape files in zip. Corrupted files maybe or wrong extensions");
		}
		return map;
	}

	@Transactional(rollbackFor = { Exception.class })
	public void updateLayerStyle(String name, String originalName, String style) throws Exception {

		String origName = (originalName==null) ? name : originalName;
		
		if (configurationManager.getLayerStyle(origName) != null) {

			Set<GosDefinition> gosDefinitions = geospatialBackendClustered.getDataMonitor().getAllGosEndpoints();
			
			// remove all references to style
			List<LayerStyleMessenger> refs = configurationManager.getLayersReferencingStyle(origName);
			List<LayerStyleMessenger> uniqueStyles = new ArrayList<LayerStyleMessenger>(); // type LayerConfig is used here solely as a container for scale values
			for (LayerStyleMessenger cfg : refs) {
				boolean addUnique = true;
				for (LayerStyleMessenger us : uniqueStyles) {
					int mins = cfg.getMinScale() != null ? cfg.getMinScale() : 0;
					int umins = us.getMinScale() != null ? us.getMinScale() : 0;
					int maxs = cfg.getMaxScale() != null ? cfg.getMaxScale() : 0;
					int umaxs = us.getMaxScale() != null ? us.getMaxScale() : 0;
					if (mins != umins || maxs != umaxs) {
						addUnique = false;
						break;
					}
				}
				if (addUnique)
					uniqueStyles.add(cfg);
				gosDefinitions.parallelStream().forEach(gosDefinition -> {
					try {
						geoserverManagement.removeLayerStyle(gosDefinition.getGosEndpoint(), cfg.getLayerName(), origName, cfg.getMinScale(), cfg.getMaxScale());
					} catch (IOException e) {
						logger.warn("Could not remove style "+origName + " from geoserver "+gosDefinition.getGeoserverEndpoint());
					}
				});
			}

			// remove all references to style as a default style
			List<LayerStyleMessenger> defaultRefs = configurationManager.getLayersReferencingDefaultStyle(origName);
			for (LayerStyleMessenger cfg : defaultRefs) {
				boolean addUnique = true;
				for (LayerStyleMessenger us : uniqueStyles) {
					int mins = cfg.getMinScale() != null ? cfg.getMinScale() : 0;
					int umins = us.getMinScale() != null ? us.getMinScale() : 0;
					int maxs = cfg.getMaxScale() != null ? cfg.getMaxScale() : 0;
					int umaxs = us.getMaxScale() != null ? us.getMaxScale() : 0;
					if (mins != umins || maxs != umaxs) {
						addUnique = false;
						break;
					}
				}
				if (addUnique)
					uniqueStyles.add(cfg);
				final String layerStyle = configurationManager.getLayerStyle(SystemPresentationConfig.DEFAULT_STYLE);
				gosDefinitions.parallelStream().forEach(gosDefinition -> {
					try {
						geoserverManagement.setDefaultLayerStyle(gosDefinition.getGosEndpoint(), cfg.getLayerName(), SystemPresentationConfig.DEFAULT_STYLE, layerStyle, null, null);
					} catch (IOException e) {
						logger.warn("Could not set default style "+ SystemPresentationConfig.DEFAULT_STYLE + " on geoserver "+gosDefinition.getGeoserverEndpoint());
			}
				});
			}

			// remove actual style instances
			for (LayerStyleMessenger us : uniqueStyles){
				gosDefinitions.parallelStream().forEach(gosDefinition -> {
					try {
						geoserverManagement.removeStyle(gosDefinition.getGosEndpoint(), origName, us.getMinScale(), us.getMaxScale());
					} catch (IOException e) {
						logger.warn("Could not remove style "+origName + " from geoserver "+gosDefinition.getGeoserverEndpoint());
					}
				});
			}

			if (!name.equals(origName)) // old style is kept
				configurationManager.addLayerStyle(name, style);

			// add updated style instances (actual style instances are published automatically if needed)
			for (LayerStyleMessenger ref : defaultRefs) {
				gosDefinitions.parallelStream().forEach(gosDefinition -> {
					try {
						geoserverManagement.setDefaultLayerStyle(gosDefinition.getGosEndpoint(), ref.getLayerName(), name, style, ref.getMinScale(), ref.getMaxScale());
					} catch (IOException e) {
						logger.warn("Could not set default style "+name + " on geoserver "+gosDefinition.getGeoserverEndpoint());
					}
				});
				if (!name.equals(origName))
					configurationManager.addDefaultTermStyle(ref.getTermId(), name);
			}
			for (LayerStyleMessenger ref : refs) {
				gosDefinitions.parallelStream().forEach(gosDefinition -> {
					try {
						geoserverManagement.addLayerStyle(gosDefinition.getGosEndpoint(), ref.getLayerName(), name, style, ref.getMinScale(), ref.getMaxScale());
					} catch (IOException e) {
						logger.warn("Could not set style "+name + " on geoserver "+gosDefinition.getGeoserverEndpoint());
					}
				});
				if (!name.equals(origName))
					configurationManager.addTermStyle(ref.getTheme(), ref.getTermId(), name);
			}

			if (name.equals(origName))
				configurationManager.updateLayerStyle(name, style);
		} else
			configurationManager.addLayerStyle(name, style);
	}

	@Transactional(rollbackFor = { Exception.class })
	public void removeLayerStyles(
//			GosDefinition gosDefinition, 
			List<String> names) throws Exception {

		geospatialBackendClustered.getDataMonitor().getAllGosEndpoints().parallelStream().forEach(gosDefinition -> {

		List<String> removed = new ArrayList<String>();
		for (String name : names) {
			try {
					geoserverManagement.removeStyle(gosDefinition.getGosEndpoint(), name);
				removed.add(name);
				} catch (IOException e) {
					logger.warn("Could not recover from failed removal of style: " + name);
				for (String r : removed) {
					try {
							geoserverManagement.addStyle(gosDefinition.getGosEndpoint(), r, configurationManager.getLayerStyle(r));
					} catch (Exception ee) {
						logger.warn("Could not recover from failed removal of style: " + r, ee);
					}
				}
			}
		}
			
		});
		
		configurationManager.removeLayerStyles(names);
	}

	@Transactional(rollbackFor = { Exception.class })
	public void removeThemes(List<String> themes) throws Exception {
		
		geospatialBackendClustered.getDataMonitor().getAllGosEndpoints().parallelStream().forEach(gosDefinition -> {
		for (String t : themes) {
				try{
			for (LayerConfig cfg : configurationManager.getLayerConfig()) {
				String ts = configurationManager.getTermStyle(t, cfg.getTermId());
				if (ts != null) {
					if (!ts.equals(configurationManager.getDefaultTermStyle(cfg.getTermId()))) {
						try {
									geoserverManagement.removeLayerStyle(gosDefinition.getGosEndpoint(), cfg.getName(), ts, cfg.getMinScale(), cfg.getMaxScale());
						}
								catch(IOException e){
									logger.warn("Could not remove style of theme "+t+" from geoserver "+gosDefinition.getGeoserverEndpoint());
								}
					}
				}
			}
		}
				catch(Exception ex){
					logger.warn("Could not remove style of theme "+t+" from geoserver "+gosDefinition.getGeoserverEndpoint());
				}
			}

			
		});
		

		configurationManager.removeThemes(themes);
	}

	@Transactional(rollbackFor = { Exception.class })
	public void configureLayer(Layer layer, String theme, String style, Integer minScale, Integer maxScale) throws Exception {

		LayerConfig cfg = configurationManager.getLayerConfig(layer.getId());
		if (cfg == null)
			throw new Exception("Layer configuration not found for term " + layer.getId());
		String ts = theme != null && !theme.equals(SystemPresentationConfig.DEFAULT_THEME)
				? configurationManager.getTermStyle(theme, layer.getId().toString())
				: configurationManager.getDefaultTermStyle(layer.getId().toString());
		/*
		 * optimization not enforced because actual styles need to be updated on the remote side so that their proper update can be ensured if(ts != null && ts.equals(style)) {
		 * if((minScale == null && cfg.getMinScale() == null) || (minScale != null && cfg.getMinScale() != null && minScale.equals(cfg.getMinScale()))) { if((maxScale == null &&
		 * cfg.getMaxScale() == null) || (maxScale != null && cfg.getMaxScale() != null && maxScale.equals(cfg.getMaxScale()))) return; //no change } }
		 */
		String sld = configurationManager.getLayerStyle(style);
		if (sld == null)
			throw new Exception("Style " + style + " not found");

		boolean found = false;
		if (ts != null) {
			List<LayerStyleMessenger> refs = configurationManager.getLayersReferencingStyle(ts);
			refs.addAll(configurationManager.getLayersReferencingDefaultStyle(ts));

			for (LayerStyleMessenger ref : refs) {
				if (!ref.getTermId().equals(layer.getId().toString())
						|| ((ref.getMinScale() == null && minScale != null)
								|| (ref.getMinScale() != null && minScale == null)
								|| ref.getMinScale() != null && minScale != null && !ref.getMinScale().equals(minScale))
						|| ((ref.getMaxScale() == null && maxScale != null)
								|| (ref.getMaxScale() == null && maxScale != null) || ref.getMaxScale() != null
										&& maxScale != null && !ref.getMaxScale().equals(maxScale))) {
					found = true;
					break;
				}
			}
		}

		final boolean foundFinally = found; //just to prevent compilers complain about not being final
		Set<GosDefinition> gosDefs = geospatialBackendClustered.getDataMonitor().getAvailableGosFor(layer.getId().toString());
		gosDefs.addAll(geospatialBackendClustered.getDataMonitor().getNotAvailableGosFor(layer.getId().toString()));
		gosDefs.parallelStream().forEach(gosDefinition -> {
			try{
		if (theme != null && !theme.equals(SystemPresentationConfig.DEFAULT_THEME)) {
					if (ts != null && !ts.equals(SystemPresentationConfig.DEFAULT_STYLE) && !foundFinally) // no other layers are referencing this particular style instance, so sync with remote server
						geoserverManagement.removeLayerStyle(gosDefinition.getGosEndpoint(), cfg.getName(), ts, cfg.getMinScale(), cfg.getMaxScale());

					geoserverManagement.addLayerStyle(gosDefinition.getGosEndpoint(), cfg.getName(), style, sld, minScale, maxScale);
		} else {
					if (ts != null && !ts.equals(SystemPresentationConfig.DEFAULT_STYLE) && !foundFinally) // no other layers are referencing this particular style instance, so sync with remote server
			{
						geoserverManagement.setDefaultLayerStyle(gosDefinition.getGosEndpoint(), cfg.getName(), SystemPresentationConfig.DEFAULT_STYLE,
								configurationManager.getLayerStyle(SystemPresentationConfig.DEFAULT_STYLE), minScale, maxScale); // remove remaining reference
						geoserverManagement.removeStyle(gosDefinition.getGosEndpoint(), ts, minScale, maxScale);
			}
					geoserverManagement.setDefaultLayerStyle(gosDefinition.getGosEndpoint(), cfg.getName(), style, sld, minScale, maxScale);
				}
			}
			catch (Exception e) {
				logger.warn("Could not update the style of layer "+layer.getId().toString()+" on geoserver "+gosDefinition.getGeoserverEndpoint());
		}
		});
		
		cfg.setMinScale(minScale);
		cfg.setMaxScale(maxScale);
		configurationManager.updateLayerConfig(cfg);
		configurationManager.updateTermStyle(theme, layer.getId().toString(), style);
	}

	@Transactional(rollbackFor = { Exception.class })
	public void removeLayer(GosDefinition gosDefinition, Layer layer) throws Exception {
		logger.info("Removing layer with id: " + layer.getId() + " and name: " + layer.getName() + " ...");

		try {
			//notify zookeeper about the deletion (set layer unavailable)
			dataCreatorGeoanalytics.updateLayerState(layer.getId().toString(), ZNodeStatus.PENDING, gosDefinition.getGosEndpoint());
			//delete from geoserver
			logger.info("Removing layer with id: " + layer.getId() + " and name: " + layer.getName() + " from geoserver...");
			geoserverManagement.deleteGeoserverLayer(gosDefinition.getGosEndpoint(), layer.getId().toString());
			logger.info("Layer with id: " + layer.getId() + " and name: " + layer.getName() + " has been removed from geoserver...");

		if (layer.getIsTemplate() > 0) {
			logger.info("Removing geocodes of template layer with id: " + layer.getId() + " and name: " + layer.getName() + " ...");
			geocodeManager.deleteGeocodesOfTemplateLayer(layer);
			logger.info("Geocodes of template layer with id: " + layer.getId() + " and name: " + layer.getName() + " have been removed...");
		}

		logger.info("Removing tenants of layer with id: " + layer.getId() + " and name: " + layer.getName() + " ...");
		layerManager.deleteLayerTenant(layer);
		logger.info("Tenants of layer with id: " + layer.getId() + " and name: " + layer.getName() + " have been removed...");

		logger.info("Removing tags of layer with id: " + layer.getId() + " and name: " + layer.getName() + " ...");
		layerManager.deleteLayerTags(layer);
		logger.info("Tags of layer with id: " + layer.getId() + " and name: " + layer.getName() + " have been removed...");

		try {
			configurationManager.removeLayerConfig(layer.getId());
			configurationManager.removeMappingConfigForLayer(layer.getId().toString());
			configurationManager.removeTermStyles(layer.getId().toString());
		} catch (Exception e) {
			logger.error("Error while removing configuration of layer [ " + layer.getId() + " ]", e);
		}

		logger.info("Removing database view and layer with id: " + layer.getId() + " and name: " + layer.getName() + " ...");

			//drop view
			builder.forIdentity(layer.getId().toString()).removeViewStatement().execute(gosDefinition.getGosEndpoint());
			//delete shapes of layer (drop layer from table)
			logger.info("Removing shapes of layer with id: "+layer.getId()+" and name: "+layer.getName()+" ...");
			geospatialBackendClustered.getShapeManagement().deleteShapesOfLayer(gosDefinition.getGosEndpoint(), layer.getId().toString());
			logger.info("Shapes of layer with id: "+layer.getId()+" and name: "+layer.getName()+" have been removed...");
			//notify zookeeper again about the deletion by removing entirely the corresponding entry
			dataCreatorGeoanalytics.deleteLayer(layer.getId().toString(), gosDefinition.getGosEndpoint());
		
		}
		catch(Exception ex){
			logger.error("Could not delete layer "+layer.getId().toString()+" from GOS "+gosDefinition.getGosEndpoint());
			//TODO: do something in this case, or throw to parent
		}
		logger.info("Finally, removing the layer (id: "+layer.getId().toString()+") entry from Layer Table of geoanalytics db");
		layerManager.deleteLayer(layer);
		logger.info("Finally, removing the layer entry from Layer Table of geoanalytics db");

		logger.info("Layer with id: " + layer.getId() + " and name: " + layer.getName() + " has been removed");
	}

	@Transactional(rollbackFor = { GeoServerBridgeException.class, Exception.class })
	public void newLayerFromImportedData(GosDefinition gosDefinition, String newLayerId, LayerConfig layerConfig, String crs, String style) throws Exception {
		Map<String, String> layerStyles = configurationManager.getLayerStyles();
		Bounds b = new Bounds(layerConfig.getBoundingBox().getMinY(), layerConfig.getBoundingBox().getMinY(), layerConfig.getBoundingBox().getMaxX(), layerConfig.getBoundingBox().getMaxY(), crs);

		FeatureType featureType = new FeatureType();
		featureType.setDatastore(gosDefinition.getDatastoreName());
		featureType.setWorkspace(gosDefinition.getGeoserverWorkspace());
		featureType.setEnabled(true);
		featureType.setName(layerConfig.getTermId());
		featureType.setTitle(layerConfig.getName());
		featureType.setSrs("EPSG:4326");
		featureType.setNativeCRS("EPSG:4326");
		featureType.setNativeBoundingBox(b);
		featureType.setLatLonBoundingBox(b);

		GeoserverLayer geoserverLayer = new GeoserverLayer();
		geoserverLayer.setDatastore(gosDefinition.getDatastoreName());
		geoserverLayer.setWorkspace(gosDefinition.getGeoserverWorkspace());
		geoserverLayer.setEnabled(true);
		geoserverLayer.setDefaultStyle(style);
		geoserverLayer.setId(layerConfig.getTermId());
		geoserverLayer.setTitle(layerConfig.getName());
		geoserverLayer.setType("VECTOR");
		
		Integer minScale = layerConfig.getMinScale();
		Integer maxScale = layerConfig.getMaxScale();
		
		geoserverManagement.addGeoserverLayer(gosDefinition.getGosEndpoint(), geoserverLayer, featureType, layerStyles, minScale, maxScale);
		
	}
	
//	@Transactional(rollbackFor = { GeoServerBridgeException.class, Exception.class })
//	public void newLayerFromImportedData(String newLayerId, LayerConfig layerConfig, String crs, String style) throws Exception {
//
//		GeoServerBridgeConfig config = configuration.getGeoServerBridgeConfig();
//
//		Bounds b = new Bounds(layerConfig.getBoundingBox().getMinY(), layerConfig.getBoundingBox().getMinY(), layerConfig.getBoundingBox().getMaxX(), layerConfig.getBoundingBox().getMaxY(), crs);
//
//		FeatureType featureType = new FeatureType();
//		featureType.setDatastore(config.getDataStoreConfig().getDataStoreName());
//		featureType.setWorkspace(config.getGeoServerBridgeWorkspace());
//		featureType.setEnabled(true);
//		featureType.setName(layerConfig.getTermId());
//		featureType.setTitle(layerConfig.getName());
//		featureType.setSrs("EPSG:4326");
//		featureType.setNativeCRS("EPSG:4326");
//		featureType.setNativeBoundingBox(b);
//		featureType.setLatLonBoundingBox(b);
//
//		GeoserverLayer geoserverLayer = new GeoserverLayer();
//		geoserverLayer.setWorkspace(config.getGeoServerBridgeWorkspace());
//		geoserverLayer.setDatastore(config.getDataStoreConfig().getDataStoreName());
//		geoserverLayer.setEnabled(true);
//		geoserverLayer.setDefaultStyle(style);
//		geoserverLayer.setId(layerConfig.getTermId());
//		geoserverLayer.setTitle(layerConfig.getName());
//		geoserverLayer.setType("VECTOR");
//
//		Integer minScale = layerConfig.getMinScale();
//		Integer maxScale = layerConfig.getMaxScale();
//		Map<String, String> layerStyles = configurationManager.getLayerStyles();
//
//		geoServerBridge.addGeoserverLayer(geoserverLayer, featureType, layerStyles, minScale, maxScale);
//		
//		geoserverManagement.addGeoserverLayer(gosEndpoint, geoserverLayer, ft, slds)
//		
//		throw new Exception("Please fill up this class, using the new GOS logic");
//	}
	
	
	@Transactional(rollbackFor = { Exception.class })
	public void editLayer(UUID layerId, LayerMessengerForAdminPortlet lmfa) throws Exception {

		logger.info("Updating layer with id: " + layerId);
		
		Layer layer = layerManager.findLayerById(layerId);
		
		layer.setLastUpdate(new Date());
		
		if(lmfa.getDescription() != null) {
			layer.setDescription(lmfa.getDescription());
		} else {
			throw new Exception("Ivalid value for the field Description");
		}
		
		if(lmfa.getName() != null){
			layer.setName(lmfa.getName());
		} else {
			throw new Exception("Ivalid value for the field Name");
		}
		
		layer.setStyle(lmfa.getStyle());
		layer.setReplicationFactor(lmfa.getReplicationFactor());
		LayerConfig layerConfig = configurationManager.getLayerConfig(layerId);
		layerConfig.setStyle(lmfa.getStyle());
		
		configurationManager.updateLayerConfig(layerConfig);
		
		Set<GosDefinition> gosDefinitions = dataMonitor.getAvailableGosFor(layerId.toString());
		
		for(GosDefinition gd : gosDefinitions) {
			geoserverManagement.setDefaultLayerStyle(gd.getGosEndpoint(), layer.getId().toString(), lmfa.getStyle(), null, null, null);
		}
		
		lmfa.getTags().forEach(tag -> tag.trim());
		
		Collection<LayerTag> ltList = null;
		if(lmfa.getTags().isEmpty()) {
			ltList = new ArrayList<LayerTag>();
		}else {
			ltList = layerManager.findLayerTagsByLayerAndTagName(layer, lmfa.getTags());
		}
		
		Collection<String> updatedTagNames = new HashSet<String>();
		ltList.forEach(lt-> {
			lt.setLastUpdate(new Date());
			layerManager.updateLayerTag(lt);
			
			updatedTagNames.add(lt.getTag().getName());
		});
		
		Collection<LayerTag> ltListDeletion = layerManager.findLayerTagsByLayerAndTagNameNotInTagNamesList(layer, lmfa.getTags());
		
		ltListDeletion.forEach(lt-> {
			layerTagDao.delete(lt);
		});
		
		Set<String> tagNames = new HashSet<String>(lmfa.getTags());
		tagNames.removeAll(updatedTagNames);
		
		Collection<String> existingTagsNotRelatedToLayer = new HashSet<String>();
		Collection<String> tagsToBeCreated = new HashSet<String>();
		tagNames.forEach(t -> {
			if(layerManager.checkIfTagtExists(t)) {
				existingTagsNotRelatedToLayer.add(t);
			} else {
				tagsToBeCreated.add(t);
			}
		});
		
		layerManager.relateExistingTagsWithLayer(existingTagsNotRelatedToLayer, layer);
		
		Set<Tag> tags = (Set<Tag>) layerManager.createNewTags(tagsToBeCreated, securityContextAccessor.getPrincipal());
		
		layerManager.createTagsOfLayer(layer, tags);
		
		layerManager.updateLayer(layer);
	}
	
}