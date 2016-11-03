package org.gcube.opensearch.opensearchlibrary.queryelements;


import java.util.Map;

import org.w3c.dom.Element;

/**
 * A factory class for the construction of BasicQueryElement objects
 * 
 * @author gerasimos.farantatos
 *
 */
public class BasicQueryElementFactory implements QueryElementFactory {

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElementFactory#newInstance(Element)
	 */
	public BasicQueryElement newInstance(Element query, Map<String, String> nsPrefixes) throws Exception {
		return new BasicQueryElement(query);
	}
}
