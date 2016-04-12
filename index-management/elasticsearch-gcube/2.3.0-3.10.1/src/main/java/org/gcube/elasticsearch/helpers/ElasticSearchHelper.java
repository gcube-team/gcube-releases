package org.gcube.elasticsearch.helpers;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.admin.indices.alias.get.GetAliasesResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.flush.FlushResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequestBuilder;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.gcube.elasticsearch.exceptions.ElasticSearchHelperException;
import org.gcube.indexmanagement.common.FullTextIndexType;
import org.gcube.indexmanagement.common.IndexType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.CharStreams;

public class ElasticSearchHelper {
	private static final Logger logger = LoggerFactory
			.getLogger(ElasticSearchHelper.class);

	private static long SCROLL_TIMEOUT = 100000;
	private static int MAX_RESULTS = 1000;

	private static final String HIGHLIGHT_PRE_TAG = "&lt;b&gt;";
	private static final String HIGHLIGHT_POST_TAG = "&lt;/b&gt;";

	public static final String SECURITY_FIELD = "sid";

	public static final String STOPWORDS_FILENAME = "stopwords.txt";
	private static List<String> STOPWORDS = null;

	static final String FILTER_BY_LENGTH_SCRIPT = "filter_by_length";
	
	static final String FREQUENT_TERMS_FACET = "frequent_terms";
	static final Integer FREQUENT_TERMS_MIN_LENGTH = 3;
	static final String ALL_COLLECTIONS_FACET_NAME = "ALL_COLLECTIONS";
	static final String ALL_LANGUAGES_PER_COLLECTION_FACET_NAME = "ALL_LANGUAGES_PER_COLLECTION";

	static {
		try (InputStream is = ElasticSearchHelper.class.getClassLoader()
				.getResourceAsStream(STOPWORDS_FILENAME)) {

			String stopWordsStr = CharStreams.toString(new InputStreamReader(
					is, Charsets.UTF_8));

			STOPWORDS = Splitter.on(CharMatcher.BREAKING_WHITESPACE)
					.trimResults().omitEmptyStrings().splitToList(stopWordsStr);
		} catch (Exception e) {
			logger.error("not able to load stopwords", e);
		}
		logger.info("stopwords : " + STOPWORDS);
	}
	
	
	public static void commit(Client client, String indexName) {
		try {
			logger.info("flush request: " + indexName);
			long before = System.currentTimeMillis();
			FlushResponse flushResponse = client.admin().indices()
					.prepareFlush(indexName).get();
			long after = System.currentTimeMillis();
			logger.info("Time for the flush request : " + (after - before)
					/ 1000.0 + " secs");
			logger.info("flush response  failed shards: "
					+ flushResponse.getFailedShards());
			logger.info("refresh request : " + indexName);
			before = System.currentTimeMillis();
			RefreshResponse refreshResponse = client.admin().indices()
					.prepareRefresh(indexName).get();
			after = System.currentTimeMillis();
			logger.info("Time for the flush request : " + (after - before)
					/ 1000.0 + " secs");
			logger.info("refresh response failed shards : "
					+ refreshResponse.getFailedShards());
		} catch (Exception e) {
			logger.error("Exception while commiting:", e);
		}
	}

