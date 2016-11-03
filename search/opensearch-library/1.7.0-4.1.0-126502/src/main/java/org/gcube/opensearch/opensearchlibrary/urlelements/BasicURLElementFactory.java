package org.gcube.opensearch.opensearchlibrary.urlelements;


import java.util.Map;

import org.gcube.opensearch.opensearchlibrary.DescriptionDocument;
import org.w3c.dom.Element;

/**
 * A factory class for the construction of BasicURLElement objects
 * 
 * @author gerasimos.farantatos
 *
 */
public class BasicURLElementFactory implements URLElementFactory {
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.urlelements.URLElementFactory#newInstance(Element, DescriptionDocument)
	 */
	public BasicURLElement newInstance(Element url, Map<String, String> nsPrefixes) throws Exception {
		return new BasicURLElement(url, nsPrefixes);
	}
}
