package org.gcube.application.framework.search.library.model;

import java.util.ArrayList;
import java.util.List;

/**
 * QueryGroup represents a set of queries that are packed together 
 * @author Valia Tsagkalidou (KNUA)
 *
 */
public class QueryGroup implements Cloneable{

	protected List<Query> queries;

	/**
	 * The generic constructor
	 */
	public QueryGroup() {
		super();
		queries = new ArrayList<Query>();
	}

	/**
	 * A contructor that get an initial set of queries
	 * @param queries the inital set of queries
	 */
	public QueryGroup(List<Query> queries) {
		super();
		this.queries = queries;
		Query q = new Query();
	}

	/**
	 * @return the queries
	 */
	public List<Query> getQueries() {
		return queries;
	}

	/**
	 * Sets the queries
	 * @param queries the queries
	 */
	public void setQueries(List<Query> queries) {
		this.queries = queries;
	}

	/**
	 * Retrieves the i-th query
	 * @param i the position in the list where the desired query rests
	 * @return the query
	 */
	public Query getQuery(int i) {
		return queries.get(i);
	}
	
	/**
	 * Adds a new query to the list of queries
	 * @param query the query
	 */
	public void setQuery(Query query) {
		queries.add(query);
	}	
}