	public static Map<String, Integer> termsFacetElasticSearch(Client client,
			String indexName, QueryBuilder qb, int maxTerms,
			List<String> projections) {

		try {
			SearchRequestBuilder srb = client.prepareSearch(indexName).setSize(0)
					.setQuery(qb);
	
			srb.addFacet(FacetBuilders.termsFacet(FREQUENT_TERMS_FACET)
					.fields(Iterables.toArray(projections, String.class))
					.size(maxTerms)
					//.script("term.length() > " + FREQUENT_TERMS_MIN_LENGTH + " ? true: false")
					.script(FILTER_BY_LENGTH_SCRIPT)
					.exclude(STOPWORDS.toArray()));
	
			srb.setFetchSource(createFetchSourceArray(projections), null);
			srb.addField(IndexType.DOCID_FIELD);
	
			logger.trace("query request : " + srb.toString());
			logger.info("query request : " + srb.toString());
			SearchResponse response = srb.get();
			logger.info("query time : " + response.getTookInMillis());
			logger.trace("query response : " + response);
	
			TermsFacet facet = response.getFacets().facet(FREQUENT_TERMS_FACET);
	
			Map<String, Integer> map = new LinkedHashMap<String, Integer>();
	
			for (TermsFacet.Entry entry : facet) {
				if (entry.getTerm().string().length() > FREQUENT_TERMS_MIN_LENGTH)
					map.put(entry.getTerm().string(), entry.getCount());
				else 
					logger.trace(entry.getTerm().string() + " longer than : " + FREQUENT_TERMS_MIN_LENGTH);
			}
			
			if (logger.isTraceEnabled()){
				logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
				for (Entry<String, Integer> term : map.entrySet()){
					logger.info(" ~> " + term.getKey() + " : " + term.getValue());
				}
				logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			}
	
			return map;
		} catch (Exception e) {
			logger.warn("error while searching for terms of index : " + indexName + ". will return empty list", e);
			return Maps.newHashMap();
		}
	}

	public static Set<String> allCollectionsOfIndexElasticSearch(Client client,
			String indexName) {

		try {
			SearchRequestBuilder srb = client.prepareSearch(indexName).setSize(0)
					.setQuery(QueryBuilders.matchAllQuery());
	
			srb.addFacet(FacetBuilders.termsFacet(ALL_COLLECTIONS_FACET_NAME)
					.fields(IndexType.COLLECTION_FIELD).size(1000));
	
			logger.trace("query request : " + srb.toString());
			logger.info("query request : " + srb.toString());
			SearchResponse response = srb.get();
			logger.info("query time : " + response.getTookInMillis());
			logger.trace("query response : " + response);
	
			TermsFacet facet = response.getFacets().facet(
					ALL_COLLECTIONS_FACET_NAME);
	
			Set<String> collections = Sets.newHashSet();
	
			for (TermsFacet.Entry entry : facet) {
				collections.add(entry.getTerm().string());
			}
	
			return collections;
		} catch (Exception e) {
			logger.warn("error while searching for collection of index : " + indexName + ". will return empty list", e);
			return Sets.newHashSet();
		}
	} 

	public static Set<String> allLanguagesOfIndexElasticSearch(Client client,
			String indexName, String collection) {

		try {
			SearchRequestBuilder srb = client
					.prepareSearch(indexName)
					.setSize(0)
					.setQuery(
							QueryBuilders.termQuery(IndexType.COLLECTION_FIELD,
									collection));
	
			srb.addFacet(FacetBuilders
					.termsFacet(ALL_LANGUAGES_PER_COLLECTION_FACET_NAME)
					.fields(IndexType.LANGUAGE_FIELD).size(1000));
	
			logger.trace("query request : " + srb.toString());
			logger.info("query request : " + srb.toString());
			SearchResponse response = srb.get();
			logger.info("query time : " + response.getTookInMillis());
			logger.trace("query response : " + response);
	
			TermsFacet facet = response.getFacets().facet(
					ALL_LANGUAGES_PER_COLLECTION_FACET_NAME);
	
			Set<String> langs = Sets.newHashSet();
	
			for (TermsFacet.Entry entry : facet) {
				langs.add(entry.getTerm().string());
			}
	
			return langs;
		} catch (Exception e) {
			logger.warn("error while searching for languages of index : " + indexName + ". will return empty list", e);
			return Sets.newHashSet();
		}
	}

	public static SearchHit[] queryElasticSearch(Client client,
			String indexName, QueryBuilder qb, int maxHits,
			List<String> projections, List<SimpleEntry<String, String>> sortBys) {
		return queryElasticSearch(client, indexName, qb, maxHits, null,
				projections, 0, 0, 0, sortBys);
	}

