package org.gcube.opensearch.opensearchlibrary.urlelements.extensions.geo;

import org.gcube.opensearch.opensearchlibrary.query.QueryBuilder;
import org.gcube.opensearch.opensearchlibrary.query.extensions.geo.GeoQueryBuilder;
import org.gcube.opensearch.opensearchlibrary.urlelements.URLElement;
import org.gcube.opensearch.opensearchlibrary.urlelements.URLElementDecorator;
import org.w3c.dom.Element;

public class GeoURLElement extends URLElementDecorator {
	private Element url;
	
	public GeoURLElement(Element url, URLElement el) {
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
		return new GeoQueryBuilder(el.getQueryBuilder());
	}

}
