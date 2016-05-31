/**
 * 
 */
package org.gcube.portlets.user.tdw.datasource.td;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationProvider;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationToken;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryOrder;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryOrderDirection;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryPage;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.data.analysis.tabulardata.service.impl.TabularDataServiceFactory;
import org.gcube.portlets.user.tdw.datasource.td.map.ColumnTypeMap;
import org.gcube.portlets.user.tdw.datasource.td.map.DataTypeMap;
import org.gcube.portlets.user.tdw.server.datasource.DataSource;
import org.gcube.portlets.user.tdw.server.datasource.DataSourceException;
import org.gcube.portlets.user.tdw.server.datasource.Direction;
import org.gcube.portlets.user.tdw.server.datasource.util.TableJSonBuilder;
import org.gcube.portlets.user.tdw.shared.model.ColumnDefinition;
import org.gcube.portlets.user.tdw.shared.model.TableDefinition;
import org.gcube.portlets.user.tdw.shared.model.TableId;
import org.gcube.portlets.user.tdw.shared.model.ValueType;
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
public class TDDataSource implements DataSource {

	public static final String JSON_ROWS_FIELD = "ROWS";
	public static final String JSON_TOTAL_LENGTH_FIELD = "total";
	public static final String JSON_OFFSET_FIELD = "offset";

	protected Logger logger = LoggerFactory.getLogger(TDDataSource.class);
	protected String dataSourceFactoryId;
	protected String tableName;
	protected TableDefinition tableDefinition;
	protected int tableSize = -1;
	protected ColumnDefinition autogeneratePrimaryColumn = null;
	protected TableJSonBuilder jsonBuilder;

	protected TabularDataService service;
	protected org.gcube.data.analysis.tabulardata.model.table.TableId serviceTableId;
	protected org.gcube.data.analysis.tabulardata.model.table.Table serviceTable;

	/**
	 * 
	 * @param dataSourceFactoryId
	 * @param tableName
	 * @return
	 */
	public static TDDataSource createTDDataSource(String dataSourceFactoryId,
			ASLSession aslSession, String tableName) throws DataSourceException {
		TDDataSource dataSource = new TDDataSource(dataSourceFactoryId,
				aslSession, tableName);
		return dataSource;
	}

	/**
	 * 
	 * @param dataSourceFactoryId
	 * @param tableName
	 * @throws DataSourceException
	 */
	public TDDataSource(String dataSourceFactoryId, ASLSession aslSession,
			String tableName) throws DataSourceException {
		if (dataSourceFactoryId == null) {
			logger.error("An error occurred, dataSourceFactoryId is null");
			throw new DataSourceException(
					"An error occurred, dataSourceFactoryId is null");
		}

		if (tableName == null) {
			logger.error("An error occurred, tableName is null");
			throw new DataSourceException(
					"An error occurred, tableName is null");
		}

		this.dataSourceFactoryId = dataSourceFactoryId;
		this.tableName = tableName;

		AuthorizationProvider.instance.set(new AuthorizationToken(aslSession
				.getUsername()));
		service = TabularDataServiceFactory.getService();

		long tableId;

		try {
			tableId = Long.parseLong(tableName);
		} catch (NumberFormatException e) {
			logger.error("An error occurred, tableName is not a long", e);
			throw new DataSourceException(
					"An error occurred, no tableName is not a long", e);
		}

		serviceTableId = new org.gcube.data.analysis.tabulardata.model.table.TableId(
				tableId);

		try {
			serviceTable = service.getTable(serviceTableId);

		} catch (NoSuchTableException e) {
			logger.error("An error occurred, no such table", e);
			throw new DataSourceException("An error occurred, no such table", e);
		}

		logger.debug("Service Table: " + serviceTable);

	}

	public String getDataSourceFactoryId() {
		return dataSourceFactoryId;
	}

	public TableDefinition getTableDefinition() throws DataSourceException {

		if (tableDefinition == null)
			tableDefinition = extractTableDefinition();
		return tableDefinition;
	}

