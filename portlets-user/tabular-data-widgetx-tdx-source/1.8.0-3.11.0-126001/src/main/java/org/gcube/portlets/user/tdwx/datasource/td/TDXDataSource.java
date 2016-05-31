/**
 * 
 */
package org.gcube.portlets.user.tdwx.datasource.td;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationProvider;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationToken;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.metadata.common.TableDescriptorMetadata;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryFilter;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryOrder;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryOrderDirection;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryPage;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.data.analysis.tabulardata.service.impl.TabularDataServiceFactory;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.RelationshipData;
import org.gcube.portlets.user.tdwx.datasource.td.filters.FiltersBuilder;
import org.gcube.portlets.user.tdwx.datasource.td.map.ColumnDefinitionBuilder;
import org.gcube.portlets.user.tdwx.datasource.td.trservice.TRService;
import org.gcube.portlets.user.tdwx.server.datasource.DataSourceX;
import org.gcube.portlets.user.tdwx.server.datasource.DataSourceXException;
import org.gcube.portlets.user.tdwx.server.datasource.Direction;
import org.gcube.portlets.user.tdwx.server.datasource.util.TableJSonBuilder;
import org.gcube.portlets.user.tdwx.shared.ColumnsReorderingConfig;
import org.gcube.portlets.user.tdwx.shared.FilterInformation;
import org.gcube.portlets.user.tdwx.shared.StaticFilterInformation;
import org.gcube.portlets.user.tdwx.shared.model.ColumnDefinition;
import org.gcube.portlets.user.tdwx.shared.model.ColumnType;
import org.gcube.portlets.user.tdwx.shared.model.TableDefinition;
import org.gcube.portlets.user.tdwx.shared.model.TableId;
import org.gcube.portlets.user.tdwx.shared.model.ValueType;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class TDXDataSource implements DataSourceX {

	private static final int PAGINGDIMENSION = 300;
	private static final String PRIMARY_KEY_COLUMN = "id";
	private static final String JSON_ROWS_FIELD = "ROWS";
	private static final String JSON_TOTAL_LENGTH_FIELD = "total";
	private static final String JSON_OFFSET_FIELD = "offset";

	private Logger logger = LoggerFactory.getLogger(TDXDataSource.class);
	private String dataSourceFactoryId;
	private String tableName;
	private TableDefinition tableDefinition;
	private int tableSize = -1;
	private TableJSonBuilder jsonBuilder;

	private TabularDataService service;
	private org.gcube.data.analysis.tabulardata.model.table.TableId serviceTableId;
	private org.gcube.data.analysis.tabulardata.model.table.Table serviceTable;
	private long serviceTabularResourceId;
	private TRService trService;
	private long tableId;

	/**
	 * 
	 * @param dataSourceFactoryId
	 * @param tableName
	 * @return
	 */
	public static TDXDataSource createTDDataSource(String dataSourceFactoryId,
			ASLSession aslSession, String tableName)
			throws DataSourceXException {
		TDXDataSource dataSource = new TDXDataSource(dataSourceFactoryId,
				aslSession, tableName);
		return dataSource;
	}

	/**
	 * 
	 * @param dataSourceFactoryId
	 * @param tableIdentifier
	 * @throws DataSourceXException
	 */
	public TDXDataSource(String dataSourceFactoryId, ASLSession aslSession,
			String tableIdentifier) throws DataSourceXException {
		if (dataSourceFactoryId == null) {
			logger.error("An error occurred, dataSourceFactoryId is null");
			throw new DataSourceXException(
					"An error occurred, dataSourceFactoryId is null");
		}

		if (tableIdentifier == null) {
			logger.error("An error occurred, tableName is null");
			throw new DataSourceXException(
					"An error occurred, tableName is null");
		}

		this.dataSourceFactoryId = dataSourceFactoryId;
		this.tableName = tableIdentifier;

		AuthorizationProvider.instance.set(new AuthorizationToken(aslSession
				.getUsername(), aslSession.getScope()));
		service = TabularDataServiceFactory.getService();

		try {
			tableId = Long.parseLong(tableIdentifier);
		} catch (NumberFormatException e) {
			logger.error("An error occurred, tableName is not a long", e);
			throw new DataSourceXException(
					"An error occurred, no tableName is not a long", e);
		}

		serviceTableId = new org.gcube.data.analysis.tabulardata.model.table.TableId(
				tableId);

		try {
			serviceTable = service.getTable(serviceTableId);
		} catch (NoSuchTableException e) {
			logger.error("An error occurred, no such table", e);
			throw new DataSourceXException("An error occurred, no such table",
					e);
		}

		// logger.debug("Service Table: " + serviceTable);

		TableDescriptorMetadata tableDesc = null;

		if (serviceTable.contains(TableDescriptorMetadata.class)) {
			tableDesc = serviceTable.getMetadata(TableDescriptorMetadata.class);
			if (tableDesc.getRefId() == 0) {
				logger.error("Error refId=0 for Table:" + serviceTable);
				throw new DataSourceXException(
						"Error no valid tabular resource associated with the table:"
								+ serviceTable.getId());
			} else {
				logger.debug("Table " + serviceTable.getId()
						+ " connect to tabular resource: "
						+ tableDesc.getRefId());
				serviceTabularResourceId = tableDesc.getRefId();
			}

		} else {
			logger.debug("No TableDescriptorMetadata found (Supposed Time Table):"
					+ tableId);
			/*
			 * TIME TABLE hasn't tabular resource connected
			 */
			/*
			 * throw new DataSourceXException(
			 * "Error no valid tabular resource associated with the table:" +
			 * serviceTable.getId());
			 */
		}

		trService = new TRService();
		trService.setService(service);
		TabularResourceId tabularResourceId = new TabularResourceId(
				serviceTabularResourceId);
		trService.setTabularResourceId(tabularResourceId);
	}

	@Override
	public String getDataSourceFactoryId() {
		return dataSourceFactoryId;
	}

	@Override
	public TableDefinition getTableDefinition() throws DataSourceXException {
		logger.debug("Retrieving table definition");
		tableDefinition = extractTableDefinition();
		return tableDefinition;
	}

	/**
	 * 
	 * @return
	 * @throws DataSourceXException
	 */
	protected TableDefinition extractTableDefinition()
			throws DataSourceXException {
		List<ColumnDefinition> columns = getColumnDefinitions();

		logger.debug("Creating tableId...");
		TableId id = new TableId(dataSourceFactoryId, tableName);

		TableDefinition tableDefinition = new TableDefinition(id, tableName,
				JSON_ROWS_FIELD, JSON_TOTAL_LENGTH_FIELD, JSON_OFFSET_FIELD,
				columns);

		tableDefinition.setModelKeyColumnId(PRIMARY_KEY_COLUMN);
		logger.debug("TableDefinition Created");
		return tableDefinition;

	}

	protected List<ColumnDefinition> getColumnDefinitions()
			throws DataSourceXException {
		logger.debug("Creating list of columns definition...");
		List<Column> serviceListColumn = serviceTable.getColumns();
		ArrayList<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		ArrayList<ColumnDefinition> dimensions = new ArrayList<ColumnDefinition>();

		Column serviceColumn;
		for (int i = 0; i < serviceListColumn.size(); i++) {
			serviceColumn = serviceListColumn.get(i);
			ColumnDefinition column = getColumnDefinition(serviceColumn, i);
			columns.add(column);
			if (column.getType() == ColumnType.DIMENSION
					|| column.getType() == ColumnType.TIMEDIMENSION) {
				dimensions.add(column);
			}
		}

		for (int i = 0; i < dimensions.size(); i++) {
			ColumnDefinition dim = dimensions.get(i);
			RelationshipData rel = dim.getRelationshipData();

			if (rel != null) {
				String cId = rel.getTargetColumnId();
				if (cId != null) {
					for (int j = 0; j < columns.size(); j++) {
						ColumnDefinition c = columns.get(j);
						if (c.getColumnLocalId() != null
								&& (c.getType() == ColumnType.VIEWCOLUMN_OF_DIMENSION || c
										.getType() == ColumnType.VIEWCOLUMN_OF_TIMEDIMENSION)
								&& c.getColumnLocalId().compareTo(cId) == 0) {
							c.setVisible(true);
							columns.set(j, c);
							break;
						}
					}
				}
			}
		}

		logger.debug("List of columns definition created");
		return columns;
	}

	protected ColumnDefinition getColumnDefinition(Column serviceColumn,
			int ordinalPosition) throws DataSourceXException {
		ColumnDefinitionBuilder columnDefinitionBuilder = new ColumnDefinitionBuilder(
				service, serviceTable, serviceColumn, ordinalPosition);
		ColumnDefinition columnDefinition = columnDefinitionBuilder.build();

		/* logger.debug("Column Definition:" + columnDefinition); */

		return columnDefinition;
	}

	protected ColumnDefinition createPrimaryKeyColumn(
			List<ColumnDefinition> columns) {
		List<String> ids = new ArrayList<String>(columns.size());
		for (ColumnDefinition column : columns)
			ids.add(column.getId());

		String id = PRIMARY_KEY_COLUMN;
		for (int i = 0; ids.contains(id); id = PRIMARY_KEY_COLUMN + i++)
			;

		return new ColumnDefinition(id, id, id, ValueType.INTEGER, -1, false,
				false,
				org.gcube.portlets.user.tdwx.shared.model.ColumnType.SYSTEM);
	}

	protected void retrieveTableSize(int start, int limit,
			QueryFilter queryFilter) throws DataSourceXException {
		tableSize = 0;
		try {
			tableSize = service.getQueryLenght(serviceTableId, queryFilter);
		} catch (NoSuchTableException e) {
			logger.error("An error occurred, tableSize is not recovered", e);
			throw new DataSourceXException(
					"An error occurred, tableSize is not recovered", e);
		}
		start = Math.max(0, start);
		start = Math.min(start, tableSize);
		if (start + limit > tableSize)
			limit = tableSize - start;
		logger.debug("checked bounds start: " + start + " limit: " + limit);
	}

	// Request from user
	@Override
	public String getDataAsJSon(int start, int limit, String sortingColumn,
			Direction direction, ArrayList<FilterInformation> filters,
			ArrayList<StaticFilterInformation> staticFilters)
			throws DataSourceXException {
		logger.debug("getDataAsJSon start: " + start + " limit: " + limit
				+ " sortingColumn: " + sortingColumn + " direction: "
				+ direction + " filters:" + filters.size() + " staticFilters:"
				+ staticFilters.size());

		TableDefinition tableDefinition = getTableDefinition();
		logger.debug("Creating queryOrder...");
		QueryOrder queryOrder = null;
		if (sortingColumn != null) {
			if (tableDefinition.getColumns().get(sortingColumn) == null) {
				logger.error("The specified sorting column \"" + sortingColumn
						+ "\" don't exists");

				throw new DataSourceXException(
						"The specified sorting column \"" + sortingColumn
								+ "\" don't exists");
			} else {
				ColumnDefinition columnDefinition = tableDefinition
						.getColumns().get(sortingColumn);
				Column column = serviceTable.getColumnByName(columnDefinition
						.getId());
				switch (direction) {
				case ASC:
					queryOrder = new QueryOrder(column.getLocalId(),
							QueryOrderDirection.ASCENDING);
					break;
				case DESC:
					queryOrder = new QueryOrder(column.getLocalId(),
							QueryOrderDirection.DESCENDING);
					break;
				default:
					break;
				}
			}
		} else {
			if (tableDefinition.getColumns().get(PRIMARY_KEY_COLUMN) == null) {
				logger.error("The primary key column \"" + PRIMARY_KEY_COLUMN
						+ "\" don't exists");
			} else {
				ColumnDefinition columnDefinition = tableDefinition
						.getColumns().get(PRIMARY_KEY_COLUMN);
				Column column = serviceTable.getColumnByName(columnDefinition
						.getId());
				queryOrder = new QueryOrder(column.getLocalId(),
						QueryOrderDirection.ASCENDING);
			}
		}

		QueryFilter queryFilter = null;
		if ((filters != null && filters.size() > 0)
				|| (staticFilters != null && staticFilters.size() > 0)) {
			FiltersBuilder filtersBuilder = new FiltersBuilder(filters,
					staticFilters, tableDefinition, serviceTable);
			queryFilter = filtersBuilder.createQueryFilter();
		}

		retrieveTableSize(start, limit, queryFilter);

		String json = getJSon(start, queryOrder, queryFilter);
		logger.trace("Returning json");
		// logger.debug(json);
		return json;

	}

	protected ArrayList<ColumnDefinition> sort(
			Collection<ColumnDefinition> columns) {

		ArrayList<ColumnDefinition> lcolumns = new ArrayList<ColumnDefinition>();
		for (ColumnDefinition column : columns) {
			lcolumns.add(column);
		}

		Collections.sort(lcolumns, new Comparator<ColumnDefinition>() {
			@Override
			public int compare(ColumnDefinition cd1, ColumnDefinition cd2) {
				int comp = 0;
				if (cd1.getPosition() == cd2.getPosition()) {
					comp = 0;
				} else {
					if (cd1.getPosition() > cd2.getPosition()) {
						comp = 1;
					} else {
						comp = -1;
					}
				}
				return comp;
			}
		});

		return lcolumns;

	}

	protected String getJSon(int start, QueryOrder queryOrder,
			QueryFilter queryFilter) throws DataSourceXException {
		logger.debug("Retrieving JSon");
		logger.debug("[" + queryOrder + ", " + queryFilter + "]");

		TableDefinition tableDefinition = getTableDefinition();
		logger.debug("Retrieved table definition");
		Collection<ColumnDefinition> columns = tableDefinition.getColumns()
				.values();
		logger.debug("Retrieved Columns");
		ArrayList<ColumnDefinition> lcolumns = sort(columns);

		// logger.debug("ColumnDefinition:\n" + lcolumns.toString());

		QueryPage queryPage = new QueryPage(start, PAGINGDIMENSION);
		logger.debug("Created queryPage");
		String serviceJson = null;
		try {
			if (queryOrder == null && queryFilter == null) {
				serviceJson = service.queryAsJson(serviceTableId, queryPage);
			} else {
				if (queryOrder == null && queryFilter != null) {
					serviceJson = service.queryAsJson(serviceTableId,
							queryPage, queryFilter);
				} else {
					if (queryOrder != null && queryFilter == null) {
						serviceJson = service.queryAsJson(serviceTableId,
								queryPage, queryOrder);
					} else {
						if (queryOrder != null && queryFilter != null) {
							logger.debug("Order & Filter: " + queryOrder + " "
									+ queryFilter);
							serviceJson = service.queryAsJson(serviceTableId,
									queryPage, queryFilter, queryOrder);
						} else {
							logger.debug("No queryAsJson valid");
						}
					}
				}

			}
		} catch (NoSuchTableException e) {
			logger.error("An error occurred, no such table", e);
			throw new DataSourceXException("An error occurred, no such table",
					e);
		} catch (Throwable e) {
			logger.error("An error occurred", e);
			throw new DataSourceXException("An error occurred", e);
		}
		logger.debug("Created serviceJson");
		// logger.debug(serviceJson);

		return createJson(start, serviceJson, lcolumns);
	}

	protected String createJson(int start, String serviceJson,
			ArrayList<ColumnDefinition> lcolumns) throws DataSourceXException {
		TableJSonBuilder json = getBuilder();
		json.startRows();
		int id = start;

		JSONArray currentRow = null;
		int i = -1;
		int j = -1;
		int totalRows = -1;
		String s = null;
		try {
			org.json.JSONObject obj = new org.json.JSONObject(serviceJson);
			org.json.JSONArray rows = obj.getJSONArray("rows");

			totalRows = rows.length();
			logger.debug("Reading rows from json");
			for (i = 0; i < totalRows; i++) {

				json.startRow();
				currentRow = rows.getJSONArray(i);

				j = 0;

				for (ColumnDefinition column : lcolumns) {

					String columnId = column.getId();

					if (currentRow.isNull(j)) {
						json.addValue(columnId, "");
					} else {

						switch (column.getValueType()) {
						case DATE:
							Long day = currentRow.getLong(j);
							Date dd = new Date();
							dd.setTime(day);
							json.addValue(columnId, day);
							break;
						case BOOLEAN:
							Boolean b = currentRow.getBoolean(j);
							json.addValue(columnId, b);
							break;
						case DOUBLE:
							Double d = currentRow.getDouble(j);
							json.addValue(columnId, d);
							break;
						case INTEGER:
							int integ = currentRow.getInt(j);
							json.addValue(columnId, integ);
							break;
						case LONG:
							Long l = currentRow.getLong(j);
							json.addValue(columnId, l);
							break;
						case STRING:
							s = currentRow.getString(j);
							json.addValue(columnId, s);
							break;
						case GEOMETRY:
							s = currentRow.getString(j);
							json.addValue(columnId, s);
							break;
						default:
							logger.warn("Unknow value type "
									+ column.getValueType());
						}
					}
					j++;
				}

				json.endRow();
			}
			id += i;

		} catch (JSONException e) {

			logger.error("An error occurred while parsing json document\n"
					+ "At Row " + i + ",Column " + j + "\nRow Content: "
					+ currentRow + "\nLenght rows " + totalRows, e);
			throw new DataSourceXException(
					"An error occurred,  while reading json of service", e);
		}

		json.endRows();

		json.setTotalLength(tableSize);

		json.setOffset(start);

		json.close();

		logger.trace("produced " + (id - start) + " rows");

		return json.toString();

	}

	protected TableJSonBuilder getBuilder() throws DataSourceXException {
		try {
			if (jsonBuilder == null) {
				TableDefinition tdef = getTableDefinition();
				logger.debug("Creating jsonBuilder...");
				if (tdef != null) {
					jsonBuilder = new TableJSonBuilder(tdef);
				} else {
					logger.error("table definition is null");
					throw new DataSourceXException("table definition is null");
				}
			} else {
				jsonBuilder.clean();
			}
			return jsonBuilder;
		} catch (Exception e) {
			logger.debug("Error Creating jsonBuilder: " + e.getMessage());
			throw new DataSourceXException("Error Creating jsonBuilder: "
					+ e.getMessage());
		}
	}

	protected int getTableSize() throws DataSourceXException {
		return tableSize;
	}

	public void close() {
		// The service is stateless there is no need to close

	}

	@Override
	public TableDefinition setColumnReordering(
			ColumnsReorderingConfig columnsReorderingConfig)
			throws DataSourceXException {
		logger.debug("SetColumnReordering: " + columnsReorderingConfig);

		trService.startChangeSingleColumnPosition(columnsReorderingConfig);

		updateTableAfterOperation();

		logger.debug("Retrieving table definition");
		tableDefinition = extractTableDefinition();
		return tableDefinition;
	}

	protected void updateTableAfterOperation() throws DataSourceXException {

		serviceTableId = new org.gcube.data.analysis.tabulardata.model.table.TableId(
				tableId);

		try {
			serviceTable = service.getTable(serviceTableId);
		} catch (NoSuchTableException e) {
			logger.error("An error occurred, no such table", e);
			throw new DataSourceXException("An error occurred, no such table",
					e);
		}

		// logger.debug("Service Table: " + serviceTable);

		TableDescriptorMetadata tableDesc = null;

		if (serviceTable.contains(TableDescriptorMetadata.class)) {
			tableDesc = serviceTable.getMetadata(TableDescriptorMetadata.class);
			if (tableDesc.getRefId() == 0) {
				logger.debug("Error refId=0 for Table:" + serviceTable);
				throw new DataSourceXException(
						"Error no valid tabular resource associated with the table:"
								+ serviceTable.getId());
			} else {
				logger.debug("Table " + serviceTable.getId()
						+ " connect to tabular resource: "
						+ tableDesc.getRefId());
				serviceTabularResourceId = tableDesc.getRefId();
			}

		} else {
			logger.debug("No TableDescriptorMetadata found (Supposed Time Table):"
					+ tableId);

			/*
			 * TIME TABLE hasn't tabular resource connected
			 */
			/*
			 * throw new DataSourceXException(
			 * "Error no valid tabular resource associated with the table:" +
			 * serviceTable.getId());
			 */
		}

		trService.setService(service);
		TabularResourceId tabularResourceId = new TabularResourceId(
				serviceTabularResourceId);
		trService.setTabularResourceId(tabularResourceId);
	}

	@Override
	public String toString() {
		return "TDXDataSource [serviceTabularResourceId="
				+ serviceTabularResourceId + ", serviceTableId="
				+ serviceTableId + "]";
	}

}
