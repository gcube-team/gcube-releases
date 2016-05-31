package org.gcube.opensearch.opensearchlibrary.queryelements;

import java.util.Map;

public abstract class QueryElementDecorator implements QueryElement {

	protected QueryElement el;
	
	public QueryElementDecorator(QueryElement el) {
		this.el = el;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#parse()
	 */
	@Override
	public void parse() throws Exception {
		el.parse();
	}
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#getRole()
	 */
	@Override
	public String getRole() throws Exception {
		return el.getRole();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#getTitle()
	 */
	@Override
	public String getTitle() throws Exception {
		return el.getTitle();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#getTotalResults()
	 */
	@Override
	public String getTotalResults() throws Exception {
		return el.getTotalResults();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#getSearchTerms()
	 */
	@Override
	public String getSearchTerms() throws Exception {
		return el.getSearchTerms();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#getCount()
	 */
	@Override
	public String getCount() throws Exception {
		return el.getCount();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#getStartIndex()
	 */
	@Override
	public String getStartIndex() throws Exception {
		return el.getStartIndex();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#getStartPage()
	 */
	@Override
	public String getStartPage() throws Exception {
		return el.getStartPage();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#getLanguage()
	 */
	@Override
	public String getLanguage() throws Exception {
		return el.getLanguage();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#getInputEncoding()
	 */
	@Override
	public String getInputEncoding() throws Exception {
		return el.getInputEncoding();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#getOutputEncoding()
	 */
	@Override
	public String getOutputEncoding() throws Exception {
		return el.getOutputEncoding();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#describesExampleQuery()
	 */
	@Override
	public boolean describesExampleQuery() throws Exception {
		return el.describesExampleQuery();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#getQueryParameters()
	 */
	@Override
	public Map<String, String> getQueryParameters() throws Exception {
		return el.getQueryParameters();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#isRoleSupported()
	 */
	@Override
	public boolean isRoleSupported() throws Exception {
		return el.isRoleSupported();
	}

}
