package gr.cite.geoanalytics.manager;

import java.awt.Rectangle;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import gr.cite.gaap.datatransferobjects.*;
import gr.cite.gaap.datatransferobjects.layeroperations.UserWorkspaceLayerPersist;
import gr.cite.geoanalytics.dataaccess.entities.layer.*;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.*;
import gr.cite.geoanalytics.geospatialbackend.GeospatialServices;
import gr.cite.geoanalytics.mvc.UserWorkspaceUtil;
import it.geosolutions.geoserver.rest.decoder.RESTLayer;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.io.netcdf.NetCDFReader;
import org.geotools.coverage.processing.Operations;
import org.geotools.factory.Hints;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.referencing.CRS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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


import gr.cite.clustermanager.actuators.layers.DataMonitor;
import gr.cite.clustermanager.model.layers.GosDefinition;
import gr.cite.clustermanager.trafficshaping.TrafficShaper;
import gr.cite.gaap.datatransferobjects.request.GeoNetworkMetadataDTO;
import gr.cite.gaap.geospatialbackend.exceptions.NoAvailableGos;
import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.GeocodeManager;
import gr.cite.gaap.servicelayer.GeospatialBackendClustered;
import gr.cite.gaap.servicelayer.Toolbox;
import gr.cite.geoanalytics.common.ShapeAttributeDataType;
import gr.cite.geoanalytics.common.ViewBuilder;
import gr.cite.geoanalytics.dataaccess.entities.coverage.Coverage;
import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
import gr.cite.geoanalytics.dataaccess.entities.layer.dao.LayerTagDao;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerBounds;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerConfig;
import gr.cite.geoanalytics.dataaccess.entities.tag.Tag;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.GeoNetworkBridge;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.exception.GeoNetworkBridgeException;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.exception.GeoServerBridgeException;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.geoservermanager.GSManagerGeoNetworkBridge;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.elements.MetaDataForm;
import gr.cite.geoanalytics.security.SecurityContextAccessor;
import gr.cite.geoanalytics.util.http.CustomException;
import gr.cite.gos.client.GeoserverManagement;

@Service
public class ImportManager {
	public static Logger logger = LoggerFactory.getLogger(ImportManager.class);
	private static String fs = File.separator;
	private String tenantNameOfBatchImport = null;
	private String batchImportLayersFolder = null;
	private String metadataFilePath;
	private String ftpUrlDownloadHeader;
	private String layerFileNameHeader;
	private String layerFileKeyword1Header;
	private String layerFileKeyword2Header;
	private String geoanalyticsEndpoint;
	private static final String fileNameSuffix = "_nl";

	@Autowired private ViewBuilder builder;
	@Autowired private GeocodeManager geocodeManager;
	@Autowired private ConfigurationManager configurationManager;
	@Autowired private SecurityContextAccessor securityContextAccessor;
	@Autowired private LayerManager layerManager;
	@Autowired private LayerTagDao layerTagDao;
	@Autowired private TenantManager tenantManager;
	@Autowired private GeospatialBackendClustered geospatialBackendClustered;
	@Autowired private DataMonitor dataMonitor;
	@Autowired private GeoserverManagement geoserverManagement;
	@Autowired private TrafficShaper trafficShaper;
	@Autowired private UserWorkspaceLayersmanager userWorkspaceLayersmanager;

	private final static Object geoNetworkLock = new Object();
	private final static Object databaseLock = new Object();

	private static final String DEFAULT_RASTER_STYLE = /*"raster_transparent";*/"raster";

	public ImportManager() {}

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
	public void importTsvLayer(LayerImport layerImport, GeoNetworkMetadataDTO metadata, String tsvData, TsvImportProperties properties, Tenant tenant) throws NoAvailableGos, Exception{
		String geocodeSystemName = layerImport.getGeocodeSystem();
		List<String> tags = properties.getTags();

		GosDefinition gosDefinition = trafficShaper.getGosForNewLayer();

		Layer layer = null;

		try {
			GeocodeSystem geocodeSystem = this.geocodeManager.findGeocodeSystemByName(geocodeSystemName, false);
			Layer templateLayer = this.layerManager.findTemplateLayerByGeocodeSystem(geocodeSystem);

			LayerConfig templateLayerConfig = configurationManager.getLayerConfig(templateLayer.getId());
			Bounds bounds = new Bounds(templateLayerConfig.getBoundingBox());

			layer = this.createLayerInDatabase(layerImport, geocodeSystem, properties.getStyle());
			Collection<Shape> shapesOfLayer = this.tsvParsing(templateLayer, layer, tsvData);

			this.createTagsOfLayer(layer, tags);
			this.createShapesOfLayer(gosDefinition, shapesOfLayer);
			this.createDataBaseView(gosDefinition, new ArrayList<>(shapesOfLayer), layer.getId().toString());
			this.publishLayerToGeoServer(gosDefinition, layer, bounds, DataSource.PostGIS, properties.getStyle());
			this.publishLayerToGeoNetworkAsync(30000, layer, metadata, bounds, tenant);
			this.updateImportStatusToSuccess(layerImport, layer);

			logger.info("Layer \"" + layer.getName() + " ( " + layer.getId() + " ) has been imported successfully!");
		} catch (Exception e) {
			importFailureHandling(gosDefinition, layer, layerImport, "Failed to complete TSV import", e);
		}
	}

	@Async("importPool")
	public void importTsvLayer(LayerImport layerImport, GeoNetworkMetadataDTO metadata, InputStream tsvDataStream, TsvImportProperties properties, Tenant tenant, UserWorkspaceUtil userWorkspaceUtil) throws NoAvailableGos, Exception{
		String geocodeSystemName = layerImport.getGeocodeSystem();
		List<String> tags = properties.getTags();

		GosDefinition gosDefinition = trafficShaper.getGosForNewLayer();

		Layer layer = null;

		try {
			GeocodeSystem geocodeSystem = this.geocodeManager.findGeocodeSystemByName(geocodeSystemName, false);
			Layer templateLayer = this.layerManager.findTemplateLayerByGeocodeSystem(geocodeSystem);

			LayerConfig templateLayerConfig = configurationManager.getLayerConfig(templateLayer.getId());
			Bounds bounds = new Bounds(templateLayerConfig.getBoundingBox());

			layer = this.createLayerInDatabase(layerImport, geocodeSystem, properties.getStyle());
			UserWorkspaceLayer uWLDB  = this.convertAndStoreuserWorkspaceLayer(userWorkspaceUtil.getUserWorkspaceLayerPersist(), layer,
					this.userWorkspaceLayersmanager.retrieveUerWorkspaceLayerFolderId(userWorkspaceUtil.getScope(), userWorkspaceUtil.getToken()));

			String tsvData = this.extractTSVDataFromInputStream(tsvDataStream);
			Collection<Shape> shapesOfLayer = this.tsvParsing(templateLayer, layer, tsvData);

			this.createTagsOfLayer(layer, tags);
			this.createShapesOfLayer(gosDefinition, shapesOfLayer);
			this.createDataBaseView(gosDefinition, new ArrayList<>(shapesOfLayer), layer.getId().toString());
			this.publishLayerToGeoServer(gosDefinition, layer, bounds, DataSource.PostGIS, properties.getStyle());
			this.publishLayerToGeoNetworkAsync(30000, layer, metadata, bounds, tenant);
			this.updateImportStatusToSuccess(layerImport, layer);

			logger.info("Layer \"" + layer.getName() + " ( " + layer.getId() + " ) has been imported successfully!");
		} catch (Exception e) {
			importFailureHandling(gosDefinition, layer, layerImport, "Failed to complete TSV import", e);
		}
	}

	private String extractTSVDataFromInputStream(InputStream tsvInputStream) throws IOException {
		final File file = File.createTempFile("temporaryTSVData", ".tsv");
		file.deleteOnExit();

		String tsvData = null;
		try (FileOutputStream fos = new FileOutputStream(file)) {
			IOUtils.copy(tsvInputStream, fos);
		} catch(IOException e){
			e.printStackTrace();
		} finally{
			tsvInputStream.close();
		}

		FileInputStream fis = new FileInputStream(file);
		byte[] data = new byte[(int) file.length()];
		fis.read(data);
		fis.close();

		tsvData = new String(data, "UTF-8");

		return tsvData;
	}

