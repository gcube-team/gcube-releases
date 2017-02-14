package org.gcube.opensearch.opensearchoperator.resource;

/**
 * A OpenSearchResource repository class used to obtain OpenSearch resources by 
 * their description document URL
 * 
 * @author gerasimos.farantatos
 *
 */
public interface ResourceRepository {
	
	/**
	 * Retrieves an OpenSearchResource identified by the description document URL of the
	 * OpenSearch provider associated with it
	 * 
	 * @param descriptionDocumentURL The URL of the description document associated with the OpenSearch
	 * provider associated with the OpenSearchResource to be retrieved
	 * @return The requested OpenSearch resource
	 * @throws Exception
	 */
	public OpenSearchResource get(String descriptionDocumentURL) throws Exception;
}
