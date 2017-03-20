package org.gcube.rest.index.service;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.apache.commons.codec.DecoderException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.global.Global;
import org.elasticsearch.search.aggregations.bucket.global.GlobalBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.DoubleTerms;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.UnmappedTerms;
import org.gcube.rest.index.common.Constants;
//import org.gcube.rest.index.common.apis.IndexServiceI;
import org.gcube.rest.index.common.entities.CollectionInfo;
import org.gcube.rest.index.common.entities.configuration.CollectionStatus;
import org.gcube.rest.index.common.entities.configuration.DatasourceType;
import org.gcube.rest.index.common.entities.fields.Field;
import org.gcube.rest.index.common.entities.fields.config.FacetType;
import org.gcube.rest.index.common.entities.fields.config.FieldConfig;
import org.gcube.rest.index.common.entities.fields.config.FieldType;
import org.gcube.rest.index.common.search.Query;

import org.gcube.rest.index.common.search.SearchResult;
import org.gcube.rest.index.common.search.Search_Response;
import org.gcube.rest.index.common.search.facets.Facet;
import org.gcube.rest.index.common.search.facets.Facets;
import org.gcube.rest.index.common.tools.MapTransformations;
import org.gcube.rest.index.common.tools.Toolbox;
import org.gcube.rest.index.service.accessors.CacheAccessor;
import org.gcube.rest.index.service.accessors.IndexAccessor;
import org.gcube.rest.index.service.cache.CacheConfig;
import org.gcube.rest.index.service.cache.IndexServiceCache;
import org.gcube.rest.index.service.elements.IndexFacet;
import org.gcube.rest.index.service.helpers.IndexFacetFunctions;
import org.gcube.rest.index.service.helpers.IndexResponseFunctions;
import org.gcube.rest.index.service.helpers.IndexSearchFunctions;
import org.gcube.rest.index.service.procedures.InternalProcedures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;


@Path("/")
public class IndexService /*implements IndexServiceI*/ {

	static final Logger logger = LoggerFactory.getLogger(IndexService.class);

	private final static Gson prettygson = new GsonBuilder().setPrettyPrinting().create();

	
	
	@GET
    @Path("/listCollections")
	@Produces(MediaType.APPLICATION_XML + "; " + "charset=UTF-8")
    public Response listCollections(@QueryParam("collectionDomain") String collectionDomain, @HeaderParam("gcube-scope") String gcubeScope) {
		List<String> col = CacheAccessor.getIndexServiceCache().listCollections(collectionDomain);
		return Response.status(200).entity(prettygson.toJson(col, new TypeToken<List<String>>(){}.getType() )).build();
    }
	