	@Async("importPool")
	public void importWfsLayer(LayerImport layerImport, WfsRequestLayer layerInfo, String pathName, WfsRequestMessenger reqM, String featureType, Tenant tenant) throws Exception {
		Principal principal = layerImport.getCreator();
		String tenantName = layerImport.getCreator().getTenant().getName();

		GosDefinition gosDefinition = trafficShaper.getGosForNewLayer();

		logger.info("Importing of layer "+ layerImport.getName() + " will be submitted to GOS: "+gosDefinition.getGosEndpoint());

		Layer layer = null;
		try {

			WfsShapeInfo wfsShapeInfo = null;
			Map<String, String> attrs = null;
			GeoNetworkMetadataDTO metadata = null;

			synchronized (this) {
				attrs = this.analyzeAttributes(pathName, "UTF-8");
				List<AttributeInfo> attrInfo = new ArrayList<AttributeInfo>();
				for (Map.Entry<String, String> attrEntry : attrs.entrySet()) {
					AttributeInfo aI = new AttributeInfo(attrEntry.getKey(), attrEntry.getValue(), null, null, true, true);
					attrInfo.add(aI);
				}
				// Get Metadata from wfs call

				metadata = this.getCapabilitiesForPublishingToGeonetwork(reqM, tenantName, featureType);
				metadata.setTitle(layerInfo.getLayerName());

				layer = this.createLayerInDatabase(layerImport, null, layerInfo.getStyle());
				Map<String, Map<String, AttributeInfo>> attrInfoM = createAttributeInfoMap(attrInfo);
				wfsShapeInfo = geospatialBackendClustered.getShapesFromShapefile(pathName, layer.getId().toString(), 4326, "UTF-8", true, attrInfoM, principal, true);
			}

			this.createTagsOfLayer(layer, metadata.getKeywords());
			this.createShapesOfLayer(gosDefinition, wfsShapeInfo.getListShape());
			this.createDataBaseView(gosDefinition, wfsShapeInfo.getListShape(), layer.getId().toString());
			this.publishLayerToGeoServer(gosDefinition, layer, wfsShapeInfo.getBounds(), DataSource.PostGIS, layerInfo.getStyle());

			if(layerInfo.isPublishOnGeoNetwork()){
				this.publishLayerToGeoNetworkAsync(30000, layer, metadata, wfsShapeInfo.getBounds(), tenant);
			}

			this.updateImportStatusToSuccess(layerImport, layer);

			logger.info("Layer \"" + layer.getName() + " ( " + layer.getId() + " ) has been imported successfully!");
		} catch (Exception e) {
			importFailureHandling(gosDefinition, layer, layerImport, "Failed to complete WFS import", e);
		}
	}

	@Async("importPool")
	public void importShapeFileLayer(LayerImport layerImport, ShapefileImportProperties properties, GeoNetworkMetadataDTO metadata, String pathName, Tenant tenant) throws Exception {
		Principal principal = layerImport.getCreator();
		List<String> tags = properties.getTags();

		Layer layer = null;

		//decide in which gos it will be added
		GosDefinition gosDefinition = trafficShaper.getGosForNewLayer();

		Map<String, String> attrs = this.analyzeAttributes(pathName, properties.getDbfEncoding());
		int srid = 4326;
		if(properties.getSrid() != null && !properties.getSrid().equals("")){
			srid = Integer.parseInt(properties.getSrid());
		}

		try {
			WfsShapeInfo wfsShapeInfo = null;

			List<AttributeInfo> attrInfo = new ArrayList<AttributeInfo>();
			for (Map.Entry<String, String> attrEntry : attrs.entrySet()) {
				AttributeInfo aI = new AttributeInfo(attrEntry.getKey(), attrEntry.getValue(), null, null, true, true);
				attrInfo.add(aI);
			}

			layer = this.createLayerInDatabase(layerImport, null, properties.getStyle());
			Map<String, Map<String, AttributeInfo>> attrInfoM = createAttributeInfoMap(attrInfo);
			wfsShapeInfo = geospatialBackendClustered.getShapesFromShapefile(pathName, layer.getId().toString(), srid, properties.getDbfEncoding(), true, attrInfoM, principal, true);

			this.createTagsOfLayer(layer, tags);
			this.createShapesOfLayer(gosDefinition, wfsShapeInfo.getListShape());
			this.createDataBaseView(gosDefinition, wfsShapeInfo.getListShape(), layer.getId().toString());
			this.publishLayerToGeoServer(gosDefinition, layer, wfsShapeInfo.getBounds(), DataSource.PostGIS, properties.getStyle());
			this.publishLayerToGeoNetworkAsync(30000, layer, metadata, wfsShapeInfo.getBounds(), tenant);
			this.updateLayerIfTemplate(layer, wfsShapeInfo.getListShape(), properties);
			this.updateImportStatusToSuccess(layerImport, layer);

			logger.info("Layer \"" + layer.getName() + " ( " + layer.getId() + " ) has been imported successfully!");
		} catch (Exception e) {
			importFailureHandling(gosDefinition, layer, layerImport, "Failed to complete ShapeFile import", e);
		}
	}

	@Async("importPool")
	public void importShapeFileLayer(LayerImport layerImport, ShapefileImportProperties properties, GeoNetworkMetadataDTO metadata, String pathName, Tenant tenant, UserWorkspaceUtil userWorkspaceUtil) throws Exception {
		Principal principal = layerImport.getCreator();
		List<String> tags = properties.getTags();

		Layer layer = null;

		//decide in which gos it will be added
		GosDefinition gosDefinition = trafficShaper.getGosForNewLayer();

		Map<String, String> attrs = this.analyzeAttributes(pathName, properties.getDbfEncoding());
		int srid = 4326;
		if(properties.getSrid() != null && !properties.getSrid().equals("")){
			srid = Integer.parseInt(properties.getSrid());
		}

		try {
			WfsShapeInfo wfsShapeInfo = null;

			List<AttributeInfo> attrInfo = new ArrayList<AttributeInfo>();
			for (Map.Entry<String, String> attrEntry : attrs.entrySet()) {
				AttributeInfo aI = new AttributeInfo(attrEntry.getKey(), attrEntry.getValue(), null, null, true, true);
				attrInfo.add(aI);
			}

			layer = this.createLayerInDatabase(layerImport, null, properties.getStyle());
			UserWorkspaceLayer uWLDB  = this.convertAndStoreuserWorkspaceLayer(userWorkspaceUtil.getUserWorkspaceLayerPersist(), layer, this.userWorkspaceLayersmanager.retrieveUerWorkspaceLayerFolderId(userWorkspaceUtil.getScope(), userWorkspaceUtil.getToken()));

			Map<String, Map<String, AttributeInfo>> attrInfoM = createAttributeInfoMap(attrInfo);
			wfsShapeInfo = geospatialBackendClustered.getShapesFromShapefile(pathName, layer.getId().toString(), srid, properties.getDbfEncoding(), true, attrInfoM, principal, true);

			this.createTagsOfLayer(layer, tags);
			this.createShapesOfLayer(gosDefinition, wfsShapeInfo.getListShape());
			this.createDataBaseView(gosDefinition, wfsShapeInfo.getListShape(), layer.getId().toString());
			this.publishLayerToGeoServer(gosDefinition, layer, wfsShapeInfo.getBounds(), DataSource.PostGIS, properties.getStyle());
			this.publishLayerToGeoNetworkAsync(30000, layer, metadata, wfsShapeInfo.getBounds(), tenant);
			this.updateLayerIfTemplate(layer, wfsShapeInfo.getListShape(), properties);
			this.updateImportStatusToSuccess(layerImport, layer);

			logger.info("Layer \"" + layer.getName() + " ( " + layer.getId() + " ) has been imported successfully!");
		} catch (Exception e) {
			importFailureHandling(gosDefinition, layer, layerImport, "Failed to complete ShapeFile import", e);
		}
	}

