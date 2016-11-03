package org.gcube.opensearch.opensearchlibrary.urlelements.extensions.geo;


import java.util.Map;

import org.gcube.opensearch.opensearchlibrary.DescriptionDocument;
import org.gcube.opensearch.opensearchlibrary.urlelements.URLElement;
import org.gcube.opensearch.opensearchlibrary.urlelements.URLElementFactory;
import org.w3c.dom.Element;

/**
 * A factory class for the construction of GeoURLElement objects
 * 
 * @author gerasimos.farantatos
 *
 */
public class GeoURLElementFactory implements URLElementFactory {
	
	URLElementFactory f;
	/**
	 * Creates a new instance of the factory
	 * @param f The {@link URLElementFactory} used to create URL elements which will be next in the chain of responsibility
	 */
	public GeoURLElementFactory(URLElementFactory f) {
		this.f = f;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.urlelements.URLElementFactory#newInstance(Element, DescriptionDocument)
	 */
	public GeoURLElement newInstance(Element url, Map<String, String> nsPrefixes) throws Exception {
		URLElement el = f.newInstance(url, nsPrefixes);
		return new GeoURLElement(url, el);
	}
}
