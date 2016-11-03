package org.gcube.rest.index.client.cache;

import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.gcube.rest.index.client.ClientFactory;
import org.gcube.rest.index.client.exceptions.NoAvailableIndexServiceInstance;
import org.gcube.rest.index.client.globals.EndpointProvider;
import org.gcube.rest.index.client.internals.EndpointsHelper;
import org.gcube.rest.index.client.security.TrustX509TrustManager;
import org.gcube.rest.index.client.security.WhitelistHostnameVerifier;
import org.gcube.rest.index.common.discover.IndexDiscoverer;
import org.gcube.rest.index.common.discover.IndexDiscovererAPI;
import org.gcube.rest.index.common.entities.CollectionInfo;
import org.gcube.rest.index.common.search.Query;
import org.gcube.rest.index.common.search.Search_Response;
import org.gcube.rest.index.common.tools.Toolbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class IndexClient {
	
	private static Gson prettygson = new GsonBuilder().setPrettyPrinting().create();

	private Client jerseyClient;
	
	private EndpointProvider endpointProvider = null;
	
	private String scope = null;
	
	private static final Logger logger = LoggerFactory.getLogger(IndexClient.class);
	
	protected IndexClient(){} //should only called by ClientFactory
	
	public void initiateClient(String scope){
		this.scope = scope;
		jerseyClient = Client.create();
		endpointProvider = new EndpointProvider(scope);
		logger.debug("Found " + endpointProvider.endpointsNumber() + " index endpoints");
	}

	
	public EndpointProvider getEndpointProvider(){
		return endpointProvider;
	}
	
	public boolean createIndex(String collectionID) throws NoAvailableIndexServiceInstance {
		int code = -1;
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = endpointProvider.getAnEndpoint();
			try{
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("create").path(collectionID);
				ClientResponse resp = tempWebResource
						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
				    	.type(MediaType.APPLICATION_FORM_URLENCODED)
						.header("gcube-scope", scope)
						.post(ClientResponse.class);
				code = resp.getStatus();
				resp.close();  
				return code == Status.CREATED.getStatusCode();
			}
			catch(ClientHandlerException | UniformInterfaceException  ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);
	}
	
	
	// returns data sources
	@Cacheable(CacheConfig.COLLECTION_NAMES)
	public ArrayList<String> getCollections() throws NoAvailableIndexServiceInstance {
		while(endpointProvider.endpointsNumber() > 0 ){
			String jsonCollResp = "";
			String ep = endpointProvider.getAnEndpoint();
			try{
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("listCollections");
				ClientResponse resp = tempWebResource
						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
		//		    	.type(MediaType.APPLICATION_JSON)
						.header("gcube-scope", scope)
						.get(ClientResponse.class);
				jsonCollResp = resp.getEntity(String.class);
				resp.close();
				try{
					return prettygson.fromJson(jsonCollResp, new TypeToken<ArrayList<String>>(){}.getType());
				}
				catch(JsonSyntaxException jsonExcep){
					logger.debug("Could not deserialise datasources list. List empty?");
					return new ArrayList<String>();
				}
			}
			catch(ClientHandlerException | UniformInterfaceException  ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);
	}
	
	
	@Cacheable(CacheConfig.COLLECTIONS_FIELDS)
	public Map<String,List<String>> getAllCollectionFields() throws NoAvailableIndexServiceInstance {
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = endpointProvider.getAnEndpoint();
			try{
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("getAllCollectionFields").queryParam("aliasFields", Boolean.toString(true));
				ClientResponse resp = tempWebResource
						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
		//		    	.type(MediaType.APPLICATION_JSON)
						.header("gcube-scope", scope)
						.get(ClientResponse.class);
				String jsonCollResp = resp.getEntity(String.class);
				resp.close();
				Map<String,List<String>> result = prettygson.fromJson(jsonCollResp, new TypeToken<Map<String,List<String>>>(){}.getType());
				return result;
			}
			catch(ClientHandlerException | UniformInterfaceException  ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);
	}
	
	
	/**
	 * Inserts a document in the specified index (collectionID). If recordID is null or empty, it will be assigned an auto-generated
	 * 
	 * @param collectionID the name of the collection - it will map to the index name
	 * @param recordID  
	 * @param recordJSON  the document in json format
	 * @return true if successfull, false otherwise
	 * @throws NoAvailableIndexServiceInstance 
	 */
	public boolean insertJson(String collectionID, String recordID, String recordJSON) throws NoAvailableIndexServiceInstance{
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = endpointProvider.getAnEndpoint();
			try{
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("insert").path(collectionID).path(recordID);
		//		try{recordJSON = Toolbox.toUnicode(recordJSON);} catch(UnsupportedEncodingException ex ){}
				MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
				formData.add("recordJSON", recordJSON);
				ClientResponse resp = tempWebResource
						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
				    	.type(MediaType.APPLICATION_FORM_URLENCODED + "; " + "charset=UTF-8")
						.header("gcube-scope", scope)
						.post(ClientResponse.class, formData);
				int code = resp.getStatus();
				resp.close();
		        return code == Status.CREATED.getStatusCode();
			}
			catch(ClientHandlerException | UniformInterfaceException  ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);
	}
	
	
	/**
	 * Inserts a document in the specified index (collectionID). The new record will be assigned an auto-generated ID
	 * 
	 * @param collectionID the name of the collection - it will map to the index name
	 * @param recordJSON  the document in json format
	 * @return true if successfull, false otherwise
	 * @throws NoAvailableIndexServiceInstance 
	 */
	public boolean insertJson(String collectionID, String recordJSON) throws NoAvailableIndexServiceInstance{
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = endpointProvider.getAnEndpoint();
			try{
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("insert").path(collectionID);
		//		try{recordJSON = Toolbox.toUnicode(recordJSON);} catch(UnsupportedEncodingException ex ){}
				MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
				formData.add("recordJSON", recordJSON);
				ClientResponse resp = tempWebResource
						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
				    	.type(MediaType.APPLICATION_FORM_URLENCODED + "; " + "charset=UTF-8")
						.header("gcube-scope", scope)
						.post(ClientResponse.class, formData);
				int code = resp.getStatus();
				
				if(code != Status.CREATED.getStatusCode())
					System.out.println("THIS RECORD COULD NOT BE INSERTED: "+recordJSON);
				
				resp.close();
		        return code == Status.CREATED.getStatusCode();
			}
			catch(ClientHandlerException | UniformInterfaceException  ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);   
	}

	
	public boolean delete(String collectionID, String recordID) throws NoAvailableIndexServiceInstance{
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = endpointProvider.getAnEndpoint();
			try{
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("delete").path(collectionID).path(recordID);
				ClientResponse resp = tempWebResource
						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
		//		    	.type(MediaType.APPLICATION_FORM_URLENCODED)
						.header("gcube-scope", scope)
						.delete(ClientResponse.class);
				int code = resp.getStatus();
				resp.close();
				return code == Status.OK.getStatusCode();
			}
			catch(ClientHandlerException | UniformInterfaceException  ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);   
	}
	
	
	public boolean dropCollection(String collectionID) throws NoAvailableIndexServiceInstance{
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = endpointProvider.getAnEndpoint();
			try{
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("dropCollection").path(collectionID);
				ClientResponse resp = tempWebResource
						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
		//		    	.type(MediaType.APPLICATION_FORM_URLENCODED)
						.header("gcube-scope", scope)
						.delete(ClientResponse.class);
				int code = resp.getStatus();
				resp.close();
				return code == 200;
			}
			catch(ClientHandlerException | UniformInterfaceException  ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);	
	}
	
	public boolean setCollectionFieldsAlias(String collectionID, Map<String, String> fieldAlias) throws NoAvailableIndexServiceInstance{
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = endpointProvider.getAnEndpoint();
			try{
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("setCollectionFieldsAlias").queryParam("collectionID", collectionID);
				String fieldAliasJSON = prettygson.toJson(fieldAlias);
				MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
				formData.add("mappingsJSON", fieldAliasJSON);
				ClientResponse resp = tempWebResource
						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
				    	.type(MediaType.APPLICATION_FORM_URLENCODED)
						.header("gcube-scope", scope)
						.post(ClientResponse.class, formData);
				int code = resp.getStatus();
				resp.close();
				return code == Status.CREATED.getStatusCode();
			}
			catch(ClientHandlerException | UniformInterfaceException  ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);	
	}
	
	public boolean deleteCollectionFieldsAlias(String collectionID) throws NoAvailableIndexServiceInstance{
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = endpointProvider.getAnEndpoint();
			try{
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("deleteCollectionFieldsAlias").queryParam("collectionID", collectionID);
				ClientResponse resp = tempWebResource
						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
		//		    	.type(MediaType.APPLICATION_FORM_URLENCODED)
						.header("gcube-scope", scope)
						.delete(ClientResponse.class);
				int code = resp.getStatus();
				resp.close();
				return code == 200;
			}
			catch(ClientHandlerException | UniformInterfaceException  ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);		
	}
	
	@Cacheable(CacheConfig.COLLECTIONS_FIELDS_ALIASES)
	public Map<String,Map<String,String>> getAllCollectionFieldsAliases(boolean fromIndexToView) throws NoAvailableIndexServiceInstance{
		return getCollectionFieldsAlias("", fromIndexToView); //this brings all
	}
	
	@Cacheable(CacheConfig.COLLECTIONS_FIELDS_ALIASES)
	public Map<String,Map<String,String>> getCollectionFieldsAlias(String collectionID, boolean fromIndexToView) throws NoAvailableIndexServiceInstance{
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = endpointProvider.getAnEndpoint();
			try{
				if((collectionID==null) || collectionID.isEmpty())
					collectionID = "";
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("getCollectionFieldsAlias")
						.queryParam("collectionID", collectionID).queryParam("fromIndexToView", Boolean.toString(fromIndexToView));
				ClientResponse resp = tempWebResource
						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
		//		    	.type(MediaType.APPLICATION_FORM_URLENCODED)
						.header("gcube-scope", scope)
						.get(ClientResponse.class);
				String jsonCollFieldsAlias = resp.getEntity(String.class);
				resp.close();
				if(jsonCollFieldsAlias==null || jsonCollFieldsAlias.isEmpty())
					return new HashMap<String,Map<String,String>>();
				return prettygson.fromJson(jsonCollFieldsAlias, new TypeToken<Map<String,Map<String,String>>>(){}.getType());
			}
			catch(ClientHandlerException | UniformInterfaceException  ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);		
	}
	
	@Cacheable(CacheConfig.JSON_TRANSFORMERS)
	public Map<String,String> getAllJSONTransformers() throws NoAvailableIndexServiceInstance{
		return getJSONTransformer("");
	}
	
	@Cacheable(CacheConfig.JSON_TRANSFORMERS)
	public HashMap<String,String> getJSONTransformer(String collectionID) throws NoAvailableIndexServiceInstance{
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = endpointProvider.getAnEndpoint();
			try{
				if((collectionID==null) || collectionID.isEmpty())
					collectionID = "";
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("getJSONTransformer").queryParam("collectionID", collectionID);
				ClientResponse resp = tempWebResource
						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
		//		    	.type(MediaType.APPLICATION_FORM_URLENCODED)
						.header("gcube-scope", scope)
						.get(ClientResponse.class);
				String jsonMap = resp.getEntity(String.class);
				resp.close();
				return prettygson.fromJson(jsonMap, new TypeToken<HashMap<String,String>>(){}.getType());
			}
			catch(ClientHandlerException | UniformInterfaceException  ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);	
	}
	
	public boolean setJSONTransformer(String collectionID, String transformerJSON) throws NoAvailableIndexServiceInstance{
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = endpointProvider.getAnEndpoint();
			try{
				if((collectionID==null) || collectionID.isEmpty()){
					logger.error("No collection id specified to set the transformer json");
					return false;
				}
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("setJSONTransformer").queryParam("collectionID", collectionID);
				MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
				formData.add("transformerJSON", transformerJSON);
				ClientResponse resp = tempWebResource
						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
				    	.type(MediaType.APPLICATION_FORM_URLENCODED)
						.header("gcube-scope", scope)
						.post(ClientResponse.class, formData);
				resp.close();
				return (resp.getStatus()==Status.CREATED.getStatusCode()) ? true : false;
			}
			catch(ClientHandlerException | UniformInterfaceException  ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);
	}

	
	public boolean deleteJSONTransformer(String collectionID) throws NoAvailableIndexServiceInstance{
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = endpointProvider.getAnEndpoint();
			try{
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("deleteJSONTransformer").queryParam("collectionID", collectionID);
				ClientResponse resp = tempWebResource
						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
		//		    	.type(MediaType.APPLICATION_FORM_URLENCODED)
						.header("gcube-scope", scope)
						.delete(ClientResponse.class);
				int code = resp.getStatus();
				resp.close();
				return code == 200;
			}
			catch(ClientHandlerException | UniformInterfaceException  ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);	
	}
	
	public Map<String,CollectionInfo> getAllCollectionsInfo() throws NoAvailableIndexServiceInstance {
		return getCollectionInfo(null);
	}
	
	@Cacheable(CacheConfig.COLLECTION_INFOS)
	public Map<String,CollectionInfo> getCollectionInfo(String collectionID) throws NoAvailableIndexServiceInstance{
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = endpointProvider.getAnEndpoint();
			try{
				if((collectionID==null) || collectionID.isEmpty())
					collectionID = "";
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("getCollectionInfo").queryParam("collectionID", collectionID);
				ClientResponse resp = tempWebResource
						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
		//		    	.type(MediaType.APPLICATION_FORM_URLENCODED)
						.header("gcube-scope", scope)
						.get(ClientResponse.class);
				
				String jsonMap = resp.getEntity(String.class);
				resp.close();
		
				Map<String,CollectionInfo> output = new HashMap<String,CollectionInfo>();
				Map <String,String> map = prettygson.fromJson(jsonMap, new TypeToken<HashMap<String,String>>(){}.getType());
				Iterator<Entry<String, String>> it = map.entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry<String, String> pair = (Map.Entry<String, String>)it.next();
			        output.put(pair.getKey().toString(), prettygson.fromJson(pair.getValue().toString(), CollectionInfo.class));
			        it.remove(); // avoids a ConcurrentModificationException
			    }
				return output;
			}
			catch(ClientHandlerException | UniformInterfaceException  ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);
	}
	
	public boolean setCollectionInfo(String collectionID, CollectionInfo collInfo) throws NoAvailableIndexServiceInstance{
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = endpointProvider.getAnEndpoint();
			try{
				if((collectionID==null) || collectionID.isEmpty()){
					logger.error("No collection id specified to set the transformer json");
					return false;
				}
				if(!collectionID.equals(collInfo.getId()))
					collInfo.setId(collectionID);
				
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("setCollectionInfo").queryParam("collectionID", collectionID);
				MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
				formData.add("infoJSON", prettygson.toJson(collInfo));
				ClientResponse resp = tempWebResource
						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
				    	.type(MediaType.APPLICATION_FORM_URLENCODED + "; " + "charset=UTF-8")
						.header("gcube-scope", scope)
						.post(ClientResponse.class, formData);
				resp.close();
				return (resp.getStatus()==Status.CREATED.getStatusCode()) ? true : false;
			}
			catch(ClientHandlerException | UniformInterfaceException  ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);
	}

	
	public boolean deleteCollectionInfo(String collectionID) throws NoAvailableIndexServiceInstance{
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = endpointProvider.getAnEndpoint();
			try{
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("deleteCollectionInfo").queryParam("collectionID", collectionID);
				ClientResponse resp = tempWebResource
						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
		//		    	.type(MediaType.APPLICATION_FORM_URLENCODED)
						.header("gcube-scope", scope)
						.delete(ClientResponse.class);
				int code = resp.getStatus();
				resp.close();
				return code == 200;
			}
			catch(ClientHandlerException | UniformInterfaceException  ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);
	}
	
	
	public Search_Response search(Query query) throws NoAvailableIndexServiceInstance{
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = endpointProvider.getAnEndpoint();
			try{
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("search");
				
				String queryJson = prettygson.toJson(query);
				queryJson = Toolbox.encode(queryJson);
				
				MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
				formData.add("query", queryJson);
				
				ClientResponse resp = tempWebResource
						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
				    	.type(MediaType.APPLICATION_FORM_URLENCODED + "; " + "charset=UTF-8")
						.header("gcube-scope", scope)
						.post(ClientResponse.class, formData);
				
				String jsonResults = resp.getEntity(String.class);
				resp.close();
				
				return prettygson.fromJson(jsonResults, Search_Response.class);
			}
			catch(ClientHandlerException | UniformInterfaceException  ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);
	}
	

}
