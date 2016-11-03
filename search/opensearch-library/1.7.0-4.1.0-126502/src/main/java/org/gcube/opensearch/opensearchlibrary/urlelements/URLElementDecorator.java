package org.gcube.opensearch.opensearchlibrary.urlelements;

import org.gcube.opensearch.opensearchlibrary.query.QueryBuilder;

public abstract class URLElementDecorator implements URLElement {

	protected URLElement el;
	
	public URLElementDecorator(URLElement el) {
		this.el = el;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.urlelements.URLElement#parse()
	 */
	@Override
	public void parse() throws Exception {
		el.parse();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.urlelements.URLElement#getMimeType()
	 */
	@Override
	public String getMimeType() throws Exception {
		return el.getMimeType();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.urlelements.URLElement#getPageOffset()
	 */
	@Override
	public int getPageOffset() throws Exception {
		return el.getPageOffset();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.urlelements.URLElement#getIndexOffset()
	 */
	@Override
	public int getIndexOffset() throws Exception {
		return el.getIndexOffset();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.urlelements.URLElement#getRel()
	 */
	@Override
	public String getRel() throws Exception {
		return el.getRel();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.urlelements.URLElement#isRelSupported()
	 */
	@Override
	public boolean isRelSupported() throws Exception {
		return el.isRelSupported();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.urlelements.URLElement#getQueryBuilder()
	 */
	@Override
	public QueryBuilder getQueryBuilder() throws Exception {
		return el.getQueryBuilder();
	}

}
