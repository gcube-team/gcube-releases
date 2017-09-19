/**
 * 
 */
package org.gcube.common.homelibrary.jcr.importing;
import org.gcube.common.homelibrary.home.workspace.folder.items.QueryType;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class ImportQueryRequest extends ImportRequest{

	protected String name;
	protected String query;
	protected QueryType queryType;

	/**
	 * Create a Query import request.
	 * @param name the query name.
	 * @param query the query value.
	 * @param queryType the query type.
	 */
	public ImportQueryRequest(String name, String query, QueryType queryType) {
		super(ImportRequestType.QUERY);
		this.name = name;
		this.query = query;
		this.queryType = queryType;
	}

	/**
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @return the queryType
	 */
	public QueryType getQueryType() {
		return queryType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return super.toString()+" name: "+name+" query: "+query+" queryType: "+queryType;
	}
}