	public static SearchHit[] queryElasticSearch(Client client,
			String indexName, QueryBuilder qb, int maxHits, int from,
			List<String> projections, List<SimpleEntry<String, String>> sortBys) {
		return queryElasticSearch(client, indexName, qb, maxHits, null,
				projections, 0, 0, from, sortBys);
	}

	public static SearchHit[] queryElasticSearch(Client client,
			String indexName, QueryBuilder qb, int maxHits,
			List<String> highlightedFields, List<String> projections,
			int maxFragmentSize, int maxFragmentCnt, int from,
			List<SimpleEntry<String, String>> sortBys) {

		SearchRequestBuilder srb = client.prepareSearch(indexName)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setSize(MAX_RESULTS).setFrom(from).setQuery(qb);

		srb.setFetchSource(createFetchSourceArray(projections), null);
		srb.addField(IndexType.DOCID_FIELD);

		if (highlightedFields != null) {
			for (String hilightedField : highlightedFields)
				if (!SnippetsHelper.NOT_HIGHLIGHTED_FIELDS
						.contains(hilightedField))
					srb.addHighlightedField(hilightedField, maxFragmentSize,
							maxFragmentCnt);
			srb.setHighlighterOrder("score");
			// srb.setHighlighterRequireFieldMatch(true);
			srb.setHighlighterPreTags(HIGHLIGHT_PRE_TAG);
			srb.setHighlighterPostTags(HIGHLIGHT_POST_TAG);
		}

		if (sortBys != null) {
			for (Entry<String, String> e : sortBys) {
				String sortByField = e.getKey();
				SortOrder sortOrder = e.getValue().equalsIgnoreCase("ASC") ? SortOrder.ASC
						: SortOrder.DESC;
				srb.addSort(SortBuilders.fieldSort(sortByField + "_raw").order(
						sortOrder));
			}
		}

		if (maxHits > 0)
			srb.setSize(maxHits);

		logger.trace("query request : " + srb.toString());
		logger.info("query request : " + srb.toString());
		SearchResponse response = srb.get();
		logger.info("query time : " + response.getTookInMillis());
		if (logger.isTraceEnabled())
			logger.trace("query response : " + response);

		return response.getHits().getHits();
	}

	public static SearchResponse queryElasticSearchScroll(Client client,
			String indexName, QueryBuilder qb, int maxHits,
			List<String> projections, List<SimpleEntry<String, String>> sortBys) {
		return queryElasticSearchScroll(client, indexName, qb, maxHits, null,
				projections, 0, 0, sortBys);
	}

	/* TODO: fix score problem */
	public static SearchResponse queryElasticSearchScroll(Client client,
			String indexName, QueryBuilder qb, int maxHits,
			List<String> highlightedFields, List<String> projections,
			int maxFragmentSize, int maxFragmentCnt,
			List<SimpleEntry<String, String>> sortBys) {

		SearchRequestBuilder srb = client
				.prepareSearch(indexName)
				.setTrackScores(true)
				// .setExplain(true)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setScroll(TimeValue.timeValueMillis(SCROLL_TIMEOUT))
				.setQuery(qb);

		srb.setFetchSource(createFetchSourceArray(projections), null);
		srb.addField(IndexType.DOCID_FIELD);

		if (maxHits > 0)
			srb.setSize(maxHits);

		if (highlightedFields != null) {
			for (String hilightedField : highlightedFields)
				if (!SnippetsHelper.NOT_HIGHLIGHTED_FIELDS
						.contains(hilightedField))
					srb.addHighlightedField(hilightedField, maxFragmentSize,
							maxFragmentCnt);
			srb.setHighlighterOrder("score");
			// srb.setHighlighterRequireFieldMatch(true);
			srb.setHighlighterPreTags(HIGHLIGHT_PRE_TAG);
			srb.setHighlighterPostTags(HIGHLIGHT_POST_TAG);

		}

		if (sortBys != null) {
			for (Entry<String, String> e : sortBys) {
				String sortByField = e.getKey();
				SortOrder sortOrder = e.getValue().equalsIgnoreCase("ASC") ? SortOrder.ASC
						: SortOrder.DESC;
				srb.addSort(SortBuilders.fieldSort(sortByField + "_raw").order(
						sortOrder));
			}
		}

		logger.info("query request : " + srb.toString());
		SearchResponse scrollResponse = srb.get();
		logger.info("query response : " + scrollResponse);

		return scrollResponse;
	}