	private Layer createLayerInDatabase(LayerImport layerImport, GeocodeSystem geocodeSystem, String style) throws Exception {
		logger.info("Creating Layer \"" + layerImport.getName() + "\" in Database");

		Principal creator = layerImport.getCreator();
		String name = layerImport.getName();
		DataSource dataSource = layerImport.getDataSource();
		GosDefinition gosDefinition = trafficShaper.getGosForNewLayer();

		Layer layer = new Layer();
		layer.setCreator(creator);
		layer.setName(name);
		layer.setWorkspace(gosDefinition.getGeoserverWorkspace());
		layer.setStyle(style);
		layer.setDataSource(dataSource);
		layer.setGeocodeSystem(geocodeSystem);
		layer.setDescription(layerImport.getDescription());
		layer.setStyle(style);
		layer.setExtraData("<extraData geographic = \"true\" />");
		layerManager.createLayer(layer);

		Tenant tenant = creator.getTenant();

		LayerTenant layerTenant = new LayerTenant();
		layerTenant.setLayer(layer);
		layerTenant.setTenant(tenant);
		layerManager.createLayerTenant(layerTenant);

		layerImport.setLayer(layer);
		layerManager.updateLayerImport(layerImport);

		logger.info("Layer with name: \"" + name + "\" and id: \"" + layer.getId() + "\" has been created successfully");

		return layer;
	}

	private Layer createNetCDFLayerInDatabase(LayerImport layerImport, GeocodeSystem geocodeSystem, String style, String netCDFLayerName, boolean isFirstLayer) throws Exception {
		logger.info("Creating Layer \"" + layerImport.getName() + "\" in Database");

		Principal creator = layerImport.getCreator();
		String name = layerImport.getName();
		DataSource dataSource = layerImport.getDataSource();
		GosDefinition gosDefinition = trafficShaper.getGosForNewLayer();

		Layer layer = new Layer();
		layer.setCreator(creator);
		layer.setName(netCDFLayerName);
		layer.setWorkspace(gosDefinition.getGeoserverWorkspace());
		layer.setStyle(style);
		layer.setDataSource(dataSource);
		layer.setGeocodeSystem(geocodeSystem);
		layer.setDescription(layerImport.getDescription());
		layer.setStyle(style);
		layer.setExtraData("<extraData geographic = \"true\" />");
		layerManager.createLayer(layer);

		Tenant tenant = creator.getTenant();

		LayerTenant layerTenant = new LayerTenant();
		layerTenant.setLayer(layer);
		layerTenant.setTenant(tenant);
		layerManager.createLayerTenant(layerTenant);

		if(isFirstLayer) {
			layerImport.setLayer(layer);
			layerManager.updateLayerImport(layerImport);
		}

		logger.info("Layer with name: \"" + name + "\" and id: \"" + layer.getId() + "\" has been created successfully");

		return layer;
	}

