package org.gcube.datacatalogue.catalogue.utils;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.LoggerFactory;


/**
 * As the name says, it just delegates GET/POST operations to CKAN
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@SuppressWarnings("unchecked")
public class Delegator {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Delegator.class);

	/**
	 * Execute the get
	 * @param caller
	 * @param context
	 * @param method
	 * @param uriInfo
	 * @return
	 * @throws Exception 
	 */
	public static String delegateGet(Caller caller, String context, String method, UriInfo uriInfo){

		DataCatalogue catalogue = CatalogueUtils.getCatalogue();
		String username = caller.getClient().getId();

		if(catalogue == null){
			String msg  = "There is no catalogue instance in context " + context + " or a temporary problem arised.";
			logger.warn(msg);
			return CatalogueUtils.createJSONOnFailure(msg);
		}else{
			try(CloseableHttpClient client = HttpClientBuilder.create().build();){

				String authorization = catalogue.getApiKeyFromUsername(username);
				String requestPath = catalogue.getCatalogueUrl().endsWith("/") ? catalogue.getCatalogueUrl() : catalogue.getCatalogueUrl() + "/";
				requestPath += method;
				MultivaluedMap<String, String> undecodedParams = uriInfo.getQueryParameters(false);
				Iterator<Entry<String, List<String>>> iterator = undecodedParams.entrySet().iterator();

				while (iterator.hasNext()) {
					Map.Entry<java.lang.String, java.util.List<java.lang.String>> entry = (Map.Entry<java.lang.String, java.util.List<java.lang.String>>) iterator
							.next();

					if(entry.getKey().equals("gcube-token"))
						continue;
					else{

						List<String> values = entry.getValue();
						for (String value : values) {
							requestPath += entry.getKey() + "=" + value + "&";
						}
					}
				}

				if(requestPath.endsWith("&"))
					requestPath = requestPath.substring(0, requestPath.length() - 1); 
				HttpGet request = new HttpGet(requestPath);
				if(authorization != null)
					request.addHeader(Constants.AUTH_CKAN_HEADER, authorization);

				logger.debug("******* REQUEST URL IS " + requestPath);

				HttpEntity entityRes = client.execute(request).getEntity();
				String json = EntityUtils.toString(entityRes);

				// substitute "help" field
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(json);
				obj.put(Constants.HELP_KEY, Constants.HELP_URL_GCUBE_CATALOGUE);
				return obj.toJSONString();

			}catch(Exception e){
				logger.error("Failed to serve the request", e);
				return CatalogueUtils.createJSONOnFailure("Failed to serve the request: " + e.getMessage());
			}
		}

	}

	/**
	 * Execute the post
	 * @param caller
	 * @param context
	 * @param groupShow
	 * @param json
	 * @param uriInfo 
	 * @throws Exception 
	 */
	public static String delegatePost(Caller caller, String context,
			String method, String json, UriInfo uriInfo){

		String username = caller.getClient().getId();
		DataCatalogue catalogue = CatalogueUtils.getCatalogue();

		if(catalogue == null){
			String msg  = "There is no catalogue instance in context " + context + " or a temporary problem arised.";
			logger.warn(msg);
			return CatalogueUtils.createJSONOnFailure(msg);
		}else{

			try(CloseableHttpClient client = HttpClientBuilder.create().build();){

				String authorization = catalogue.getApiKeyFromUsername(username);
				String requestPath = catalogue.getCatalogueUrl().endsWith("/") ? catalogue.getCatalogueUrl() : catalogue.getCatalogueUrl() + "/";
				requestPath += method + "?";

				MultivaluedMap<String, String> undecodedParams = uriInfo.getQueryParameters(false);
				Iterator<Entry<String, List<String>>> iterator = undecodedParams.entrySet().iterator();

				while (iterator.hasNext()) {
					Map.Entry<java.lang.String, java.util.List<java.lang.String>> entry = (Map.Entry<java.lang.String, java.util.List<java.lang.String>>) iterator
							.next();

					if(entry.getKey().equals("gcube-token"))
						continue;
					else{

						List<String> values = entry.getValue();
						for (String value : values) {
							requestPath += entry.getKey() + "=" + value + "&";
						}
					}
				}

				if(requestPath.endsWith("&"))
					requestPath = requestPath.substring(0, requestPath.length() - 1); 

				logger.debug("POST request url is going to be " + requestPath);

				HttpPost request = new HttpPost(requestPath);
				request.addHeader(Constants.AUTH_CKAN_HEADER, authorization);
				logger.debug("Sending json to CKAN is " + json);
				StringEntity params = new StringEntity(json, ContentType.APPLICATION_JSON);
				request.setEntity(params);
				HttpEntity entityRes = client.execute(request).getEntity();
				String jsonRes = EntityUtils.toString(entityRes);

				logger.debug("Result from CKAN is " + jsonRes);

				// substitute "help" field
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(jsonRes);
				obj.put(Constants.HELP_KEY, Constants.HELP_URL_GCUBE_CATALOGUE);

				logger.debug("replaced information " + obj);
				return obj.toJSONString();

			}catch(Exception e){
				logger.error("Failed to serve the request", e);
				return CatalogueUtils.createJSONOnFailure("Failed to serve the request: " + e.getMessage());
			}
		}

	}

	/**
	 * Execute post with multipart (e.g. for resource upload)
	 * @param caller
	 * @param context
	 * @param resourceCreate
	 * @param multiPart
	 * @param uriInfo
	 * @return
	 */
	public static String delegatePost(Caller caller, String context,
			String method, FormDataMultiPart multiPart, UriInfo uriInfo) {

		String username = caller.getClient().getId();
		DataCatalogue catalogue = CatalogueUtils.getCatalogue();

		if(catalogue == null){
			String msg  = "There is no catalogue instance in context " + context + " or a temporary problem arised.";
			logger.warn(msg);
			return CatalogueUtils.createJSONOnFailure(msg);
		}else{

			try{

				String authorization = catalogue.getApiKeyFromUsername(username);
				String requestPath = catalogue.getCatalogueUrl().endsWith("/") ? catalogue.getCatalogueUrl() : catalogue.getCatalogueUrl() + "/";
				requestPath += method + "?";

				MultivaluedMap<String, String> undecodedParams = uriInfo.getQueryParameters(false);
				Iterator<Entry<String, List<String>>> iterator = undecodedParams.entrySet().iterator();

				while (iterator.hasNext()) {
					Map.Entry<java.lang.String, java.util.List<java.lang.String>> entry = (Map.Entry<java.lang.String, java.util.List<java.lang.String>>) iterator
							.next();

					if(entry.getKey().equals("gcube-token"))
						continue;
					else{

						List<String> values = entry.getValue();
						for (String value : values) {
							requestPath += entry.getKey() + "=" + value + "&";
						}
					}
				}

				if(requestPath.endsWith("&"))
					requestPath = requestPath.substring(0, requestPath.length() - 1); 

				logger.debug("POST request url is going to be " + requestPath);

				// use jersey client
				logger.debug("Sending multipart to CKAN " + multiPart);

				FormDataBodyPart upload = multiPart.getField("upload");
				if(upload != null){
					File file = upload.getValueAs(File.class);
					long fileLenghtBytes = file.length();
					long fileLenghtMegaByte = fileLenghtBytes >> 20;
					logger.debug("File lenght in MegaByte is " + fileLenghtMegaByte);

					if(fileLenghtMegaByte > Constants.MAX_UPLOADABLE_FILE_SIZE_MB)
						throw new Exception("Exceeding maximum uploadable file size!");

				}else
					throw new Exception("No 'upload' field has been provided!");

				Client client = ClientBuilder.newClient();
				client.register(MultiPartFeature.class);
				WebTarget webResource = client.target(requestPath);
				JSONObject jsonRes = 
						webResource
						.request(MediaType.APPLICATION_JSON)						
						.header(Constants.AUTH_CKAN_HEADER, authorization)
						.post(Entity.entity(multiPart, multiPart.getMediaType()), JSONObject.class);

				logger.debug("Result from CKAN is " + jsonRes);

				// substitute "help" field
				jsonRes.put(Constants.HELP_KEY, Constants.HELP_URL_GCUBE_CATALOGUE);
				return jsonRes.toJSONString();

			}catch(Exception e){
				logger.error("Failed to serve the request", e);
				return CatalogueUtils.createJSONOnFailure("Failed to serve the request: " + e.getMessage());
			}
		}
	}


}