	public static Long collectionDocumentsCountElasticSearch(Client client,
			String indexName, String collectionID) {

		// CountRequestBuilder countRequest =
		// client.prepareCount(indexName).setQuery(termQuery(IndexType.COLLECTION_FIELD,
		// collectionID));
		//
		// logger.info("query request : " + countRequest.request());
		// CountResponse countResponse = countRequest.execute().actionGet();
		// logger.info("query response : " + countResponse);

		String queryString = IndexType.COLLECTION_FIELD + " == " + collectionID;
		QueryBuilder qb = QueryBuilders.queryString(queryString);

		Long count = queryCountElasticSearch(client, indexName, qb);

		return count;

	}

	public static Long queryCountElasticSearch(Client client, String indexName,
			QueryBuilder qb) {

		CountRequestBuilder countRequest = client.prepareCount(indexName)
				.setQuery(qb);

		logger.info("query request : " + countRequest.request());
		CountResponse countResponse = countRequest.get();
		logger.info("query response : " + countResponse);

		return countResponse.getCount();
	}

	public static SearchResponse getNextSearchResponse(Client client,
			SearchResponse scrollResponse) {
		scrollResponse = client
				.prepareSearchScroll(scrollResponse.getScrollId())
				.setScroll(TimeValue.timeValueMillis(SCROLL_TIMEOUT)).get();

		logger.info("scroll response : " + scrollResponse);

		return scrollResponse;
	}

	public static boolean deleteAllIndices(Client client) {
		logger.info("deleting all indices");

		try {
			DeleteIndexResponse delete = client.admin().indices()
					.delete(new DeleteIndexRequest("_all"))
					.actionGet(2, TimeUnit.MINUTES);

			if (!delete.isAcknowledged()) {
				logger.error("Index wasn't deleted");
			}
			return delete.isAcknowledged();
		} catch (Exception e) {
			logger.error("error deleting all indices", e);
			return false;
		}
	}

	public static boolean delete(Client client, String indexName) {
		logger.info("deleting index : " + indexName);

		// Map<String, IndexMetaData> map = client.admin().cluster().state(new
		// ClusterStateRequest()).actionGet(30,
		// TimeUnit.SECONDS).getState().getMetaData().getIndices();
		// boolean exists = map.containsKey(indexName);

		boolean exists = client
				.admin()
				.indices()
				.exists(new IndicesExistsRequest(indexName)).actionGet()
				.isExists();

		if (!exists) {
			logger.info("index : " + indexName + " does not exist");
			return true;
		} else {
			try {
				logger.info("index : " + indexName + " exists");
				DeleteIndexResponse delete = client
						.admin()
						.indices()
						.delete(new DeleteIndexRequest(indexName))
						.actionGet(2, TimeUnit.MINUTES);
				if (!delete.isAcknowledged()) {
					logger.error("Index wasn't deleted");
				}
	
				logger.info("deleted index : " + indexName + " ? "
						+ delete.isAcknowledged());
	
				return delete.isAcknowledged();
			} catch (Exception e) {
				logger.warn("error while deleting index : " + indexName, e);
				return false;
			}
		}
	}

	public static boolean exists(Client indexClient, String indexName) {
		try {
			IndicesAdminClient iac = indexClient.admin().indices();
			boolean exists = iac.prepareExists(indexName).get().isExists();
			logger.info("index : " + indexName + " exists returned : " + exists);
			return exists;
		} catch (Exception e) {
			logger.warn("failed to query index exist", e);
			return false;
		}
	}