	private void createShapesOfLayer(GosDefinition gosDefinition, Collection<Shape> shapesOfLayer) throws Exception{
		logger.info("Inserting " + shapesOfLayer.size() + " shapes into database (through GOS)");

		boolean status = geospatialBackendClustered.createShapesOfLayer(gosDefinition, shapesOfLayer);

		if(status)
			logger.info(shapesOfLayer.size() + " shapes have been inserted successfully on gos endpoint: "+gosDefinition.getGosEndpoint());
		else
			logger.info(shapesOfLayer.size() + " shapes have NOT been inserted successfully on gos endpoint: "+gosDefinition.getGosEndpoint());
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

	private void createTagsOfLayerForBatchLayerImport(Layer layer, List<String> keywords) throws Exception {
		if (keywords != null && keywords.size() > 0) {
			Principal principal = securityContextAccessor.getAdministrators().get(0);
			List<Tag> tags = keywords.stream().map(name -> new Tag().withName(name).withCreator(principal)).collect(Collectors.toList());

			logger.info("Inserting " + tags.size() + " tags of layer into database");

			layerManager.createTagsOfLayer(layer, tags);

			logger.info(tags.size() + " tags have been inserted successfully!");
		}
	}

	public LayerImport createTsvLayerImport(TsvImportProperties properties) throws Exception {
		Principal creator = securityContextAccessor.getPrincipal();

		LayerImport layerImport = new LayerImport();
		layerImport.setImportType(ImportType.TSV);
		layerImport.setCreator(creator);
		layerImport.setName(properties.getLayerName());
		layerImport.setGeocodeSystem(properties.getGeocodeSystem());
		layerImport.setSource(properties.getFileName());
		layerImport.setDescription(properties.getDescription());
		layerImport.setDataSource(DataSource.PostGIS);
		layerManager.createLayerImport(layerImport);

		return layerImport;
	}

	public LayerImport createWfsLayerImport(WfsRequestLayer layerInfo, String sourceURL) throws Exception {
		Principal creator = securityContextAccessor.getPrincipal();

		LayerImport layerImport = new LayerImport();
		layerImport.setImportType(ImportType.WFS);
		layerImport.setDataSource(DataSource.PostGIS);
		layerImport.setCreator(creator);
		layerImport.setName(layerInfo.getLayerName());
		layerImport.setSource(sourceURL);
		layerImport.setDescription(layerInfo.getLayerDescription());
		layerManager.createLayerImport(layerImport);

		return layerImport;
	}

	public LayerImport createShapeFileLayerImport(ShapefileImportProperties properties, String filename) throws Exception {
		Principal creator = securityContextAccessor.getPrincipal();

		LayerImport layerImport = new LayerImport();
		layerImport.setImportType(ImportType.SHAPEFILE);
		layerImport.setDataSource(DataSource.PostGIS);
		layerImport.setCreator(creator);
		layerImport.setName(properties.getNewLayerName());
		layerImport.setSource(filename);
		layerImport.setDescription(properties.getDescription());

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

	public void updateImportStatusToFailureForNetCDFLayer(LayerImport layerImport){
		try {
			this.updateImportStatusToFailure(layerImport);
		} catch (Exception e) {
			logger.error("Failed to update status for layer: " + layerImport.getName(), e);
		}
	}

	private void publishLayerToGeoServer(GosDefinition gosDefinition, Layer layer, Bounds bounds, DataSource dataSource, String style) throws Exception{
		try {
			LayerBounds layerBounds = bounds.createLayerBounds();
			LayerConfig layerConfig = createLayerConfig(layer, layerBounds, dataSource, style);

			newLayerFromImportedData(gosDefinition, layer.getId().toString(), layerConfig, bounds.getCrs());
			//TODO: this addLayerConfig should be removed in the feature... should be added on LayerManager.addLayer()
			configurationManager.addLayerConfig(layerConfig);
		} catch (Exception e) {
			throw new Exception("Error occured while publishing Layer to GeoServer", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = { Exception.class })
	private void createDataBaseView(GosDefinition gosDefinition, List<Shape> layerShapes, String identity) throws Exception {
		synchronized (databaseLock) {
			logger.info("Creating Materialized View of \"" + identity + "\"");

			boolean status = builder.forIdentity(identity).forShapes(layerShapes).createViewStatement().execute(gosDefinition.getGosEndpoint());

			if (status) {
				logger.info("Materialized View of \"" + identity + "\" has been created successfully!");
			} else {
				throw new Exception("Could not create Materialized View of \"" + identity + "\"");
			}
		}
	}

	private void publishLayerToGeoNetworkAsync(long zookeeperTimeoutMillis, Layer layer, GeoNetworkMetadataDTO metadata, Bounds bounds, Tenant tenant) throws Exception {
		//note that the data monitor might have not been already notified about the layer, since it was just before inserted on geoservers,
		//so we need to wait till the datamonitor is notified.

		if (metadata != null) {
			String layerName = layer.getName();
			String layerID = layer.getId().toString();

			new Thread() {

				@Override
				public void run() {
					logger.info("Async publishing to geonetwork is spawned... might take some time");
					try {
						publishLayerToGeoNetwork(layer, metadata, bounds, tenant);
					} catch (Exception ex) {
						logger.info("Publishing metadata of layer \"" + layerName + "\" to geonetwork has failed!");
					}
				}
			}.start();
		}
	}


	public void publishLayerToGeoNetwork(Layer layer, GeoNetworkMetadataDTO metadata, Bounds bounds, Tenant tenantObject) throws Exception {
		String layerName = layer.getName();
		String layerId = layer.getId().toString();
		String tenant = tenantObject.getName();


		long elapsedTime = 0, stepping = 100;
		Set<GosDefinition> availableGosForLayer = dataMonitor.getAvailableGosFor(layerId);
		long zookeeperTimeoutMillis = 3000;
		while ((availableGosForLayer == null || availableGosForLayer.isEmpty()) && elapsedTime <= zookeeperTimeoutMillis) {
			availableGosForLayer = dataMonitor.getAvailableGosFor(layerId);
			try {
				Thread.sleep(stepping);
			} catch (InterruptedException ex) {}
			elapsedTime += stepping;
		}
		if (availableGosForLayer == null || availableGosForLayer.isEmpty()) {
			logger.info("Could not publish metadata to geonetwork for layer \"" + layerName
					+ "\" . Could not get information from any gos geoserver. Is it inserted in any geoserver?");
			return;
		}
		GosDefinition gosDefinition = new ArrayList<GosDefinition>(availableGosForLayer).get(0);


		logger.info("Publishing metadata to geonetwork for layer [" + layerId + ", \"" + layerName + "\"]");

		MetaDataForm meta = new MetaDataForm(metadata.getAuthor().getOrganisationName(), metadata.getTitle(), new Date());
		meta.setAbstractField(metadata.getDescription());
		meta.setPurpose(metadata.getPurpose());
		meta.setKeywords(metadata.getKeywords());
		meta.setUserLimitation(metadata.getLimitation());
		meta.setDistributorOrganisationName(metadata.getDistributor().getOrganisationName());
		meta.setDistributorIndividualName(metadata.getDistributor().getIndividualName());
		meta.setDistributorSite(metadata.getDistributor().getOnlineResource());
		meta.setProviderIndividualName(metadata.getProvider().getIndividualName());
		meta.setProviderSite(metadata.getProvider().getOnlineResource());
		meta.setProviderOrganisationName(metadata.getProvider().getOrganisationName());
		meta.setExtent(bounds.getMinx(), bounds.getMaxx(), bounds.getMiny(), bounds.getMaxy());
		meta.setBaseURL(this.getGeoanalyticsEndpoint());

// TODO: Fix WMS url
		GeoserverLayer geoserverLayer = geoserverManagement.getGeoserverLayer(gosDefinition.getGosEndpoint(), layerId);
		if (geoserverLayer != null && !geoserverLayer.getType().equalsIgnoreCase(RESTLayer.Type.UNKNOWN.name()) ) {
			LayerType layerType = geoserverManagement.getLayerType(gosDefinition.getGosEndpoint(), layerId);
			meta.setGraphicOverviewFromGeoserverLayer(geoserverLayer, layerType, gosDefinition.getGeoserverEndpoint());

//			if(geoserverLayer.getType().equalsIgnoreCase(RESTLayer.Type.VECTOR.name())) {
//				FeatureType featureType = geoserverManagement.getFeatureType(gosDefinition.getGosEndpoint(), layerId);
//				featureType.setSrs("EPSG:4326");
//				meta.setGraphicOverviewFromGeoserverLayer(geoserverLayer, featureType, gosDefinition.getGeoserverEndpoint());
//			}
//
//			if(geoserverLayer.getType().equalsIgnoreCase(RESTLayer.Type.RASTER.name())) {
//				CoverageType coverageType = geoserverManagement.(gosDefinition.getGosEndpoint(), layerId);
//				meta.setGraphicOverviewFromGeoserverLayer(geoserverLayer, coverageType, gosDefinition.getGeoserverEndpoint());
//			}
		}

		synchronized (geoNetworkLock) {
			try {
				GeoNetworkBridge geo = new GSManagerGeoNetworkBridge();
				long geonetworkId = geo.publishGeonetwork(tenant, meta);

				layerManager.editLayerGeonetwork(geonetworkId, layerId);
			} catch (GeoNetworkBridgeException e) {
				throw new GeoNetworkBridgeException("Could not publish layer metadata to GeoNetwork", e);
			}
		}

		logger.info("Metadata for layer [" + layerId + ", \"" + layerName + "\"] have been published to GeoNetwork successfully");
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
							geoAttributeValue = geoAttributeValue.toLowerCase();

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
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = null;
		webTarget = client.target(url);//.queryParams(nameValuePairs);

		MultivaluedMap<String, String> nameValuePairs = new MultivaluedHashMap<>();
		for (Map.Entry<String, String> params : parameters.entrySet()) {
			if (params.getKey() != null && !params.getKey().isEmpty() && params.getValue() != null && !params.getValue().isEmpty()){
				webTarget = webTarget.queryParam(params.getKey(), params.getValue());
			}
//				nameValuePairs.add(params.getKey(), params.getValue());
		}
//		ClientResponse response = webTarget.request().get(ClientResponse.class);
		Response response = webTarget.request().get();

		if (response.getStatus() == 201 || response.getStatus() == 200) {
			try {
				if (inputStream)
					return response.readEntity(InputStream.class);
				return response.readEntity(String.class);

			} catch (Exception e) {
				System.err.println("Exception occured!");
			}
		}
		return null;
	}

	public GeoNetworkMetadataDTO getCapabilitiesForPublishingToGeonetwork(WfsRequestMessenger reqM, String tenant, String featureType) throws Exception {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("service", "wfs");
		parameters.put("version", "1.0.0");
		parameters.put("request", "GetCapabilities");

		String body = (String) doTheRequest(reqM.getUrl(), parameters, false);

		if (body == null)
			return null;

		return parseGetCapabilitiesForPublishingToGeonetwork(body, tenant, featureType);
	}

	public GeoNetworkMetadataDTO parseGetCapabilitiesForPublishingToGeonetwork(String body, String tenant, String featureType) throws Exception {
		NodeList featureTypes = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(body));

		Document document = db.parse(is);

		XPath xPath = XPathFactory.newInstance().newXPath();
		featureTypes = (NodeList) xPath.compile("/WFS_Capabilities/FeatureTypeList/FeatureType").evaluate(document, XPathConstants.NODESET);


		String name = (String) xPath.compile("/WFS_Capabilities/Service/Name/text()").evaluate(document, XPathConstants.STRING);
		String title = (String) xPath.compile("/WFS_Capabilities/Service/Title/text()").evaluate(document, XPathConstants.STRING);
		String abstractText = (String) xPath.compile("/WFS_Capabilities/Service/Abstract/text()").evaluate(document, XPathConstants.STRING);
		String keywords = (String) xPath.compile("/WFS_Capabilities/Service/Keywords/text()").evaluate(document, XPathConstants.STRING);
		String onlineResource = (String) xPath.compile("/WFS_Capabilities/Service/OnlineResource/text()").evaluate(document, XPathConstants.STRING);


		for (int i = 0; i < featureTypes.getLength(); i++) {
			Node node = featureTypes.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {

				Element elem = (Element) node;

				if (elem.getElementsByTagName("Name").item(0).getFirstChild() != null){
					name = elem.getElementsByTagName("Name").item(0).getFirstChild().getNodeValue();
					if(!name.equals(featureType))
						continue;
				}

				if (elem.getElementsByTagName("Title").item(0).getFirstChild() != null)
					title = elem.getElementsByTagName("Title").item(0).getFirstChild().getNodeValue();
				if (elem.getElementsByTagName("Abstract").item(0).getFirstChild() != null)
					abstractText = elem.getElementsByTagName("Abstract").item(0).getFirstChild().getNodeValue();
				if (elem.getElementsByTagName("Keywords").item(0).getFirstChild() != null)
					keywords = elem.getElementsByTagName("Keywords").item(0).getFirstChild().getNodeValue();

			}
		}

		GeoNetworkMetadataDTO wfsImportMetadata = new GeoNetworkMetadataDTO();
		wfsImportMetadata.setAbstractField(abstractText);

		List<String> keyWs = new ArrayList<String>();
		String[] pieces = keywords.split(",");
		for (String k : pieces)
			keyWs.add(k);
		wfsImportMetadata.setKeywords(keyWs);
		wfsImportMetadata.getProvider().setIndividualName(name);
		wfsImportMetadata.getProvider().setOnlineResource(onlineResource);
		wfsImportMetadata.getProvider().setOrganisationName(name);
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

		GeoNetworkMetadataDTO wfsImportMetadata = new GeoNetworkMetadataDTO();
		wfsImportMetadata.setAbstractField(abstractText);

		List<String> keyWs = new ArrayList<String>();
		String[] pieces = keywords.split(",");
		for (String k : pieces)
			keyWs.add(k);
		wfsImportMetadata.setKeywords(keyWs);
		wfsImportMetadata.getProvider().setIndividualName(name);
		wfsImportMetadata.getProvider().setOnlineResource(onlineResource);
		wfsImportMetadata.getProvider().setOrganisationName(name);
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

			boolean containsShp = false;
			boolean containsShx = false;
			boolean containsDbf = false;

			ZipFile zipFile = new ZipFile(f);
			Enumeration<?> enu = zipFile.entries();

			while (enu.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) enu.nextElement();

				if(!zipEntry.isDirectory()){
					String name = zipEntry.getName();
					name = name.substring(name.lastIndexOf("/") + 1, name.length());
					long size = zipEntry.getSize();
					long compressedSize = zipEntry.getCompressedSize();

					logger.info(String.format("name: %-20s | size: %6d | compressed size: %6d", name, size, compressedSize));

					containsShp = containsShp || name.endsWith(".shp");
					containsShx = containsShx || name.endsWith(".shx");
					containsDbf = containsDbf || name.endsWith(".dbf");

					InputStream is = new BufferedInputStream(zipFile.getInputStream(zipEntry));
					map.put(name, is);
				}
			}

			if(!(containsShp && containsShx && containsDbf)){
				String errorMessage = "ZIP is missing ";
				errorMessage += !containsShp ? ".shp " : "";
				errorMessage += !containsShx ? ".shx " : "";
				errorMessage += !containsDbf ? ".dbf " : "";
				errorMessage += StringUtils.countMatches(errorMessage, ".") > 1 ? "files" : "file";
				throw new CustomException(HttpStatus.BAD_REQUEST, errorMessage);
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "No valid shape files in zip. Corrupted files maybe or wrong extensions");
		}
		return map;
	}

	@Transactional(rollbackFor = { GeoServerBridgeException.class, Exception.class })
	public void newLayerFromImportedData(GosDefinition gosDefinition, String newLayerId, LayerConfig layerConfig, String crs) throws Exception {
		Map<String, String> layerStyles = configurationManager.getLayerStyles();

		Bounds boundingBox = new Bounds();
		boundingBox.setMinx(layerConfig.getBoundingBox().getMinX());
		boundingBox.setMiny(layerConfig.getBoundingBox().getMinY());
		boundingBox.setMaxx(layerConfig.getBoundingBox().getMaxX());
		boundingBox.setMaxy(layerConfig.getBoundingBox().getMaxY());
		boundingBox.setCrs(crs);

		FeatureType featureType = new FeatureType();
		featureType.setDatastore(gosDefinition.getDatastoreName());
		featureType.setWorkspace(gosDefinition.getGeoserverWorkspace());
		featureType.setEnabled(true);
		featureType.setName(layerConfig.getLayerId());
		featureType.setTitle(layerConfig.getName());
		featureType.setSrs("EPSG:4326");
		featureType.setNativeCRS("EPSG:4326");
		featureType.setNativeBoundingBox(boundingBox);
		featureType.setLatLonBoundingBox(boundingBox);

		GeoserverLayer geoserverLayer = new GeoserverLayer();
		geoserverLayer.setDatastore(gosDefinition.getDatastoreName());
		geoserverLayer.setWorkspace(gosDefinition.getGeoserverWorkspace());
		geoserverLayer.setEnabled(true);
		geoserverLayer.setDefaultStyle(layerConfig.getStyle());
		geoserverLayer.setId(layerConfig.getLayerId());
		geoserverLayer.setTitle(layerConfig.getName());
		geoserverLayer.setType("VECTOR");

		Integer minScale = layerConfig.getMinScale();
		Integer maxScale = layerConfig.getMaxScale();

		geoserverManagement.addGeoserverLayer(gosDefinition.getGosEndpoint(), geoserverLayer, featureType, layerStyles, minScale, maxScale);
	}

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

	public LayerImport createGeoTIFFLayerImport(GeoTiffImportProperties properties, String source) throws Exception {
		Principal creator = securityContextAccessor.getPrincipal();

		LayerImport layerImport = new LayerImport();
		layerImport.setImportType(ImportType.GEOTIFF);
		layerImport.setDataSource(DataSource.GeoTIFF);
		layerImport.setCreator(creator);
		layerImport.setName(properties.getLayerName());
		layerImport.setSource(source);
		layerImport.setDescription(properties.getDescription());
		layerManager.createLayerImport(layerImport);

		return layerImport;
	}

	public LayerImport createNetCDFLayerImport(NetCDFImportProperties properties, String source) throws Exception {
		Principal creator = securityContextAccessor.getPrincipal();

		LayerImport layerImport = new LayerImport();
		layerImport.setImportType(ImportType.NETCDF);
		layerImport.setDataSource(DataSource.NetCDF);
		layerImport.setCreator(creator);
		layerImport.setName(properties.getLayerName());
		layerImport.setSource(source);
		layerImport.setDescription(properties.getDescription());
		layerManager.createLayerImport(layerImport);

		return layerImport;
	}

	private LayerImport createGeoTIFFLayerImportForBatchImport(GeoTiffImportProperties properties, String source) throws Exception {
		//System principal
		Principal creator = securityContextAccessor.getAdministrators().get(0);

		LayerImport layerImport = new LayerImport();
		layerImport.setImportType(ImportType.NETCDF);
		layerImport.setDataSource(DataSource.NetCDF);
		layerImport.setCreator(creator);
		layerImport.setName(properties.getLayerName());
		layerImport.setSource(source);
		layerImport.setDescription(properties.getDescription());
		layerManager.createLayerImport(layerImport);

		return layerImport;
	}

	@Async("importPool")
	public void importGeoTIFFLayer(LayerImport layerImport, GeoTiffImportProperties properties, GeoNetworkMetadataDTO metadata, byte[] geotiff, Tenant tenant) {
		logger.info("Importing GeoTIFF layer");

		try {
			GosDefinition gosDefinition = trafficShaper.getGosForNewLayer();

			Layer layer = null;

			try {
				Bounds bounds = this.getLatLongBounds(geotiff);

				List<String> tags = properties.getTags();

				layer = this.createLayerInDatabase(layerImport, null, DEFAULT_RASTER_STYLE);
				Coverage coverage = this.createCoverage(layer, layerImport, geotiff);

				this.createTagsOfLayer(layer, tags);
				this.createCoverageOfLayer(gosDefinition, coverage);
				this.publishGeoTIFFLayerToGeoServer(gosDefinition, layer, bounds, coverage);
				this.publishLayerToGeoNetworkAsync(30000, layer, metadata, bounds, tenant);
				this.updateImportStatusToSuccess(layerImport, layer);

				logger.info("Layer \"" + layer.getName() + " ( " + layer.getId() + " ) has been imported successfully!");
			} catch (Exception e) {
				importFailureHandling(gosDefinition, layer, layerImport, "Failed to complete GeoTIFF import", e);
			}
		} catch (Exception e) {
			logger.error("Could not get GOS node", e);
		}
	}

	@Async("importPool")
	public void importGeoTIFFLayer(LayerImport layerImport, GeoTiffImportProperties properties, GeoNetworkMetadataDTO metadata, InputStream geotiff, Tenant tenant, boolean isBatchImport, UserWorkspaceUtil userWorkspaceUtil) {
		logger.info("Importing GeoTIFF layer");

		try {
			GosDefinition gosDefinition = trafficShaper.getGosForNewLayer();

			Layer layer = null;

			try {

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				org.apache.commons.io.IOUtils.copy(geotiff, baos);
				geotiff.close();
				geotiff = null;
				byte[] bytes = baos.toByteArray();
				ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
				Bounds bounds = this.getLatLongBounds(bais);
				bais.close();
				bais = null;
				List<String> tags = properties.getTags();

				layer = this.createLayerInDatabase(layerImport, null, DEFAULT_RASTER_STYLE);
				if(userWorkspaceUtil != null) {
					UserWorkspaceLayer uWLDB  = this.userWorkspaceLayerPersistToUserWorkspaceLayer(userWorkspaceUtil.getUserWorkspaceLayerPersist(), layer, this.userWorkspaceLayersmanager.retrieveUerWorkspaceLayerFolderId(userWorkspaceUtil.getScope(), userWorkspaceUtil.getToken()));
					uWLDB = this.userWorkspaceLayersmanager.createUserWorkspaceLayer(uWLDB);
				}
				Coverage coverage = this.createCoverage(layer, layerImport);


				if(isBatchImport) {
					this.createTagsOfLayerForBatchLayerImport(layer, tags);
				} else {
					this.createTagsOfLayer(layer,tags);
				}

				bais = new ByteArrayInputStream(bytes);
				this.createCoverageOfLayer(gosDefinition, coverage, bais);
				bais.close();
				bais = null;
				bais = new ByteArrayInputStream(bytes);
				this.publishGeoTIFFLayerToGeoServer(gosDefinition, layer, bounds, coverage, bais);
				bais.close();
				bais = null;

				System.out.println("the layer name:" + layer.getName() + "coverage name:" +coverage.getName());
				String gosEndpoint = gosDefinition.getGosEndpoint();

				String seedRequest = GeospatialServices.createSeedRequest(layer.getWorkspace()+":"+layer.getId(),1,bounds,"EPSG:4326","seed", 0, 15, DEFAULT_RASTER_STYLE);
//				geoserverManagement.seedGeoTIFFLayerGeoWebCache(gosEndpoint, seedRequest, layer.getWorkspace()+":"+layer.getId());


				this.publishLayerToGeoNetworkAsync(30000, layer, metadata, bounds, tenant);
				this.updateImportStatusToSuccess(layerImport, layer);

				logger.info("Layer \"" + layer.getName() + " ( " + layer.getId() + " ) has been imported successfully!");
			} catch (Exception e) {
				importFailureHandling(gosDefinition, layer, layerImport, "Failed to complete GeoTIFF import", e);
			}

		} catch (Exception e) {
			logger.error("Could not get GOS node", e);
		}
	}

	@Async("importPool")
	public void importNetCDFLayer(LayerImport layerImport, NetCDFImportProperties properties, GeoNetworkMetadataDTO metadata, byte[] geotiff, Tenant tenant) {
		logger.info("Importing GeoTIFF layer");

		try {
			GosDefinition gosDefinition = trafficShaper.getGosForNewLayer();

			Layer layer = null;

			try {
				Bounds bounds = this.getLatLongBounds(geotiff);

				List<String> tags = properties.getTags();

				layer = this.createLayerInDatabase(layerImport, null, DEFAULT_RASTER_STYLE);
				Coverage coverage = this.createCoverage(layer, layerImport, geotiff);

				this.createTagsOfLayer(layer, tags);
				this.createCoverageOfLayer(gosDefinition, coverage);
				this.publishGeoTIFFLayerToGeoServer(gosDefinition, layer, bounds, coverage);
				this.publishLayerToGeoNetworkAsync(30000, layer, metadata, bounds, tenant);
				this.updateImportStatusToSuccess(layerImport, layer);

				logger.info("Layer \"" + layer.getName() + " ( " + layer.getId() + " ) has been imported successfully!");
			} catch (Exception e) {
				importFailureHandling(gosDefinition, layer, layerImport, "Failed to complete GeoTIFF import", e);
			}
		} catch (Exception e) {
			logger.error("Could not get GOS node", e);
		}
	}

	@Async("importPool")
	@Transactional(rollbackFor = Exception.class)
	public void importNetCDFLayer(LayerImport layerImport, NetCDFImportProperties properties, GeoNetworkMetadataDTO metadata,
                                  File netCDF, Tenant tenant, UserWorkspaceUtil userWorkspaceUtil) throws Exception {
		logger.info("Importing NetCDF layer");

		boolean mustThrowException = false;
		InputStream netcdfStream = null;
		try {
			netcdfStream = new FileInputStream(netCDF);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			GosDefinition gosDefinition = trafficShaper.getGosForNewLayer();

			Layer layer = null;
			List<Layer> layers = new ArrayList<Layer>();

			try {

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				org.apache.commons.io.IOUtils.copy(netcdfStream, baos);
				netcdfStream.close();
				byte[] bytes = baos.toByteArray();
				baos.close();
				ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

				NetCDFUtilClass netCDFConfig = this.getLatLongBoundsFromNetCDFFile(netCDF, layerImport);

				Bounds bounds = netCDFConfig.getBounds();
				List<String> tags = properties.getTags();

				Coverage coverage = null;
				int numOfLayersInCoverage = 0;
				Map<UUID,String> layeIdToNameMap = new HashMap<UUID,String>();
				for(String layername : netCDFConfig.getNamesOfLayersInCoverage()) {
					boolean isFirstLayer = numOfLayersInCoverage == 0;
					List<String> theTags = new ArrayList<String>(tags);
					theTags.add(layerImport.getName());
					if(isFirstLayer) {
						String theLayerName = netCDFConfig.getNamesOfLayersInCoverage().size() > 1 ? layername : layerImport.getName();
						layer = this.createNetCDFLayerInDatabase(layerImport, null, DEFAULT_RASTER_STYLE, theLayerName, isFirstLayer);
                        if(userWorkspaceUtil != null) {
                            UserWorkspaceLayer uWLDB  = this.convertAndStoreuserWorkspaceLayer(userWorkspaceUtil.getUserWorkspaceLayerPersist(), layer, this.userWorkspaceLayersmanager.retrieveUerWorkspaceLayerFolderId(userWorkspaceUtil.getScope(), userWorkspaceUtil.getToken()));
                        }
						layeIdToNameMap.put(layer.getId(), layername);

						coverage = this.createCoverage(layer, layerImport);
						this.createCoverageOfLayer(gosDefinition, coverage, bais);
						bais.close();
						bais = null;
//						this.addNewLayerInInfrastructure(gosDefinition, layer);
						this.createTagsOfLayer(layer, theTags);
					} else {
						break;
//						Layer l = this.createNetCDFLayerInDatabase(layerImport, null, DEFAULT_RASTER_STYLE, layername, isFirstLayer);
//						this.addNewLayerInInfrastructure(gosDefinition, l);
//						layers.add(l);
//						layeIdToNameMap.put(l.getId(), layername);
//						this.createTagsOfLayer(l, theTags);
					}

					numOfLayersInCoverage++;
				}

				bais = new ByteArrayInputStream(bytes);
				this.publishNetCDFLayerToGeoServer(gosDefinition, layer, netCDFConfig, coverage, bais, layeIdToNameMap, layers);
				bais.close();

//				System.out.println("the layer name:" + layer.getName() + "coverage name:" +coverage.getName());
				String gosEndpoint = gosDefinition.getGosEndpoint();

				this.publishLayerToGeoNetworkAsync(30000, layer, metadata, bounds, tenant);
//				if(layers.size() > 0) {
//					for(Layer la : layers) {
//						this.publishLayerToGeoNetworkAsync(30000, la, metadata, bounds, tenant);
//					}
//				}

				this.updateImportStatusToSuccess(layerImport, layer);

				logger.info("Layer \"" + layer.getName() + " ( " + layer.getId() + " ) has been imported successfully!");
			} catch (Exception e) {
				importFailureHandlingForNetCDFLayers(layer.getId().toString(), gosDefinition, layerImport);
				mustThrowException = true;
			}

		} catch (Exception e) {
			logger.error("Could not get GOS node", e);
		}

		if(netCDF != null)
			netCDF.delete();

		if(mustThrowException)
			throw new Exception("Test exception");

	}

	private void addNewLayerInInfrastructure(GosDefinition gosDefinition, Layer newLayer) throws Exception {
		logger.info("Inserting " + newLayer.getName() + " layer into infrastructure");

		try {
			geospatialBackendClustered.addLayerInInfrastructure(gosDefinition, newLayer.getId());
		} catch (Exception e) {
			throw new Exception("Failed to insert GeoTIFF " + newLayer.getName() + " in database of GOS endpoint: " + gosDefinition.getGosEndpoint(), e);
		}

		logger.info(newLayer.getName() + " layer has been inserted successfully on gos endpoint: " + gosDefinition.getGosEndpoint());
	}

	private void createCoverageOfLayer(GosDefinition gosDefinition, Coverage geotiff) throws Exception {
		logger.info("Inserting " + geotiff.getName() + " geotiff into database (through GOS)");

		try {
			geospatialBackendClustered.createCoverageOfLayer(gosDefinition, geotiff);
		} catch (Exception e) {
			throw new Exception("Failed to insert GeoTIFF " + geotiff.getName() + " in database of GOS endpoint: " + gosDefinition.getGosEndpoint(), e);
		}

		logger.info(geotiff.getName() + " geotiff has been inserted successfully on gos endpoint: " + gosDefinition.getGosEndpoint());
	}

	private void createCoverageOfLayer(GosDefinition gosDefinition, Coverage coverage, InputStream geotiff) throws Exception {
		logger.info("Inserting " + coverage.getName() + " geotiff into database (through GOS)");

		try {
			geospatialBackendClustered.createCoverageOfLayer(gosDefinition, coverage, geotiff);
		} catch (Exception e) {
			throw new Exception("Failed to insert GeoTIFF " + coverage.getName() + " in database of GOS endpoint: " + gosDefinition.getGosEndpoint(), e);
		}

		logger.info(coverage.getName() + " geotiff has been inserted successfully on gos endpoint: " + gosDefinition.getGosEndpoint());
	}

	private void publishGeoTIFFLayerToGeoServer(GosDefinition gosDefinition, Layer layer, Bounds bounds, Coverage coverage, InputStream geotiff) throws Exception {
		logger.info("Adding layer entry " + layer.getId().toString() + " to geoserver of gos: " + gosDefinition.getGosEndpoint());

		try {
			String gosEndpoint = gosDefinition.getGosEndpoint();

			LayerBounds layerBounds = bounds.createLayerBounds();
			LayerConfig layerConfig = createLayerConfig(layer, layerBounds, DataSource.GeoTIFF, DEFAULT_RASTER_STYLE);
			PublishConfig publishConfig = new PublishConfig(layer.getId().toString(), layer.getName(), bounds.getCrs(), layerBounds, DEFAULT_RASTER_STYLE);
			logger.info("Just before adding layer entry " + layer.getId().toString() + " to geoserver of gos: " + gosDefinition.getGosEndpoint());

			//TODO: this addLayerConfig should be removed in the feature... should be added on LayerManager.addLayer()
			this.geoserverManagement.publishGeoTIFF(gosEndpoint, publishConfig, coverage, geotiff);

			logger.info("Just before adding layer config " + layer.getId().toString() + " to geoserver of gos: " + gosDefinition.getGosEndpoint());

			this.configurationManager.addLayerConfig(layerConfig);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Could not publish GeoTIFF " + layer.getId().toString() + " layer to GeoServer", e);
		}

		logger.info("Layer entry " + layer.getId().toString() + " has beend added to GeoServer of gos: " + gosDefinition.getGosEndpoint());
	}
	private void publishGeoTIFFLayerToGeoServer(GosDefinition gosDefinition, Layer layer, Bounds bounds, Coverage coverage) throws Exception {
		logger.info("Adding layer entry " + layer.getId().toString() + " to geoserver of gos: " + gosDefinition.getGosEndpoint());

		try {
			String gosEndpoint = gosDefinition.getGosEndpoint();

			LayerBounds layerBounds = bounds.createLayerBounds();
			LayerConfig layerConfig = createLayerConfig(layer, layerBounds, DataSource.GeoTIFF, DEFAULT_RASTER_STYLE);
			PublishConfig publishConfig = new PublishConfig(layer.getId().toString(), layer.getName(), bounds.getCrs(), layerBounds, DEFAULT_RASTER_STYLE);
			logger.info("Just before adding layer entry " + layer.getId().toString() + " to geoserver of gos: " + gosDefinition.getGosEndpoint());

			//TODO: this addLayerConfig should be removed in the feature... should be added on LayerManager.addLayer()
			this.geoserverManagement.publishGeoTIFF(gosEndpoint, publishConfig, coverage);
			logger.info("Just before adding layer config " + layer.getId().toString() + " to geoserver of gos: " + gosDefinition.getGosEndpoint());

			this.configurationManager.addLayerConfig(layerConfig);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Could not publish GeoTIFF " + layer.getId().toString() + " layer to GeoServer", e);
		}

		logger.info("Layer entry " + layer.getId().toString() + " has beend added to GeoServer of gos: " + gosDefinition.getGosEndpoint());
	}

	private void publishNetCDFLayerToGeoServer(GosDefinition gosDefinition, Layer layer, NetCDFUtilClass netCDFConfig,
											   Coverage coverage, InputStream netcdf, Map<UUID,String> layeIdToNameMap, List<Layer> layersInFile) throws Exception {
		logger.info("Adding layer entry " + layer.getId().toString() + " to geoserver of gos: " + gosDefinition.getGosEndpoint());

		try {
			String gosEndpoint = gosDefinition.getGosEndpoint();

			LayerBounds layerBounds = netCDFConfig.getBounds().createLayerBounds();
			LayerConfig layerConfig = createLayerConfig(layer, layerBounds, DataSource.NetCDF, DEFAULT_RASTER_STYLE);

			List<LayerConfig> layerConfigsOfLayersInFile = new ArrayList<LayerConfig>();
//			layersInFile.forEach(lif -> {
//				LayerConfig layerConf = createLayerConfig(lif, layerBounds, DataSource.NetCDF, DEFAULT_RASTER_STYLE);
//				layerConfigsOfLayersInFile.add(layerConf);
//			});

			NetCDFPublishConfig publishConfig =
					new NetCDFPublishConfig(layer.getId().toString(), layer.getName(), netCDFConfig.getBounds().getCrs(), layerBounds, DEFAULT_RASTER_STYLE, layeIdToNameMap, netCDFConfig.getStoreName());

			logger.info("Just before adding layer entry " + layer.getId().toString() + " to geoserver of gos: " + gosDefinition.getGosEndpoint());

			//TODO: this addLayerConfig should be removed in the feature... should be added on LayerManager.addLayer()
			this.geoserverManagement.publishNetCDF(gosEndpoint, publishConfig, coverage, netcdf);

			logger.info("Just before adding layer config " + layer.getId().toString() + " to geoserver of gos: " + gosDefinition.getGosEndpoint());

			this.configurationManager.addLayerConfig(layerConfig);

//			for(LayerConfig lc : layerConfigsOfLayersInFile) {
//				this.configurationManager.addLayerConfig(lc);
//			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Could not publish NetCDF " + layer.getId().toString() + " layer to GeoServer", e);
		}

		logger.info("Layer entry " + layer.getId().toString() + " has beend added to GeoServer of gos: " + gosDefinition.getGosEndpoint());
	}

	public Bounds getLatLongBounds(byte[] geotiff) throws Exception {
		try{
			ByteArrayInputStream geotiffByteArrayInputStream = new ByteArrayInputStream(geotiff);

			GeoTiffReader reader = new GeoTiffReader(geotiffByteArrayInputStream, new Hints());
			GridCoverage2D nativeCoverage = reader.read(null);
			GridCoverage2D latLongCoverage = null;

			if(nativeCoverage.getCoordinateReferenceSystem().getName().equals(CRS.decode("EPSG:4326").getName())){
				latLongCoverage = nativeCoverage;
			} else{
				latLongCoverage = (GridCoverage2D) Operations.DEFAULT.resample(nativeCoverage, CRS.decode("EPSG:4326"));
			}

			Rectangle boundingBox = latLongCoverage.getEnvelope2D().getBounds();
			String nativeCrs = CRS.toSRS(nativeCoverage.getCoordinateReferenceSystem2D());

			Bounds bounds = new Bounds();
			bounds.setMinx(boundingBox.getMinX());
			bounds.setMiny(boundingBox.getMinY());
			bounds.setMaxx(boundingBox.getMaxX());
			bounds.setMaxy(boundingBox.getMaxY());
			bounds.setCrs(nativeCrs);

			return bounds;
		}catch(Exception e){
			throw new Exception("Could not read bounds of GeoTIFF file", e);
		}
	}

	public Bounds getLatLongBounds(InputStream geotiff) throws Exception {
		try{
//			ByteArrayInputStream geotiffByteArrayInputStream = new ByteArrayInputStream(geotiff);

			GeoTiffReader reader = new GeoTiffReader(geotiff, new Hints());
			GridCoverage2D nativeCoverage = reader.read(null);
			GridCoverage2D latLongCoverage = null;

			if(nativeCoverage.getCoordinateReferenceSystem().getName().equals(CRS.decode("EPSG:4326").getName())){
				latLongCoverage = nativeCoverage;
			} else{
				latLongCoverage = (GridCoverage2D) Operations.DEFAULT.resample(nativeCoverage, CRS.decode("EPSG:4326"));
			}

			Rectangle boundingBox = latLongCoverage.getEnvelope2D().getBounds();
			String nativeCrs = CRS.toSRS(nativeCoverage.getCoordinateReferenceSystem2D());

			Bounds bounds = new Bounds();
			bounds.setMinx(boundingBox.getMinX());
			bounds.setMiny(boundingBox.getMinY());
			bounds.setMaxx(boundingBox.getMaxX());
			bounds.setMaxy(boundingBox.getMaxY());
			bounds.setCrs(nativeCrs);
//			System.out.println("crs:"+ bounds.getCrs() +" minX:"+ bounds.getMinx() +"maxY"+ bounds.getMaxy());
			return bounds;
		}catch(Exception e){
			throw new Exception("Could not read bounds of GeoTIFF file", e);
		}
	}

	public NetCDFUtilClass getLatLongBoundsFromNetCDFFile(File netcdf/*InputStream netcdf*/, LayerImport layerImport) throws Exception {
		NetCDFUtilClass netCDFConfig;
		File netcdfUnzipped = null;
		String unippedFileName = null;
		//unzip file. Files to be imported in Geoserver must be zipped.
		try {

			ZipFile zipFile = new ZipFile(netcdf);
			Enumeration<?> enu = zipFile.entries();
			boolean isUnzipped = false;

			while (enu.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) enu.nextElement();

				if( !zipEntry.isDirectory() && zipEntry.getName().indexOf(".nc") > -1 ){
					InputStream inputStream = new BufferedInputStream(zipFile.getInputStream(zipEntry));

					File ff = Files.createTempDir();
					netcdfUnzipped = new File(ff.getAbsolutePath() + "/" + zipEntry.getName());

					unippedFileName = zipEntry.getName();

					FileOutputStream fos = new FileOutputStream(netcdfUnzipped);
					int length;
					byte[] bytes = new byte[1024];
					while ((length = inputStream.read(bytes)) >= 0) {
						fos.write(bytes, 0, length);
					}
					fos.close();
					inputStream.close();

					isUnzipped = true;
				}

				if(isUnzipped) break;
			}

			zipFile.close();
		} catch (Exception e) {
			throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Unzipping netcdf file failed!!");
		}

		//get bounds
		try {
			// Creation of the NetCDF reader
			final NetCDFReader reader = new NetCDFReader(netcdfUnzipped, null);

			// Getting the coverage names
			String[] names = reader.getGridCoverageNames();

			// Selection of the first coverage name
			String first = names[0];

			// Selection of the coverage associated to the name
			GridCoverage2D grid = reader.read(first, null);
			GridCoverage2D latLongCoverage = null;

			if(grid.getCoordinateReferenceSystem().getName().equals(CRS.decode("EPSG:4326").getName())){
				latLongCoverage = grid;
			} else{
				latLongCoverage = (GridCoverage2D) Operations.DEFAULT.resample(grid, CRS.decode("EPSG:4326"));
			}

			Rectangle boundingBox = latLongCoverage.getEnvelope2D().getBounds();
			String nativeCrs = CRS.toSRS(grid.getCoordinateReferenceSystem2D());

			Bounds bounds = new Bounds();
			bounds.setMinx(boundingBox.getMinX());
			bounds.setMiny(boundingBox.getMinY());
			bounds.setMaxx(boundingBox.getMaxX());
			bounds.setMaxy(boundingBox.getMaxY());
			bounds.setCrs(nativeCrs);

			netCDFConfig = new NetCDFUtilClass(bounds, Arrays.asList(names), unippedFileName, layerImport.getName());

			netcdfUnzipped.delete();

			return netCDFConfig;
		} catch (Exception e) {
			throw new Exception("Could not read bounds of NetCDF file", e);
		}
	}

