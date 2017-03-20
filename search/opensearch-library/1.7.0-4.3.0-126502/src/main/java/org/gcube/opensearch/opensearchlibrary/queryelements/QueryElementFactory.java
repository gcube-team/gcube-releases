package org.gcube.opensearch.opensearchlibrary.queryelements;

import java.util.Map;

import org.w3c.dom.Element;

/**
 * Interface of the factory class used to construct QueryElement objects
 * 
 * @author gerasimos.farantatos
 *
 */
public interface QueryElementFactory {
	
	/**
	 * Creates a new QueryElement object
	 * 
	 * @param query The Element corresponding to a query element contained in a description document
	 * @return A new QueryElement object
	 * @throws Exception If an error occurred during construction
	 */
	public QueryElement newInstance(Element query, Map<String, String> nsPrefixes) throws Exception;
}
