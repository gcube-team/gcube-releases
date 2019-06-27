package gr.cite.gos.client;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLEventReader;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import gr.cite.gaap.datatransferobjects.LayerBean;
import gr.cite.gaap.datatransferobjects.LayerGwcAtomXML;
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.*;
import org.glassfish.jersey.client.ClientResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import gr.cite.gaap.datatransferobjects.StyleMessenger;
import gr.cite.geoanalytics.dataaccess.entities.coverage.Coverage;
import gr.cite.geoanalytics.dataaccess.entities.layer.DataSource;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.SystemPresentationConfig;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class GeoserverManagement extends GosManagement {
	private static final Logger logger = LoggerFactory.getLogger(GeoserverManagement.class);
//	private static final XmlMapper xmlMapper = new XmlMapper();

	public GeoserverManagement(String authenticationStr) {
		super(authenticationStr);
	}

	public boolean initializeEnvironment(String gosEndpoint, List<LayerConfig> layerConfigs, SystemPresentationConfig systemPresentationConfig, List<StyleMessenger> styles) throws IOException{
		MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		formData.add("layerConfigsJSON", getMapper().writeValueAsString(layerConfigs));
		formData.add("systemPresentationConfigJSON", getMapper().writeValueAsString(systemPresentationConfig));
		formData.add("stylesJSON", getMapper().writeValueAsString(styles));

		Invocation.Builder invocationBuilder = getJerseyClient().target(gosEndpoint).
				path("/GeoserverManagement/initializeEnvironment").request( MediaType.APPLICATION_JSON);
		Response resp = invocationBuilder.post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		logger.info("Response status: " + resp.getStatus());

		return (resp.getStatus()==200);
	}
	
	
	public List<GeoserverLayer> getGeoserverLayers(String gosEndpoint) throws IOException{
		
		String geoserverLayersJSON = getJerseyClient().target(gosEndpoint)
    			.path("/GeoserverManagement/getGeoserverLayers")
    			.request(MediaType.APPLICATION_JSON)
    			.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr)
    			.get(String.class);
    	TypeReference<List<GeoserverLayer>> mappingType = new TypeReference<List<GeoserverLayer>>() {};
		return (List<GeoserverLayer>)getMapper().readValue(geoserverLayersJSON, mappingType);
	}



	public boolean deleteGeoserverLayer(String gosEndpoint, String layerName, String layerId, DataSource dataSource) throws IOException{
		Invocation.Builder invocationBuilder = getJerseyClient().target(gosEndpoint)
				.path("/GeoserverManagement/deleteGeoserverLayer/"+ layerName + "/" + layerId + "/" + dataSource)
				.request(MediaType.APPLICATION_JSON);
		Response resp = invocationBuilder.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr).delete();

		return (resp.getStatus()==200);
	}

	public boolean deleteGeoserverCoverageStoreByCoverageId(String gosEndpoint, String coverageId) {
		Invocation.Builder invocationBuilder = getJerseyClient().target(gosEndpoint)
				.path("/GeoserverManagement/coverage/" + coverageId + "/coveragestore/delete/")
				.request(MediaType.APPLICATION_JSON);
		Response resp = invocationBuilder.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr).delete();

		return (resp.getStatus()==200);
	}

	public boolean geoserverCoverageStoreOfCoverageExists(String gosEndpoint, String coveragestoreId) {
		Invocation.Builder invocationBuilder = getJerseyClient().target(gosEndpoint)
				.path("/GeoserverManagement/coverage/" + coveragestoreId + "/coeveragestore/exists")
				.request(MediaType.APPLICATION_JSON);
		Response resp = invocationBuilder.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr).get();

		return (resp.getStatus()==200);
	}
	
	public boolean addDataStore(String gosEndpoint, DataStore datastore) throws IOException{
		MultivaluedMap<String, String> formData = new MultivaluedHashMap();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		formData.add("dataStoreJSON", getMapper().writeValueAsString(datastore));
		Response resp = getJerseyClient().target(gosEndpoint)
			.path("/GeoserverManagement/addDataStore")
    		.request(MediaType.APPLICATION_JSON)
    		.post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		return (resp.getStatus()==201);
	}
	
	
	public boolean addGeoserverLayer(String gosEndpoint, LayerConfig layerConfig, Map<String,String> slds, String crs, String style) throws IOException{
		
		MultivaluedMap<String,String> formData = new MultivaluedHashMap();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		formData.add("layerConfigJSON", getMapper().writeValueAsString(layerConfig));
		formData.add("slds", getMapper().writeValueAsString(slds));
		formData.add("crs", crs);
		formData.add("style", style);
		Response resp = getJerseyClient().target(gosEndpoint)
			.path("/GeoserverManagement/addGeoserverLayerFromConfig")
    		.request(MediaType.APPLICATION_JSON)
    		.post(Entity.entity(formData,MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		return (resp.getStatus()==201);
	}
	
	
	public boolean addGeoserverLayer(String gosEndpoint, GeoserverLayer geoserverLayer, FeatureType ft, Map<String,String> slds, Integer minScale, Integer maxScale) throws IOException{
        MultivaluedMap<String,String> formData = new MultivaluedHashMap<>();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		formData.add("geoserverLayer", getMapper().writeValueAsString(geoserverLayer));
		formData.add("ft", getMapper().writeValueAsString(ft));
		formData.add("slds", getMapper().writeValueAsString(slds));
		if(minScale!=null) formData.add("minScale", String.valueOf(minScale));
		if(maxScale!=null) formData.add("maxScale", String.valueOf(maxScale));
		Response resp = getJerseyClient().target(gosEndpoint)
			.path("/GeoserverManagement/addGeoserverLayer")
    		.request(MediaType.APPLICATION_JSON)
    		.post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		return (resp.getStatus()==201);
	}
	
	public boolean addGeoserverLayer(String gosEndpoint, GeoserverLayer geoserverLayer, FeatureType ft, Map<String,String> slds) throws IOException{
		return addGeoserverLayer(gosEndpoint, geoserverLayer, ft, slds, null, null);
	}
	

	
	
	public boolean addLayerStyle(String gosEndpoint, String layerName, String styleName, String sld, Integer minScale, Integer maxScale) throws IOException{
        MultivaluedMap<String,String> formData = new MultivaluedHashMap<>();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		formData.add("layerName", layerName);
		formData.add("styleName", styleName);
		formData.add("sld", sld);
		if(minScale!=null) formData.add("minScale", String.valueOf(minScale));
		if(maxScale!=null) formData.add("maxScale", String.valueOf(maxScale));
		Invocation.Builder invocationBuilder  = getJerseyClient().target(gosEndpoint)
			.path("/GeoserverManagement/addLayerStyle")
    		.request(MediaType.APPLICATION_JSON);
		Response resp = invocationBuilder.post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		return (resp.getStatus()==201);
	}
	
	public boolean addLayerStyle(String gosEndpoint, String layerName, String styleName, String sld) throws IOException{
		return addLayerStyle(gosEndpoint, layerName, styleName, sld, null, null);
	}
	
	

	public boolean addStyle(String gosEndpoint, String styleName, String sld, Integer minScale, Integer maxScale) throws IOException{
        MultivaluedMap<String,String> formData = new MultivaluedHashMap<>();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		formData.add("styleName", styleName);
		formData.add("sld", sld);
		if(minScale!=null) formData.add("minScale", String.valueOf(minScale));
		if(maxScale!=null) formData.add("maxScale", String.valueOf(maxScale));

		Invocation.Builder invocationBuilder  = getJerseyClient().target(gosEndpoint)
				.path("/GeoserverManagement/addStyle")
				.request(MediaType.APPLICATION_JSON);
		Response resp = invocationBuilder.post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		return (resp.getStatus()==201);
	}

	public boolean addStyleIcons(String gosEndpoint, String styleName, List<MultipartFile> icons){
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);
		headers.set(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
		try {
			for (MultipartFile file : icons) {
				File temp = new File(file.getOriginalFilename());
				file.transferTo(temp);
				formData.add("files", new FileSystemResource(temp));
			}
		}
		catch (IOException e) {
			logger.error("Error in conversion file icon");
			e.printStackTrace();
		}
		formData.add("name", styleName);
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(formData, headers);
			ResponseEntity<String> response = restTemplate.postForEntity( gosEndpoint+"/GeoserverManagement/addStyleIcons", request, String.class );
			return (response.getStatusCode().equals(HttpStatus.CREATED));
		}
		catch (Exception e) {
			logger.error("Error sending icons to Gos");
			e.printStackTrace();
			return false;
		}
	}

	public boolean addStyle(String gosEndpoint, String styleName, String sld) throws IOException{
		return addStyle(gosEndpoint, styleName, sld, null, null);
	}


	
	public boolean addWorkspace(String gosEndpoint, String name, String uri) throws IOException{
        MultivaluedMap<String,String> formData = new MultivaluedHashMap<>();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		formData.add("styleName", name);
		formData.add("sld", uri);

		Invocation.Builder invocationBuilder  = getJerseyClient().target(gosEndpoint)
				.path("/GeoserverManagement/addWorkspace")
				.request(MediaType.APPLICATION_JSON);
		Response resp = invocationBuilder.post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		return (resp.getStatus()==201);
	}
	
	
	public boolean dataStoreExists(String gosEndpoint, String workspaceName, String dataStoreName) throws IOException{
        MultivaluedMap<String,String> formData = new MultivaluedHashMap<>();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		formData.add("workspaceName", workspaceName);
		formData.add("dataStoreName", dataStoreName);

		Invocation.Builder invocationBuilder  = getJerseyClient().target(gosEndpoint)
				.path("/GeoserverManagement/dataStoreExists")
				.request(MediaType.APPLICATION_JSON);
		Response resp = invocationBuilder.post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		return (resp.getStatus()==200);
	}
	
	
	public DataStore getDataStore(String gosEndpoint, String dataStoreName) throws IOException{
		Response resp = getJerseyClient().target(gosEndpoint)
				.path("/GeoserverManagement/getDataStore/"+dataStoreName)
				.request(MediaType.APPLICATION_JSON)
				.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr)
    			.get();
		return getMapper().readValue(resp.readEntity(String.class), DataStore.class);
	}
	
	public FeatureType getFeatureType(String gosEndpoint, String name) throws IOException{
		Response resp = getJerseyClient().target(gosEndpoint)
				.path("/GeoserverManagement/getFeatureType/"+name)
				.request(MediaType.APPLICATION_JSON)

				.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr).get();
		return getMapper().readValue(resp.readEntity(String.class), FeatureType.class);
	}
	
	public LayerType getLayerType(String gosEndpoint, String name) throws IOException{
		Response resp = getJerseyClient().target(gosEndpoint)
				.path("/GeoserverManagement/getLayerType/"+name)
				.request(MediaType.APPLICATION_JSON)
				.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr)
				.get();
		return getMapper().readValue(resp.readEntity(String.class), LayerType.class);
	}
	
	public GeoserverLayer getGeoserverLayer(String gosEndpoint, String name) throws IOException{
		Response resp = getJerseyClient().target(gosEndpoint)
				.path("/GeoserverManagement/getGeoserverLayer/"+name)
				.request(MediaType.APPLICATION_JSON)
				.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr)
    			.get();
		return getMapper().readValue(resp.readEntity(String.class), GeoserverLayer.class);
	}
	
	
	
	
	public List<GeoserverLayer> getGeoserverLayers(String gosEndpoint, String datastoreName) throws IOException{
		String geoserverLayersJSON = getJerseyClient().target(gosEndpoint)
    			.path("/GeoserverManagement/getGeoserverLayers/"+datastoreName)
				.request(MediaType.APPLICATION_JSON)
				.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr)
    			.get(String.class);
    	TypeReference<List<GeoserverLayer>> mappingType = new TypeReference<List<GeoserverLayer>>() {};
		return (List<GeoserverLayer>)getMapper().readValue(geoserverLayersJSON, mappingType);
	}
	
	
	
	
	
	public String getStyle(String gosEndpoint, String name) throws IOException{
		Response resp = getJerseyClient().target(gosEndpoint)
				.path("/GeoserverManagement/getStyle/"+name)
				.request(MediaType.APPLICATION_JSON)
				.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr)
    			.get();
		return resp.readEntity(String.class);
	}
	
	
	public List<String> getAllStyles(String gosEndpoint) throws IOException{
		String stylesJSON = getJerseyClient().target(gosEndpoint)
    			.path("/GeoserverManagement/getAllStyles")
				.request(MediaType.APPLICATION_JSON)
				.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr)
    			.get(String.class);
    	TypeReference<List<String>> mappingType = new TypeReference<List<String>>() {};
		return (List<String>)getMapper().readValue(stylesJSON, mappingType);
	}
	
	
	public List<String> listDataStores(String gosEndpoint) throws IOException{
		String datastoresJSON = getJerseyClient().target(gosEndpoint)
    			.path("/GeoserverManagement/listDataStores")
				.request(MediaType.APPLICATION_JSON)
				.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr)
    			.get(String.class);
    	TypeReference<List<String>> mappingType = new TypeReference<List<String>>() {};
		return (List<String>)getMapper().readValue(datastoresJSON, mappingType);
	}
	
	

	public List<GeoserverLayer> listLayersOfDataStore(String gosEndpoint, String datastoreName) throws IOException{
		String geoserverLayersJSON = getJerseyClient().target(gosEndpoint)
    			.path("/GeoserverManagement/listLayersOfDataStore/"+datastoreName)
				.request(MediaType.APPLICATION_JSON)

				.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr)
    			.get(String.class);
    	TypeReference<List<GeoserverLayer>> mappingType = new TypeReference<List<GeoserverLayer>>() {};
		return (List<GeoserverLayer>)getMapper().readValue(geoserverLayersJSON, mappingType);
	}
	

	
	public boolean removeLayerStyle(String gosEndpoint, String layerName, String styleName, Integer minScale, Integer maxScale) throws IOException{
		MultivaluedMap<String,String> formData = new MultivaluedHashMap<>();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		formData.add("layerName", layerName);
		formData.add("styleName", styleName);
		if(minScale!=null) formData.add("minScale", String.valueOf(minScale));
		if(maxScale!=null) formData.add("maxScale", String.valueOf(maxScale));

		Invocation.Builder invocationBuilder  = getJerseyClient().target(gosEndpoint)
				.path("/GeoserverManagement/removeLayerStyle")
				.request(MediaType.APPLICATION_JSON);
		Response resp = invocationBuilder.post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		return (resp.getStatus()==201);
	}
	
	
	public boolean removeStyle(String gosEndpoint, String styleName, Integer minScale, Integer maxScale) throws IOException{
        MultivaluedMap<String,String> formData = new MultivaluedHashMap<>();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		formData.add("styleName", styleName);
		if(minScale!=null) formData.add("minScale", String.valueOf(minScale));
		if(maxScale!=null) formData.add("maxScale", String.valueOf(maxScale));

		Invocation.Builder invocationBuilder  = getJerseyClient().target(gosEndpoint)
				.path("/GeoserverManagement/removeStyle")
				.request(MediaType.APPLICATION_JSON);
		Response resp = invocationBuilder.post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		return (resp.getStatus()==201);
	}
	
	
	public boolean removeStyle(String gosEndpoint, String styleName) throws IOException{
		return removeStyle(gosEndpoint, styleName, null, null);
	}
	
	
	public boolean setDefaultLayerStyle(String gosEndpoint, String layerName, String styleName, String sld, Integer minScale, Integer maxScale) throws IOException{
        MultivaluedMap<String,String> formData = new MultivaluedHashMap<>();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		formData.add("layerName", layerName);
		formData.add("styleName", styleName);
		formData.add("sld", sld);
		if(minScale!=null) formData.add("minScale", String.valueOf(minScale));
		if(maxScale!=null) formData.add("maxScale", String.valueOf(maxScale));
		Invocation.Builder invocationBuilder  = getJerseyClient().target(gosEndpoint)
				.path("/GeoserverManagement/setDefaultLayerStyle")
				.request(MediaType.APPLICATION_JSON);
		Response resp = invocationBuilder.post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		return (resp.getStatus()==201);
	}
	
	
	public boolean workspaceExists(String gosEndpoint, String workspaceName) throws IOException{
        MultivaluedMap<String,String> formData = new MultivaluedHashMap<>();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		formData.add("workspaceName", workspaceName);

		Invocation.Builder invocationBuilder  = getJerseyClient().target(gosEndpoint)
				.path("/GeoserverManagement/workspaceExists")
				.request(MediaType.APPLICATION_JSON);
		Response resp = invocationBuilder.post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		return resp.readEntity(Boolean.class).booleanValue();
	}
	
	public PublishConfig getGeoTIFFPublishConfig(String gosEndpoint, String layerId) throws Exception {

		ClientResponse resp = getJerseyClient().target(gosEndpoint)
				.path("/GeoserverManagement/getGeoTIFFPublishConfig/"+layerId)
				.request(MediaType.APPLICATION_JSON)
				.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr)
	    		.get(ClientResponse.class);
		return getMapper().readValue(resp.readEntity(String.class), PublishConfig.class);
	}
	
	public void publishGeoTIFF(String gosEndpoint, PublishConfig publishConfig, Coverage coverage) throws Exception{
        MultivaluedMap<String,String> formData = new MultivaluedHashMap<>();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		formData.add("publishConfig", getMapper().writeValueAsString(publishConfig));
		formData.add("geotiff", getMapper().writeValueAsString(coverage));

		Invocation.Builder invocationBuilder  = getJerseyClient().target(gosEndpoint)
				.path("/GeoserverManagement/publishGeoTIFF")
				.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE));



		if (response.getStatus() != CREATED.getStatusCode()) {
			throw new Exception();
		}
	}

	public void publishGeoTIFF(String gosEndpoint, PublishConfig publishConfig, Coverage coverage, InputStream geotiff) throws Exception{

		FormDataBodyPart filePart = new FormDataBodyPart("file", geotiff, MediaType.APPLICATION_OCTET_STREAM_TYPE);

		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
		final FormDataMultiPart formData = (FormDataMultiPart) formDataMultiPart
				.field(	"publishConfig", getMapper().writeValueAsString(publishConfig))
				.field("coverage", getMapper().writeValueAsString(coverage))
				.bodyPart(filePart);

		Invocation.Builder invocationBuilder  = getJerseyClient().target(gosEndpoint)
				.path("/GeoserverManagement/publishGeoTIFF")
				.request(MediaType.APPLICATION_JSON)
				.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		Response response = invocationBuilder.post(Entity.entity(formData, MediaType.MULTIPART_FORM_DATA));

		formDataMultiPart.close();
		formData.close();
		if (response.getStatus() != CREATED.getStatusCode()) {
			throw new Exception();
		}
	}

	/** publish geotiff in replication **/
	public void publishGeoTIFFStream(String gosEndpoint, PublishConfig publishConfig, InputStream inputStream) throws Exception{

		String sContentDisposition = "attachment; config=\"" + getMapper().writeValueAsString(publishConfig)+"\"";

		Invocation.Builder invocationBuilder  = getJerseyClient().target(gosEndpoint)
				.path("/GeoserverManagement/publishGeoTIFF/stream")
				.request().header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		Response response = invocationBuilder.post(Entity.entity(inputStream, MediaType.APPLICATION_OCTET_STREAM));

		if (response.getStatus() != CREATED.getStatusCode()) {
			throw new Exception();
		}
	}

	public void publishNetCDF(String gosEndpoint, NetCDFPublishConfig publishConfig, Coverage coverage, InputStream netCDF) throws Exception{

		FormDataBodyPart filePart = new FormDataBodyPart("file", netCDF, MediaType.APPLICATION_OCTET_STREAM_TYPE);

		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
		final FormDataMultiPart formData = (FormDataMultiPart) formDataMultiPart
				.field(	"publishConfig", getMapper().writeValueAsString(publishConfig))
				.field("coverage", getMapper().writeValueAsString(coverage))
				.bodyPart(filePart);

		Invocation.Builder invocationBuilder  = getJerseyClient().target(gosEndpoint)
				.path("/GeoserverManagement/publishNetCDF")
				.request(MediaType.APPLICATION_JSON)
				.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		Response response = invocationBuilder.post(Entity.entity(formData, MediaType.MULTIPART_FORM_DATA));

		formDataMultiPart.close();
		formData.close();
		formData.getBodyParts().forEach(bp -> bp.cleanup());
		if (response.getStatus() != CREATED.getStatusCode()) {
			throw new Exception();
		}
	}

	/** publish geotiff in replication **/
	public void publishNetCDFStream(String gosEndpoint, PublishConfig publishConfig, InputStream inputStream) throws Exception{

		String sContentDisposition = "attachment; config=\"" + getMapper().writeValueAsString(publishConfig)+"\"";

		Invocation.Builder invocationBuilder  = getJerseyClient().target(gosEndpoint)
				.path("/GeoserverManagement/publishNetCDF/stream")
				.request().header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		Response response = invocationBuilder.post(Entity.entity(inputStream, MediaType.APPLICATION_OCTET_STREAM));

		if (response.getStatus() != CREATED.getStatusCode()) {
			throw new Exception();
		}
	}

	public void seedGeoTIFFLayerGeoWebCache(String gosEndpoint, String seedRequest, String layerName) {

		logger.debug("Sending seed request to gos");
        MultivaluedMap<String,String> multipart = new MultivaluedHashMap<>();
		multipart.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		multipart.add("seedRequest", seedRequest);

		Invocation.Builder invocationBuilder  = getJerseyClient().target(gosEndpoint)
				.path("/GeoserverManagement/seedGeoTIFF/" + layerName)
				.request().header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		Response response = invocationBuilder.post(Entity.entity(multipart, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		if (response.getStatus() != OK.getStatusCode()) {
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public List<String> getCachedLayers(String gosEndpoint) {
        MultivaluedMap<String,String> formData = new MultivaluedHashMap<>();
        formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
//		System.out.println("header:"+super.HEADER_AUTHENTICATION_PARAM_NAME + ":"+authenticationStr);
		Response response = getJerseyClient().target(gosEndpoint)
				.path("/GeoserverManagement/getCachedLayers")
				.request(MediaType.APPLICATION_XML)
				.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr)
				.get();
		TypeReference<List<String>> mappingType = new TypeReference<List<String>>() {};
		String responseString = response.readEntity(String.class);

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(LayerGwcAtomXML.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			//We had written this file in marshalling example
			LayerGwcAtomXML layers = (LayerGwcAtomXML) jaxbUnmarshaller.unmarshal( new StringReader(responseString) );
			List<String> res = layers.getLayers().stream().map(LayerBean::getName)
					.collect(Collectors.toList());
//			res.stream().forEach(p -> System.out.println(p));
			return  res;

		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
	}

	public FeatureType getLayerAttributesByLayerID(UUID layerID, String gosEndpoint) throws IOException {
        MultivaluedMap<String,String> formData = new MultivaluedHashMap<>();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		formData.add("layerID", getMapper().writeValueAsString(layerID.toString()));

		Invocation.Builder invocationBuilder  = getJerseyClient().target(gosEndpoint)
				.path("/GeoserverManagement/getLayerAttributesByLayerID/")
				.request();
		Response resp = invocationBuilder.post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE));


		return getMapper().readValue(resp.readEntity(String.class), FeatureType.class);
	}

	private static Document convertStringToXMLDocument(String xmlString) {
		//Parser that produces DOM object trees from XML content
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		//API to obtain DOM Document instance
		DocumentBuilder builder = null;
		try
		{
			//Create DocumentBuilder with default configuration
			builder = factory.newDocumentBuilder();

			//Parse the content to Document object
			Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
			return doc;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static String inputStreamToString(InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();
		return sb.toString();
	}
}