	private Coverage createCoverage(Layer layer, LayerImport layerImport, byte[] coverageData) {
		Coverage coverage = new Coverage();
		coverage.setLayerID(layer.getId());
		coverage.setName(layerImport.getSource());
		coverage.setCreator(layerImport.getCreator().getId());
		coverage.setImage(coverageData);
		return coverage;
	}

	private Coverage createCoverage(Layer layer, LayerImport layerImport) {
		Coverage coverage = new Coverage();
		coverage.setLayerID(layer.getId());
		coverage.setName(layerImport.getSource());
		coverage.setCreator(layerImport.getCreator().getId());
//		coverage.setImage(coverageData);
		return coverage;
	}

	public LayerConfig createLayerConfig(Layer layer, LayerBounds boundingBox, DataSource dataSource, String style) {
		LayerConfig layerConfig = new LayerConfig();
		layerConfig.setName(layer.getName());
		layerConfig.setLayerId(layer.getId().toString());
		layerConfig.setBoundingBox(boundingBox);
		layerConfig.setStyle(style);
		layerConfig.setDataSource(dataSource);
		return layerConfig;
	}

	public void importFailureHandling(GosDefinition gosDefinition, Layer layer, LayerImport layerImport, String errorMessage, Exception e) {
		try {
			if (layer != null) {
				this.layerManager.deleteLayerFromInfra(layer.getId().toString());
			}
		} catch (Exception ex) {
			logger.error("Error while rolling back layer and database view", ex);
		}

		try {
			this.updateImportStatusToFailure(layerImport);
		} catch (Exception ex) {
			logger.error("Failed to update Layer Import status to failure", ex);
		}

		logger.error(errorMessage, e);
	}

