/**
 * 
 */
package org.gcube.socialnetworking.socialdataindexer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.Attachment;
import org.gcube.portal.databook.shared.Comment;
import org.gcube.portal.databook.shared.EnhancedFeed;
import org.gcube.portal.databook.shared.Feed;
import org.gcube.socialnetworking.social_data_indexing_common.ex.BulkInsertionFailedException;
import org.gcube.socialnetworking.social_data_indexing_common.utils.ElasticSearchRunningCluster;
import org.gcube.socialnetworking.social_data_indexing_common.utils.IndexFields;
import org.gcube.vremanagement.executor.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The SocialDataIndexerPlugin synchronizes and indexes data coming from the cassandra 
 * cluster in the elasticsearch engine.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class SocialDataIndexerPlugin extends Plugin<SocialDataIndexerPluginDeclaration>{

	//Logger
	private static Logger logger = LoggerFactory.getLogger(SocialDataIndexerPlugin.class);

	// the cluster name
	private String clusterName;

	// list of hosts to contact
	private List<String> hostsToContact;

	// private port number
	private List<Integer> portNumbers;

	// the elasticsearch client
	private TransportClient client;

	// connection to cassandra
	private DatabookStore store;

	private int count = 0;

	public SocialDataIndexerPlugin(SocialDataIndexerPluginDeclaration pluginDeclaration){

		super(pluginDeclaration);
		logger.debug("Constructor");

	}

	/**{@inheritDoc}*/
	@Override
	public void launch(Map<String, Object> inputs){

		try{

			// TODO auth 2.0 doesn't allow scope set anymore, we will need to pass a valid token
			// for the scope in which we would like the plugin to be run

			String scope = null;

			// retrieve the scope from inputs, if any
			if(inputs.containsKey("scope"))
				scope = (String) inputs.get("scope");
			else
				logger.error("Scope variable is not set. The context will be evaluated later...");

			// connection to cassandra
			store = new DBCassandraAstyanaxImpl(scope);

			// retrieve ElasticSearch Endpoint and set hosts/port numbers
			ElasticSearchRunningCluster elasticCluster = new ElasticSearchRunningCluster(scope);

			// save info
			clusterName = elasticCluster.getClusterName();
			hostsToContact = elasticCluster.getHosts();
			portNumbers = elasticCluster.getPorts();

			logger.info("Creating elasticsearch client connection for hosts = " + hostsToContact + ", ports = " + portNumbers + " and "
					+ " cluster's name = " + clusterName);

			// set cluster's name to check and the sniff property to true.
			// Cluster's name: each node must have this name.
			// Sniff property: allows the client to recover cluster's structure.
			// Look at https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/transport-client.html
			Settings settings = Settings.settingsBuilder()
					.put("cluster.name", this.clusterName) // force unique cluster's name check
					.put("client.transport.sniff", true)
					.build();

			// build the client
			client = TransportClient.builder().settings(settings).build();

			// add the nodes to contact
			int reachableHosts = 0;
			for (int i = 0; i < hostsToContact.size(); i++){
				try {
					client.addTransportAddress(
							new InetSocketTransportAddress(
									InetAddress.getByName(hostsToContact.get(i)), portNumbers.get(i))
							);
					reachableHosts ++;
				} catch (UnknownHostException e) {
					logger.error("Error while adding " + hostsToContact.get(i) + ":" + portNumbers.get(i) + " as host to be contacted.");
				}
			}

			if(reachableHosts == 0){
				logger.error("Unable to reach elasticsearch cluster. Exiting ...");
				return;
			}

			logger.info("Connection to ElasticSearch cluster done. Synchronization starts running...");
			final long init = System.currentTimeMillis();

			// we build a index reachable under the path /social/enhanced_feed
			List<String> vreIds = store.getAllVREIds();

			// save feeds & comments
			for (String vreID : vreIds) {
				try{
					List<Feed> feeds = store.getAllFeedsByVRE(vreID);
					addEnhancedFeedsInBulk(feeds, init);
					logger.info("Number of indexed feeds is " + feeds.size() + " for vre " + vreID);
				}catch(Exception e){
					logger.error("Exception while saving feeds/comments into the index for vre " + vreID, e);
					continue;
				}

			}

			logger.info("Inserted " + count + " docs into the index");

			// refresh data (note that the index must be refreshed according to the new value of _timestamp)
			client.admin().indices().prepareRefresh().execute().actionGet();

			// delete documents with timestamp lower than init
			deleteDocumentsWithTimestampLowerThan(init);

			long end = System.currentTimeMillis();
			logger.info("Synchronization thread ends running. It took " + (end - init) + " milliseconds " + 
					" that is " + (double)(end - init)/(1000.0 * 60.0) + " minutes.");

		}catch(Exception e){

			logger.error("Error while synchronizing data.", e);

		}finally{
			// close connection to elasticsearch
			if(client != null){
				logger.info("Closing connection to elasticsearch cluster. " + client.toString());
				client.close();
			}
			
			// close connection to cassandra
			if(store != null){
				logger.info("Closing connection to cassandra nodes. " + store.toString());
				store.closeConnection();
			}
		}
	}

	/**
	 * Add feeds into the elasticsearch index.
	 * @param feeds
	 * @param init is the timestamp that will be put in the document
	 * @throws BulkInsertionFailedException 
	 */
	private void addEnhancedFeedsInBulk(List<Feed> feeds, final long init) throws BulkInsertionFailedException {
		logger.debug("Starting bulk insert enhanced feeds operation");
		BulkProcessor bulkProcessor = BulkProcessor.builder(
				client,  
				new BulkProcessor.Listener() {
					@Override
					public void beforeBulk(long executionId,
							BulkRequest request) { 
						logger.debug("Going to execute new bulk composed of {} actions", request.numberOfActions());
					} 
					@Override
					public void afterBulk(long executionId,
							BulkRequest request,
							BulkResponse response) {
						logger.debug("Executed bulk composed of {} actions", request.numberOfActions());
						if (response.hasFailures()) {
							logger.warn("There was failures while executing bulk", response.buildFailureMessage());
							if (logger.isDebugEnabled()) {
								for (BulkItemResponse item : response.getItems()) {
									if (item.isFailed()) {
										logger.debug("Error for {}/{}/{} for {} operation: {}", item.getIndex(),
												item.getType(), item.getId(), item.getOpType(), item.getFailureMessage());
									}
								}
							}
						}
					} 

					@Override
					public void afterBulk(long executionId,
							BulkRequest request,
							Throwable failure) {
						logger.error("Error executing bulk", failure);
						if(failure instanceof NoNodeAvailableException){
							throw new RuntimeException("No node available. Exiting..."); 
						}
					} 
				})
				.setBulkActions(1000) 
				.setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB)) 
				.setFlushInterval(TimeValue.timeValueSeconds(5)) 
				.setConcurrentRequests(0) 
				.setBackoffPolicy(
						BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(50), 8)) 
						.build();

		// save feeds
		for (Feed feed: feeds) {

			String enhFeedUUID = null;
			try{
				// enhance and convert
				String json = enhanceAndConvertToJson(feed);
				enhFeedUUID = feed.getKey();
				IndexRequest ind = new IndexRequest(IndexFields.INDEX_NAME, IndexFields.EF_FEEDS_TABLE, enhFeedUUID)// set timestamp
				.timestamp(String.valueOf(init)) // add json object
				.source(json);

				// add
				bulkProcessor.add(ind);

				count++;
			}catch(Exception e){
				logger.error("Skip inserting feed with id "  + enhFeedUUID, e);
			}
		}

		// close bulk operations
		try {
			bulkProcessor.awaitClose(60000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			logger.debug("Interrupted while waiting for awaitClose()", e);
		}
	}

	/**
	 * Build enhanced feed and convert to json
	 * @param feed to enhanced and convert
	 * @return json object
	 * @throws Exception 
	 */
	private String enhanceAndConvertToJson(Feed feed) throws Exception {

		boolean isMultiFileUpload = feed.isMultiFileUpload();

		// retrieve attachments
		ArrayList<Attachment> attachments = new ArrayList<Attachment>();
		if (isMultiFileUpload) {
			logger.debug("Retrieving attachments for feed with id="+feed.getKey());
			attachments = (ArrayList<Attachment>) store.getAttachmentsByFeedId(feed.getKey());
		}

		// retrieve comments
		ArrayList<Comment> comments = getAllCommentsByFeed(feed.getKey());

		// build enhanced feed
		EnhancedFeed enFeed = new EnhancedFeed(feed, false, false, comments, attachments);

		// convert to json
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(enFeed);
	}

	/**
	 * retrieve and sort comments given a feed id
	 * @param feedid
	 * @return
	 */
	private ArrayList<Comment> getAllCommentsByFeed(String feedid) {
		//		logger.debug("Asking comments for " + feedid);
		ArrayList<Comment> toReturn =  (ArrayList<Comment>) store.getAllCommentByFeed(feedid);
		Collections.sort(toReturn);
		return toReturn;
	}

	/**
	 * Delete disabled feeds in the index
	 * @param timestamp delete feeds whose _timestamp is lower than timestamp
	 * @return
	 */
	public void deleteDocumentsWithTimestampLowerThan(long timestamp) {

		logger.debug("Removing docs with timestamp lower than " + timestamp);

		// query on _timestamp field
		BoolQueryBuilder filter = QueryBuilders.boolQuery();
		filter.must(QueryBuilders.matchAllQuery());
		filter.filter(QueryBuilders.rangeQuery("_timestamp").gte(0).lt(timestamp));

		SearchResponse scrollResp = client.prepareSearch(IndexFields.INDEX_NAME)
				.setSize(100) // get 100 elements at most at time
				.setScroll(new TimeValue(60000)) // keep alive this query for 1 minute
				.setQuery(filter)
				.execute()
				.actionGet();

		int deleteDocs = 0;

		//Scroll until no more hits are returned
		while (true) {

			for (SearchHit hit : scrollResp.getHits().getHits()) {

				String docID = hit.getId();
				DeleteResponse response = client.prepareDelete(IndexFields.INDEX_NAME, IndexFields.EF_FEEDS_TABLE, docID).get();
				//logger.debug("deleting doc with id = " + docID +  "...found? " + response.isFound()); // found of course..
				if(response.isFound())
					deleteDocs ++;

			}

			// more enhanced feeds requested
			scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();

			//Break condition: No hits are returned
			if (scrollResp.getHits().getHits().length == 0) {
				logger.debug("No more hits to delete");
				break;
			}
		}

		logger.info("Number of delete documents is " + deleteDocs);
	}

	/**{@inheritDoc}*/
	@Override
	protected void onStop() throws Exception {
		logger.debug("onStop()");
		Thread.currentThread().interrupt();
	}
}
