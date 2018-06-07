/**
 * 
 */
package org.gcube.portlets.user.tdw.server.datasource;

import org.gcube.portlets.user.tdw.shared.model.TableDefinition;

/**
 * A TDW datasource.
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 */
public interface DataSource {
	
	/**
	 * Returns the id of the {@link DataSourceFactory} that have generated it.
	 * @return the {@link DataSourceFactory} id.
	 */
	public String getDataSourceFactoryId();
	
	/**
	 * Returns the table definition.
	 * @return a table definition.
	 * @throws DataSourceException if an error occurred retrieving the table definition.
	 */
	public TableDefinition getTableDefinition() throws DataSourceException;
	
	/**
	 * Retrieves the table data as JSON object.
	 * @param start the starting row index.
	 * @param limit the number of rows after the starting index to retrieve.
	 * @param sortingColumn the column used for sorting.
	 * @param direction the sorting direction.
	 * @return the JSON object as String.
	 * @throws DataSourceException if an error occurred retrieving the table data.
	 */
	public String getDataAsJSon(int start, int limit, String sortingColumn, Direction direction) throws DataSourceException;

}
