package org.gcube.rest.index.client.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.gcube.rest.index.client.ClientFactory;
import org.gcube.rest.index.client.exceptions.BadCallException;
import org.gcube.rest.index.client.exceptions.IndexException;
import org.gcube.rest.index.client.exceptions.NoAvailableIndexServiceInstance;
import org.gcube.rest.index.client.globals.EndpointProvider;
import org.gcube.rest.index.common.discover.exceptions.IndexDiscoverException;
import org.gcube.rest.index.common.entities.CollectionInfo;
import org.gcube.rest.index.common.entities.configuration.DatasourceType;
import org.gcube.rest.index.common.entities.fields.Field;
import org.gcube.rest.index.common.entities.fields.config.FacetType;
import org.gcube.rest.index.common.entities.fields.config.FieldConfig;
import org.gcube.rest.index.common.search.Query;
import org.gcube.rest.index.common.search.Search_Response;
import org.gcube.rest.index.common.tools.Toolbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
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
	
	@Deprecated
	public boolean createEmptyIndex(CollectionInfo collectionInfo) throws NoAvailableIndexServiceInstance {
		if(!collectionInfo.isValid()){
			logger.debug("Trying to create an index with the following invalid collectionInfo: "+collectionInfo.toString());
			return false;
		}
		int code = -1;
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = "";
			try{
				ep = endpointProvider.getAnEndpoint();
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("createEmptyIndex");
				MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
				formData.add("cci", prettygson.toJson(collectionInfo));
				ClientResponse resp = tempWebResource
//						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
				    	.type(MediaType.APPLICATION_FORM_URLENCODED + "; " + "charset=UTF-8")
						.header("gcube-scope", scope)
						.post(ClientResponse.class, formData);
				resp.close();
				if(resp.getStatus() != Status.CREATED.getStatusCode())
					throw new IndexException("Index endpoint at \""+ep+"\" is not functional. Blacklisting and using another (if available) endpoint");
				return (resp.getStatus()==Status.CREATED.getStatusCode()) ? true : false;
			}
			catch(ClientHandlerException | UniformInterfaceException | IndexException ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			} catch (IndexDiscoverException e) {
				logger.error("No index service found on scope "+scope);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);
	}
	
	
	
	
	public boolean reIndex(CollectionInfo collectionInfo) throws NoAvailableIndexServiceInstance {
		if(!collectionInfo.isValid()){
			logger.debug("Trying to reindex a collection with the following invalid collectionInfo: "+collectionInfo.toString());
			return false;
		}
		int code = -1;
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = "";
			try{
				ep = endpointProvider.getAnEndpoint();
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("reIndex");
				MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
				formData.add("cci", prettygson.toJson(collectionInfo));
				ClientResponse resp = tempWebResource
//						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
				    	.type(MediaType.APPLICATION_FORM_URLENCODED + "; " + "charset=UTF-8")
						.header("gcube-scope", scope)
						.post(ClientResponse.class, formData);
				resp.close();
				if(resp.getStatus() != Status.OK.getStatusCode())
					throw new IndexException("Index endpoint at \""+ep+"\" is not functional. Blacklisting and using another (if available) endpoint");
				return (resp.getStatus()==Status.OK.getStatusCode()) ? true : false;
			}
			catch(ClientHandlerException | UniformInterfaceException | IndexException ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			} catch (IndexDiscoverException e) {
				logger.error("No index service found on scope "+scope);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);
	}
	
	
	
	
	// returns data sources