	@GET
    @Path("/getAllCollectionFields")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response getAllCollectionFields(@DefaultValue("true") @QueryParam("aliasFields") boolean aliasFields, @QueryParam("collectionDomain") String collectionDomain, @HeaderParam("gcube-scope") String gcubeScope) {
		Map<String, Set<Field>> allCollFields;
		try {
			allCollFields = CacheAccessor.getIndexServiceCache().getAllCollectionFields(aliasFields, collectionDomain);
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Failed to fetch field names").build();
		}
		return Response.status(200).entity(prettygson.toJson(allCollFields, new TypeToken<Map<String, Set<Field>>>(){}.getType() )).build();
    }
	
	
	@POST
	@Path("/createEmptyIndex")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED + "; " + "charset=UTF-8")
	@Deprecated
    public Response createEmptyIndex(
    		@FormParam(value = "cci") CollectionInfo completeColInfo,
    		@HeaderParam("gcube-scope") String gcubeScope){
		
		
		if(completeColInfo.getId()==null||completeColInfo.getId().isEmpty()) return Response.status(Status.BAD_REQUEST).entity("No collection identifier specified").build();
		
		Map<String, CollectionInfo> coll = CacheAccessor.getIndexServiceCache().getCompleteCollectionInfo(completeColInfo.getId().toLowerCase(), completeColInfo.getCollectionDomain());
		if(coll==null || coll.isEmpty()){
			CollectionInfo ci = new CollectionInfo(completeColInfo.getId(), completeColInfo.getCollectionDomain(), DatasourceType.ELASTIC, null, null, new Date());
			setCompleteCollectionInfo(prettygson.toJson(ci, new TypeToken<CollectionInfo>(){}.getType()), completeColInfo.getCollectionDomain());
		}
		
		try{
			IndexAccessor.getFullTextNode().createIndex(completeColInfo);
		}
		catch(IOException ex){
			ex.printStackTrace();
			return Response.status(Status.NOT_MODIFIED).entity("Failed to create index: "+completeColInfo.getId()).build();
		}
		return Response.status(Status.CREATED).entity("Created index: "+completeColInfo.getId().toLowerCase()).build();
	}
	
	
	@POST
	@Path("/reIndex")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED + "; " + "charset=UTF-8")
    public Response reIndex(
    		@FormParam(value = "cci") String completeColInfoJSON,
    		@HeaderParam("gcube-scope") String gcubeScope){

		CacheAccessor.getIndexServiceCache().clearCompleteCache();
		
		if(completeColInfoJSON==null || completeColInfoJSON.isEmpty()) return Response.status(Status.BAD_REQUEST).entity("No 'cci' parameter specified").build();
		
		CollectionInfo completeColInfo = null;
		try{
			completeColInfo = ((CollectionInfo)prettygson.fromJson(completeColInfoJSON, new TypeToken<CollectionInfo>(){}.getType()));
		}
		catch(JsonSyntaxException ex){
			logger.debug("Could not parse a serialized org.gcube.rest.index.common.entities.CollectionInfo");
		}
		
		if(completeColInfo.getId()==null||completeColInfo.getId().isEmpty()) return Response.status(Status.BAD_REQUEST).entity("No collection identifier specified").build();
		
		Map<String, CollectionInfo> coll = CacheAccessor.getIndexServiceCache().getCompleteCollectionInfo(completeColInfo.getId().toLowerCase(), completeColInfo.getCollectionDomain());
		if(coll==null || coll.isEmpty()){
			logger.error("Did not find collection with id: "+completeColInfo.getId() +" within domain: " +completeColInfo.getCollectionDomain());
			return Response.status(Status.BAD_REQUEST).entity("No index with id: "+completeColInfo.getId()+" on domain: "+completeColInfo.getCollectionDomain()+" to reindex: "+completeColInfo.getId()).build();
		}
		
		Map<String,FacetType> fieldsFacets = 
				completeColInfo.getCollectionFieldsConfigs().entrySet().stream()
					.map(entry -> new AbstractMap.SimpleEntry<String, FacetType>(entry.getKey(), entry.getValue().getFacetType()))
					.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
		
		completeColInfo.setStatus(CollectionStatus.REINDEXING);
		setCompleteCollectionInfo(prettygson.toJson(completeColInfo), gcubeScope); 
		boolean status = false;
		try{
			status = InternalProcedures.softReIndex(completeColInfo.getId(), fieldsFacets);
		}
		catch(IOException ex){
			completeColInfo.setStatus(CollectionStatus.ERROR);
			//revert all facet types to none
			completeColInfo.getCollectionFieldsConfigs().entrySet().parallelStream().forEach(entry -> entry.getValue().setFacetType(FacetType.NONE));
			setCompleteCollectionInfo(prettygson.toJson(completeColInfo), gcubeScope);
			logger.error("Problem while reindexing collection with id: "+completeColInfo.getId(), ex);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Failed to reindex: "+completeColInfo.getId()).build();
		}
		if(status){
			completeColInfo.setStatus(CollectionStatus.OK);
			setCompleteCollectionInfo(prettygson.toJson(completeColInfo), gcubeScope);
			return Response.status(Status.OK).entity("Created index: "+completeColInfo.getId().toLowerCase()).build();
		}
		else{
			completeColInfo.setStatus(CollectionStatus.ERROR);
			//revert all facet types to none
			completeColInfo.getCollectionFieldsConfigs().entrySet().parallelStream().forEach(entry -> entry.getValue().setFacetType(FacetType.NONE));
			setCompleteCollectionInfo(prettygson.toJson(completeColInfo), gcubeScope);
			return Response.status(Status.NOT_MODIFIED).entity("Failed to reindex: "+completeColInfo.getId()).build();
		}
	}
	
	
	
	
	
	/**
	 * Inserts a document in the specified index (collectionID). If recordID is null or empty, it will be assigned an auto-generated
	 * 
	 * @param collectionID the name of the collection - it will map to the index name
	 * @param recordID  if null or empty, index assigns it an auto-generated 
	 * @param recordJSON  the document in json format
	 * @return true if successfull, false otherwise
	 */
	@POST
	@Path("/insertWithRecID")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED  + "; " + "charset=UTF-8")
    public Response insertWithRecID(
    		@QueryParam("collectionID") String collectionID,
    		@QueryParam("recordID") String recordID,
    		@FormParam(value = "recordJSON") String recordJSON,
    		@QueryParam("domain") String domain,
    		@HeaderParam("gcube-scope") String gcubeScope) {

		if(collectionID==null||collectionID.isEmpty()) return Response.status(Status.BAD_REQUEST).entity("No collection identifier specified").build();
		if(recordJSON==null||recordJSON.isEmpty()) return Response.status(Status.BAD_REQUEST).entity("No record payload is provided").build();
		if(domain==null || domain.isEmpty()) return Response.status(Status.BAD_REQUEST).entity("No domain is specified").build();
		
		//now insert (if index does not exist, will be created by E.S.)
		IndexResponse response = (recordID==null||recordID.isEmpty()) ?
				IndexAccessor.getFullTextNode().addRecord(collectionID.toLowerCase(), recordJSON) :
				IndexAccessor.getFullTextNode().addRecord(collectionID.toLowerCase(), recordID, recordJSON);
		
		String modType = response.isCreated() ? "Created" : "Updated";
		return Response.status(Status.CREATED).entity(modType+" record with id: "+response.getId()).build();
    }
	
	
	@POST
	@Path("/insertWithoutRecID")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED + "; " + "charset=UTF-8")
    public Response insertWithoutRecID(
    		@QueryParam("collectionID") String collectionID,
    		@QueryParam("domain") String domain,
    		@FormParam(value = "recordJSON") String recordJSON,
    		@HeaderParam("gcube-scope") String gcubeScope){
		return insertWithRecID(collectionID.toLowerCase(), null, recordJSON, domain, gcubeScope);
	}
	
	
	@DELETE
	@Path("/delete/{collectionID}/{recordID}")
    public Response delete(
    		@PathParam("collectionID") String collectionID,
    		@PathParam("recordID") String recordID, 
    		@HeaderParam("gcube-scope") String gcubeScope) {

		if(collectionID==null||collectionID.isEmpty()) return Response.status(Status.BAD_REQUEST).entity("No collection identifier specified").build();
		
		DeleteResponse response = IndexAccessor.getFullTextNode().getIndexClient().prepareDelete(collectionID.toLowerCase(), Constants.INDEX_TYPE, recordID).get();
		
		String responseStr = response.isFound() ? 
							"Deleted document with id "+recordID+" from collection "+collectionID.toLowerCase() : 
							"Could not find document with id "+recordID+" from collection "+collectionID.toLowerCase() +" to delete!";
		
		return Response.status(Status.OK).entity(responseStr+" record with id: "+response.getId()).build();
    }
	
	
	@DELETE
	@Path("/dropCollection/{collectionID}")
    public Response dropCollection(@PathParam("collectionID") String collectionID, @HeaderParam("gcube-scope") String gcubeScope) {
		DeleteIndexResponse response = IndexAccessor.getFullTextNode().getIndexClient().admin().indices().delete(new DeleteIndexRequest(collectionID.toLowerCase())).actionGet();
		int status = deleteCompleteCollectionInfo(collectionID, gcubeScope).getStatus();
		if (!response.isAcknowledged() || status != Status.OK.getStatusCode())
			return Response.status(Status.NOT_MODIFIED).entity("Did not delete collection  " + collectionID.toLowerCase()).build();
        return Response.status(200).entity("Deleted collection  " + collectionID.toLowerCase()).build();
    }
	
	
	@GET
    @Path("/listFulltextEndpoints")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response listFulltextEndpoints(@QueryParam("scope") String scope, @HeaderParam("gcube-scope") String gcubeScope) {
		return CacheAccessor.getIndexServiceCache().listFulltextEndpoints(scope);
    }

	
	
	
	@GET
    @Path("/getCompleteCollectionInfo")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response getCompleteCollectionInfo(@QueryParam("collectionID") String collectionID, @QueryParam("collectionDomain") String collectionDomain, @HeaderParam("gcube-scope") String gcubeScope) {
		try{
			Map<String, CollectionInfo> coll = CacheAccessor.getIndexServiceCache().getCompleteCollectionInfo(collectionID.toLowerCase(), collectionDomain);
			return Response.status(200).entity(prettygson.toJson(coll, new TypeToken<Map<String,CollectionInfo>>(){}.getType() )).build();
		}
		catch(JsonSyntaxException e){
			return Response.status(500).entity("Could not parse the serialized CollectionInfo into a List<CollectionInfo>").build();
		}
		catch(IndexNotFoundException ex){
			
			return Response.status(500).entity("No index instance available").build();
		}
    }
	
	
	@POST
    @Path("/setCompleteCollectionInfo")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response setCompleteCollectionInfo(
    		@FormParam(value = "cci") String completeCollectionInformation,
    		@HeaderParam("gcube-scope") String gcubeScope) {
		
		CacheAccessor.getIndexServiceCache().clearCompleteCache();
		
		if(completeCollectionInformation==null || completeCollectionInformation.isEmpty()) return Response.status(Status.BAD_REQUEST).entity("No 'completeCollectionInformation' parameter specified").build();
		String collectionID = null;
		try{
			collectionID = ((CollectionInfo)prettygson.fromJson(completeCollectionInformation, new TypeToken<CollectionInfo>(){}.getType())).getId();
		}
		catch(JsonSyntaxException ex){
			logger.debug("Could not parse a serialized org.gcube.rest.index.common.entities.CollectionInfo");
		}
		if(collectionID==null) return Response.status(Status.BAD_REQUEST).entity("Specified serialized CollectionInfo class instance is unparseable").build();
		completeCollectionInformation = "{\"cci\":\""+Toolbox.encode(completeCollectionInformation)+"\"}";
		IndexResponse response = IndexAccessor.getFullTextNode().addRecord(Constants.COMPLETE_COLLECTION_INFORMATION, collectionID.toLowerCase(), completeCollectionInformation);
		String modType = response.isCreated() ? "Created" : "Updated";
		return Response.status(Status.CREATED).entity(modType+" mappings of collection: "+response.getId()).build();
    }
	
	

	@DELETE
    @Path("/deleteCompleteCollectionInfo")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response deleteCompleteCollectionInfo(@QueryParam("collectionID") String collectionID, @HeaderParam("gcube-scope") String gcubeScope) {
		
		CacheAccessor.getIndexServiceCache().clearCompleteCache();
		
		if(collectionID==null||collectionID.isEmpty()) return Response.status(Status.BAD_REQUEST).entity("No collection identifier specified").build();
		
		DeleteResponse response = IndexAccessor.getFullTextNode().getIndexClient().prepareDelete(Constants.COMPLETE_COLLECTION_INFORMATION, Constants.INDEX_TYPE, collectionID.toLowerCase()).get();
		
		String responseStr = response.isFound() ? 
							"Deleted complete information of collection "+collectionID.toLowerCase() : 
							"Could not find any complete information for collection "+collectionID.toLowerCase()+" to delete!";
		return Response.status(Status.OK).entity(responseStr).build();
		
    }
	
	
	@GET
    @Path("/getCollectionFieldsAlias")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response getCollectionFieldsAlias(@QueryParam("collectionID") String collectionID, @QueryParam("collectionDomain") String collectionDomain, @DefaultValue("true") @QueryParam("fromIndexToView") boolean fromIndexToView, @HeaderParam("gcube-scope") String gcubeScope) {
		Map<String, Map<String, String>> res = CacheAccessor.getIndexServiceCache().getCollectionFieldsAlias(collectionID.toLowerCase(), collectionDomain, fromIndexToView);
		return Response.status(200).entity(prettygson.toJson(res)).build();
    }
	
	
	@POST
    @Path("/setCollectionFieldsAlias")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response setCollectionFieldsAlias(
    		@QueryParam("collectionID") String collectionID,
    		@FormParam(value = "mappingsJSON") String mappingsJSON,
    		@HeaderParam("gcube-scope") String gcubeScope) {
		
		if(collectionID==null||collectionID.isEmpty()) return Response.status(Status.BAD_REQUEST).entity("No collection identifier specified").build();
		if(mappingsJSON==null||mappingsJSON.isEmpty()) return Response.status(Status.BAD_REQUEST).entity("No mappings json is provided").build();
		
		CacheAccessor.getIndexServiceCache().clearCompleteCache();
		
		Map<String, CollectionInfo> collInfos = CacheAccessor.getIndexServiceCache().getCompleteCollectionInfo(collectionID, null);
		CollectionInfo collInfo;
		if(collInfos==null || collInfos.isEmpty())
			return Response.status(Status.NO_CONTENT).entity("No collection with id "+collectionID +" was found! Please create one first").build();
		else {
			collInfo = collInfos.get(collectionID);
			Map<String,String> collFieldAlias = prettygson.fromJson(mappingsJSON, new TypeToken<Map<String,String>>(){}.getType());
			for(Map.Entry<String,String> entry : collFieldAlias.entrySet()){
				FieldConfig fc = collInfo.getCollectionFieldsConfigs().get(entry.getKey());
				if(fc==null)
					collInfo.getCollectionFieldsConfigs().put(entry.getKey(), new FieldConfig(entry.getValue()));
				else
					collInfo.getCollectionFieldsConfigs().get(entry.getKey()).setFieldNameAlias(entry.getValue());
			}
			return setCompleteCollectionInfo(prettygson.toJson(collInfo), gcubeScope);
		}
		
    }
	
	
	@DELETE
    @Path("/deleteCollectionFieldsAlias")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response deleteCollectionFieldsAlias(@QueryParam("collectionID") String collectionID, @HeaderParam("gcube-scope") String gcubeScope) {
		
		if(collectionID==null||collectionID.isEmpty()) return Response.status(Status.BAD_REQUEST).entity("No collection identifier specified").build();
		
		CacheAccessor.getIndexServiceCache().clearCompleteCache();

		Map<String,CollectionInfo> collInfos = CacheAccessor.getIndexServiceCache().getCompleteCollectionInfo(collectionID, null);
		CollectionInfo collInfo;
		if(collInfos==null || collInfos.isEmpty()) //no need to do something
			return Response.status(Status.NO_CONTENT).entity("No collection fields aliases were found to delete").build();
		else {
			collInfo = collInfos.get(collectionID);
			for(String key : collInfo.getCollectionFieldsConfigs().keySet())
				collInfo.getCollectionFieldsConfigs().get(key).setFieldNameAlias(null);
			return setCompleteCollectionInfo(prettygson.toJson(collInfo), gcubeScope);
		}
		
    }
	
	
	
	
	
	
	@POST
    @Path("/search")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED + "; " + "charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response search(@FormParam(value = "query") String queryJson, @QueryParam(value = "collectionDomain") String collectionDomain, @HeaderParam("gcube-scope") String gcubeScope) {
		
		Search_Response searchresponse;
		
		Map<String,Map<String,String>> mapAlias = prettygson.fromJson((String)getCollectionFieldsAlias("", collectionDomain, false, gcubeScope).getEntity(), new TypeToken<Map<String,Map<String,String>>>(){}.getType());
		Map<String,Map<String,String>> inversedMapAlias = prettygson.fromJson((String)getCollectionFieldsAlias("", collectionDomain, true, gcubeScope).getEntity(), new TypeToken<Map<String,Map<String,String>>>(){}.getType());

		Map<String,List<String>> flatMapAlias = MapTransformations.flatMap(mapAlias);
		Map<String,List<String>> flatInversedMapAlias = MapTransformations.flatMap(inversedMapAlias);
		
		Map<String, CollectionInfo> collectionInfoMap = prettygson.fromJson((String)getCompleteCollectionInfo("", collectionDomain, gcubeScope).getEntity(), new TypeToken<Map<String, CollectionInfo>>(){}.getType());
		
		try{
			queryJson = Toolbox.decode(queryJson);
		}
		catch(DecoderException ex){
			
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Could not access or apply the collection field alias names").build();
		}

		Query query = prettygson.fromJson(queryJson , Query.class);
	
		SearchRequestBuilder search_results = IndexSearchFunctions.buildSearchRequest(query, inversedMapAlias, collectionInfoMap);
		
		
		//------------------------------------------------------------------------------------------
		
		
		//add also any Facets requested
		IndexFacet indexFacet = IndexFacetFunctions.buildFacetRequest(collectionInfoMap);
		
		boolean noneFacetType = indexFacet.isNoneFacetType();
		for(TermsBuilder aggr : indexFacet.getListOfAggregations()) {
			search_results.addAggregation(aggr);
		}
		 
		//------------------------------------------------------------------------------------------
		logger.debug("===========SUBMITTED QUERY========== "+query.toString());
		
		//Response
		searchresponse = IndexResponseFunctions.buildSearchResponse(search_results, inversedMapAlias, flatInversedMapAlias, noneFacetType);
		
		
		return Response.status(Status.OK).entity(prettygson.toJson(searchresponse)).build();
    }

	
	
	
}
