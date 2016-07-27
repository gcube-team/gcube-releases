package org.gcube.elasticsearch;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.events.KeyValueEvent;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.carrot2.elasticsearch.ClusteringAction;
import org.carrot2.elasticsearch.ClusteringAction.ClusteringActionRequestBuilder;
import org.carrot2.elasticsearch.ClusteringAction.ClusteringActionResponse;
import org.carrot2.elasticsearch.DocumentGroup;
import org.carrot2.elasticsearch.ListAlgorithmsAction;
import org.carrot2.elasticsearch.ListAlgorithmsAction.ListAlgorithmsActionResponse;
import org.carrot2.elasticsearch.LogicalField;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.gcube.elasticsearch.entities.ClusterResponse;
import org.gcube.elasticsearch.helpers.ElasticSearchHelper;
import org.gcube.elasticsearch.helpers.QueryParser;
import org.gcube.elasticsearch.parser.ElasticSearchParser;
import org.gcube.indexmanagement.common.IndexException;
import org.gcube.indexmanagement.common.IndexType;
import org.gcube.indexmanagement.resourceregistry.RRadaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class FullTextNodeHelpers implements Serializable {
	private static final long serialVersionUID = 1L;

	static final Logger logger = LoggerFactory.getLogger(FullTextNodeHelpers.class);
	
	private static final long RSTIMEOUT = 30;
	
	/**
	 * Executes the query of gRS2 locator of maximum maxHits records (if >0)
	 * 
	 * @param queryString
	 * @param maxHits
	 * @return gRS2 locator of the results
	 * @throws IndexException
	 * @throws GRS2WriterException
	 */
	public static String query(
			final Client indexClient, 
			final String queryString, 
			final Integer from, 
			Integer maxHits, 
			final Set<String> securityIdentifiers, 
			final FTNodeCache cache,
			final Integer defaultMaxResults,
			final Integer maxFragmentCnt,
			final Integer maxFragmentSize,
			final RRadaptor rradaptor,
			final String indexName,
			final ExecutorService executorService) throws GRS2WriterException, IndexException {

		logger.info("queryString received : " + queryString);
		logger.info("securityIdentifiers  : " + securityIdentifiers);
		logger.info("maxHits              : " + maxHits);
		
		final long starttime = System.currentTimeMillis();
		long starttime_part = System.currentTimeMillis();
		
		
		long starttime_part_t = System.currentTimeMillis();
		ElasticSearchParser parsedQueryContainer = new ElasticSearchParser(queryString, rradaptor, filterSecurityIdentifiers(securityIdentifiers));
		long endtime_part_t = System.currentTimeMillis();
		logger.info(" ~> time to create the ElasticSearchParser : " + (endtime_part_t - starttime_part_t) / 1000.0 + " secs");
		
		starttime_part_t = System.currentTimeMillis();
		final List<String> projections = parsedQueryContainer.getProjects();
		endtime_part_t = System.currentTimeMillis();
		logger.info(" ~> projections : " + projections);
		logger.info(" ~> time to get the projections from parser : " + (endtime_part_t - starttime_part_t) / 1000.0 + " secs");
		
		starttime_part_t = System.currentTimeMillis();
		final QueryBuilder qb = parsedQueryContainer.parse();
		endtime_part_t = System.currentTimeMillis();
		logger.info(" ~> query builder : " + qb);
		logger.info(" ~> time to parse the query in the parser : " + (endtime_part_t - starttime_part_t) / 1000.0 + " secs");
		
		logger.info("query after parse : " + qb.toString());
		
		starttime_part_t = System.currentTimeMillis();
		final List<SimpleEntry<String, String>> sortBys = parsedQueryContainer.getSortBys();
		endtime_part_t = System.currentTimeMillis();
		logger.info(" ~> sortbys : " + sortBys);
		logger.info(" ~> time to get the sortbys from parser : " + (endtime_part_t - starttime_part_t) / 1000.0 + " secs");
		
		final boolean distinct = parsedQueryContainer.getDistincts().size() > 0;
		
		logger.info(" ~> distinct : " + distinct);
		logger.info(" ~> time to get distinct from parser : " + (endtime_part_t - starttime_part_t) / 1000.0 + " secs");
		
		starttime_part_t = System.currentTimeMillis();
		final Set<String> collIDs = parsedQueryContainer.getCollections();
		endtime_part_t = System.currentTimeMillis();
		logger.info(" ~> time to get the collection from parser : " + (endtime_part_t - starttime_part_t) / 1000.0 + " secs");
		
		logger.info("collectionID of query : " + collIDs);

		starttime_part_t = System.currentTimeMillis();
		final Set<String> indexTypes = ElasticSearchHelper.getIndexTypesByCollectionIDs(cache.indexTypesByCollIDs, collIDs, indexClient, indexName);
		endtime_part_t = System.currentTimeMillis();
		logger.info(" ~> time to get the indexTypes from index (or cache) : " + (endtime_part_t - starttime_part_t) / 1000.0 + " secs");
		
		
		logger.info("indexTypes for collectionIDs : " + indexTypes);

		logger.info("cache : " + cache);
		logger.info("cache indextypes    by coll id    : " + cache.indexTypesByCollIDs);
		logger.info("cache presentables    by idx type : " + cache.presentableFieldsPerIndexType);
		logger.info("cache searchables     by idx type : " + cache.searchableFieldsPerIndexType);
		logger.info("cache highlightables  by idx type : " + cache.highlightableFieldsPerIndexType);
		
		final List<String> presentables = QueryParser.createPresentableForIndexTypes(cache.presentableFieldsPerIndexType, indexTypes);
		logger.info("presentables for index types : " + presentables);

		final List<String> searchables = QueryParser.createSearchablesForIndexTypes(cache.searchableFieldsPerIndexType, indexTypes);
		logger.info("searchables for index types : " + searchables);
		
		final List<String> highlightables = QueryParser.createHighlightablesForIndexTypes(cache.highlightableFieldsPerIndexType, indexTypes);
		logger.info("highlightables for index types : " + highlightables);
		
		
		
		
		List<String> projectedFields = new ArrayList<String>(projections);
		
		if (projectedFields.contains(IndexType.WILDCARD))
			projectedFields = presentables;
		if (projectedFields.contains(IndexType.SNIPPET))
			projectedFields.remove(IndexType.SNIPPET);
		if (projectedFields.contains(IndexType.DOCID_FIELD)) // it will be returned all the times
			projectedFields.remove(IndexType.DOCID_FIELD);
		
		long endtime_part = System.currentTimeMillis();
		logger.info("parsing time : " + (endtime_part - starttime_part) / 1000.0 + " secs");
		
		
		if (maxHits < 0 && defaultMaxResults != null){
			logger.trace("max results for query not given. will use global maxResults : " + defaultMaxResults);
			maxHits = defaultMaxResults;
		} else if (maxHits > 0 && defaultMaxResults != null){
			logger.trace("max results for query given." + maxHits + " global maxResults : " + defaultMaxResults);
			maxHits = Math.min(maxHits, defaultMaxResults.intValue());
		}
		
		starttime_part = System.currentTimeMillis();
		final SearchHit[] hits = projections.contains(IndexType.SNIPPET) ? 
					ElasticSearchHelper.queryElasticSearch(indexClient, indexName, qb, maxHits, highlightables, projectedFields, maxFragmentSize, maxFragmentCnt, from, sortBys) 
				  : ElasticSearchHelper.queryElasticSearch(indexClient, indexName, qb, maxHits, from, projectedFields, sortBys);
		
		endtime_part = System.currentTimeMillis();
		logger.info("elasticsearch query time : " + (endtime_part - starttime_part) / 1000.0 + " secs");
		logger.info("Number of hits returned by index : " + hits.length);

		// send an event for the total number of results
		logger.info("emitting key value event with key : " + IndexType.RESULTSNOFINAL_EVENT + " and value : " + hits.length);
		

		final List<String> returnFields = (projections.contains(IndexType.WILDCARD)) ? 
				presentables : projections;
		
		
		
		final RecordWriter<GenericRecord> rsWriter = QueryParser.initRSWriterForSearchHits(returnFields, rradaptor);
		rsWriter.emit(new KeyValueEvent(IndexType.RESULTSNOFINAL_EVENT, String.valueOf(hits.length)));
		final Set<Integer> recHashes = new HashSet<Integer>();
		
		final Runnable writerRun = new Runnable() {
			public void run() {
				try {
					for (SearchHit hit : hits) {
						if (distinct){
							//in distinct we might not return the maximum fields
							
							int recHash = hit.getSourceAsString().hashCode();
							if (recHashes.contains(recHash)){
								logger.info("duplicate found. skipping..");
								continue;
							}
							recHashes.add(hit.getSourceAsString().hashCode());
						}
						
						// while the reader hasn't stopped reading
						if (!QueryParser.writeSearchHitInResultSet(hit, rsWriter, returnFields, maxFragmentCnt, RSTIMEOUT))
							break;
					}
					if (rsWriter.getStatus() != Status.Dispose)
						rsWriter.close();
				} catch (Exception e) {
					logger.error("Error during search.", e);
					try {
						if (rsWriter.getStatus() != Status.Dispose)
							rsWriter.close();
					} catch (Exception ex) {
						logger.error("Error while closing RS writer.", ex);
					}
				}
				logger.info("total query time : " + (System.currentTimeMillis() - starttime) / 1000.0 + " secs");
			}
		};
		
		executorService.execute(writerRun);

		logger.info("results locator : " + rsWriter.getLocator());

		return rsWriter.getLocator().toString();
	}
	
	
	
	public static String queryStream(
			final Client indexClient, 
			final String queryString, 
			Integer maxHits, 
			final FTNodeCache cache,
			final Set<String> securityIdentifiers,
			final Integer defaultMaxResults,
			final Integer maxFragmentCnt,
			final Integer maxFragmentSize,
			final RRadaptor rradaptor,
			final String indexName,
			final ExecutorService executorService) throws GRS2WriterException, IndexException {

		logger.info("queryString received : " + queryString);
		logger.info("maxHits              : " + maxHits);
		
		final long starttime = System.currentTimeMillis();
		long starttime_part = System.currentTimeMillis();
		ElasticSearchParser parsedQueryContainer = new ElasticSearchParser(queryString, rradaptor, filterSecurityIdentifiers(securityIdentifiers));
		final List<String> projections = parsedQueryContainer.getProjects();
		final QueryBuilder qb = parsedQueryContainer.parse();
		
		logger.info("query after parse : " + qb.toString());
		
		final List<SimpleEntry<String, String>> sortBys = parsedQueryContainer.getSortBys();
		
		@SuppressWarnings("unused")
		final boolean distinct = parsedQueryContainer.getDistincts().size() > 0;
		
		final Set<String> collIDs = parsedQueryContainer.getCollections();
		logger.info("collectionID of query : " + collIDs);

		final Set<String> indexTypes = ElasticSearchHelper.getIndexTypesByCollectionIDs(cache.indexTypesByCollIDs, collIDs, indexClient, indexName);
		logger.info("indexTypes for collectionIDs : " + indexTypes);

		logger.info("cache : " + cache);
		logger.info("cache indextypes    by coll id    : " + cache.indexTypesByCollIDs);
		logger.info("cache presentables    by idx type : " + cache.presentableFieldsPerIndexType);
		logger.info("cache searchables     by idx type : " + cache.searchableFieldsPerIndexType);
		logger.info("cache highlightables  by idx type : " + cache.highlightableFieldsPerIndexType);
		
		final List<String> presentables = QueryParser.createPresentableForIndexTypes(cache.presentableFieldsPerIndexType, indexTypes);
		logger.info("presentables for index types : " + presentables);

		final List<String> searchables = QueryParser.createSearchablesForIndexTypes(cache.searchableFieldsPerIndexType, indexTypes);
		logger.info("searchables for index types : " + searchables);
		
		final List<String> highlightables = QueryParser.createHighlightablesForIndexTypes(cache.highlightableFieldsPerIndexType, indexTypes);
		logger.info("highlightables for index types : " + highlightables);
		
		
		
		
		List<String> projectedFields = new ArrayList<String>(projections);
		if (projectedFields.contains(IndexType.WILDCARD))
			projectedFields = presentables;
		if (projectedFields.contains(IndexType.SNIPPET))
			projectedFields.remove(IndexType.SNIPPET);
		
		long endtime_part = System.currentTimeMillis();
		logger.info("parsing time : " + (endtime_part - starttime_part) / 1000.0 + " secs");
		
		
		if (maxHits < 0 && defaultMaxResults != null){
			logger.trace("max results for query not given. will use global maxResults : " + defaultMaxResults);
			maxHits = defaultMaxResults;
		} else if (maxHits > 0 && defaultMaxResults != null){
			logger.trace("max results for query given." + maxHits + " global maxResults : " + defaultMaxResults);
			maxHits = Math.min(maxHits, defaultMaxResults.intValue());
		}
		
		
		long numberOfHits = ElasticSearchHelper.queryCountElasticSearch(indexClient, indexName, qb);

		if (maxHits > 0 && numberOfHits > maxHits)
			numberOfHits = maxHits;

		logger.info("Number of hits returned by index : " + numberOfHits);
		
		final List<String> returnFields = (projections.contains(IndexType.WILDCARD)) ? 
				presentables : projections;

		final RecordWriter<GenericRecord> rsWriter = QueryParser.initRSWriterForSearchHits(returnFields, rradaptor);
		// send an event for the total number of results

		logger.info("emitting key value event with key : " + IndexType.RESULTSNOFINAL_EVENT + " and value : " + numberOfHits);

		rsWriter.emit(new KeyValueEvent(IndexType.RESULTSNOFINAL_EVENT, String.valueOf(numberOfHits)));

		
		
		
		final List<String> projectedFieldsList = projectedFields;
		final int fHits = maxHits;
		
		final Runnable writerRun = new Runnable() {
			public void run() {
				try {
					SearchResponse scrollResp = projections.contains(IndexType.SNIPPET) ? 
							ElasticSearchHelper.queryElasticSearchScroll(indexClient, indexName, qb, fHits, highlightables, projectedFieldsList, maxFragmentSize, maxFragmentCnt, sortBys) 
						  : ElasticSearchHelper.queryElasticSearchScroll(indexClient, indexName, qb, fHits, projectedFieldsList, sortBys);

					int hits = 0;

					while (true) {
						scrollResp = ElasticSearchHelper.getNextSearchResponse(indexClient, scrollResp);
						logger.info("hits from scroll : " + scrollResp.getHits().getHits().length);

						for (SearchHit hit : scrollResp.getHits()) {
							// while the reader hasn't stopped reading
							if (!QueryParser.writeSearchHitInResultSet(hit, rsWriter, returnFields, maxFragmentCnt, RSTIMEOUT))
								break;
						}
						if (rsWriter.getStatus() != Status.Dispose)
							rsWriter.close();

						if (scrollResp.getHits().getHits().length == 0) {
							break;
						}
						hits++;
						if (hits > fHits)
							break;
					}
				} catch (Exception e) {
					logger.error("Error during search.", e);
					try {
						if (rsWriter.getStatus() != Status.Dispose)
							rsWriter.close();
					} catch (Exception ex) {
						logger.error("Error while closing RS writer.", ex);
					}
				}
				
				logger.info("total query time : " + (System.currentTimeMillis() - starttime) / 1000.0 + " secs");
			}
		};
		
		executorService.execute(writerRun);

		logger.info("results locator : " + rsWriter.getLocator());

		return rsWriter.getLocator().toString();
	}
	
	
	private static Set<String> filterSecurityIdentifiers(Set<String> sids){
		
		if (sids == null)
			return Sets.newHashSet();
		
		final Predicate<String> predicate = new Predicate<String>() {
		    @Override
		    public boolean apply(String input) {
		    	boolean ret = Strings.isNullOrEmpty(input) == false;
		    	
		        return ret;
		    }
		};
		
		return Sets.newHashSet(Iterables.filter(sids, predicate));
	}
	
	
	
	
	public static Map<String, Integer> frequentTerms(
			final Client indexClient, 
			final String queryString, 
			Integer maxTerms, 
			final Set<String> securityIdentifiers, 
			final FTNodeCache cache,
			final RRadaptor rradaptor,
			final String indexName) throws GRS2WriterException, IndexException {

		logger.info("queryString received : " + queryString);
		logger.info("securityIdentifiers  : " + securityIdentifiers);
		logger.info("maxTerms             : " + maxTerms);
		
		final long starttime = System.currentTimeMillis();
		long starttime_part = System.currentTimeMillis();
		
		
		long starttime_part_t = System.currentTimeMillis();
		ElasticSearchParser parsedQueryContainer = new ElasticSearchParser(queryString, rradaptor, filterSecurityIdentifiers(securityIdentifiers));
		long endtime_part_t = System.currentTimeMillis();
		logger.info(" ~> time to create the ElasticSearchParser : " + (endtime_part_t - starttime_part_t) / 1000.0 + " secs");
		
		starttime_part_t = System.currentTimeMillis();
		final List<String> projections = parsedQueryContainer.getProjects();
		endtime_part_t = System.currentTimeMillis();
		logger.info(" ~> projections : " + projections);
		logger.info(" ~> time to get the projections from parser : " + (endtime_part_t - starttime_part_t) / 1000.0 + " secs");
		
		starttime_part_t = System.currentTimeMillis();
		final QueryBuilder qb = parsedQueryContainer.parse();
		endtime_part_t = System.currentTimeMillis();
		logger.info(" ~> query builder : " + qb);
		logger.info(" ~> time to parse the query in the parser : " + (endtime_part_t - starttime_part_t) / 1000.0 + " secs");
		
		logger.info("query after parse : " + qb.toString());
		
		starttime_part_t = System.currentTimeMillis();
		final List<SimpleEntry<String, String>> sortBys = parsedQueryContainer.getSortBys();
		endtime_part_t = System.currentTimeMillis();
		logger.info(" ~> sortbys : " + sortBys);
		logger.info(" ~> time to get the sortbys from parser : " + (endtime_part_t - starttime_part_t) / 1000.0 + " secs");
		
		final boolean distinct = parsedQueryContainer.getDistincts().size() > 0;
		
		logger.info(" ~> distinct : " + distinct);
		logger.info(" ~> time to get distinct from parser : " + (endtime_part_t - starttime_part_t) / 1000.0 + " secs");
		
		starttime_part_t = System.currentTimeMillis();
		final Set<String> collIDs = parsedQueryContainer.getCollections();
		endtime_part_t = System.currentTimeMillis();
		logger.info(" ~> time to get the collection from parser : " + (endtime_part_t - starttime_part_t) / 1000.0 + " secs");
		
		logger.info("collectionID of query : " + collIDs);

		starttime_part_t = System.currentTimeMillis();
		final Set<String> indexTypes = ElasticSearchHelper.getIndexTypesByCollectionIDs(cache.indexTypesByCollIDs, collIDs, indexClient, indexName);
		endtime_part_t = System.currentTimeMillis();
		logger.info(" ~> time to get the indexTypes from cache : " + (endtime_part_t - starttime_part_t) / 1000.0 + " secs");
		
		
		logger.info("indexTypes for collectionIDs : " + indexTypes);

		logger.info("cache : " + cache);
		logger.info("cache indextypes    by coll id    : " + cache.indexTypesByCollIDs);
		logger.info("cache presentables    by idx type : " + cache.presentableFieldsPerIndexType);
		logger.info("cache searchables     by idx type : " + cache.searchableFieldsPerIndexType);
		logger.info("cache highlightables  by idx type : " + cache.highlightableFieldsPerIndexType);
		
		final List<String> presentables = QueryParser.createPresentableForIndexTypes(cache.presentableFieldsPerIndexType, indexTypes);
		logger.info("presentables for index types : " + presentables);

		final List<String> searchables = QueryParser.createSearchablesForIndexTypes(cache.searchableFieldsPerIndexType, indexTypes);
		logger.info("searchables for index types : " + searchables);
		
		final List<String> highlightables = QueryParser.createHighlightablesForIndexTypes(cache.highlightableFieldsPerIndexType, indexTypes);
		logger.info("highlightables for index types : " + highlightables);
		
		
		
		
		List<String> projectedFields = new ArrayList<String>(projections);
		
		if (projectedFields.contains(IndexType.WILDCARD))
			projectedFields = presentables;
		if (projectedFields.contains(IndexType.SNIPPET))
			projectedFields.remove(IndexType.SNIPPET);
		if (projectedFields.contains(IndexType.DOCID_FIELD)) // it will be returned all the times
			projectedFields.remove(IndexType.DOCID_FIELD);
		
		long endtime_part = System.currentTimeMillis();
		logger.info("parsing time : " + (endtime_part - starttime_part) / 1000.0 + " secs");
		
		
		starttime_part = System.currentTimeMillis();
		final Map<String, Integer> hits = 
					ElasticSearchHelper.termsFacetElasticSearch(indexClient, indexName, qb, maxTerms, projectedFields); 
		
		endtime_part = System.currentTimeMillis();
		logger.info("elasticsearch query time : " + (endtime_part - starttime_part) / 1000.0 + " secs");
		logger.info("Number of hits returned by index : " + hits.size());

		
		logger.info("total query time : " + (System.currentTimeMillis() - starttime) / 1000.0 + " secs");

		return hits;
	}
	
	
	
	
	public static List<ClusterResponse> clustering(
			final Client indexClient, 
			String query,
			String queryHint, 
			Integer clustersCount, 
			String urlField,
			List<String> titleFields, 
			List<String> contentFields,
			List<String> languageFields,
			RRadaptor rradaptor,
			Set<String> securityIdentifiers,
			String algorithm,
			Integer searchHits) {
		
		Map<String, LogicalField> fieldsMap = new HashMap<String, LogicalField>();
		fieldsMap.put(urlField, LogicalField.URL);
		
		if (titleFields != null && titleFields.size() > 0)
			for (String field : titleFields)
				fieldsMap.put(field, LogicalField.TITLE);
		
		
		if (contentFields != null && contentFields.size() > 0)
			for (String field : contentFields)
				fieldsMap.put(field, LogicalField.CONTENT);
		
		
		if (languageFields != null && languageFields.size() > 0)
			for (String field : languageFields)
				fieldsMap.put(field, LogicalField.LANGUAGE);
		
		
		
		if (searchHits == null)
			searchHits = clustersCount * 50;
		
		ListAlgorithmsActionResponse ral = ListAlgorithmsAction.INSTANCE.newRequestBuilder(indexClient).get();
		logger.info("algorithms : " +  ral.getAlgorithms());
		
		if (ral.getAlgorithms().contains(algorithm) == false){
			throw new IllegalArgumentException("algorithm : " + algorithm + " not in : " + ral.getAlgorithms());
		}
		
		long starttime_part_t = System.currentTimeMillis();
		ElasticSearchParser parsedQueryContainer = new ElasticSearchParser(query, rradaptor, filterSecurityIdentifiers(securityIdentifiers));
		long endtime_part_t = System.currentTimeMillis();
		logger.info(" ~> time to create the ElasticSearchParser : " + (endtime_part_t - starttime_part_t) / 1000.0 + " secs");
		
		starttime_part_t = System.currentTimeMillis();
		final List<String> projections = parsedQueryContainer.getProjects();
		endtime_part_t = System.currentTimeMillis();
		logger.info(" ~> projections : " + projections);
		logger.info(" ~> time to get the projections from parser : " + (endtime_part_t - starttime_part_t) / 1000.0 + " secs");
		
		starttime_part_t = System.currentTimeMillis();
		final QueryBuilder qb = parsedQueryContainer.parse();
		endtime_part_t = System.currentTimeMillis();
		logger.info(" ~> query builder : " + qb);
		logger.info(" ~> time to parse the query in the parser : " + (endtime_part_t - starttime_part_t) / 1000.0 + " secs");

		
		SearchRequestBuilder srb = indexClient.prepareSearch(FullTextNode.ACTIVE_INDEX)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setSize(searchHits)
				.setQuery(qb);
		
		for (String field : fieldsMap.keySet()){
			srb = srb.addField(field);
		}
		
		
		logger.info("search request : " +  srb.toString());
		
		ClusteringActionRequestBuilder ca = ClusteringAction.INSTANCE.newRequestBuilder(indexClient)
				.setAlgorithm(algorithm)
				.setQueryHint(queryHint)
				.setMaxHits(0)
				.setSearchRequest(srb.request())
				.addAttribute("LingoClusteringAlgorithm.desiredClusterCountBase", clustersCount);
		
		for (Entry<String, LogicalField> e : fieldsMap.entrySet()){
			ca =  ca.addFieldMapping(e.getKey(), e.getValue());
			
		}
		
		logger.info("clustering request : " +  ca.toString());
		
		ClusteringActionResponse car = ca.get();
		
		logger.info("clustering response : " +  car.toString());
		
		//SearchHit[] hits = car.getSearchResponse().getHits().getHits();
		
		DocumentGroup[] groups = car.getDocumentGroups();
		
		logger.info("number of clusters found : " +  groups.length);
		
		List<ClusterResponse> response = Lists.newArrayList();
		
		
		List<String> allFields = Lists.newArrayList(titleFields);
		allFields.addAll(contentFields);
		
		for (DocumentGroup group : groups){
			logger.info("cluster name : " + group.getLabel() + ", score : " + group.getScore());

			Double score = group.getScore();
			String clusterName = group.getLabel();
			
			List<MultiGetItemResponse> documents = ElasticSearchHelper.getMultipleDocumentsOfAlias(indexClient, FullTextNode.ACTIVE_INDEX, Arrays.asList(group.getDocumentReferences()));
			List<String> docs = Lists.newArrayListWithCapacity(documents.size());
			
			logger.info("cluster contains " + documents.size() + " documents");
			
			for (MultiGetItemResponse mgir : documents){
				logger.info("\t" + mgir.getResponse().getSourceAsString());
				
				String doc = extractValues(allFields, mgir.getResponse().getSource());
				
				docs.add(doc);
			}
			
			response.add(new ClusterResponse(clusterName, score, docs));
		}
		
		return response;
		
	}
	
	public static String extractValues(List<String> fields, Map<String, Object> map){
		List<String> values = Lists.newArrayList();
		
		for (String field : fields){
			if (map.containsKey(field))
				values.add(map.get(field).toString());
		}
		
		return Joiner.on(", ").join(values);
		
	}
	
	public static String extractValues2(final List<String> fields, Map<String, Object> map){
		final Function<Object, String> function = new Function<Object, String>() {
			@Override
			public String apply(Object input) {
				return input.toString();
			}
		};
		
		final FluentIterable<String> values = FluentIterable
				.from(Maps.filterKeys(map, Predicates.in(fields)).values())
				.transform(function);
		
		return Joiner.on(", ").join(values);
	}
	
	
	
	public static void main(String[] args) {
		Map<String, Object> map = ImmutableMap.<String, Object>builder()
				.put("key1", "value1")
				.put("key2", "value2")
				.put("key3", "value3")
				.build();
		
		List<String> list = ImmutableList.<String>builder()
				//.add("key1")
				.add("key2")
				.build();
		
		String ex1 = extractValues(list, map);
		
		String ex2 = extractValues(list, map);
		
		System.out.println(ex1);
		System.out.println(ex2);
		
//		Set<String> mysids = null;
//		System.out.println(filterSecurityIdentifiers(mysids));
//		
//		
//		mysids = Sets.newHashSet();
//		System.out.println(filterSecurityIdentifiers(mysids));
//		
//		mysids = Sets.newHashSet("");
//		mysids.add("sid1");
//		mysids.add("");
//		mysids.add("sid2");
//		System.out.println(filterSecurityIdentifiers(mysids));
//		
//		mysids = Sets.newHashSet("", "");
//		System.out.println(filterSecurityIdentifiers(mysids));
		
	}
}