	private void importFailureHandlingForNetCDFLayers(String coverageId, GosDefinition gosDefinition, LayerImport layerImport) {
//		try {
//			this.updateImportStatusToFailure(layerImport);
//		} catch (Exception e) {
//			logger.error("Failed to update Layer Import status to failure", e);
//		}

		String gosEndpoint = gosDefinition.getGosEndpoint();
		if(this.geoserverManagement.geoserverCoverageStoreOfCoverageExists(gosEndpoint, coverageId))
			this.geoserverManagement.deleteGeoserverCoverageStoreByCoverageId(gosEndpoint, coverageId);
	}

	private UserWorkspaceLayerPersist retrieveFileLayerFromUsersWorkspace(UUID layerId, String userToken, String scope) {
		//TODO Implement this
		return null;
	}

	private UserWorkspaceLayer userWorkspaceLayerPersistToUserWorkspaceLayer(UserWorkspaceLayerPersist userWorkspaceLayerPersistObject, Layer layer, UUID directoryId){
		//TODO Implement this
		//Insert needed properties in geoanalytics.properies file

		UserWorkspaceLayer uWL = new UserWorkspaceLayer();
		uWL.setCreationDate(new Date());
		uWL.setDirectoryId(directoryId);
		uWL.setLastUpdate(new Date());
		uWL.setLayer(layer);
		uWL.setName(userWorkspaceLayerPersistObject.getName());
		uWL.setOwner(layer.getCreator());
		uWL.setPath(this.userWorkspaceLayersmanager.getItemPathById(userWorkspaceLayerPersistObject.getId().toString()));
		uWL.setTitle(userWorkspaceLayerPersistObject.getTitle());
		uWL.setId(userWorkspaceLayerPersistObject.getId());

		return uWL;
	}

