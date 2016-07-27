package org.gcube.opensearch.opensearchlibrary.urlelements.extensions.sru;

import org.gcube.opensearch.opensearchlibrary.query.QueryBuilder;
import org.gcube.opensearch.opensearchlibrary.query.extensions.sru.SRUQueryBuilder;
import org.gcube.opensearch.opensearchlibrary.urlelements.URLElement;
import org.gcube.opensearch.opensearchlibrary.urlelements.URLElementDecorator;
import org.w3c.dom.Element;

public class SRUURLElement extends URLElementDecorator {
	private Element url;
	
	public SRUURLElement(Element url, URLElement el) {
		super(el);
		this.url = url;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.urlelements.URLElement#getQueryBuilder()
	 */
	public QueryBuilder getQueryBuilder() throws Exception {
		//If indexOffset and pageOffset are specified as optional parameters, their default values are
		//those specified in the URL element
		return new SRUQueryBuilder(el.getQueryBuilder());
	}

}
