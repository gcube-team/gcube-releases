package org.gcube.portlets.user.tdwx.server.datasource;

import java.util.ArrayList;

import org.gcube.portlets.user.tdwx.shared.ColumnsReorderingConfig;
import org.gcube.portlets.user.tdwx.shared.FilterInformation;
import org.gcube.portlets.user.tdwx.shared.StaticFilterInformation;
import org.gcube.portlets.user.tdwx.shared.model.TableDefinition;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public interface DataSourceX {

	/**
	 * Returns the id of the {@link DataSourceXFactory} that have generated it.
	 * 
	 * @return the {@link DataSourceXFactory} id.
	 */
	public String getDataSourceFactoryId();

	/**
	 * Returns the table definition.
	 * 
	 * @return a table definition.
	 * @throws DataSourceXException
	 *             if an error occurred retrieving the table definition.
	 */
	public TableDefinition getTableDefinition() throws DataSourceXException;
	
	
	/**
	 * Set column reordering on current table
	 * 
	 * @param columnReorderingConfigure
	 * @throws DataSourceXException
	 */
	public TableDefinition setColumnReordering(ColumnsReorderingConfig columnsReorderingConfig)
	throws DataSourceXException;
	
	/**
	 * Retrieves the table data as JSON object.
	 * 
	 * @param start the starting row index.
	 * @param limit the number of rows after the starting index to retrieve.
	 * @param sortingColumn the column used for sorting.
	 * @param direction the sorting direction.
	 * @param filters dynamic filters
	 * @param staticFilters static filters
	 * @return the JSON object as String.
	 * @throws DataSourceXException
	 *             if an error occurred retrieving the table data.
	 */
	public String getDataAsJSon(int start, int limit, String sortingColumn,
			Direction direction, ArrayList<FilterInformation> filters,
			ArrayList<StaticFilterInformation> staticFilters)
			throws DataSourceXException;

}
