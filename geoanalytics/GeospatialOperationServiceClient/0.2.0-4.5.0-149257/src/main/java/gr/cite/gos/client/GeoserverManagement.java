package gr.cite.gos.client;

import static javax.ws.rs.core.Response.Status.CREATED;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import gr.cite.gaap.datatransferobjects.StyleMessenger;
import gr.cite.geoanalytics.dataaccess.entities.coverage.Coverage;
import gr.cite.geoanalytics.dataaccess.entities.layer.DataSource;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.SystemPresentationConfig;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.DataStore;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.FeatureType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.GeoserverLayer;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.PublishConfig;

public class GeoserverManagement extends GosManagement {

	public GeoserverManagement(String authenticationStr) {
		super(authenticationStr);
	}

	public boolean initializeEnvironment(String gosEndpoint, List<LayerConfig> layerConfigs, SystemPresentationConfig systemPresentationConfig, List<StyleMessenger> styles) throws IOException{
		MultivaluedMap formData = new MultivaluedMapImpl();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		formData.add("layerConfigsJSON", getMapper().writeValueAsString(layerConfigs));
		formData.add("systemPresentationConfigJSON", getMapper().writeValueAsString(systemPresentationConfig));
		formData.add("stylesJSON", getMapper().writeValueAsString(styles));
		
		ClientResponse resp = getJerseyClient().resource(gosEndpoint)
			.path("/GeoserverManagement/initializeEnvironment")
    		.accept(MediaType.APPLICATION_JSON)
    		.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
    		.post(ClientResponse.class, formData);
		return (resp.getStatus()==200);
	}
	
	
	public List<GeoserverLayer> getGeoserverLayers(String gosEndpoint) throws IOException{
		
		String geoserverLayersJSON = getJerseyClient().resource(gosEndpoint)
    			.path("/GeoserverManagement/getGeoserverLayers")
    			.accept(MediaType.APPLICATION_JSON)
    			.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr)
    			.get(String.class);
    	TypeReference<List<GeoserverLayer>> mappingType = new TypeReference<List<GeoserverLayer>>() {};
		return (List<GeoserverLayer>)getMapper().readValue(geoserverLayersJSON, mappingType);
	}
	
	
	
	public boolean deleteGeoserverLayer(String gosEndpoint, String layerId, DataSource dataSource) throws IOException{
		ClientResponse resp = getJerseyClient().resource(gosEndpoint)
				.path("/GeoserverManagement/deleteGeoserverLayer/" + layerId + "/" + dataSource)
    			.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr)
    			.accept(MediaType.APPLICATION_JSON)
    			.delete(ClientResponse.class);
		return (resp.getStatus()==200);
	}
	
	
	public boolean addDataStore(String gosEndpoint, DataStore datastore) throws IOException{
		MultivaluedMap formData = new MultivaluedMapImpl();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		formData.add("dataStoreJSON", getMapper().writeValueAsString(datastore));
		ClientResponse resp = getJerseyClient().resource(gosEndpoint)
			.path("/GeoserverManagement/addDataStore")
    		.accept(MediaType.APPLICATION_JSON)
    		.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
    		.post(ClientResponse.class, formData);
		return (resp.getStatus()==201);
	}
	
	
	public boolean addGeoserverLayer(String gosEndpoint, LayerConfig layerConfig, Map<String,String> slds, String crs, String style) throws IOException{
		
		MultivaluedMap formData = new MultivaluedMapImpl();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		formData.add("layerConfigJSON", getMapper().writeValueAsString(layerConfig));
		formData.add("slds", getMapper().writeValueAsString(slds));
		formData.add("crs", crs);
		formData.add("style", style);
		ClientResponse resp = getJerseyClient().resource(gosEndpoint)
			.path("/GeoserverManagement/addGeoserverLayerFromConfig")
    		.accept(MediaType.APPLICATION_JSON)
    		.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
    		.post(ClientResponse.class, formData);
		return (resp.getStatus()==201);
	}
	
	
	public boolean addGeoserverLayer(String gosEndpoint, GeoserverLayer geoserverLayer, FeatureType ft, Map<String,String> slds, Integer minScale, Integer maxScale) throws IOException{
		MultivaluedMap formData = new MultivaluedMapImpl();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		formData.add("geoserverLayer", getMapper().writeValueAsString(geoserverLayer));
		formData.add("ft", getMapper().writeValueAsString(ft));
		formData.add("slds", getMapper().writeValueAsString(slds));
		if(minScale!=null) formData.add("minScale", minScale);
		if(maxScale!=null) formData.add("maxScale", maxScale);
		ClientResponse resp = getJerseyClient().resource(gosEndpoint)
			.path("/GeoserverManagement/addGeoserverLayer")
    		.accept(MediaType.APPLICATION_JSON)
    		.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
    		.post(ClientResponse.class, formData);
		return (resp.getStatus()==201);
	}
	
	public boolean addGeoserverLayer(String gosEndpoint, GeoserverLayer geoserverLayer, FeatureType ft, Map<String,String> slds) throws IOException{
		return addGeoserverLayer(gosEndpoint, geoserverLayer, ft, slds, null, null);
	}
	

	
	
	public boolean addLayerStyle(String gosEndpoint, String layerName, String styleName, String sld, Integer minScale, Integer maxScale) throws IOException{
		MultivaluedMap formData = new MultivaluedMapImpl();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		formData.add("layerName", layerName);
		formData.add("styleName", styleName);
		formData.add("sld", sld);
		if(minScale!=null) formData.add("minScale", minScale);
		if(maxScale!=null) formData.add("maxScale", maxScale);
		ClientResponse resp = getJerseyClient().resource(gosEndpoint)
			.path("/GeoserverManagement/addLayerStyle")
    		.accept(MediaType.APPLICATION_JSON)
    		.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
    		.post(ClientResponse.class, formData);
		return (resp.getStatus()==201);
	}
	
	public boolean addLayerStyle(String gosEndpoint, String layerName, String styleName, String sld) throws IOException{
		return addLayerStyle(gosEndpoint, layerName, styleName, sld, null, null);
	}
	
	

	public boolean addStyle(String gosEndpoint, String styleName, String sld, Integer minScale, Integer maxScale) throws IOException{
		MultivaluedMap formData = new MultivaluedMapImpl();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		formData.add("styleName", styleName);
		formData.add("sld", sld);
		if(minScale!=null) formData.add("minScale", minScale);
		if(maxScale!=null) formData.add("maxScale", maxScale);
		ClientResponse resp = getJerseyClient().resource(gosEndpoint)
			.path("/GeoserverManagement/addStyle")
    		.accept(MediaType.APPLICATION_JSON)
    		.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
    		.post(ClientResponse.class, formData);
		return (resp.getStatus()==201);
	}
	
	public boolean addStyle(String gosEndpoint, String styleName, String sld) throws IOException{
		return addStyle(gosEndpoint, styleName, sld, null, null);
	}
	
	
	public boolean addWorkspace(String gosEndpoint, String name, String uri) throws IOException{
		MultivaluedMap formData = new MultivaluedMapImpl();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		formData.add("styleName", name);
		formData.add("sld", uri);
		ClientResponse resp = getJerseyClient().resource(gosEndpoint)
			.path("/GeoserverManagement/addWorkspace")
    		.accept(MediaType.APPLICATION_JSON)
    		.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
    		.post(ClientResponse.class, formData);
		return (resp.getStatus()==201);
	}
	
	
	public boolean dataStoreExists(String gosEndpoint, String workspaceName, String dataStoreName) throws IOException{
		MultivaluedMap formData = new MultivaluedMapImpl();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		formData.add("workspaceName", workspaceName);
		formData.add("dataStoreName", dataStoreName);
		ClientResponse resp = getJerseyClient().resource(gosEndpoint)
			.path("/GeoserverManagement/dataStoreExists")
    		.accept(MediaType.APPLICATION_JSON)
    		.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
    		.post(ClientResponse.class, formData);
		return (resp.getStatus()==200);
	}
	
	
	public DataStore getDataStore(String gosEndpoint, String dataStoreName) throws IOException{
		ClientResponse resp = getJerseyClient().resource(gosEndpoint)
			.path("/GeoserverManagement/getDataStore/"+dataStoreName)
			.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr)
    		.accept(MediaType.APPLICATION_JSON)
    		.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
    		.get(ClientResponse.class);
		return getMapper().readValue(resp.getEntity(String.class), DataStore.class);
	}
	
	
	
	
	
	public FeatureType getFeatureType(String gosEndpoint, String name) throws IOException{
		ClientResponse resp = getJerseyClient().resource(gosEndpoint)
			.path("/GeoserverManagement/getFeatureType/"+name)
			.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr)
    		.accept(MediaType.APPLICATION_JSON)
    		.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
    		.get(ClientResponse.class);
		return getMapper().readValue(resp.getEntity(String.class), FeatureType.class);
	}
	
	
	
	
	public GeoserverLayer getGeoserverLayer(String gosEndpoint, String name) throws IOException{
		ClientResponse resp = getJerseyClient().resource(gosEndpoint)
			.path("/GeoserverManagement/getGeoserverLayer/"+name)
			.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr)
    		.accept(MediaType.APPLICATION_JSON)
    		.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
    		.get(ClientResponse.class);
		return getMapper().readValue(resp.getEntity(String.class), GeoserverLayer.class);
	}
	
	
	
	
	public List<GeoserverLayer> getGeoserverLayers(String gosEndpoint, String datastoreName) throws IOException{
		String geoserverLayersJSON = getJerseyClient().resource(gosEndpoint)
    			.path("/GeoserverManagement/getGeoserverLayers/"+datastoreName)
    			.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr)
    			.accept(MediaType.APPLICATION_JSON)
    			.get(String.class);
    	TypeReference<List<GeoserverLayer>> mappingType = new TypeReference<List<GeoserverLayer>>() {};
		return (List<GeoserverLayer>)getMapper().readValue(geoserverLayersJSON, mappingType);
	}
	
	
	
	
	
	public String getStyle(String gosEndpoint, String name) throws IOException{
		ClientResponse resp = getJerseyClient().resource(gosEndpoint)
			.path("/GeoserverManagement/getStyle/"+name)
			.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr)
    		.accept(MediaType.APPLICATION_JSON)
    		.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
    		.get(ClientResponse.class);
		return resp.getEntity(String.class);
	}
	
	
	public List<String> getAllStyles(String gosEndpoint) throws IOException{
		String stylesJSON = getJerseyClient().resource(gosEndpoint)
    			.path("/GeoserverManagement/getAllStyles")
    			.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr)
    			.accept(MediaType.APPLICATION_JSON)
    			.get(String.class);
    	TypeReference<List<String>> mappingType = new TypeReference<List<String>>() {};
		return (List<String>)getMapper().readValue(stylesJSON, mappingType);
	}
	
	
	public List<String> listDataStores(String gosEndpoint) throws IOException{
		String datastoresJSON = getJerseyClient().resource(gosEndpoint)
    			.path("/GeoserverManagement/listDataStores")
    			.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr)
    			.accept(MediaType.APPLICATION_JSON)
    			.get(String.class);
    	TypeReference<List<String>> mappingType = new TypeReference<List<String>>() {};
		return (List<String>)getMapper().readValue(datastoresJSON, mappingType);
	}
	
	

	public List<GeoserverLayer> listLayersOfDataStore(String gosEndpoint, String datastoreName) throws IOException{
		String geoserverLayersJSON = getJerseyClient().resource(gosEndpoint)
    			.path("/GeoserverManagement/listLayersOfDataStore/"+datastoreName)
    			.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr)
    			.accept(MediaType.APPLICATION_JSON)
    			.get(String.class);
    	TypeReference<List<GeoserverLayer>> mappingType = new TypeReference<List<GeoserverLayer>>() {};
		return (List<GeoserverLayer>)getMapper().readValue(geoserverLayersJSON, mappingType);
	}
	

	
	public boolean removeLayerStyle(String gosEndpoint, String layerName, String styleName, Integer minScale, Integer maxScale) throws IOException{
		MultivaluedMap formData = new MultivaluedMapImpl();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		formData.add("layerName", layerName);
		formData.add("styleName", styleName);
		if(minScale!=null) formData.add("minScale", minScale);
		if(maxScale!=null) formData.add("maxScale", maxScale);
		ClientResponse resp = getJerseyClient().resource(gosEndpoint)
			.path("/GeoserverManagement/removeLayerStyle")
    		.accept(MediaType.APPLICATION_JSON)
    		.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
    		.post(ClientResponse.class, formData);
		return (resp.getStatus()==201);
	}
	
	
	public boolean removeStyle(String gosEndpoint, String styleName, Integer minScale, Integer maxScale) throws IOException{
		MultivaluedMap formData = new MultivaluedMapImpl();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		formData.add("styleName", styleName);
		if(minScale!=null) formData.add("minScale", minScale);
		if(maxScale!=null) formData.add("maxScale", maxScale);
		ClientResponse resp = getJerseyClient().resource(gosEndpoint)
			.path("/GeoserverManagement/removeStyle")
    		.accept(MediaType.APPLICATION_JSON)
    		.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
    		.post(ClientResponse.class, formData);
		return (resp.getStatus()==201);
	}
	
	
	public boolean removeStyle(String gosEndpoint, String styleName) throws IOException{
		return removeStyle(gosEndpoint, styleName, null, null);
	}
	
	
	public boolean setDefaultLayerStyle(String gosEndpoint, String layerName, String styleName, String sld, Integer minScale, Integer maxScale) throws IOException{
		MultivaluedMap formData = new MultivaluedMapImpl();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		formData.add("layerName", layerName);
		formData.add("styleName", styleName);
		formData.add("sld", sld);
		if(minScale!=null) formData.add("minScale", minScale);
		if(maxScale!=null) formData.add("maxScale", maxScale);
		ClientResponse resp = getJerseyClient().resource(gosEndpoint)
			.path("/GeoserverManagement/setDefaultLayerStyle")
    		.accept(MediaType.APPLICATION_JSON)
    		.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
    		.post(ClientResponse.class, formData);
		return (resp.getStatus()==201);
	}
	
	
	public boolean workspaceExists(String gosEndpoint, String workspaceName) throws IOException{
		MultivaluedMap formData = new MultivaluedMapImpl();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		formData.add("workspaceName", workspaceName);
		ClientResponse resp = getJerseyClient().resource(gosEndpoint)
			.path("/GeoserverManagement/workspaceExists")
    		.accept(MediaType.APPLICATION_JSON)
    		.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
    		.post(ClientResponse.class, formData);
		return resp.getEntity(Boolean.class).booleanValue();
	}
	
	public PublishConfig getGeoTIFFPublishConfig(String gosEndpoint, String layerId) throws Exception {  
		ClientResponse resp = getJerseyClient().resource(gosEndpoint)
				.path("/GeoserverManagement/getGeoTIFFPublishConfig/"+layerId)
				.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr)
	    		.accept(MediaType.APPLICATION_JSON)
	    		.get(ClientResponse.class);
		return getMapper().readValue(resp.getEntity(String.class), PublishConfig.class);
	}
	
	public void publishGeoTIFF(String gosEndpoint, PublishConfig publishConfig, Coverage coverage) throws Exception{	
		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		formData.add("publishConfig", getMapper().writeValueAsString(publishConfig));
		formData.add("geotiff", getMapper().writeValueAsString(coverage));
		
		ClientResponse response = getJerseyClient()
			.resource(gosEndpoint)
			.path("/GeoserverManagement/publishGeoTIFF")
			.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
    		.post(ClientResponse.class, formData);
		
		if (response.getStatus() != CREATED.getStatusCode()) {
			throw new Exception();
		}
	}
}
