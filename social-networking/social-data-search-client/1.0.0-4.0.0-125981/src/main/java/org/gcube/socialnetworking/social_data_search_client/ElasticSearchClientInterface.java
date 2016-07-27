package org.gcube.socialnetworking.social_data_search_client;

import java.util.List;
import java.util.Set;

import org.gcube.portal.databook.shared.EnhancedFeed;

/**
 * The ElasticSearch client interface.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 *
 */
public interface ElasticSearchClientInterface {

	/**
	 * Given a query, the method find matching enhanced feeds into the elasticsearch index and return 
	 * at most <b>quantity</b> hits starting from <b>from</b>.
	 * @param query the query to match
	 * @param vreIDS specifies the vre(s) to which the returning feeds must belong
	 * @param from start hits index
	 * @param quantity max number of hits to return starting from <b>from</b>
	 * @return A list of matching enhanced feeds or nothing
	 */
	public List<EnhancedFeed> searchInEnhancedFeeds(String query, Set<String> vreIDS, int from, int quantity);
	
	/**
	 * Delete from the index a document with id docID.
	 * @param docID the id of the doc to delete
	 * @return true on success, false otherwise
	 */
	public boolean deleteDocument(String docID);

}