	public static boolean deleteDocuments(Client client, String indexName,
			List<String> docIDs) {
		logger.info("docIds to be deleted : " + docIDs);

		boolean res = true;
		
		for (String docID : docIDs) {
			logger.info("deleting document with " + IndexType.COLLECTION_FIELD
					+ " : " + docID);

			DeleteByQueryRequestBuilder requestBuilder = client
					.prepareDeleteByQuery(indexName).setQuery(
							termQuery(IndexType.DOCID_FIELD, docID));
			logger.info("delete request : " + requestBuilder.request());
			DeleteByQueryResponse response = requestBuilder.get();
			if (response.status() != RestStatus.OK){
				logger.warn("delete of : " + docID + " failed");
				res = false;
			}
				
		}
		commit(client, indexName);
		
		return res;
	}

	public static Boolean deleteCollections(Client client, String indexName,
			Collection<String> colls) {
		Boolean ret = true;
		for (String coll : colls) {
			Boolean collDelete = deleteCollection(client, indexName, coll);
			if (!collDelete) {
				logger.warn("couldn't delete collection : " + coll);
				ret = false;
			}
		}
		return ret;
	}

	public static Boolean deleteCollection(Client client, String indexName,
			String collID) {
		logger.info("collId to be deleted : " + collID);

		logger.info("deleting document with " + IndexType.COLLECTION_FIELD
				+ " : " + collID);

		DeleteByQueryRequestBuilder requestBuilder = client
				.prepareDeleteByQuery(indexName).setQuery(
						termQuery(IndexType.COLLECTION_FIELD, collID));
		logger.info("delete request : " + requestBuilder.request());
		DeleteByQueryResponse response = requestBuilder.get();

		logger.info("delete response : " + response.toString());

		commit(client, indexName);

		return true;
	}

	public static Boolean collectionExists(Client client, String indexName,
			String collID) {
		logger.info("collId to be checked : " + collID);

		logger.info("checking collection with " + IndexType.COLLECTION_FIELD
				+ " : " + collID);

		SearchRequestBuilder requestBuilder = client.prepareSearch(indexName)
				.setQuery(termQuery(IndexType.COLLECTION_FIELD, collID));
		logger.info("search request : " + requestBuilder.request());
		SearchResponse response = requestBuilder.get();

		if (response.getHits().getTotalHits() > 0) {
			return true;
		} else {
			return false;
		}

	}

	public static Set<String> indicesOfCollection(Client client,
			String indexName, String collID) {
		Set<String> indices = new HashSet<String>();

		logger.info("collId to be checked : " + collID);

		logger.info("checking collection with " + IndexType.COLLECTION_FIELD
				+ " : " + collID);

		SearchRequestBuilder requestBuilder = client.prepareSearch(indexName)
				.setQuery(termQuery(IndexType.COLLECTION_FIELD, collID));
		logger.info("search request : " + requestBuilder.request());
		SearchResponse response = requestBuilder.get();

		for (SearchHit hit : response.getHits().getHits()) {
			indices.add(hit.getIndex());
		}

		return indices;
	}

	public static Set<String> getIndicesOfAlias(Client client, String alias) {

		logger.info("calling aliasResponse");
		GetAliasesResponse aliasResponse = client.admin().indices()
				.prepareGetAliases(alias).get();

		logger.info("alias response : " + aliasResponse);

		ImmutableOpenMap<String, List<AliasMetaData>> aliases = aliasResponse
				.getAliases();

		logger.info("aliases : " + aliases);

		if (aliases == null || aliases.size() == 0)
			return null;

		Set<String> indices = Sets.newHashSet(aliases.keysIt());

		logger.info("indices of alias : " + indices);

		return indices;
	}

	public static List<MultiGetItemResponse> getMultipleDocumentsOfAlias(
			Client client, String alias, Collection<String> docIDs) {

		logger.info("getting multiple documents of alias for alias : " + alias
				+ " docIDs : " + docIDs);
		Set<String> indicesOfAlias = getIndicesOfAlias(client, alias);

		logger.info("indicesOfAlias : " + indicesOfAlias);

		if (indicesOfAlias == null)
			return new ArrayList<MultiGetItemResponse>();

		return getMultipleDocuments(client, indicesOfAlias, docIDs);

	}

