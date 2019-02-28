package gr.cite.gos.client;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;




import gr.cite.geoanalytics.dataaccess.entities.coverage.Coverage;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import java.io.FileInputStream;
import java.io.InputStream;

import static javax.ws.rs.core.Response.Status.*;

public class RasterManagement extends GosManagement {

	public RasterManagement(String authenticationStr) {
		super(authenticationStr);
	}

	public void createCoverage(String gosEndpoint, Coverage coverage) throws Exception {
        MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
		formData.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
        formData.add("coverage", getMapper().writeValueAsString(coverage));

		Response response =  getJerseyClient().target(gosEndpoint)
										.path("/RasterManagement/coverage/create").request()
										.post(Entity.entity(formData,MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		
		errorHandling(response, CREATED, "Could not insert GeoTIFF image in database. GOS is unavailable");
	}

	public void createCoverage(String gosEndpoint, InputStream coverageStream) throws Exception {
//		Coverage coverage,
//		String sContentDisposition = "attachment; filename=\"" + name+"\"";
//		WebResource fileResource = a_client.resource(a_sUrl);
//
		Response response =  getJerseyClient().target(gosEndpoint)
				.path("/RasterManagement/coverage/create/stream")
                .request()
				.post(Entity.entity(coverageStream,MediaType.APPLICATION_OCTET_STREAM));

		errorHandling(response, CREATED, "Could not insert GeoTIFF image in database. GOS is unavailable");

//		final StreamDataBodyPart filePart = new StreamDataBodyPart("file", coverageStream);
//		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
//		final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("coverage", getMapper().writeValueAsString(coverage)).bodyPart(filePart);
//
//		ClientResponse response =  getJerseyClient().resource(gosEndpoint)
//				.path("/RasterManagement/coverage/create/stream")
//				.type(MediaType.MULTIPART_FORM_DATA)
//				.post(ClientResponse.class, multipart);
//		errorHandling(response, CREATED, "Could not insert GeoTIFF image in database. GOS is unavailable");

	}

	public void createCoverage(String gosEndpoint, Coverage coverage, InputStream coverageStream) throws Exception {
		FormDataBodyPart bodyPart = null;
		FormDataMultiPart formDataMultiPart = null;
		FormDataMultiPart multipart = null;
		Response response = null;
		try{
//		final StreamDataBodyPart filePart = new StreamDataBodyPart("file", coverageStream);
			bodyPart = new FormDataBodyPart("file", coverageStream, MediaType.APPLICATION_OCTET_STREAM_TYPE);
			formDataMultiPart = new FormDataMultiPart();
			multipart = (FormDataMultiPart) formDataMultiPart.field("coverage", getMapper().writeValueAsString(coverage))
	//				.field("file", coverageStream, MediaType.APPLICATION_OCTET_STREAM_TYPE);
					.bodyPart(bodyPart);


			Invocation.Builder invocationBuilder =  getJerseyClient().target(gosEndpoint) //.register(MultiPartFeature.class)
					.path("/RasterManagement/coverage/create")
					.request();
			response = invocationBuilder.post(Entity.entity(multipart, MediaType.MULTIPART_FORM_DATA));


			errorHandling(response, CREATED, "Could not insert GeoTIFF image in database. GOS is unavailable");
		}
		finally {
			bodyPart.cleanup();
			formDataMultiPart.close();
			multipart.close();
			response.close();
		}

	}
	
	public Coverage getGeoTIFFCoverage(String gosEndpoint, String layerId) throws Exception {  
		Response response =  getJerseyClient().target(gosEndpoint)
				.path("/RasterManagement/coverage/get/" + layerId)
				.request()
				.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr)
//				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.get();

		errorHandling(response, OK, "Could not retrieve GeoTIFF image in database. GOS is unavailable");
	
		return getMapper().readValue(response.readEntity(String.class), Coverage.class);
	}

	public InputStream getGeoTIFFCoverageStream(String gosEndpoint, String layerId) throws Exception {
		Response response =  getJerseyClient().target(gosEndpoint)
				.path("/RasterManagement/coverage/get/" + layerId)
                .request(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr)
//				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.get();

		errorHandling(response, OK, "Could not retrieve GeoTIFF image in database. GOS is unavailable");

		return (InputStream) response.getEntity();
	}


	public void deleteCoverageOfLayer(String gosEndpoint, String layerId) throws Exception {
		Response response =  getJerseyClient().target(gosEndpoint)
				.path("/RasterManagement/coverage/delete/" + layerId)
                .request()
				.header(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr)
//				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.delete();

		errorHandling(response, OK, "Could not delete GeoTIFF image from database. GOS is unavailable");
	}
	
	private void errorHandling(Response response, Status status, String errorMessage) throws Exception{
		if (response.getStatus() != status.getStatusCode()) {
			String responseMessage = response.readEntity(String.class);
			if (responseMessage == null || responseMessage.isEmpty()) {
				throw new Exception(errorMessage);
			} else {
				throw new Exception(responseMessage);
			}
		}		
	}
}
