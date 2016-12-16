package org.gcube.rest.index.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;

import org.apache.commons.codec.DecoderException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
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
import org.gcube.rest.index.common.apis.IndexServiceI;
import org.gcube.rest.index.common.search.Query;
import org.gcube.rest.index.common.search.SearchResult;
import org.gcube.rest.index.common.search.Search_Response;
import org.gcube.rest.index.common.search.facets.Facet;
import org.gcube.rest.index.common.search.facets.Facets;
import org.gcube.rest.index.common.tools.MapTransformations;
import org.gcube.rest.index.common.tools.Toolbox;
import org.gcube.rest.index.service.accessors.CacheAccessor;
import org.gcube.rest.index.service.accessors.IndexAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


@Path("/")
public class IndexService implements IndexServiceI {

	static final Logger logger = LoggerFactory.getLogger(IndexService.class);

	private final static Gson prettygson = new GsonBuilder().setPrettyPrinting().create();

	
	
	@GET
    @Path("/listCollections")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response listCollections(@HeaderParam("gcube-scope") String gcubeScope) {
		return CacheAccessor.getIndexServiceCache().listCollections();
    }
	

	@GET
    @Path("/getAllCollectionFields")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response getAllCollectionFields(@DefaultValue("true") @QueryParam("aliasFields") boolean aliasFields, @HeaderParam("gcube-scope") String gcubeScope) {
		return CacheAccessor.getIndexServiceCache().getAllCollectionFields(aliasFields);
    }
	
	
	