	public static List<MultiGetItemResponse> getMultipleDocuments(
			Client client, Set<String> indices, Collection<String> docIDs) {
		List<MultiGetItemResponse> documents = new ArrayList<MultiGetItemResponse>(
				docIDs.size());

		for (String index : indices) {
			MultiGetRequestBuilder grb = new MultiGetRequestBuilder(client)
					.add(index, null, docIDs);
			MultiGetResponse mgr = grb.get();

			for (MultiGetItemResponse response : mgr.getResponses()) {
				if (response != null && response.getResponse() != null
						&& response.getResponse().isExists()) {
					documents.add(response);
					// System.out.println(response.getResponse().getSourceAsString());
				}
			}
		}

		return documents;
	}

	public static Set<String> getLanguagesOfCollection(Client client,
			String indexName, String collection) {
		Set<String> langs = allLanguagesOfIndexElasticSearch(client, indexName,
				collection);
		return langs;
	}

	public static Set<String> getAllCollectionsOfIndex(Client client,
			String indexName) {
		logger.info("getting all collections of index : " + indexName);
		return allCollectionsOfIndexElasticSearch(client, indexName);

		// Set<String> collections = new HashSet<String>();
		// SearchRequestBuilder requestBuilder =
		// client.prepareSearch(indexName).setQuery(QueryBuilders.matchAllQuery()).addField(IndexType.COLLECTION_FIELD);
		// SearchResponse sr = requestBuilder.get();
		// for (SearchHit hit : sr.getHits().getHits()){
		// String collection =
		// hit.getFields().get(IndexType.COLLECTION_FIELD).getValue().toString();
		// collections.add(collection);
		// }
		//
		// return collections;
	}

	public static void clearIndex(Client client, String indexName,
			String indexTypeName) {
		logger.info("index to be deleted : ");
		DeleteByQueryResponse dr = client.prepareDeleteByQuery(indexName)
				.setQuery(QueryBuilders.matchAllQuery())
				.setTypes(indexTypeName).get();
		logger.info("Delete response : " + dr.toString());

		commit(client, indexName);
	}

	public static void insertSimple(String jsonDoc, Client indexClient,
			String indexName, String indexType, Set<String> allowedIndexTypes)
			throws ElasticSearchHelperException {

		if (!allowedIndexTypes.contains(indexType))
			throw new ElasticSearchHelperException("index type : " + indexType
					+ " is not in registered index types : "
					+ allowedIndexTypes);

		IndexResponse response = indexClient.prepareIndex(indexName, indexType)
				.setSource(jsonDoc).get();
		logger.info("indexResponse : " + response);
	}

	public static void insertBulk(BulkRequestBuilder bulkRequest,
			String jsonDoc, Client indexClient, String indexName,
			String indexType, Set<String> allowedIndexTypes)
			throws ElasticSearchHelperException {

		if (!allowedIndexTypes.contains(indexType + "-" + indexName))
			throw new ElasticSearchHelperException("index type : " + indexType
					+ " is not in registered index types : "
					+ allowedIndexTypes);

		IndexRequestBuilder indexRequest = indexClient.prepareIndex(indexName,
				indexType).setSource(jsonDoc);
		bulkRequest.add(indexRequest);
		logger.info("indexRequest : " + indexRequest);
	}

	public static Boolean insertRowSet(BulkRequestBuilder bulkRequest,
			Client client, String indexName, FullTextIndexType idxType,
			Set<String> allowedIndexTypes, String rowsetXML,
			Set<String> securityIdentifiers) {
		logger.trace("indexName : " + indexName);
		logger.trace("idxType : " + idxType.getIndexTypeName());
		logger.trace("allowedIndexTypes : " + allowedIndexTypes);
		logger.trace("rowsetXML : " + rowsetXML);

		try {
			FullTextIndexDocument document = new FullTextIndexDocument(
					rowsetXML);
			Map<String, List<String>> docFields = document.getFields();
			
			if (docFields.containsKey(SECURITY_FIELD))
				logger.debug("document contains security field. using this (" + docFields.get(SECURITY_FIELD)  + ") instead of given : (" + securityIdentifiers + ")");
			else
				docFields.put(SECURITY_FIELD, new ArrayList<String>(securityIdentifiers));
			

			insertBulk(bulkRequest, createJSONObject(document.getFields())
					.string(), client, indexName, idxType.getIndexTypeName(),
					allowedIndexTypes);
			return true;
		} catch (Exception e) {
			logger.warn("Exception while inserting documents", e);
			return false;
		}
	}

