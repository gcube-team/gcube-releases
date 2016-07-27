package org.gcube.opensearch.opensearchlibrary.query;

import java.util.List;

import org.gcube.opensearch.opensearchlibrary.query.extensions.geo.GeoQueryBuilder;
import org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement;

public abstract class QueryBuilderDecorator implements QueryBuilder {

	protected QueryBuilder qb;
	
	public QueryBuilderDecorator(QueryBuilder qb) {
		this.qb = qb;
	}
	
	@Override
	public QueryBuilder clone() {
		QueryBuilder qb;
		try {
			qb = new GeoQueryBuilder(this.qb);
		}catch(Exception e) {
			return null;
		}
		return qb;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#hasParameter(String)
	 */
	@Override
	public boolean hasParameter(String name) {
		return qb.hasParameter(name);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#getRequiredParameters()
	 */
	@Override
	public List<String> getRequiredParameters() {
		return qb.getRequiredParameters();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#getOptionalParameters()
	 */
	@Override
	public List<String> getOptionalParameters() {
		return qb.getOptionalParameters();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#getUnsetParameters()
	 */
	@Override
	public List<String> getUnsetParameters() {
		return qb.getUnsetParameters();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#getParameterValue(String)
	 */
	@Override
	public String getParameterValue(String name)
			throws NonExistentParameterException, Exception {
		return qb.getParameterValue(name);
	}

	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#setParameter(String, String)
	 */
	@Override
	public QueryBuilder setParameter(String name, String value) throws NonExistentParameterException, Exception {
		qb.setParameter(name, value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#setParameter(String, Integer)
	 */
	@Override
	public QueryBuilder setParameter(String name, Integer value) throws NonExistentParameterException, Exception {
		qb.setParameter(name, value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#setParameters(List, List)
	 */
	@Override
	public QueryBuilder setParameters(List<String> names, List<Object> values) throws NonExistentParameterException, Exception {
		qb.setParameters(names, values);
		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#setParameters(QueryElement)
	 */
	@Override
	public QueryBuilder setParameters(QueryElement queryEl) throws Exception {
		qb.setParameters(queryEl);
		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#isParameterSet(String)
	 */
	@Override
	public boolean isParameterSet(String name) {
		return qb.isParameterSet(name);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#getStartIndexDef()
	 */
	@Override
	public Integer getStartIndexDef() {
		return qb.getStartIndexDef();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#getStartPageDef()
	 */
	@Override
	public Integer getStartPageDef() {
		return qb.getStartPageDef();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#isQueryComplete()
	 */
	@Override
	public boolean isQueryComplete() {
		return qb.isQueryComplete();
	}

	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#getQuery()
	 */
	@Override
	public String getQuery() throws IncompleteQueryException, MalformedQueryException, Exception {
		return qb.getQuery();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#getRawTemplate()
	 */
	@Override
	public String getRawTemplate() {
		return qb.getRawTemplate();
	}

}
