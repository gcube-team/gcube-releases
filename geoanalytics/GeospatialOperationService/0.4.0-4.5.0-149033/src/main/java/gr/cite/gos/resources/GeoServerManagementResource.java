package gr.cite.gos.resources;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import gr.cite.gaap.datatransferobjects.StyleMessenger;
import gr.cite.geoanalytics.context.GeoServerBridgeConfig;
import gr.cite.geoanalytics.dataaccess.entities.coverage.Coverage;
import gr.cite.geoanalytics.dataaccess.entities.layer.DataSource;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.SystemPresentationConfig;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.GeoServerBridge;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.DataStore;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.FeatureType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.GeoserverLayer;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.PublishConfig;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.exception.GeoServerBridgeException;
import gr.cite.gos.environment.EnvironmentInitializer;


@Component
@Path("/GeoserverManagement")
public class GeoServerManagementResource {
	
	private EnvironmentInitializer environmentInitializer;
	private GeoServerBridgeConfig configuration;
	private GeoServerBridge geoserverBridge;
	private ObjectMapper mapper = new ObjectMapper();
	
	private static final Logger logger = LoggerFactory.getLogger(GeoServerManagementResource.class);
	
	public GeoServerManagementResource(GeoServerBridge geoserverBridge, GeoServerBridgeConfig configuration) throws Exception{
		this.geoserverBridge = geoserverBridge;
		this.configuration = configuration;
	}


	@Inject
	public void setEnvironmentInitializer(EnvironmentInitializer environmentInitializer){
		this.environmentInitializer = environmentInitializer;
	}
	
	
	@POST
	@Path("initializeEnvironment")
	public Response initializeEnvironment(
			@FormParam("layerConfigsJSON") String layerConfigsJSON, 
			@FormParam("systemPresentationConfigJSON")  String systemPresentationConfigJSON,
			@FormParam("stylesJSON")  String stylesJSON
			){
		
		try{
			SystemPresentationConfig systemPresentationConfig = mapper.readValue(systemPresentationConfigJSON, SystemPresentationConfig.class);
			List<LayerConfig> layerConfigs = mapper.readValue(layerConfigsJSON, new TypeReference<List<LayerConfig>>(){});
			List<StyleMessenger> styles = mapper.readValue(stylesJSON, new TypeReference<List<StyleMessenger>>(){});
			environmentInitializer.initializeGeoserverEnvironment(layerConfigs, systemPresentationConfig, styles);
			return Response.status(200).entity("Initialized geoserver successfully").build();
		}
		catch (Exception e) {
			logger.error("Could not initialize geoserver", e);
			return Response.status(500).entity("Could not initialize geoserver").build();
		}
	}
	
	
	
	@DELETE
	@Path("deleteGeoserverLayer/{layerId}/{dataSource}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response deleteLayer(@PathParam("layerId") String layerId, @PathParam("dataSource") DataSource dataSource) throws IOException {
		try {
			this.geoserverBridge.deleteLayer(layerId, dataSource);
		} catch (GeoServerBridgeException ex) {
			return Response.status(500).entity("Could not delete layer with ID " + layerId + " from geoserver").build();
		}

		return Response.status(200).entity("Layer with id " + layerId + " was deleted on this geoserver").build();
	}
	

	
	@POST
	@Path("addDataStore")
	public Response addDataStore(@FormParam("dataStore") String dataStoreJSON){
		try {
			DataStore dataStore = mapper.readValue(dataStoreJSON, DataStore.class);
			this.geoserverBridge.addDataStore(dataStore);
			return Response.status(201).entity("Created datastore with name: "+dataStore.getDataStoreName()).build();
		} catch (GeoServerBridgeException | IOException e) {
			return Response.status(500).entity("Could not add to geoserver the datastore: " +dataStoreJSON).build();
		}
	}
	
	
	