	public static XContentBuilder createJSONObject(
			Map<String, ? extends Object> keyValues) throws IOException {
		XContentBuilder xcb = jsonBuilder();

		xcb = xcb.startObject();
		for (Entry<String, ? extends Object> keyvalue : keyValues.entrySet()) {
			if (keyvalue.getValue() instanceof List
					&& ((List<?>) keyvalue.getValue()).size() == 1) {
				// if 1 record only flatten it
				logger.debug("single value : " + keyvalue.getValue());
				String val = ((List<?>) keyvalue.getValue()).get(0).toString();

				xcb = xcb.field(keyvalue.getKey(), val);
			} else {
				logger.debug("multi value : " + keyvalue.getValue());
				xcb = xcb.field(keyvalue.getKey(), keyvalue.getValue());
			}
		}

		return xcb.endObject();
	}
	
	public static Set<String> getIndexTypesByCollectionIDNoCache(String collID,
			Client client, String indexName) {
		Set<String> indexTypes = null;

		logger.info("indexTypes for collectionID not found in cache");
		indexTypes = new HashSet<String>();

		SearchRequestBuilder srb = client.prepareSearch(indexName)
				.setQuery(termQuery(IndexType.COLLECTION_FIELD, collID))
				.setNoFields();

		logger.trace("query request : " + srb.toString());

		SearchResponse response = srb.execute().actionGet();

		logger.trace("query response : " + response);

		for (SearchHit hit : response.getHits().getHits())
			indexTypes.add(hit.getType());

		logger.info("for collectionID : " + collID + " indexTypes found : " + indexTypes);

		return indexTypes;
	}
	
	public static Set<String> getIndexTypesByCollectionIDs(Map<String, Set<String>> indexTypesByCollIDs,
			Set<String> collIDs, Client client, String indexName) {
		Set<String> indexTypes = new HashSet<String>();

		for (String collID : collIDs)
			indexTypes.addAll(getIndexTypesByCollectionID(indexTypesByCollIDs, collID, client, indexName));

		return indexTypes;
	}
	
	public static Set<String> getIndexTypesByCollectionID(Map<String, Set<String>> indexTypesByCollIDs, String collID,
			Client client, String indexName) {
		Set<String> indexTypes = null;

		if (indexTypesByCollIDs.containsKey(collID)) {
			indexTypes = indexTypesByCollIDs.get(collID);
			logger.info("indexTypes for collectionID found in cache");
		} else {
			logger.info("indexTypes for collectionID not found in cache");
			indexTypes = new HashSet<String>();

			SearchRequestBuilder srb = client.prepareSearch(indexName)
					.setQuery(termQuery(IndexType.COLLECTION_FIELD, collID))
					.setNoFields();

			logger.trace("query request : " + srb.toString());

			SearchResponse response = srb.execute().actionGet();

			logger.trace("query response : " + response);

			for (SearchHit hit : response.getHits().getHits())
				indexTypes.add(hit.getType());
		}

		logger.info("for collectionID : " + collID + " indexTypes found : " + indexTypes);

		return indexTypes;
	}
	
	public static String[] createFetchSourceArray(List<String> projections) {
		Set<String> fetchSource = new HashSet<String>();
		if (projections != null && projections.size() > 0)
			fetchSource.addAll(projections);

		// fetchSource.add(IndexType.DOCID_FIELD);

		return fetchSource.toArray(new String[0]);
	}

}
