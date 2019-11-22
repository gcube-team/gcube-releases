package org.gcube.socialnetworking.social_data_search_client;

import java.util.List;
import java.util.Set;

import org.gcube.portal.databook.shared.EnhancedFeed;
import org.gcube.socialnetworking.social_data_indexing_common.utils.SearchableFields;

/**
 * The ElasticSearchClient client interface to search in social data.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public interface ElasticSearchClient {

	/**
	 * Given a query, the method find matching enhanced feeds into the elasticsearch index and return 
	 * at most <b>quantity</b> hits starting from <b>from</b>. A multimatch query is performed against all 
	 * searchable fields.
	 * @param query the query to match
	 * @param vreIDS specifies the vre(s) to which the returning feeds must belong
	 * @param from start hits index
	 * @param quantity max number of hits to return starting from <b>from</b>
	 * @return A list of matching enhanced feeds or nothing
	 */
	List<EnhancedFeed> search(String query, Set<String> vreIDS, int from, int quantity);

	/**
	 * Given a query, the method find matching enhanced feeds into the elasticsearch index and return 
	 * at most <b>quantity</b> hits starting from <b>from</b>. The query is performed against one of the searchable fields.
	 * @param query the query to match
	 * @param vreIDS specifies the vre(s) to which the returning feeds must belong
	 * @param from start hits index
	 * @param quantity max number of hits to return starting from <b>from</b>
	 * @param field the field against which the query is performed
	 * @return A list of matching enhanced feeds or nothing
	 */
	List<EnhancedFeed> searchInField(String query, Set<String> vreIDS, int from, int quantity, SearchableFields field);
	
	/**
	 * Delete from the index a document with id docID.
	 * @param docID the id of the doc to delete
	 * @return true on success, false otherwise
	 */
	boolean deleteDocument(String docID);

}