	@POST
	@Path("addGeoserverLayerFromConfig")
	public Response addGeoserverLayerFromConfig(
			@FormParam("layerConfigJSON") String layerConfigJSON,
			@FormParam("slds") String sldsJSON,
			@FormParam("crs") String crs,
			@FormParam("style") String style
			){
		
		
		GeoserverLayer geoserverLayer = new GeoserverLayer();
		FeatureType featureType = new FeatureType();
		
		try {
			
			LayerConfig layerConfig = mapper.readValue(layerConfigJSON, LayerConfig.class);
			
			Bounds b = new Bounds(layerConfig.getBoundingBox().getMinY(), layerConfig.getBoundingBox().getMinY(),
					layerConfig.getBoundingBox().getMaxX(), layerConfig.getBoundingBox().getMaxY(), crs);

			featureType.setDatastore(configuration.getPostgisDataStoreConfig().getDataStoreName());
			featureType.setWorkspace(configuration.getGeoServerBridgeWorkspace());
			featureType.setEnabled(true);
			featureType.setName(layerConfig.getLayerId());
			featureType.setTitle(layerConfig.getName());
			featureType.setSrs("EPSG:4326");
			featureType.setNativeCRS("EPSG:4326");
			featureType.setNativeBoundingBox(b);
			featureType.setLatLonBoundingBox(b);

			
			geoserverLayer.setWorkspace(configuration.getGeoServerBridgeWorkspace());
			geoserverLayer.setDatastore(configuration.getPostgisDataStoreConfig().getDataStoreName());
			geoserverLayer.setEnabled(true);
			geoserverLayer.setDefaultStyle(style);
			geoserverLayer.setId(layerConfig.getLayerId());
			geoserverLayer.setTitle(layerConfig.getName());
			geoserverLayer.setType("VECTOR");
			
			Integer minScale = layerConfig.getMinScale();
			Integer maxScale = layerConfig.getMaxScale();

			
			Map<String,String> slds = mapper.readValue(sldsJSON, new TypeReference<Map<String, String>>(){});
			if(minScale==null && maxScale==null)
				this.geoserverBridge.addGeoserverLayer(geoserverLayer, featureType, slds);
			else
				this.geoserverBridge.addGeoserverLayer(geoserverLayer, featureType, slds, minScale, maxScale);
			return Response.status(201).entity("Created geoserver layer with id: "+geoserverLayer.getId()).build();
		} catch (GeoServerBridgeException | IOException e) {
			return Response.status(500).entity("Could not create geoserver layer: " +geoserverLayer).build();
		}
	}
	
	@POST
	@Path("addGeoserverLayer")
	public Response addGeoserverLayer(@FormParam("geoserverLayer") String geoserverLayerJSON, @FormParam("ft")  String ftJSON, @FormParam("slds") String sldsJSON, @FormParam("minScale") Integer minScale, @FormParam("maxScale") Integer maxScale){
		try {
			GeoserverLayer geoserverLayer = mapper.readValue(geoserverLayerJSON, GeoserverLayer.class);
			FeatureType ft = mapper.readValue(ftJSON, FeatureType.class);
			
			//override (this is essential code when replicating from other domain)
			ft.setDatastore(configuration.getPostgisDataStoreConfig().getDataStoreName());
			ft.setWorkspace(configuration.getGeoServerBridgeWorkspace());
			geoserverLayer.setWorkspace(configuration.getGeoServerBridgeWorkspace());
			geoserverLayer.setDatastore(configuration.getPostgisDataStoreConfig().getDataStoreName());
			
			Map<String,String> slds = mapper.readValue(sldsJSON, new TypeReference<Map<String, String>>(){});
			if(minScale==null && maxScale==null)
				this.geoserverBridge.addGeoserverLayer(geoserverLayer, ft, slds);
			else
				this.geoserverBridge.addGeoserverLayer(geoserverLayer, ft, slds, minScale, maxScale);
			return Response.status(201).entity("Created geoserver layer with id: "+geoserverLayer.getId()).build();
		} catch (GeoServerBridgeException | IOException e) {
			return Response.status(500).entity("Could not create geoserver layer: " +geoserverLayerJSON).build();
		}
	}
	
	@POST
	@Path("addLayerStyle")
	public Response addLayerStyle(@FormParam("layerName") String layerName, @FormParam("styleName")  String styleName, @FormParam("sld") String sld, @FormParam("minScale") Integer minScale, @FormParam("maxScale") Integer maxScale){
		
		try{
			if(minScale==null && maxScale==null)
				this.geoserverBridge.addLayerStyle(layerName, styleName, sld);
			else
				this.geoserverBridge.addLayerStyle(layerName, styleName, sld, minScale, maxScale);
			return Response.status(201).entity("Added style "+styleName+" on layer "+layerName).build();
		}
		catch(GeoServerBridgeException ex){
			return Response.status(500).entity("Could not add style "+styleName+" on layer "+layerName).build();
		}
	}
	