//	@Cacheable(CacheConfig.COLLECTION_NAMES)
	public List<String> getCollections(String collectionDomain) throws NoAvailableIndexServiceInstance {
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = "";
			try{
				ep = endpointProvider.getAnEndpoint();
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("listCollections")
						.queryParam("collectionDomain", collectionDomain);
				ClientResponse resp = tempWebResource
						.accept(MediaType.APPLICATION_XML + "; " + "charset=UTF-8")
		//		    	.type(MediaType.APPLICATION_JSON)
						.header("gcube-scope", scope)
						.get(ClientResponse.class);
				if(resp.getStatus() != Status.OK.getStatusCode())
					throw new IndexException("Index endpoint at \""+ep+"\" is not functional. Blacklisting and using another (if available) endpoint");
				try{
					return prettygson.fromJson(resp.getEntity(String.class), new TypeToken<List<String>>(){}.getType());
				}
				catch(JsonSyntaxException jsonExcep){
					logger.debug("Could not deserialise datasources list. List empty?");
					return new ArrayList<String>();
				}
				finally{
					resp.close();
				}
			}
			catch(ClientHandlerException | UniformInterfaceException | IndexException ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
			catch (IndexDiscoverException e) {
				logger.error("No index service found on scope "+scope);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);
	}
	
	
//	@Cacheable(CacheConfig.COLLECTIONS_FIELDS)
	public Map<String,List<Field>> getAllCollectionFields(String collectionDomain) throws NoAvailableIndexServiceInstance {
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = "";
			try{
				ep = endpointProvider.getAnEndpoint();
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("getAllCollectionFields").queryParam("aliasFields", Boolean.toString(true))
						.queryParam("collectionDomain", collectionDomain);
				ClientResponse resp = tempWebResource
//						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
		//		    	.type(MediaType.APPLICATION_JSON)
						.header("gcube-scope", scope)
						.get(ClientResponse.class);
				if(resp.getStatus() != Status.OK.getStatusCode())
					throw new IndexException("Index endpoint at \""+ep+"\" is not functional. Blacklisting and using another (if available) endpoint");
				Map<String, List<Field>> result = new HashMap<String, List<Field>>();
				result = prettygson.fromJson(resp.getEntity(String.class), new TypeToken<Map<String,List<Field>>>(){}.getType());
				resp.close();
				return result;
			}
			catch(ClientHandlerException | UniformInterfaceException | JsonSyntaxException | IndexException ex  ){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
			catch (IndexDiscoverException e) {
				logger.error("No index service found on scope "+scope);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);
	}
	
	
	/**
	 * Inserts a document in the specified index (collectionID). If recordID is null or empty, it will be assigned an auto-generated
	 * 
	 * @param domain is the sub-domain that this datasource resides. (don't confuse it with "scope" on which the whole index runs) 
	 * @param collectionID the name of the collection - it will map to the index name
	 * @param recordID  
	 * @param recordJSON  the document in json format
	 * @return true if successfull, false otherwise
	 * @throws NoAvailableIndexServiceInstance 
	 */
	public boolean insertJson(String domain, String collectionID, String recordID, String recordJSON) throws NoAvailableIndexServiceInstance{
		int triedSoFar = 0;
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = "";
			try{
				ep = endpointProvider.getAnEndpoint();
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("insertWithRecID")
						.queryParam("collectionID", collectionID)
						.queryParam("recordID", recordID)
						.queryParam("domain", domain);
		//		try{recordJSON = Toolbox.toUnicode(recordJSON);} catch(UnsupportedEncodingException ex ){}
				MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
				formData.add("recordJSON", recordJSON);
				ClientResponse resp = tempWebResource
//						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
				    	.type(MediaType.APPLICATION_FORM_URLENCODED + "; " + "charset=UTF-8")
						.header("gcube-scope", scope)
						.post(ClientResponse.class, formData);
				int code = resp.getStatus();
				
				if(code == Status.NOT_FOUND.getStatusCode())
					throw new IndexDiscoverException("Endpoint "+ep+" is not found");
				
				if(code != Status.CREATED.getStatusCode())
					throw new IndexException("Bad request on index or internal server error");
				
				resp.close();
		        return code == Status.CREATED.getStatusCode();
			}
			
			catch(ClientHandlerException | UniformInterfaceException ex){
				logger.warn("Endpoint \""+ep+"\" seems to be not able to serve the request. Reason: "+ex.getMessage()+" Will be blacklisted for a while, and switch the execution on another endpoint!");
				endpointProvider.remove(ep);
			}
			catch(IndexException ie){ //in this case, we would like to let it try on the remaining endpoints, and not blacklist it...
				triedSoFar++;
				if(triedSoFar==endpointProvider.endpointsNumber())
					return false;
				logger.warn("Could not insert a json into the collection "+collectionID+" at endpoint "+ep+" Will try on another endpoint...");
			}
			catch (IndexDiscoverException e) {
				logger.error("Endpoint \""+ep+"\" seems to got a bad request. Will be blacklisted");
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
	public boolean insertJson(String domain, String collectionID, String recordJSON) throws NoAvailableIndexServiceInstance{
		int triedSoFar = 0;
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = "";
			try{
				ep = endpointProvider.getAnEndpoint();
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("insertWithoutRecID")
						.queryParam("collectionID",collectionID)
						.queryParam("domain", domain);
		//		try{recordJSON = Toolbox.toUnicode(recordJSON);} catch(UnsupportedEncodingException ex ){}
				MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
				formData.add("recordJSON", recordJSON);
				ClientResponse resp = tempWebResource
//						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
				    	.type(MediaType.APPLICATION_FORM_URLENCODED + "; " + "charset=UTF-8")
						.header("gcube-scope", scope)
						.post(ClientResponse.class, formData);
				int code = resp.getStatus();
				
				if(code == Status.NOT_FOUND.getStatusCode())
					throw new IndexDiscoverException("Endpoint "+ep+" is not found");
				
				if(code != Status.CREATED.getStatusCode())
					throw new IndexException("Bad request on index or internal server error");
				
				resp.close();
		        return code == Status.CREATED.getStatusCode();
			}
			catch(ClientHandlerException | UniformInterfaceException ex){
				logger.warn("Endpoint \""+ep+"\" seems to be not able to serve the request. Reason: "+ex.getMessage()+" Will be blacklisted for a while, and switch the execution on another endpoint!");
				endpointProvider.remove(ep);
			}
			catch(IndexException ie){ //in this case, we would like to let it try on the remaining endpoints, and not blacklist it...
				triedSoFar++;
				if(triedSoFar==endpointProvider.endpointsNumber())
					return false;
				logger.warn("Could not insert a json into the collection "+collectionID+" at endpoint "+ep+" Will try on another endpoint...");
			}
			catch (IndexDiscoverException e) {
				logger.error("Endpoint \""+ep+"\" seems to got a bad request. Will be blacklisted");
				endpointProvider.remove(ep);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);   
	}

	
	public boolean delete(String collectionID, String recordID) throws NoAvailableIndexServiceInstance{
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = "";
			try{
				ep = endpointProvider.getAnEndpoint();
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("delete").path(collectionID).path(recordID);
				ClientResponse resp = tempWebResource
//						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
		//		    	.type(MediaType.APPLICATION_FORM_URLENCODED)
						.header("gcube-scope", scope)
						.delete(ClientResponse.class);
				int code = resp.getStatus();
				if(code != Status.OK.getStatusCode())
					throw new IndexException("Index endpoint at \""+ep+"\" is not functional. Blacklisting and using another (if available) endpoint");
				resp.close();
				return code == Status.OK.getStatusCode();
			}
			catch(ClientHandlerException | UniformInterfaceException | IndexException ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
			catch (IndexDiscoverException e) {
				logger.error("No index service found on scope "+scope);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);   
	}
	
	
	public boolean dropCollection(String collectionID) throws NoAvailableIndexServiceInstance{
		clearCompletelyCache();
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = "";
			try{
				ep = endpointProvider.getAnEndpoint();
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("dropCollection").path(collectionID);
				ClientResponse resp = tempWebResource
//						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
		//		    	.type(MediaType.APPLICATION_FORM_URLENCODED)
						.header("gcube-scope", scope)
						.delete(ClientResponse.class);
				int code = resp.getStatus();
				if(code != Status.OK.getStatusCode() && code != Status.NOT_MODIFIED.getStatusCode())
					throw new IndexException("Index endpoint at \""+ep+"\" is not functional. Blacklisting and using another (if available) endpoint");
				resp.close();
				return code == 200;
			}
			catch(ClientHandlerException | UniformInterfaceException | IndexException ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
			catch (IndexDiscoverException e) {
				logger.error("No index service found on scope "+scope);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);	
	}
	
	@Deprecated
	public boolean setCollectionFieldsAlias(String collectionID, Map<String, String> fieldAlias) throws NoAvailableIndexServiceInstance{
		clearCompletelyCache();
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = "";
			try{
				ep = endpointProvider.getAnEndpoint();
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("setCollectionFieldsAlias").queryParam("collectionID", collectionID);
				String fieldAliasJSON = prettygson.toJson(fieldAlias);
				MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
				formData.add("mappingsJSON", fieldAliasJSON);
				ClientResponse resp = tempWebResource
//						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
				    	.type(MediaType.APPLICATION_FORM_URLENCODED)
						.header("gcube-scope", scope)
						.post(ClientResponse.class, formData);
				int code = resp.getStatus();
				if(code != Status.CREATED.getStatusCode() && code != Status.NO_CONTENT.getStatusCode())
					throw new IndexException("Index endpoint at \""+ep+"\" is not functional. Blacklisting and using another (if available) endpoint");
				resp.close();
				return code == Status.CREATED.getStatusCode();
			}
			catch(ClientHandlerException | UniformInterfaceException | IndexException ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
			catch (IndexDiscoverException e) {
				logger.error("No index service found on scope "+scope);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);	
	}
	
	@Deprecated
	public boolean deleteCollectionFieldsAlias(String collectionID) throws NoAvailableIndexServiceInstance{
		clearCompletelyCache();
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = "";
			try{
				ep = endpointProvider.getAnEndpoint();
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("deleteCollectionFieldsAlias").queryParam("collectionID", collectionID);
				ClientResponse resp = tempWebResource
//						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
		//		    	.type(MediaType.APPLICATION_FORM_URLENCODED)
						.header("gcube-scope", scope)
						.delete(ClientResponse.class);
				int code = resp.getStatus();
				if(code != Status.OK.getStatusCode())
					throw new IndexException("Index endpoint at \""+ep+"\" is not functional. Blacklisting and using another (if available) endpoint");
				resp.close();
				return code == 200;
			}
			catch(ClientHandlerException | UniformInterfaceException | IndexException ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
			catch (IndexDiscoverException e) {
				logger.error("No index service found on scope "+scope);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);		
	}
	
	
	@Deprecated
//	@Cacheable(CacheConfig.COLLECTIONS_FIELDS_ALIASES)
	public Map<String,Map<String,String>> getCollectionFieldsAlias(String collectionID, boolean fromIndexToView, String collectionDomain) throws NoAvailableIndexServiceInstance{
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = "";
			try{
				ep = endpointProvider.getAnEndpoint();
				if((collectionID==null) || collectionID.isEmpty())
					collectionID = "";
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("getCollectionFieldsAlias")
						.queryParam("collectionID", collectionID)
						.queryParam("fromIndexToView", Boolean.toString(fromIndexToView))
						.queryParam("collectionDomain", collectionDomain);
				ClientResponse resp = tempWebResource
//						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
		//		    	.type(MediaType.APPLICATION_FORM_URLENCODED)
						.header("gcube-scope", scope)
						.get(ClientResponse.class);
				if(resp.getStatus() != Status.OK.getStatusCode())
					throw new IndexException("Index endpoint at \""+ep+"\" is not functional. Blacklisting and using another (if available) endpoint");
				String jsonCollFieldsAlias = resp.getEntity(String.class);
				resp.close();
				if(jsonCollFieldsAlias==null || jsonCollFieldsAlias.isEmpty())
					return new HashMap<String,Map<String,String>>();
				return prettygson.fromJson(jsonCollFieldsAlias, new TypeToken<Map<String,Map<String,String>>>(){}.getType());
			}
			catch(ClientHandlerException | UniformInterfaceException | IndexException ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
			catch (IndexDiscoverException e) {
				logger.error("No index service found on scope "+scope);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);		
	}
	
	
	
//	@Cacheable(CacheConfig.COMPLETE_COLLECTION_INFOS)
	public List<CollectionInfo> getCompleteCollectionInfo(String collectionID, String collectionDomain) throws NoAvailableIndexServiceInstance{
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = "";
			try{
				ep = endpointProvider.getAnEndpoint();
				if((collectionID==null) || collectionID.isEmpty())
					collectionID = "";
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("getCompleteCollectionInfo")
						.queryParam("collectionID", collectionID)
						.queryParam("collectionDomain", collectionDomain);
				ClientResponse resp = tempWebResource
						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
		//		    	.type(MediaType.APPLICATION_FORM_URLENCODED)
						.header("gcube-scope", scope)
						.get(ClientResponse.class);
				if(resp.getStatus()!=Status.OK.getStatusCode())
					throw new IndexException("Index endpoint at \""+ep+"\" is not functional. Blacklisting and using another (if available) endpoint");
				String jsonMap = resp.getEntity(String.class);
				resp.close();
				Map<String,CollectionInfo> collinfoMap = prettygson.fromJson(jsonMap, new TypeToken<HashMap<String,CollectionInfo>>(){}.getType());
				return new ArrayList<CollectionInfo>(collinfoMap.values());
			}
			catch(ClientHandlerException | UniformInterfaceException | IndexException ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
			catch (IndexDiscoverException e) {
				logger.error("No index service found on scope "+scope);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);
	}
	
	
	
	public boolean setCompleteCollectionInfo(CollectionInfo coll) throws NoAvailableIndexServiceInstance, BadCallException{
		clearCompletelyCache();
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = "";
			try{
				ep = endpointProvider.getAnEndpoint();
				if(coll==null){
					logger.error("Cannot setCompleteCollectionInfo to a NULL collection");
					throw new BadCallException("Cannot setCompleteCollectionInfo to a NULL collection");
				}
				if((coll.getId()==null) || coll.getId().isEmpty()){
					logger.error("CollectionInfo.getId() is null... That's a problem...");
					throw new BadCallException("CollectionInfo.getId() is null... That's a problem...");
				}
				
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("setCompleteCollectionInfo");
				MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
				formData.add("cci", prettygson.toJson(coll));
				ClientResponse resp = tempWebResource
//						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
				    	.type(MediaType.APPLICATION_FORM_URLENCODED + "; " + "charset=UTF-8")
						.header("gcube-scope", scope)
						.post(ClientResponse.class, formData);
				resp.close();
				
				if(resp.getStatus()!=Status.CREATED.getStatusCode())
					throw new IndexException("Index endpoint at \""+ep+"\" is not functional. Blacklisting and using another (if available) endpoint");
				return (resp.getStatus()==Status.CREATED.getStatusCode()) ? true : false;
			}
			catch(ClientHandlerException | UniformInterfaceException | IndexException ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
			catch (IndexDiscoverException e) {
				logger.error("No index service found on scope "+scope);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);
	}
	
	
	public boolean deleteCompleteCollectionInfo(String collectionID) throws NoAvailableIndexServiceInstance{
		clearCompletelyCache();
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = "";
			try{
				ep = endpointProvider.getAnEndpoint();
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("deleteCompleteCollectionInfo").queryParam("collectionID", collectionID);
				ClientResponse resp = tempWebResource
//						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
		//		    	.type(MediaType.APPLICATION_FORM_URLENCODED)
						.header("gcube-scope", scope)
						.delete(ClientResponse.class);
				int code = resp.getStatus();
				resp.close();
				if(resp.getStatus()!=Status.OK.getStatusCode())
					throw new IndexException("Index endpoint at \""+ep+"\" is not functional. Blacklisting and using another (if available) endpoint");
				return code == 200;
			}
			catch(ClientHandlerException | UniformInterfaceException | IndexException ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
			catch (IndexDiscoverException e) {
				logger.error("No index service found on scope "+scope);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);
	}
	
	
	public Search_Response search(Query query, String collectionDomain) throws NoAvailableIndexServiceInstance{
		while(endpointProvider.endpointsNumber() > 0 ){
			String ep = "";
			try{
				ep = endpointProvider.getAnEndpoint();
				WebResource webResource = jerseyClient.resource(ep);
				WebResource tempWebResource = webResource.path("search")
					.queryParam("collectionDomain", collectionDomain);
				
				
				String queryJson = prettygson.toJson(query);
				queryJson = Toolbox.encode(queryJson);
				
				MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
				formData.add("query", queryJson);
				
				ClientResponse resp = tempWebResource
//						.accept(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
				    	.type(MediaType.APPLICATION_FORM_URLENCODED + "; " + "charset=UTF-8")
						.header("gcube-scope", scope)
						.post(ClientResponse.class, formData);
				
				String jsonResults = resp.getEntity(String.class);
				resp.close();

				if(resp.getStatus()!=Status.OK.getStatusCode())
					throw new IndexException("Index endpoint at \""+ep+"\" is not functional. Blacklisting and using another (if available) endpoint");

				return prettygson.fromJson(jsonResults, Search_Response.class);
			}
			catch(ClientHandlerException | UniformInterfaceException | IndexException ex){
				logger.debug("Endpoint \""+ep+"\" seems to be dead. Will be blacklisted for a while");
				endpointProvider.remove(ep);
			}
			catch (IndexDiscoverException e) {
				logger.error("No index service found on scope "+scope);
			}
		}
		throw new NoAvailableIndexServiceInstance("There are no available index services within scope " + scope);
	}
	


	@CacheEvict(value = { CacheConfig.COMPLETE_COLLECTION_INFOS, CacheConfig.COLLECTIONS_FIELDS_ALIASES, 
			CacheConfig.COLLECTIONS_FIELDS, CacheConfig.COLLECTION_NAMES, CacheConfig.ENDPOINTS, CacheConfig.JSON_TRANSFORMERS }
			, allEntries = true, beforeInvocation=true)
	public void clearCompletelyCache(){}
	
	
}
