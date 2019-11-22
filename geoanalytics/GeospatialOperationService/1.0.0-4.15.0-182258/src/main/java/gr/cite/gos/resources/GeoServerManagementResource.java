package gr.cite.gos.resources;


import java.io.*;
import java.util.*;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.*;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.geoservermanager.geowebcache.GwcClient;

import gr.cite.geoanalytics.dataaccess.xml.NetCDFCoverage;
import it.geosolutions.geoserver.rest.HTTPUtils;
import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
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
import gr.cite.geoanalytics.dataaccess.geoserverbridge.exception.GeoServerBridgeException;
import gr.cite.gos.environment.EnvironmentInitializer;


@Component
@Path("/GeoserverManagement")
public class GeoServerManagementResource {
	
	private EnvironmentInitializer environmentInitializer;
	private GeoServerBridgeConfig configuration;
	private GeoServerBridge geoserverBridge;
	private GwcClient gwcClient;
	private ObjectMapper mapper = new ObjectMapper();
	
	private static final Logger logger = LoggerFactory.getLogger(GeoServerManagementResource.class);
	
	public GeoServerManagementResource(GeoServerBridge geoserverBridge, GeoServerBridgeConfig configuration, GwcClient gwcClient) throws Exception{
		this.geoserverBridge = geoserverBridge;
		this.configuration = configuration;
		this.gwcClient = gwcClient;
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
	@Path("deleteGeoserverLayer/{layerName}/{layerId}/{dataSource}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response deleteLayer(@PathParam("layerName") String layerName, @PathParam("layerId") String layerId, @PathParam("dataSource") DataSource dataSource) throws IOException {
		try {
			this.geoserverBridge.deleteLayer(layerId, layerName, dataSource);
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

	@DELETE
	@Path("coverage/{coverageId}/coveragestore/delete")
	public Response deleteCoverageStore(@PathParam("coverageId") String coverageId) {
		try {
			this.geoserverBridge.deleteCoverageStoreByCoverageId(configuration.getGeoServerBridgeWorkspace(), coverageId, true);
		} catch (GeoServerBridgeException ex) {
			return Response.status(500).entity("Could not delete coverage store  with name " + coverageId + " from geoserver").build();
		}

		return Response.status(200).entity("Coverage store with name " + coverageId + " was deleted on this geoserver").build();
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
	@Path("addStyleIcons")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response addStyleIcons(final FormDataMultiPart multiPart){
		List<FormDataBodyPart> bodyParts = multiPart.getFields("files");
		FormDataBodyPart name= multiPart.getField("name");

		for (int i = 0; i < bodyParts.size(); i++) {
			BodyPartEntity bodyPartEntity = (BodyPartEntity) bodyParts.get(i).getEntity();
			String fileName = bodyParts.get(i).getContentDisposition().getFileName();
			InputStream initialStream = null;
			OutputStream outputStream = null;

			try {
				initialStream = bodyPartEntity.getInputStream();

				File targetFile = new File(fileName);
				outputStream =
						new FileOutputStream(targetFile);

				int read = 0;
				byte[] bytes = new byte[1024];

				while ((read = initialStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}

				this.geoserverBridge.addStyleIcon(fileName, targetFile);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

			return Response.status(201).entity("Added style "+name.getValue()).build();


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
	@Path("coverage/{coverageid}/coeveragestore/exists")
	@Produces({MediaType.APPLICATION_JSON})
	public Response coveragestoreExists(@PathParam("coverageid") String coverageId) {
		try {
			if( this.geoserverBridge.coverageStoreOfCoverageByIdExists(configuration.getGeoServerBridgeWorkspace(), coverageId) )
				return Response.status(200).build();
			else
				return Response.status(404).build();
		} catch(GeoServerBridgeException e) {
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
	@Path("getLayerType/{name}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getLayerType(@PathParam("name") String name){
		try {
			String featureTypeJSON = mapper.writeValueAsString(this.geoserverBridge.getLayerType(name));
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
            System.out.println("THE STYLES:"+listStylesJSON);

			return Response.status(200).entity(listStylesJSON).build();
		} catch (GeoServerBridgeException | IOException e) {
            System.out.println("THE STYLES FAILED");
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
	@Consumes({MediaType.MULTIPART_FORM_DATA})
	public Response publishGeoTIFF(@FormDataParam("file") InputStream geotiff,
								   @FormDataParam("coverage") String fileMetaData,
								   @FormDataParam("publishConfig") String publishConfigJson) throws Exception {
		try {

			PublishConfig publishConfig = mapper.readValue(publishConfigJson, PublishConfig.class);
			Coverage coverage = mapper.readValue(fileMetaData, Coverage.class);
//			coverage.setImage(IOUtils.toByteArray(geotiff));
			Assert.notNull(publishConfig, "");
//			Assert.notNull(geotiff, "");

			this.geoserverBridge.publishGeoTIFF(publishConfig, coverage, geotiff);
		} catch (GeoServerBridgeException e) {
			logger.error(null, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.status(Response.Status.CREATED).build();
	}

	@POST
	@Path("publishNetCDF")
	@Consumes({MediaType.MULTIPART_FORM_DATA})
	public Response publishNetCDF(@FormDataParam("file") InputStream netcdf,
								   @FormDataParam("coverage") String fileMetaData,
								   @FormDataParam("publishConfig") String publishConfigJson) throws Exception {
		NetCDFPublishConfig publishConfig = null;
		try {
			publishConfig = mapper.readValue(publishConfigJson, NetCDFPublishConfig.class);

			if(publishConfig != null)
				logger.debug("publishConfig received: " + publishConfig.toString());
			else
				logger.debug("PublishConfig is null");

			logger.debug("coverage received");
			Coverage coverage = mapper.readValue(fileMetaData, Coverage.class);
			logger.info(coverage.toString());

			Assert.notNull(publishConfig, "");

			try {
				this.geoserverBridge.publishNetCDF(publishConfig, coverage, netcdf, configuration);

//				Assert.isTrue( this.createCoverageStoreForNetCDFLayers(publishConfig), "");
//				Assert.isTrue(this.publishNetCDFLayersFromZippedFile(publishConfig, netcdf), "");
//				Assert.isTrue(this.configureNetCDFLayers(publishConfig), "");
			}catch (Exception ex){
				throw new GeoServerBridgeException("Failed to publish netCDF File. Reason: ", ex);
			}
		} catch (GeoServerBridgeException e) {
			if( this.getNetCDFCoverageStore(publishConfig) )
				this.deleteNetCDFCoverageStoreAndContainingLayers(publishConfig);

			logger.error(null, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.status(Response.Status.CREATED).build();
	}

	private boolean createCoverageStoreForNetCDFLayers(NetCDFPublishConfig publishConfig) throws JAXBException {
		String layerName = publishConfig.getLayerId();
		String storeName = publishConfig.getStoreName();
		String workspace = configuration.getGeoServerBridgeWorkspace();
		String netcdfFileName = "testFile.nc";

		NetCDFCoverage nc = new NetCDFCoverage();
		nc.setEnabled(true);
		nc.setName(storeName);
		nc.setWorkspace(workspace);
		nc.setUrl("file:" + workspace + "\\/" + storeName + "\\/" + netcdfFileName);
		nc.setType("NetCDF");
		String xmlBody = this.marshall(nc);
		logger.info("XML to be send to Geoserver as part of the request: " + xmlBody);

		StringBuilder sbUrl = new StringBuilder(configuration.getGeoServerBridgeUrl()).append("/rest/workspaces/").append(workspace).append("/coveragestores?configure=all");
		logger.info("Submitting request to geoserver at URL: " + sbUrl.toString());
		String result = HTTPUtils.postXml(sbUrl.toString(), xmlBody, configuration.getGeoServerBridgeUser(), configuration.getGeoServerBridgePassword());
		logger.info("Result from request:" + result);

		return result != null;
	}

	private boolean publishNetCDFLayersFromZippedFile(NetCDFPublishConfig publishConfig, InputStream netCDFFileStream) throws IOException {
		String layerTitle = publishConfig.getLayerName();
		String storeName = publishConfig.getStoreName();
		String workspace = configuration.getGeoServerBridgeWorkspace();

		File netCDFFile = File.createTempFile(publishConfig.getLayerName() , ".zip");
		FileUtils.copyInputStreamToFile(netCDFFileStream, netCDFFile);
		netCDFFileStream.close();

		StringBuilder sbUrl = new StringBuilder(configuration.getGeoServerBridgeUrl()).append("/rest/workspaces/").append(workspace)
				.append("/coveragestores/").append(storeName).append("/file.netcdf");
		logger.info("Submitting request to geoserver at: " + sbUrl.toString());

		String result = HTTPUtils.put(sbUrl.toString(), netCDFFile, "application/zip", configuration.getGeoServerBridgeUser(), configuration.getGeoServerBridgePassword());
		logger.info("Result from request: " + result);

		if (result != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Coverage successfully created " + workspace + ":" + storeName);
			}
		} else if (logger.isErrorEnabled()) {
			logger.error("Error creating coverage " + workspace + ":" + storeName + " (" + result + ")");
		}

		return result != null;
	}

	private boolean configureNetCDFLayers(NetCDFPublishConfig publishConfig) {
		boolean result = true;
		String workspace = configuration.getGeoServerBridgeWorkspace();
		String storeName = publishConfig.getStoreName();

		Map<UUID, String> layerIdsToNames = new HashMap<UUID, String>(publishConfig.getLayeIdToNameMap());
		Iterator<Map.Entry<UUID, String>> entryIt = layerIdsToNames.entrySet().iterator();

		while( entryIt.hasNext() ) {
			Map.Entry<UUID,String> idName = entryIt.next();

			StringBuilder sbUrl = new StringBuilder(configuration.getGeoServerBridgeUrl()).append("/rest/workspaces/")
					.append(workspace).append("/coveragestores/").append(storeName).append("/coverages/").append(idName.getValue());

			String xmlBody = "<coverage><name>" + idName.getKey() + "</name></coverage>";

			String response = HTTPUtils.putXml(sbUrl.toString(), xmlBody, configuration.getGeoServerBridgeUser(), configuration.getGeoServerBridgePassword());
			logger.info("Result from request:" + result);

			result = result && response != null;
		}

		return result;
	}

	private  boolean deleteNetCDFCoverageStoreAndContainingLayers(NetCDFPublishConfig publishConfig) {
		String workspace = configuration.getGeoServerBridgeWorkspace();
		String storeName = publishConfig.getStoreName();

		StringBuilder sbUrl = new StringBuilder(configuration.getGeoServerBridgeUrl()).append("/rest/workspaces/")
				.append(workspace).append("/coveragestores/").append(storeName).append("?recurse=true&purge=true");

		boolean result = HTTPUtils.delete(sbUrl.toString(), configuration.getGeoServerBridgeUser(), configuration.getGeoServerBridgePassword());

		return result;
	}

	private boolean getNetCDFCoverageStore(NetCDFPublishConfig publishConfig) {
		StringBuilder sbUrl = new StringBuilder(configuration.getGeoServerBridgeUrl()).append("/rest/workspaces/")
				.append(configuration.getGeoServerBridgeWorkspace()).append("/coveragestores/")
				.append(publishConfig.getStoreName()).append("?recurse=true&purge=true");

		String result = HTTPUtils.get(sbUrl.toString(), configuration.getGeoServerBridgeUser(), configuration.getGeoServerBridgePassword());

		return result != null;
	}

	private String marshall(Object xmlEntity) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(xmlEntity.getClass());
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		StringWriter sw = new StringWriter();
		m.marshal(xmlEntity, sw);

		return sw.toString();
	}


	@POST
	@Path("seedGeoTIFF/{layerName}")
	public Response seedGeoTIFFToGCW(@FormParam("seedRequest") String seedRequest, @PathParam("layerName") String layerName) throws Exception {
		try {
			Assert.notNull(seedRequest, "");
			logger.debug("Seed request received for layer:" + layerName);
			this.gwcClient.seedGeoTIFF(seedRequest, layerName);

		} catch (Exception e) {
			logger.error(null, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.status(Response.Status.OK).build();
	}

	@GET
	@Path("getCachedLayers")
    public Response getCachedLayers()  {
        try {
			logger.debug("Request received for cached layers");
			return this.gwcClient.getCachedLayers();

		} catch (Exception e) {
			logger.error(null, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
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