	@POST
	@Path("addStyle")
	public Response addStyle(@FormParam("styleName")  String styleName, @FormParam("sld") String sld, @FormParam("minScale") Integer minScale, @FormParam("maxScale") Integer maxScale){
		try{
			if(minScale==null && maxScale==null)
				this.geoserverBridge.addStyle(styleName, sld);
			else
				this.geoserverBridge.addStyle(styleName, sld, minScale, maxScale);
			return Response.status(201).entity("Added style "+styleName).build();
		}
		catch(GeoServerBridgeException ex){
			return Response.status(500).entity("Could not add style "+styleName).build();
		}
	}
	
	
	@POST
	@Path("addWorkspace")
	public Response addWorkspace(@FormParam("name")  String name, @FormParam("uri") String uri){
		try{
			if(uri==null)
				this.geoserverBridge.addWorkspace(name);
			else
				this.geoserverBridge.addWorkspace(name, uri);
			return Response.status(201).entity("Added workspace "+name).build();
		}
		catch(GeoServerBridgeException ex){
			return Response.status(500).entity("Could not add workspace "+name).build();
		}
	}
	
	
	@POST
	@Path("dataStoreExists")
	public Response dataStoreExists(@FormParam("workspaceName")  String workspaceName, @FormParam("dataStoreName") String dataStoreName){
		try{
			Boolean dataStoreExists = this.geoserverBridge.dataStoreExists(workspaceName, dataStoreName);
			return Response.status(200).entity(dataStoreExists).build();
		}
		catch(GeoServerBridgeException ex){
			return Response.status(500).build();
		}
	}
	
	
	@GET
	@Path("getDataStore/{dataStoreName}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getDataStore(@PathParam("dataStoreName") String dataStoreName){
		try {
			String dataStoreJSON = mapper.writeValueAsString(this.geoserverBridge.getDataStore(dataStoreName));
			return Response.status(200).entity(dataStoreJSON).build();
		} catch (IOException | GeoServerBridgeException e) {
			return Response.status(500).build();
		}
	}
	
	
	@GET
	@Path("getFeatureType/{name}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getFeatureType(@PathParam("name") String name){
		try {
			String featureTypeJSON = mapper.writeValueAsString(this.geoserverBridge.getFeatureType(name));
			return Response.status(200).entity(featureTypeJSON).build();
		} catch (IOException | GeoServerBridgeException e) {
			return Response.status(500).build();
		}
	}
	

	@GET
	@Path("getGeoserverLayer/{name}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getGeoserverLayer(@PathParam("name") String name){
		try {
			String geoserverLayerJSON = mapper.writeValueAsString(this.geoserverBridge.getGeoserverLayer(name));
			return Response.status(200).entity(geoserverLayerJSON).build();
		} catch (IOException | GeoServerBridgeException e) {
			return Response.status(500).build();
		}
	}
	
	@GET
	@Path("getGeoserverLayers")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getGeoserverLayers(){
		try {
			String geoserverLayersJSON = mapper.writeValueAsString(this.geoserverBridge.getGeoserverLayers());
			return Response.status(200).entity(geoserverLayersJSON).build();
		} catch (IOException | GeoServerBridgeException e) {
			return Response.status(500).build();
		}
	}
	
	
	
	@GET
	@Path("getGeoserverLayers/{datastoreName}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getGeoserverLayersOfDataStore(@PathParam("datastoreName") String datastoreName){
		try {
			String geoserverLayersOfDataStoreJSON = mapper.writeValueAsString(this.geoserverBridge.getGeoserverLayersOfDataStore(datastoreName));
			return Response.status(200).entity(geoserverLayersOfDataStoreJSON).build();
		} catch (IOException | GeoServerBridgeException e) {
			return Response.status(500).build();
		}
	}
	
	
	
	@GET
	@Path("getStyle/{name}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getStyle(@PathParam("name") String name){
		try {
			String style = this.geoserverBridge.getStyle(name);
			return Response.status(200).entity(style).build();
		} catch (GeoServerBridgeException e) {
			return Response.status(500).build();
		}
	}
	
	@GET
	@Path("getAllStyles")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getAllStyles(){
		try {
			List<String> styles = this.geoserverBridge.getAllStyles();
			String listStylesJSON = mapper.writeValueAsString(styles);
			return Response.status(200).entity(listStylesJSON).build();
		} catch (GeoServerBridgeException | IOException e) {
			return Response.status(500).build();
		}
	}
	
