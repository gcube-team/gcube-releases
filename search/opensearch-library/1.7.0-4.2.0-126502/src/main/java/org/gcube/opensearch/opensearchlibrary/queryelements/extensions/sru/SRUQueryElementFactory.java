package org.gcube.opensearch.opensearchlibrary.queryelements.extensions.sru;

import java.util.Map;

import org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement;
import org.gcube.opensearch.opensearchlibrary.queryelements.QueryElementFactory;
import org.w3c.dom.Element;

/**
 * A factory class for the construction of SRUQueryElement objects
 * 
 * @author gerasimos.farantatos
 *
 */
public class SRUQueryElementFactory implements QueryElementFactory {

	QueryElementFactory f;
	
	/**
	 * Creates a new instance of the factory
	 * @param f The {@link QueryElementFactory} used to create Query elements which will be next in the chain of responsibility
	 */
	public SRUQueryElementFactory(QueryElementFactory f) {
		this.f = f;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElementFactory#newInstance(Element)
	 */
	public SRUQueryElement newInstance(Element query, Map<String, String> nsPrefixes) throws Exception {
		QueryElement el = f.newInstance(query, nsPrefixes);
		return new SRUQueryElement(query, nsPrefixes, el);
	}
}