	private UserWorkspaceLayer convertAndStoreuserWorkspaceLayer(UserWorkspaceLayerPersist userWorkspaceLayerPersistObject, Layer layer, UUID directoryId) throws Exception {
		UserWorkspaceLayer uWLDb = this.userWorkspaceLayerPersistToUserWorkspaceLayer(userWorkspaceLayerPersistObject, layer, directoryId);

		UserWorkspaceLayer uWLDb2 = null;
		try {
			uWLDb2 = this.userWorkspaceLayersmanager.findUserWorkspaceLayerById(uWLDb.getId());
		} catch(Exception e) {}

		if(uWLDb2 != null) {
			logger.error("User workspace layer already imported");
			throw new Exception("You have already imported this layer from your workspace");
		}

		return this.userWorkspaceLayersmanager.createUserWorkspaceLayer(uWLDb);
	}

	private UUID getUserWorkspaceLayerFolderIdFromDB(Principal principal, String userToken) {
//		if(principal.getUserWorkspaceLayersOwner().size() > 0) {
//			return principal.getUserWorkspaceLayersOwner().iterator().next().getDirectoryId();
//		}

		return this.userWorkspaceLayersmanager.retrieveUerWorkspaceLayerFolderId(principal.getTenant().getName(), userToken);
	}

	public String getGeoanalyticsEndpoint() { return geoanalyticsEndpoint; }
	@Value(value = "${gr.cite.geoanalytics.functions.geoanalytics.endpoint}")
	public void setGeoanalyticsEndpoint(String geoanalyticsEndpoint) { this.geoanalyticsEndpoint = geoanalyticsEndpoint; }
}