	@GET
	@Path("listDataStores")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response listDataStores(){
		try {
			String listDataStoresJSON = mapper.writeValueAsString(this.geoserverBridge.listDataStores());
			return Response.status(200).entity(listDataStoresJSON).build();
		} catch (GeoServerBridgeException | IOException e) {
			return Response.status(500).build();
		}
	}
	
	
	@GET
	@Path("listLayers")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response listLayers(){
		try {
			String listLayersJSON = mapper.writeValueAsString(this.geoserverBridge.listLayers());
			return Response.status(200).entity(listLayersJSON).build();
		} catch (GeoServerBridgeException | IOException e) {
			return Response.status(500).build();
		}
	}
	
	
	@GET
	@Path("listLayersOfDataStore/{name}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response listLayersOfDataStore(@PathParam("name") String name){
		try {
			String listLayersOfDataStoreJSON = mapper.writeValueAsString(this.geoserverBridge.listLayersOfDataStore(name));
			return Response.status(200).entity(listLayersOfDataStoreJSON).build();
		} catch (GeoServerBridgeException | IOException e) {
			return Response.status(500).build();
		}
	}
	
	
	@POST
	@Path("removeLayerStyle")
	public Response removeLayerStyle(@FormParam("layerName") String layerName, @FormParam("styleName")  String styleName, @FormParam("minScale") Integer minScale, @FormParam("maxScale") Integer maxScale){
		try{
			if(minScale==null && maxScale==null)
				this.geoserverBridge.removeLayerStyle(layerName, styleName);
			else
				this.geoserverBridge.removeLayerStyle(layerName, styleName, minScale, maxScale);
			return Response.status(201).entity("Removed style "+styleName +" from layer "+layerName).build();
		}
		catch(GeoServerBridgeException ex){
			return Response.status(500).entity("Could not remove style "+styleName +" from layer "+layerName).build();
		}
	}
	
	@POST
	@Path("removeStyle")
	public Response removeStyle(@FormParam("styleName") String styleName, @FormParam("minScale") Integer minScale, @FormParam("maxScale") Integer maxScale){
		try{
			if(minScale==null && maxScale==null)
				this.geoserverBridge.removeStyle(styleName);
			else
				this.geoserverBridge.removeStyle(styleName, minScale, maxScale);
			return Response.status(201).entity("Removed style "+styleName).build();
		}
		catch(GeoServerBridgeException ex){
			return Response.status(500).entity("Could not remove style "+styleName).build();
		}
	}
	
	
	
	@POST
	@Path("setDefaultLayerStyle")
	public Response setDefaultLayerStyle(@FormParam("layerName") String layerName, @FormParam("styleName")  String styleName, @FormParam("sld")  String sld,  @FormParam("minScale") Integer minScale, @FormParam("maxScale") Integer maxScale){
		try{
			if(minScale==null && maxScale==null)
				this.geoserverBridge.setDefaultLayerStyle(layerName, styleName, sld);
			else
				this.geoserverBridge.setDefaultLayerStyle(layerName, styleName, sld, minScale, maxScale);
			return Response.status(201).entity("Successfully set default style "+styleName +" on layer "+layerName).build();
		}
		catch(GeoServerBridgeException ex){
			return Response.status(500).entity("Could not set default style "+styleName +" on layer "+layerName).build();
		}
	}
	
	
	
	@POST
	@Path("workspaceExists")
	public Response workspaceExists(@FormParam("workspaceName")  String workspaceName){
		try{
			Boolean workspaceExists = this.geoserverBridge.workspaceExists(workspaceName);
			return Response.status(200).entity(workspaceExists).build();
		}
		catch(GeoServerBridgeException ex){
			return Response.status(500).entity(new Boolean(false)).build();
		}
	}
	
	@GET
	@Path("getGeoTIFFPublishConfig/{layerId}")
	@Produces (MediaType.APPLICATION_JSON)
	public Response getGeoTIFFPublishConfig(@PathParam("layerId") String layerId){
		String publishConfigJSON = null;
		
		try {
			publishConfigJSON = mapper.writeValueAsString(this.geoserverBridge.getGeoTIFFPublishConfig(layerId));
		} catch (Exception e) {
			return Response.status(500).build();
		}
		
		return Response.status(200).entity(publishConfigJSON).build();
	}
	
