package gr.cite.gos.client;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import gr.cite.geoanalytics.dataaccess.entities.coverage.Coverage;

import static javax.ws.rs.core.Response.Status.*;

public class RasterManagement extends GosManagement {

	public RasterManagement(String authenticationStr) {
		super(authenticationStr);
	}

	public void createCoverage(String gosEndpoint, Coverage coverage) throws Exception {        
        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
        formData.add("coverage", getMapper().writeValueAsString(coverage));	
        
		ClientResponse response =  getJerseyClient().resource(gosEndpoint)
												.path("/RasterManagement/coverage/create")
												.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
												.post(ClientResponse.class, formData);
		
		errorHandling(response, CREATED, "Could not insert GeoTIFF image in database. GOS is unavailable");
	}
	
	public Coverage getGeoTIFFCoverage(String gosEndpoint, String layerId) throws Exception {  
		ClientResponse response =  getJerseyClient().resource(gosEndpoint)
												.path("/RasterManagement/coverage/get/" + layerId)
												.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr)
												.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
												.get(ClientResponse.class);

		errorHandling(response, OK, "Could not retrieve GeoTIFF image in database. GOS is unavailable");
	
		return getMapper().readValue(response.getEntity(String.class), Coverage.class);
	}
	
	public void deleteCoverageOfLayer(String gosEndpoint, String layerId) throws Exception {   
		ClientResponse response =  getJerseyClient().resource(gosEndpoint)
												.path("/RasterManagement/coverage/delete/" + layerId)
												.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr)
												.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
												.delete(ClientResponse.class);
		
		errorHandling(response, OK, "Could not delete GeoTIFF image from database. GOS is unavailable");
	}
	
	private void errorHandling(ClientResponse response, Status status, String errorMessage) throws Exception{
		if (response.getStatus() != status.getStatusCode()) {
			String responseMessage = response.getEntity(String.class);
			if (responseMessage == null || responseMessage.isEmpty()) {
				throw new Exception(errorMessage);
			} else {
				throw new Exception(responseMessage);
			}
		}		
	}
}