	@POST
	@Path("/create/{collectionID}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED + "; " + "charset=UTF-8")
    public Response create(
    		@PathParam("collectionID") String collectionID,
    		@HeaderParam("gcube-scope") String gcubeScope){
		
		if(collectionID==null||collectionID.isEmpty()) return Response.status(Status.BAD_REQUEST).entity("No collection identifier specified").build();
		
		try{
			IndexAccessor.getFullTextNode().createIndex(collectionID.toLowerCase());
		}
		catch(IOException ex){
			ex.printStackTrace();
			return Response.status(Status.NOT_MODIFIED).entity("Failed to create index: "+collectionID.toLowerCase()).build();
		}
		return Response.status(Status.CREATED).entity("Created index: "+collectionID.toLowerCase()).build();
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
	@Path("/insert/{collectionID}/{recordID}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED  + "; " + "charset=UTF-8")
    public Response insert(
    		@PathParam("collectionID") String collectionID,
    		@PathParam("recordID") String recordID,
    		@FormParam(value = "recordJSON") String recordJSON,
    		@HeaderParam("gcube-scope") String gcubeScope) {

		if(collectionID==null||collectionID.isEmpty()) return Response.status(Status.BAD_REQUEST).entity("No collection identifier specified").build();
		if(recordJSON==null||recordJSON.isEmpty()) return Response.status(Status.BAD_REQUEST).entity("No record payload is provided").build();
		
		//check if there's a transformer for this record...
		Response resp = getJSONTransformer(collectionID.toLowerCase(), gcubeScope);
		String transformer = (resp.getStatus()!=500) ? 
				((Map<String,String>)prettygson.fromJson((String)resp.getEntity(), new TypeToken<Map<String,String>>(){}.getType())).get(collectionID.toLowerCase()) 
				: null;
		if(transformer!=null && !transformer.isEmpty()){
	        Chainr chainr = Chainr.fromSpec( JsonUtils.jsonToList(transformer) );
	        recordJSON = JsonUtils.toJsonString( chainr.transform( JsonUtils.jsonToObject(recordJSON) ) );
		}
		//now insert
		IndexResponse response = (recordID==null||recordID.isEmpty()) ?
				IndexAccessor.getFullTextNode().addRecord(collectionID.toLowerCase(), recordJSON) :
				IndexAccessor.getFullTextNode().addRecord(collectionID.toLowerCase(), recordID, recordJSON);
				
		String modType = response.isCreated() ? "Created" : "Updated";
		return Response.status(Status.CREATED).entity(modType+" record with id: "+response.getId()).build();
    }
	
	
	@POST
	@Path("/insert/{collectionID}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED + "; " + "charset=UTF-8")
    public Response insert(
    		@PathParam("collectionID") String collectionID,
    		@FormParam(value = "recordJSON") String recordJSON,
    		@HeaderParam("gcube-scope") String gcubeScope){
		return insert(collectionID.toLowerCase(), null, recordJSON, gcubeScope);
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
		if (!response.isAcknowledged())
			Response.status(Status.NOT_MODIFIED).entity("Did not delete collection  " + collectionID.toLowerCase()).build();
		else
			Response.status(200).entity("Deleted collectiList<SearchResult>on  " + collectionID.toLowerCase()).build();
        return Response.status(200).entity("Deleted collection  " + collectionID.toLowerCase()).build();
    }
	
	
	@GET
    @Path("/listFulltextEndpoints")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response listFulltextEndpoints(@QueryParam("scope") String scope, @HeaderParam("gcube-scope") String gcubeScope) {
		return CacheAccessor.getIndexServiceCache().listFulltextEndpoints(scope);
    }


	@GET
    @Path("/getCollectionFieldsAlias")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response getCollectionFieldsAlias(@QueryParam("collectionID") String collectionID, @DefaultValue("true") @QueryParam("fromIndexToView") boolean fromIndexToView, @HeaderParam("gcube-scope") String gcubeScope) {
		return CacheAccessor.getIndexServiceCache().getCollectionFieldsAlias(collectionID.toLowerCase(), fromIndexToView);
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
		mappingsJSON = "{\"mappings\":\""+Toolbox.encode(mappingsJSON)+"\"}";
		IndexResponse response = IndexAccessor.getFullTextNode().addRecord(Constants.MAPPINGS_COLLECTION_NAME, collectionID.toLowerCase(), mappingsJSON);
		String modType = response.isCreated() ? "Created" : "Updated";
		return Response.status(Status.CREATED).entity(modType+" mappings of collection: "+response.getId()).build();
    }
	
	@DELETE
    @Path("/deleteCollectionFieldsAlias")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response deleteCollectionFieldsAlias(@QueryParam("collectionID") String collectionID, @HeaderParam("gcube-scope") String gcubeScope) {
		
		if(collectionID==null||collectionID.isEmpty()) return Response.status(Status.BAD_REQUEST).entity("No collection identifier specified").build();
		
		DeleteResponse response = IndexAccessor.getFullTextNode().getIndexClient().prepareDelete(Constants.MAPPINGS_COLLECTION_NAME, Constants.INDEX_TYPE, collectionID.toLowerCase()).get();
		
		String responseStr = response.isFound() ? 
							"Deleted configuration of collection "+collectionID.toLowerCase() : 
							"Could not find any configuration for collection "+collectionID.toLowerCase()+" to delete!";
		return Response.status(Status.OK).entity(responseStr).build();
		
    }
	
	
	
	@GET
    @Path("/getJSONTransformer")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response getJSONTransformer(@QueryParam("collectionID") String collectionID, @HeaderParam("gcube-scope") String gcubeScope) {
		return CacheAccessor.getIndexServiceCache().getJSONTransformer(collectionID.toLowerCase());
    }
	
	
	@POST
    @Path("/setJSONTransformer")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED + "; " + "charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response setJSONTransformer(
    		@QueryParam("collectionID") String collectionID,
    		@FormParam(value = "transformerJSON") String transformerJSON,
    		@HeaderParam("gcube-scope") String gcubeScope) {
		
		if(collectionID==null||collectionID.isEmpty()) return Response.status(Status.BAD_REQUEST).entity("No collection identifier specified").build();
		if(transformerJSON==null||transformerJSON.isEmpty()) return Response.status(Status.BAD_REQUEST).entity("No transformer json is provided").build();
		transformerJSON = "{\"transformer\":\""+Toolbox.encode(transformerJSON)+"\"}";
		IndexResponse response = IndexAccessor.getFullTextNode().addRecord(Constants.TRANSFORMERS_COLLECTION_NAME, collectionID.toLowerCase(), transformerJSON);
		if(response.isCreated())
			while (CacheAccessor.getIndexServiceCache().getJSONTransformerUncached(collectionID.toLowerCase()).get(collectionID.toLowerCase())==null)
				try{Thread.sleep(100L);}catch(InterruptedException ex){}; //Let's assume you didn't see this... :P It's a way to tackle async events
		String modType = response.isCreated() ? "Created" : "Updated";
		return Response.status(Status.CREATED).entity(modType+" mappings of collection: "+response.getId()).build();
    }
	
	@DELETE
    @Path("/deleteJSONTransformer")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response deleteJSONTransformer(@QueryParam("collectionID") String collectionID, @HeaderParam("gcube-scope") String gcubeScope) {
		
		if(collectionID==null||collectionID.isEmpty()) return Response.status(Status.BAD_REQUEST).entity("No collection identifier specified").build();
		
		DeleteResponse response = IndexAccessor.getFullTextNode().getIndexClient().prepareDelete(Constants.TRANSFORMERS_COLLECTION_NAME, Constants.INDEX_TYPE, collectionID.toLowerCase()).get();
		
		String responseStr = response.isFound() ? 
							"Deleted configuration of collection "+collectionID.toLowerCase() : 
							"Could not find any configuration for collection "+collectionID.toLowerCase()+" to delete!";
		return Response.status(Status.OK).entity(responseStr).build();
		
    }
	
	
	@GET
    @Path("/getCollectionInfo")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response getCollectionInfo(@QueryParam("collectionID") String collectionID, @HeaderParam("gcube-scope") String gcubeScope) {
		return CacheAccessor.getIndexServiceCache().getCollectionInfo(collectionID.toLowerCase());
    }
	
	
	@POST
    @Path("/setCollectionInfo")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED + "; " + "charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response setCollectionInfo(
    		@QueryParam("collectionID") String collectionID,
    		@FormParam(value = "infoJSON") String infoJSON,
    		@HeaderParam("gcube-scope") String gcubeScope) {
		
		if(collectionID==null||collectionID.isEmpty()) return Response.status(Status.BAD_REQUEST).entity("No collection identifier specified").build();
		if(infoJSON==null||infoJSON.isEmpty()) return Response.status(Status.BAD_REQUEST).entity("No collection info json is provided").build();
		infoJSON = "{\"info\":\""+Toolbox.encode(infoJSON)+"\"}";
		IndexResponse response = IndexAccessor.getFullTextNode().addRecord(Constants.DESCRIPTIONS_COLLECTION_NAME, collectionID.toLowerCase(), infoJSON);
		String modType = response.isCreated() ? "Created" : "Updated";
		return Response.status(Status.CREATED).entity(modType+" information of collection: "+response.getId()).build();
    }
	
	@DELETE
    @Path("/deleteCollectionInfo")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response deleteCollectionInfo(@QueryParam("collectionID") String collectionID, @HeaderParam("gcube-scope") String gcubeScope) {
		
		if(collectionID==null||collectionID.isEmpty()) return Response.status(Status.BAD_REQUEST).entity("No collection identifier specified").build();
		
		DeleteResponse response = IndexAccessor.getFullTextNode().getIndexClient().prepareDelete(Constants.DESCRIPTIONS_COLLECTION_NAME, Constants.INDEX_TYPE, collectionID.toLowerCase()).get();
		
		String responseStr = response.isFound() ? 
							"Deleted information of collection "+collectionID.toLowerCase() : 
							"Could not find any information for collection "+collectionID.toLowerCase()+" to delete!";
		return Response.status(Status.OK).entity(responseStr).build();
		
    }
	
	
	
	@POST
    @Path("/search")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED + "; " + "charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response search(@FormParam(value = "query") String queryJson, @HeaderParam("gcube-scope") String gcubeScope) {
			
		long totalHits;
		Search_Response searchresponse;
		BoolQueryBuilder qb;
		BoolQueryBuilder mainQb = QueryBuilders.boolQuery();
		
		Map<String,Map<String,String>> mapAlias = prettygson.fromJson((String)getCollectionFieldsAlias("", false, gcubeScope).getEntity(), new TypeToken<Map<String,Map<String,String>>>(){}.getType());
		Map<String,Map<String,String>> inversedMapAlias = prettygson.fromJson((String)getCollectionFieldsAlias("", true, gcubeScope).getEntity(), new TypeToken<Map<String,Map<String,String>>>(){}.getType());

		Map<String,List<String>> flatMapAlias = MapTransformations.flatMap(mapAlias);
		Map<String,List<String>> flatInversedMapAlias = MapTransformations.flatMap(inversedMapAlias);
		
		try{
			queryJson = Toolbox.decode(queryJson);
		}
		catch(DecoderException ex){
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Could not access or apply the collection field alias names").build();
		}
		
		Query query = prettygson.fromJson(queryJson , Query.class);
		Map<String,Map<String,String>> outerMap = query.get_SearchTerms();
		int position_paging = query.getPosition_paging(); 
		int size_paging = query.getSize_paging();
			
		List<String> list = new ArrayList<String>();
		
		Iterator<String> keySetIteratorOuter = outerMap.keySet().iterator();
		
		while(keySetIteratorOuter.hasNext())
		{ 
			qb = QueryBuilders.boolQuery();
			
			String keyOuter =  keySetIteratorOuter.next(); 	
			
			Map<String,String> innerMap = outerMap.get(keyOuter);
				
			list.add(keyOuter);
			
			Iterator<String> keySetIteratorInner = innerMap.keySet().iterator();
			
			while(keySetIteratorInner.hasNext())
			{ 
				String fieldAliasName = keySetIteratorInner.next(); 
				String fieldValue = innerMap.get(fieldAliasName);
				
				Map<String,String> collection_alias = mapAlias.get(keyOuter);
				if(collection_alias != null)
				{
					String fieldName = collection_alias.get(fieldAliasName);
								
					if(fieldName == null)
					{   // "must" denotes an AND logic - that's used between the fields of the collection 
						qb = qb.must(QueryBuilders.queryStringQuery(fieldValue).field(fieldAliasName));
					}else{
						qb = qb.must(QueryBuilders.queryStringQuery(fieldValue).field(fieldName));
					}
				}else{
					qb = qb.must(QueryBuilders.queryStringQuery(fieldValue).field(fieldAliasName));
				}
			}
			// "should" denotes an OR logic - that's used between the collections
			mainQb = mainQb.should(QueryBuilders.indicesQuery(qb,keyOuter).noMatchQuery("none"));

		}
	
		String[] Indexes = list.toArray(new String[list.size()]);
		
		SearchRequestBuilder search_results = IndexAccessor.getFullTextNode().getIndexClient()
				.prepareSearch(Indexes)
				.setSearchType(SearchType.QUERY_THEN_FETCH)
				.setQuery(mainQb)
				.setFrom(position_paging).setSize(size_paging);
		
		if(!query.getFacetFields().isEmpty()){
			
			//TODO: Change the inverted alias names to have a one to one mapping. Consider having also collectionid within the mapping, 
			//      smth like {collectionid::fieldName , aliasFieldName} mapping
			Set<String> alreadyAdded = new HashSet<String>();
			
			//add also any Facets requested
			GlobalBuilder aggregation = AggregationBuilders.global("aggregations");
			query.getFacetFields().forEach( (fieldAliasName, howMany) -> {
				
				List<String> alias = flatMapAlias.get(fieldAliasName);
				if(alias != null && !alias.isEmpty())
				{
					String fieldName = alias.get(0);
					if(!alreadyAdded.contains(fieldName)){
						aggregation.subAggregation(AggregationBuilders.terms(fieldName).field(fieldName).size(howMany));
						alreadyAdded.add(fieldName);
					}
					
				}
				else{
					if(!alreadyAdded.contains(fieldAliasName)){
						aggregation.subAggregation(AggregationBuilders.terms(fieldAliasName).field(fieldAliasName).size(howMany));
						alreadyAdded.add(fieldAliasName);
					}
				}
				
			});
			search_results.addAggregation(aggregation);
		}
		
		logger.debug("===========SUBMITTED QUERY========== "+query.toString());
		
		
		SearchResponse response = search_results.execute().actionGet();
		totalHits = response.getHits().getTotalHits();
		List<SearchResult> results = new ArrayList<SearchResult>();
		
		SearchHit[] search_hits = response.getHits().getHits();
		
		for (SearchHit hit : search_hits) {
			
			Map<String,Object> source_alias_map = hit.getSource();
			Map<String,Object> source_map = new HashMap<String,Object>();
			Iterator<String> SourceIteratorOuter = source_alias_map.keySet().iterator();
			
			while(SourceIteratorOuter.hasNext())
			{ 
				String fieldName = SourceIteratorOuter.next();
				Object fieldValue = source_alias_map.get(fieldName);
				Map<String,String> collection_inversed_alias = inversedMapAlias.get(hit.getIndex());
				if(collection_inversed_alias != null)
				{
					String fieldAliasName = collection_inversed_alias.get(fieldName);
					if(fieldAliasName == null)
					{
						source_map.put(fieldName, fieldValue);
					}else{
						source_map.put(fieldAliasName, fieldValue);
					}
				}else{
					source_map.put(fieldName, fieldValue);
				}
				
			}
			
			SearchResult searchResult = new SearchResult(hit.getId(),hit.getIndex(),source_map);
			results.add(searchResult);
		}
		
		searchresponse = new Search_Response();
		searchresponse.setTotalHits(totalHits);
		searchresponse.setSearchResultList(results);

		//create facets if requested any
		if(!query.getFacetFields().isEmpty()){
			Map<String,Aggregation> aggregations = ((Global)response.getAggregations().get("aggregations")).getAggregations().getAsMap();
			Facets facets = new Facets();
			aggregations.forEach(
				(fieldName,terms) -> {
					Facet facet = new Facet();
					
					List<Bucket> buckets; 
					if(terms instanceof StringTerms) buckets = ((StringTerms)terms).getBuckets();
					else if(terms instanceof DoubleTerms) buckets = ((DoubleTerms)terms).getBuckets();
					else if(terms instanceof LongTerms)	buckets = ((LongTerms)terms).getBuckets();
					else buckets = ((UnmappedTerms)terms).getBuckets();
					
					buckets.forEach(
						(bucket)->{
							facet.addPair(bucket.getKeyAsString(), bucket.getDocCount());
						}
					);
					
					List<String> inversedAlias = flatInversedMapAlias.get(fieldName);
					if(inversedAlias != null)
					{
						String fieldAliasName = inversedAlias.get(0);
						facets.addFacet(fieldAliasName, facet);
						
					}else{
						facets.addFacet(fieldName, facet);
					}
				}
			);
			searchresponse.setFacets(facets);
		}
		
		
		return Response.status(Status.CREATED).entity(prettygson.toJson(searchresponse)).build();
    }

	
	
	
}
