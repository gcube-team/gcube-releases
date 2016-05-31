package org.gcube.opensearch.opensearchlibrary.urlelements;


import java.util.Map;

import org.gcube.opensearch.opensearchlibrary.DescriptionDocument;
import org.w3c.dom.Element;

/**
 * A factory class for the construction of ExtendedURLElement objects
 * 
 * @author gerasimos.farantatos
 *
 */
public class ExtendedURLElementFactory implements URLElementFactory {
	
	URLElementFactory f;
	/**
	 * Creates a new instance of the factory
	 * @param f The {@link URLElementFactory} used to create URL elements which will be next in the chain of responsibility
	 */
	public ExtendedURLElementFactory(URLElementFactory f) {
		this.f = f;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.urlelements.URLElementFactory#newInstance(Element, DescriptionDocument)
	 */
	public ExtendedURLElement newInstance(Element url, Map<String, String> nsPrefixes) throws Exception {
		URLElement el = f.newInstance(url, nsPrefixes);
		return new ExtendedURLElement(el, url, nsPrefixes);
	}
}