	@POST
	@Path("publishGeoTIFF")
	public Response publishGeoTIFF(@FormParam("publishConfig") String publishConfigJson, @FormParam("geotiff") String geotiffJson) throws Exception {
		try {
			PublishConfig publishConfig = mapper.readValue(publishConfigJson, PublishConfig.class);
			Coverage geotiff = mapper.readValue(geotiffJson, Coverage.class);
			
			Assert.notNull(publishConfig, "");
			Assert.notNull(geotiff, "");
			
			this.geoserverBridge.publishGeoTIFF(publishConfig, geotiff);
		} catch (GeoServerBridgeException e) {
			logger.error(null, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.status(Response.Status.CREATED).build();
	}
	
//	@GET
//	@Path("{gosIdentifier}")
//	@Produces({ MediaType.APPLICATION_JSON })
//	public Response getGeoserverUrl(@PathParam("gosIdentifier") String gosIdentifier) throws IOException {
//		String geoserverUrl = this.dataMonitor.getGeoserverUrlFor(gosIdentifier);
//		if((geoserverUrl == null) || geoserverUrl.isEmpty())
//			return Response.status(Status.NO_CONTENT).build();
//		return Response.status(200).entity(geoserverUrl).build();
//	}
	
	
	
//	@POST
//	@Path(LayerOperations.GET_SHAPES_OF_TERM)
//	@Produces({MediaType.APPLICATION_JSON})
//	public Response getShapesOfTerm(NameTaxonomyPair nameTaxonomyPair){
//		
//		List<ShapeMessenger> shapeMessengers = new ArrayList<ShapeMessenger>();
//		try {
//			shapeMessengers = this.layerOperations.getShapesOfTerm(nameTaxonomyPair.termName, nameTaxonomyPair.termTaxonomy);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
//		}
//		return Response.status(Status.OK).entity(shapeMessengers).build();
//	}
//	
//	@POST
//	@Path(LayerOperations.FIND_TERM_MAPPINGS_OF_LAYER_SHAPES)
//	@Produces({MediaType.APPLICATION_JSON})
//	public Response findTermMappingsOfLayerShapes(TaxonomyTermMessenger layerTerm){
//		
//		List<TaxonomyTermShapeMessenger> taxonomyTermShapeMessengers = new ArrayList<TaxonomyTermShapeMessenger>();
//		try {
//			taxonomyTermShapeMessengers = this.layerOperations.findTermMappingsOfLayerShapes(layerTerm);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
//		}
//		return Response.status(Status.OK).entity(taxonomyTermShapeMessengers).build();
//	}
//	
//	@POST
//	@Path(LayerOperations.GET_ATTRIBUTE_VALUES_OF_SHAPES_BY_TERM)
//	@Produces({MediaType.APPLICATION_JSON})
//	public Response getAttributeValuesOfShapesByTerm(TaxonomyTermAttributePair taxonomyTermAttributePair){
//		
//		Set<String> attributeValuesOfShapeByTerm = new HashSet<String>();
//		try {
//			attributeValuesOfShapeByTerm = this.layerOperations.getAttributeValuesOfShapesByTerm(taxonomyTermAttributePair.getLayerTerm(), taxonomyTermAttributePair.getAttr());
//		} catch (Exception e) {
//			e.printStackTrace();
//			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
//		}
//		return Response.status(Status.OK).entity(attributeValuesOfShapeByTerm).build();
//	}
//	
//	@POST
//	@Path(LayerOperations.GET_SHAPE_INFO_FOR_TERM)
//	@Produces({MediaType.APPLICATION_JSON})
//	public Response getShapeOfTerm(NameTaxonomyPair nameTaxonomyPair){
//		
//		List<ShapeInfoMessenger> ShapeInfoMessengers = new ArrayList<ShapeInfoMessenger>();
//		try {
//			ShapeInfoMessengers = this.layerOperations.getShapeInfoForTerm(nameTaxonomyPair.getTermName(), nameTaxonomyPair.getTermTaxonomy());
//		} catch (Exception e) {
//			e.printStackTrace();
//			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
//		}
//		return Response.status(Status.OK).entity(ShapeInfoMessengers).build();
//	}
//	
//	@POST
//	@Path(LayerOperations.GENERATE_SHAPE_BOUNDARY)
//	@Produces({MediaType.APPLICATION_JSON})
//	public Response getShapeOfTerm(ShapeBoundaryRequest shapeBoundaryRequesst){
//		
//		try {
//			this.layerOperations.generateShapeBoundary(shapeBoundaryRequesst.getLayerTerm(), shapeBoundaryRequesst.getBoundaryTerm(), shapeBoundaryRequesst.getUserMessenger());
//		} catch (Exception e) {
//			e.printStackTrace();
//			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
//		}
//		return Response.status(Status.OK).build();
//	}
}