	/**
	 * 
	 * @return
	 * @throws DataSourceException
	 */
	protected TableDefinition extractTableDefinition()
			throws DataSourceException {
		List<ColumnDefinition> columns = getColumnDefinitions();

		TableId id = new TableId(dataSourceFactoryId, tableName);

		TableDefinition tableDefinition = new TableDefinition(id, tableName,
				JSON_ROWS_FIELD, JSON_TOTAL_LENGTH_FIELD, JSON_OFFSET_FIELD,
				columns);

		tableDefinition.setModelKeyColumnId("id");

		return tableDefinition;

	}

	protected List<ColumnDefinition> getColumnDefinitions()
			throws DataSourceException {
		List<Column> serviceListColumn = serviceTable.getColumns();
		ArrayList<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();

		int i = 0;
		Column serviceColumn;
		for (; i < serviceListColumn.size(); i++) {
			serviceColumn = serviceListColumn.get(i);
			ColumnDefinition column = getColumnDefinition(serviceColumn, i);
			columns.add(column);
		}
		return columns;
	}

	protected ColumnDefinition getColumnDefinition(Column serviceColumn,
			int ordinalPosition) {

		String columnLabel;
		String columnName = serviceColumn.getName();
		boolean visible = true;
		org.gcube.portlets.user.tdw.shared.model.ColumnType type = org.gcube.portlets.user.tdw.shared.model.ColumnType.USER;
		// columnId=serviceColumn.getLocalId().getValue().toString();

		ColumnType ct = serviceColumn.getColumnType();
		if (ColumnTypeMap.isIdColumnType(ct)) {
			columnLabel = "Id";
			visible = false;
			type = org.gcube.portlets.user.tdw.shared.model.ColumnType.SYSTEM;
		} else {
			NamesMetadata labelsMetadata = null;
			try {
				labelsMetadata = serviceColumn.getMetadata(NamesMetadata.class);
			} catch (NoSuchMetadataException e) {
				logger.info("labelMetadata: NoSuchMetadataException "
						+ e.getLocalizedMessage());
			}

			if (labelsMetadata == null) {
				columnLabel = "nolabel";
				// logger.info("LabelsMetadata no labels");
			} else {
				LocalizedText cl = null;
				cl = labelsMetadata.getTextWithLocale("en");
				if (cl == null) {
					columnLabel = "nolabel";
					logger.info("ColumnLabel no label in en");
				} else {
					columnLabel = cl.getValue();
					// logger.info("Column Set Label: "+columnLabel);
				}
			}

		}

		ColumnDefinition columnDefinition;
		columnDefinition = new ColumnDefinition(columnName, columnLabel);
		DataType dataType = serviceColumn.getDataType();
		columnDefinition.setValueType(DataTypeMap.getValueType(dataType));
		columnDefinition.setType(type);
		columnDefinition.setPosition(ordinalPosition);

		columnDefinition.setWidth(100);
		columnDefinition.setEditable(false);
		columnDefinition.setVisible(visible);
		return columnDefinition;
	}

	protected ColumnDefinition createPrimaryKeyColumn(
			List<ColumnDefinition> columns) {
		List<String> ids = new ArrayList<String>(columns.size());
		for (ColumnDefinition column : columns)
			ids.add(column.getId());

		String id = "idColumn";
		for (int i = 0; ids.contains(id); id = "idColumn" + i++)
			;

		return new ColumnDefinition(id, id, ValueType.INTEGER, -1, false,
				false,
				org.gcube.portlets.user.tdw.shared.model.ColumnType.SYSTEM);
	}

