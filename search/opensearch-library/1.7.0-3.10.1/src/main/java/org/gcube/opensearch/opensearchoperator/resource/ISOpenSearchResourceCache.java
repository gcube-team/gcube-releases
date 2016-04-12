package org.gcube.opensearch.opensearchoperator.resource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A resource cache that caches generic resources, description documents in XML form
 * and OpenSearchResource objects. Used to avoid using the network or querying the 
 * InformationSystem every time a previously retrieved resource is requested 
 * 
 * @author gerasimos.farantatos
 *
 */
public class ISOpenSearchResourceCache {
	/**
	 * Associates description document URLs with description document XML representations
	 */
	public Map<String, String> descriptionDocuments = new ConcurrentHashMap<>();
	/**
	 * Associates description document URLs with OpenSearchResources
	 */
	public Map<String, OpenSearchResource> resources = new ConcurrentHashMap<>();
	/**
	 * Associates description document URLs with OpenSearchResource XML representations
	 */
	public Map<String, String> resourcesXML = new ConcurrentHashMap<>();
	/**
	 * Associates XSLT generic resource names with their XML representations 
	 */
	public Map<String, String> XSLTs = new ConcurrentHashMap<>();
}
