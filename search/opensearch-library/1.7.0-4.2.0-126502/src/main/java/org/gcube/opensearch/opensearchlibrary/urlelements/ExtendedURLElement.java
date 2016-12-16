package org.gcube.opensearch.opensearchlibrary.urlelements;

import java.net.URL;
import java.util.Map;

import org.gcube.opensearch.opensearchlibrary.query.QueryBuilder;
import org.w3c.dom.Element;

/**
 * URL element class implementing the URLElement interface, which provides support for extended
 * rel values
 * 
 * @author gerasimos.farantatos
 *
 */
public class ExtendedURLElement implements URLElement {

	private URLElement el;
	
	/**
	 * Creates a new ExtendedURLElement
	 * 
	 * @param el The {@link URLElement} that will be next in the chain of responsibility
	 * @param url An Element instance containing the URL element to be processed
	 * @param nsPrefixes The mapping from namespace URIs to namespace prefixes for all namespaces contained in a description document
	 */
	public ExtendedURLElement(URLElement el, Element url, Map<String, String> nsPrefixes) {
		this.el = el;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.urlelements.URLElement#parse()
	 */
	public void parse() throws Exception {
		
		el.parse();
		if(el.isRelSupported() == false) {
			try {
				new URL(el.getRel());
			}catch(Exception e) {
					throw new Exception("Rel value is not a valid URL", e);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.urlelements.URLElement#isRelSupported()
	 */
	public boolean isRelSupported() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.urlelements.URLElement#getMimeType()
	 */
	public String getMimeType() throws Exception {
		return el.getMimeType();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.urlelements.URLElement#getPageOffset()
	 */
	public int getPageOffset() throws Exception {
		return el.getPageOffset();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.urlelements.URLElement#getIndexOffset())
	 */
	public int getIndexOffset() throws Exception {
		return el.getIndexOffset();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.urlelements.URLElement#getRel()
	 */
	public String getRel() throws Exception {
		return el.getRel();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.urlelements.URLElement#getQueryBuilder()
	 */
	public QueryBuilder getQueryBuilder() throws Exception {
		return el.getQueryBuilder();
	}
}