	public String getDataAsJSon(int start, int limit, String sortingColumn,
			Direction direction) throws DataSourceException {
		logger.trace("getDataAsJSon start: " + start + " limit: " + limit
				+ " sortingColumn: " + sortingColumn + " direction: "
				+ direction);
		tableSize = 0;
		try {
			tableSize = service.getQueryLenght(serviceTableId, null);
		} catch (NoSuchTableException e) {
			logger.error("An error occurred, tableSize is not recovered", e);
			throw new DataSourceException(
					"An error occurred, tableSize is not recovered", e);
		}
		start = Math.max(0, start);
		start = Math.min(start, tableSize);
		if (start + limit > tableSize)
			limit = tableSize - start;
		logger.trace("checked bounds start: " + start + " limit: " + limit);

		TableDefinition tableDefinition = getTableDefinition();
		QueryOrder queryOrder = null;

		if (sortingColumn != null) {
			if (tableDefinition.getColumns().get(sortingColumn) == null) {
				logger.error("The specified sorting column \"" + sortingColumn
						+ "\" don't exists");

				throw new DataSourceException("The specified sorting column \""
						+ sortingColumn + "\" don't exists");
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

		}

		String json = getJSon(start, queryOrder);
		logger.trace("Returning json");
		return json;

	}

	protected ArrayList<ColumnDefinition> sort(
			Collection<ColumnDefinition> columns) {

		ArrayList<ColumnDefinition> lcolumns = new ArrayList<ColumnDefinition>();
		for (ColumnDefinition column : columns) {
			lcolumns.add(column);
		}

		int hight = lcolumns.size() - 1;
		int i;
		while (hight > 0) {
			for (i = 0; i < hight; i++) {
				if (lcolumns.get(i).getPosition() > lcolumns.get(i + 1)
						.getPosition()) {
					// swap
					ColumnDefinition cd = lcolumns.get(i);
					lcolumns.set(i, lcolumns.get(i + 1));
					lcolumns.set(i + 1, cd);
				}
			}
			hight = hight - 1;
		}
		return lcolumns;

	}

	protected String getJSon(int start, QueryOrder queryOrder)
			throws DataSourceException {
		TableJSonBuilder json = getBuilder();

		TableDefinition tableDefinition = getTableDefinition();
		Collection<ColumnDefinition> columns = tableDefinition.getColumns()
				.values();
		ArrayList<ColumnDefinition> lcolumns = sort(columns);

		logger.debug("ColumnDefinition:\n" + lcolumns.toString());

		json.startRows();

		int id = start;

		QueryPage queryPage = new QueryPage(start, 200);

		String serviceJson = null;
		try {
			if (queryOrder == null) {
				serviceJson = service.queryAsJson(serviceTableId, queryPage);
			} else {
				serviceJson = service.queryAsJson(serviceTableId, queryPage,
						queryOrder);
			}
		} catch (NoSuchTableException e) {
			logger.error("An error occurred, no such table", e);
			throw new DataSourceException("An error occurred, no such table", e);
		}
		JSONArray currentRow = null;
		int i = -1;
		int j = -1;
		int totalRows = -1;

		try {
			org.json.JSONObject obj = new org.json.JSONObject(serviceJson);
			org.json.JSONArray rows = obj.getJSONArray("rows");

			totalRows = rows.length();

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
						case BOOLEAN:
							Boolean b = currentRow.getBoolean(j);
							json.addValue(columnId, b);
							break;
						case DOUBLE:
							Double d = currentRow.getDouble(j);
							json.addValue(columnId, d);
							break;
						case FLOAT:
							Double f = currentRow.getDouble(j);
							json.addValue(columnId, f);
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
							String s = currentRow.getString(j);
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
			throw new DataSourceException(
					"An error occurred,  while reading json of service", e);
		}

		json.endRows();

		json.setTotalLength(tableSize);

		json.setOffset(start);

		logger.trace("produced " + (id - start) + " rows");

		json.close();

		return json.toString();
	}

	protected TableJSonBuilder getBuilder() throws DataSourceException {
		if (jsonBuilder == null)
			jsonBuilder = new TableJSonBuilder(getTableDefinition());
		else
			jsonBuilder.clean();

		return jsonBuilder;
	}

	protected int getTableSize() throws DataSourceException {
		return tableSize;
	}

	public void close() {
		// The service is stateless there is no need to close

	}

}
