package org.gcube.portlets.user.td.gwtservice.server.trservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.Equals;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryFilter;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryOrder;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryOrderDirection;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryPage;
import org.gcube.data.analysis.tabulardata.query.parameters.group.QueryGroup;
import org.gcube.data.analysis.tabulardata.query.parameters.select.QueryColumn;
import org.gcube.data.analysis.tabulardata.query.parameters.select.QueryColumn.Function;
import org.gcube.data.analysis.tabulardata.query.parameters.select.QuerySelect;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.ConditionCode;
import org.gcube.portlets.user.td.gwtservice.shared.tr.batch.Occurrences;
import org.gcube.portlets.user.td.gwtservice.shared.tr.batch.OccurrencesForReplaceBatchColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.batch.ShowOccurrencesType;
import org.gcube.portlets.user.td.gwtservice.shared.tr.paging.Direction;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class QueryService {

	private static Logger logger = LoggerFactory.getLogger(QueryService.class);

	public static ArrayList<Occurrences> queryOccurences(TabularDataService service,
			OccurrencesForReplaceBatchColumnSession occurrencesSession, Direction direction)
			throws TDGWTServiceException {

		try {

			logger.debug("QueryOccurences");
			ColumnData column = occurrencesSession.getColumnData();

			ArrayList<Occurrences> occurences = new ArrayList<Occurrences>();

			TableId tableId;
			if (column.getTrId().isViewTable()) {
				tableId = new TableId(new Long(column.getTrId().getReferenceTargetTableId()));
			} else {
				tableId = new TableId(new Long(column.getTrId().getTableId()));
			}
			ColumnLocalId columnId = new ColumnLocalId(column.getColumnId());
			QuerySelect querySelect = null;
			QueryGroup queryGroup = null;

			ConditionCode conditionCode = occurrencesSession.getConditionCode();
			if (conditionCode == null) {
				logger.debug("Replace Batch no validations");
				if (column.isViewColumn()) {
					// Use View for Occurrence
					tableId = new TableId(new Long(column.getTrId().getTableId()));

					ColumnLocalId sourceColumnId = new ColumnLocalId(
							column.getColumnViewData().getSourceTableDimensionColumnId());
					querySelect = new QuerySelect(Arrays.asList(new QueryColumn(columnId),
							new QueryColumn(sourceColumnId), new QueryColumn(columnId, Function.COUNT)));
					logger.debug("Occurences querySelect:" + querySelect.toString());
					queryGroup = new QueryGroup(Arrays.asList(columnId, sourceColumnId));
					logger.debug("Occurences queryGroup:" + queryGroup.toString());
				} else {
					querySelect = new QuerySelect(
							Arrays.asList(new QueryColumn(columnId), new QueryColumn(columnId, Function.COUNT)));
					logger.debug("Occurences querySelect:" + querySelect.toString());
					queryGroup = new QueryGroup(Arrays.asList(columnId));
					logger.debug("Occurences queryGroup:" + queryGroup.toString());
				}
			} else {
				logger.debug("Replace Batch from validations");
				switch (conditionCode) {
				case AllowedColumnType:
					break;
				case AmbiguousValueOnExternalReference:
					if (column.isViewColumn()) {
						ColumnLocalId sourceColumnId = new ColumnLocalId(
								column.getColumnViewData().getSourceTableDimensionColumnId());

						querySelect = new QuerySelect(Arrays.asList(new QueryColumn(columnId),
								new QueryColumn(sourceColumnId), new QueryColumn(columnId, Function.COUNT)));
						logger.debug("Occurences querySelect:" + querySelect.toString());
						queryGroup = new QueryGroup(Arrays.asList(columnId, sourceColumnId));
						logger.debug("Occurences queryGroup:" + queryGroup.toString());
					} else {
						// ColumnLocalId idColumn =
						// retrieveColumnLocalIdOFIdColumnType(
						// column.getTrId(), service);
						querySelect = new QuerySelect(
								Arrays.asList(new QueryColumn(columnId), new QueryColumn(columnId, Function.COUNT)));
						logger.debug("Occurences querySelect:" + querySelect.toString());
						queryGroup = new QueryGroup(Arrays.asList(columnId));
						logger.debug("Occurences queryGroup:" + queryGroup.toString());
					}
					break;
				case CastValidation:
					break;
				case CodeNamePresence:
					break;
				case DuplicateTupleValidation:
					break;
				case DuplicateValueInColumn:
					break;
				case GenericTupleValidity:
					break;
				case GenericValidity:
					break;
				case MaxOneCodenameForDataLocale:
					break;
				case MustContainAtLeastOneDimension:
					break;
				case MustContainAtLeastOneMeasure:
					break;
				case MustHaveDataLocaleMetadataAndAtLeastOneLabel:
					break;
				case OnlyOneCodeColumn:
					break;
				case OnlyOneCodenameColumn:
					break;
				case ValidPeriodFormat:
					break;
				case MissingValueOnExternalReference:
				default:
					if (column.isViewColumn()) {
						ColumnLocalId sourceColumnId = new ColumnLocalId(
								column.getColumnViewData().getSourceTableDimensionColumnId());
						querySelect = new QuerySelect(Arrays.asList(new QueryColumn(columnId),
								new QueryColumn(sourceColumnId), new QueryColumn(columnId, Function.COUNT)));
						logger.debug("Occurences querySelect:" + querySelect.toString());
						queryGroup = new QueryGroup(Arrays.asList(columnId, sourceColumnId));
						logger.debug("Occurences queryGroup:" + queryGroup.toString());
					} else {
						querySelect = new QuerySelect(
								Arrays.asList(new QueryColumn(columnId), new QueryColumn(columnId, Function.COUNT)));
						logger.debug("Occurences querySelect:" + querySelect.toString());
						queryGroup = new QueryGroup(Arrays.asList(columnId));
						logger.debug("Occurences queryGroup:" + queryGroup.toString());
					}
					break;
				}
			}

			QueryOrder queryOrder = null;
			switch (direction) {
			case ASC:
				queryOrder = new QueryOrder(columnId, QueryOrderDirection.ASCENDING);
				break;
			case DESC:
				queryOrder = new QueryOrder(columnId, QueryOrderDirection.DESCENDING);
				break;
			default:
				break;

			}

			QueryPage queryPage = null;// All occurences
			logger.debug("Occurences queryPage all");

			QueryFilter queryFilter = null;
			if (occurrencesSession.getShowType().compareTo(ShowOccurrencesType.ONLYERRORS) == 0) {
				logger.debug("Filter on error");
				if (occurrencesSession.isHasValidationColumns()) {
					Expression exp = ExpressionGenerator.genReplaceBatchOccurrencesFilter(occurrencesSession);
					logger.debug("Expression: " + exp);
					if (exp != null) {
						queryFilter = new QueryFilter(exp);
					} else {
						logger.debug("Expression generated is null");
					}
				} else {
					logger.debug("No validation columns");
					return occurences;
				}
			} else {
				logger.debug("No Filter on error");
			}

			logger.debug("Query on " + tableId.toString() + " queryPage:" + queryPage + ", queryFilter:" + queryFilter
					+ ", querySelect:" + querySelect + ", queryGroup:" + queryGroup + ", queryOrder:" + queryOrder);

			if (querySelect != null) {
				logger.debug("Query Select Columns:" + querySelect.getColumns());
			}

			if (queryGroup != null) {
				logger.debug("Query Group Columns: " + queryGroup.getColumns());
			}

			String serviceJson = null;
			try {
				if (queryOrder == null) {
					if (queryFilter == null) {
						if (queryGroup == null) {
							logger.debug("1-QueryAsJson-->tableId:" + tableId + ", queryPage:" + queryPage
									+ ", querySelect:" + querySelect);
							serviceJson = service.queryAsJson(tableId, queryPage, querySelect);
						} else {
							logger.debug("2-QueryAsJson-->tableId:" + tableId + ", queryPage:" + queryPage
									+ ", querySelect:" + querySelect + ", queryGroup:" + queryGroup);
							serviceJson = service.queryAsJson(tableId, queryPage, querySelect, queryGroup);
						}
					} else {
						if (queryGroup == null) {
							logger.debug("3-QueryAsJson-->[tableId:" + tableId + ", queryPage:" + queryPage
									+ ", queryFilter:" + queryFilter + ", querySelect:" + querySelect + "]");
							serviceJson = service.queryAsJson(tableId, queryPage, queryFilter, querySelect);
						} else {
							logger.debug("4-QueryAsJson-->[tableId:" + tableId + ", queryPage:" + queryPage
									+ ", queryFilter:" + queryFilter + ", querySelect:" + querySelect + ", queryGroup:"
									+ queryGroup + "]");
							serviceJson = service.queryAsJson(tableId, queryPage, queryFilter, querySelect, queryGroup);
						}
					}
				} else {
					if (queryFilter == null) {
						if (queryGroup == null) {
							logger.debug("5-QueryAsJson-->[tableId:" + tableId + ", queryPage:" + queryPage
									+ ", queryOrder:" + queryOrder + ", querySelect:" + querySelect + "]");
							serviceJson = service.queryAsJson(tableId, queryPage, queryOrder, querySelect);
						} else {
							logger.debug("6-QueryAsJson-->[tableId:" + tableId + ", queryPage:" + queryPage
									+ ", queryOrder:" + queryOrder + ", querySelect:" + querySelect + ", queryGroup:"
									+ queryGroup + "]");
							serviceJson = service.queryAsJson(tableId, queryPage, queryOrder, querySelect, queryGroup);
						}
					} else {
						if (queryGroup == null) {
							logger.debug("7-QueryAsJson-->[tableId:" + tableId + ", queryPage:" + queryPage
									+ ", queryFilter:" + queryFilter + ", queryOrder:" + queryOrder + ", querySelect:"
									+ querySelect + "]");
							serviceJson = service.queryAsJson(tableId, queryPage, queryFilter, queryOrder, querySelect);

						} else {
							logger.debug("8-QueryAsJson-->[tableId:" + tableId + ", queryPage:" + queryPage
									+ ", queryFilter:" + queryFilter + ", queryOrder:" + queryOrder + ", querySelect:"
									+ querySelect + ", queryGroup:" + queryGroup + "]");
							serviceJson = service.queryAsJson(tableId, queryPage, queryFilter, queryOrder, querySelect,
									queryGroup);
						}
					}
				}
			} catch (Throwable e) {
				logger.debug("Error by running the query on the server:" + e.getLocalizedMessage());
				e.printStackTrace();
				throw new TDGWTServiceException("An error occurred while running query on service", e);
			}

			logger.debug("Created serviceJson");
			// logger.debug(serviceJson);

			JSONArray currentRow = null;
			int i = -1;
			int j = -1;
			int totalRows = -1;

			try {
				org.json.JSONObject obj = new org.json.JSONObject(serviceJson);
				org.json.JSONArray rows = obj.getJSONArray("rows");

				totalRows = rows.length();
				logger.debug("Reading rows from json");
				Occurrences occurence = null;
				for (i = 0; i < totalRows; i++) {
					currentRow = rows.getJSONArray(i);
					if (conditionCode == null) {
						if (column.isViewColumn()) {
							occurence = new Occurrences(currentRow.getString(0), currentRow.getString(1),
									currentRow.getInt(2));
						} else {
							occurence = new Occurrences(currentRow.getString(0), currentRow.getInt(1));
						}
					} else {
						switch (conditionCode) {
						case AllowedColumnType:
							break;
						case AmbiguousValueOnExternalReference:
							if (column.isViewColumn()) {
								occurence = new Occurrences(currentRow.getString(0), currentRow.getString(1),
										currentRow.getInt(2));
							} else {
								occurence = new Occurrences(currentRow.getString(0), "1", currentRow.getInt(1));
							}
							break;
						case CastValidation:
							break;
						case CodeNamePresence:
							break;
						case DuplicateTupleValidation:
							break;
						case DuplicateValueInColumn:
							break;
						case GenericTupleValidity:
							break;
						case GenericValidity:
							break;
						case MaxOneCodenameForDataLocale:
							break;

						case MustContainAtLeastOneDimension:
							break;
						case MustContainAtLeastOneMeasure:
							break;
						case MustHaveDataLocaleMetadataAndAtLeastOneLabel:
							break;
						case OnlyOneCodeColumn:
							break;
						case OnlyOneCodenameColumn:
							break;
						case ValidPeriodFormat:
							break;
						case MissingValueOnExternalReference:
						default:
							if (column.isViewColumn()) {
								occurence = new Occurrences(currentRow.getString(0), currentRow.getString(1),
										currentRow.getInt(2));
							} else {
								occurence = new Occurrences(currentRow.getString(0), currentRow.getInt(1));
							}
							break;

						}
					}
					logger.debug("Occurence: " + occurence.toString());
					occurences.add(occurence);

				}

			} catch (JSONException e) {

				logger.error("An error occurred while parsing json document\n" + "At Row " + i + ",Column " + j
						+ "\nRow Content: " + currentRow + "\nLenght rows " + totalRows, e);
				e.printStackTrace();
				throw new TDGWTServiceException("An error occurred,  while reading json of service", e);
			}
			logger.debug("Retieved occurences: size " + occurences.size());
			return occurences;

		} catch (Throwable e) {
			logger.error("QueryService - error on queryOccurences():" + e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException(e.getLocalizedMessage());
		}

	}

	/**
	 * Retrieve ColumnLocalId value of IdColumnType
	 *
	 * @param trId
	 *            TR id
	 * @param service
	 *            Tabular data service
	 * @return Column local id
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public static ColumnLocalId retrieveColumnLocalIdOFIdColumnType(TRId trId, TabularDataService service)
			throws TDGWTServiceException {
		try {
			ColumnLocalId columnLocalId = null;
			Table table;

			table = service.getTable(new TableId(Long.valueOf(trId.getTableId())));

			List<Column> cols = table.getColumns();
			for (Column c : cols) {
				if (c.getColumnType() instanceof IdColumnType) {
					columnLocalId = c.getLocalId();
					break;
				}
			}
			return columnLocalId;

		} catch (NumberFormatException e) {
			logger.error("QueryService - retrieveColumnLocalIdOFIdColumn: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException(e.getLocalizedMessage());
		} catch (NoSuchTableException e) {
			logger.error("QueryService - retrieveColumnLocalIdOFIdColumn: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException(e.getLocalizedMessage());
		} catch (Throwable e) {
			logger.error("QueryService - retrieveColumnLocalIdOFIdColumn: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException(e.getLocalizedMessage());
		}

	}

	public static String retrieveColumnDimensionValue(String rowId, ColumnData column, TabularDataService service)
			throws TDGWTServiceException {
		try {
			logger.debug("RetriveColumnDimensionValue: [rowId=" + rowId + ", column=" + column + "]");

			column.getColumnViewData().getSourceTableDimensionColumnId();

			TableId tableId = new TableId(new Long(column.getTrId().getTableId()));
			ColumnLocalId columnId = new ColumnLocalId(column.getColumnId());
			QuerySelect querySelect = null;

			if (!column.isViewColumn()) {
				logger.error("The column selected is not a view column:" + column);
				throw new TDGWTServiceException("The column selected is not a view column");
			}
			ColumnLocalId sourceColumnId = new ColumnLocalId(
					column.getColumnViewData().getSourceTableDimensionColumnId());
			querySelect = new QuerySelect(Arrays.asList(new QueryColumn(columnId), new QueryColumn(sourceColumnId)));
			logger.debug("QuerySelect:" + querySelect.toString());

			QueryPage queryPage = null;// All occurences
			logger.debug("Occurences queryPage all");

			ColumnLocalId idColumn = retrieveColumnLocalIdOFIdColumnType(column.getTrId(), service);

			ColumnReference cr = new ColumnReference(tableId, idColumn);
			Equals rowIdEqual = new Equals(cr, new TDInteger(Integer.parseInt(rowId)));

			QueryFilter queryFilter = new QueryFilter(rowIdEqual);

			logger.debug("Query on " + tableId.toString() + " queryPage:" + queryPage + ", queryFilter:" + queryFilter
					+ ", querySelect:" + querySelect);

			String serviceJson = null;
			logger.debug("3-QueryAsJson-->[tableId:" + tableId + ", queryPage:" + queryPage + ", queryFilter:"
					+ queryFilter + ", querySelect:" + querySelect + "]");
			serviceJson = service.queryAsJson(tableId, queryPage, queryFilter, querySelect);

			logger.debug("Created serviceJson");
			// logger.debug(serviceJson);

			JSONArray currentRow = null;
			int i = 0;
			int totalRows = -1;

			org.json.JSONObject obj = new org.json.JSONObject(serviceJson);
			org.json.JSONArray rows = obj.getJSONArray("rows");

			totalRows = rows.length();
			logger.debug("Reading rows from json");
			String colValue = null;
			String dimensionValue = null;
			if (i < totalRows) {
				currentRow = rows.getJSONArray(i);
				colValue = currentRow.getString(0);
				dimensionValue = currentRow.getString(1);
			}

			if (dimensionValue == null) {
				logger.error("The column selected has not a dimension with valid value: " + column
						+ ", dimenensionValue: " + dimensionValue);
				throw new TDGWTServiceException("The column selected has not a dimension with valid value");
			} else {
				logger.debug("Retrieved: [ColumnValue:" + colValue + ", DimensionValue:" + dimensionValue + "]");
			}

			return dimensionValue;

		} catch (Throwable e) {
			logger.error("QueryService: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException(e.getLocalizedMessage());
		}

	}
}
