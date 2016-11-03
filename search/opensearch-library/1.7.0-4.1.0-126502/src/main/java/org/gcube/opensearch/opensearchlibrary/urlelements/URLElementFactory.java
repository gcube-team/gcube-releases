package org.gcube.opensearch.opensearchlibrary.urlelements;

import java.util.Map;

import org.w3c.dom.Element;

/**
 * Interface of the factory class used to construct URLElement objects
 * 
 * @author gerasimos.farantatos
 *
 */
public interface URLElementFactory {
	
	/**
	 * Creates a new URLElement object
	 * 
	 * @param url An Element instance containing the URL element to be processed
	 * @param dd The DescriptionDocument associated with the URL element to be processed
	 * @return A new URLElement object
	 * @throws Exception If an error occurred during construction
	 */
	public URLElement newInstance(Element url, Map<String, String> nsPrefixes) throws Exception;
}
