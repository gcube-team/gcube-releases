package org.gcube.portlets.user.td.gwtservice.server.trservice;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.Equals;
import org.gcube.data.analysis.tabulardata.expression.logical.And;
import org.gcube.data.analysis.tabulardata.expression.logical.Not;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.batch.OccurrencesForReplaceBatchColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.batch.ReplaceBatchColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.batch.ReplaceEntry;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.ReplaceColumnByExpressionSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.ReplaceColumnSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class ExpressionGenerator {
	private static Logger logger = LoggerFactory
			.getLogger(ExpressionGenerator.class);

	public static Expression genReplaceValueParameterCondition(
			ReplaceColumnSession replaceColumnSession,
			TabularDataService service) throws TDGWTServiceException {
		try {
			if (replaceColumnSession.isReplaceDimension()) {
				// Dimension
				TableId tableId;
				if (replaceColumnSession.getColumnData().getTrId()
						.isViewTable()) {
					tableId = new TableId(Long.valueOf(replaceColumnSession
							.getColumnData().getTrId()
							.getReferenceTargetTableId()));
				} else {
					tableId = new TableId(Long.valueOf(replaceColumnSession
							.getColumnData().getTrId().getTableId()));
				}
				ColumnReference cr = new ColumnReference(tableId,
						new ColumnLocalId(replaceColumnSession.getColumnData()
								.getColumnViewData()
								.getSourceTableDimensionColumnId()));

				String rowId=QueryService.retrieveColumnDimensionValue(
						replaceColumnSession.getRowId(), replaceColumnSession.getColumnData(),
						service);

				Equals eq = new Equals(cr, new TDInteger(
						Integer.parseInt(rowId)));

				return eq;
			} else {
				// Simple
				TableId tableId;
				if (replaceColumnSession.getColumnData().getTrId()
						.isViewTable()) {
					tableId = new TableId(Long.valueOf(replaceColumnSession
							.getColumnData().getTrId()
							.getReferenceTargetTableId()));

				} else {
					tableId = new TableId(Long.valueOf(replaceColumnSession
							.getColumnData().getTrId().getTableId()));

				}

				ColumnReference cr = new ColumnReference(tableId,
						new ColumnLocalId(replaceColumnSession.getColumnData()
								.getColumnId()));

				TDTypeValue td = TDTypeValueMap.map(replaceColumnSession
						.getColumnData().getDataTypeName(),
						replaceColumnSession.getValue());
				Equals eq = new Equals(cr, td);

				return eq;
			}
		} catch (Throwable e) {
			logger.debug("Error in genReplaceValueParameterCondition: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException(
					"Error generating condition parameter: "
							+ e.getLocalizedMessage());
		}
	}

	public static Expression genReplaceValueParameterValue(
			ReplaceColumnSession replaceColumnSession)
			throws TDGWTServiceException {
		try {
			if (replaceColumnSession.isReplaceDimension()) {
				TDInteger value = new TDInteger(
						Integer.parseInt(replaceColumnSession.getReplaceValue()));
				return value;
			} else {
				TDTypeValue td = TDTypeValueMap.map(replaceColumnSession
						.getColumnData().getDataTypeName(),
						replaceColumnSession.getReplaceValue());
				return td;
			}
		} catch (Throwable e) {
			logger.debug("Error in genReplaceValueParameterValue: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException(
					"Error generating value parameter: "
							+ e.getLocalizedMessage());
		}
	}
	
	public static Expression genReplaceValueParameterValue(
			ReplaceColumnByExpressionSession replaceColumnByExpressionSession)
			throws TDGWTServiceException {
		try {
				TDTypeValue td = TDTypeValueMap.map(replaceColumnByExpressionSession
						.getColumn().getDataTypeName(),
						replaceColumnByExpressionSession.getReplaceValue());
				return td;
			
		} catch (Throwable e) {
			logger.debug("Error in genReplaceValueParameterValue: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException(
					"Error generating value parameter: "
							+ e.getLocalizedMessage());
		}
	}
	

	public static Expression genReplaceValueParameterCondition(
			ReplaceBatchColumnSession replaceBatchColumnSession, ReplaceEntry re)
			throws TDGWTServiceException {
		try {
			if (replaceBatchColumnSession.isReplaceDimension()) {
				// Dimension
				TableId tableId;
				if (replaceBatchColumnSession.getColumnData().getTrId()
						.isViewTable()) {
					tableId = new TableId(
							Long.valueOf(replaceBatchColumnSession
									.getColumnData().getTrId()
									.getReferenceTargetTableId()));
				} else {
					tableId = new TableId(
							Long.valueOf(replaceBatchColumnSession
									.getColumnData().getTrId().getTableId()));
				}
				ColumnReference cr = new ColumnReference(tableId,
						new ColumnLocalId(replaceBatchColumnSession
								.getColumnData().getColumnViewData()
								.getSourceTableDimensionColumnId()));

				Equals eq = new Equals(cr, new TDInteger(Integer.parseInt(re
						.getRowId())));

				return eq;
			} else {
				// Simple
				TableId tableId;
				if (replaceBatchColumnSession.getColumnData().getTrId()
						.isViewTable()) {
					tableId = new TableId(
							Long.valueOf(replaceBatchColumnSession
									.getColumnData().getTrId()
									.getReferenceTargetTableId()));

				} else {
					tableId = new TableId(
							Long.valueOf(replaceBatchColumnSession
									.getColumnData().getTrId().getTableId()));

				}

				ColumnReference cr = new ColumnReference(tableId,
						new ColumnLocalId(replaceBatchColumnSession
								.getColumnData().getColumnId()));

				TDTypeValue td = TDTypeValueMap.map(replaceBatchColumnSession
						.getColumnData().getDataTypeName(), re.getValue());
				Equals eq = new Equals(cr, td);

				return eq;
			}
		} catch (Throwable e) {
			logger.debug("Error in genReplaceValueParameterCondition: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException(
					"Error generating condition parameter: "
							+ e.getLocalizedMessage());
		}
	}

	public static Expression genReplaceBatchValueParameterValue(
			ReplaceBatchColumnSession replaceBatchColumnSession, ReplaceEntry re)
			throws TDGWTServiceException {
		try {
			if (replaceBatchColumnSession.isReplaceDimension()) {
				TDInteger value = new TDInteger(Integer.parseInt(re
						.getReplacementDimensionRow().getRowId()));
				return value;
			} else {
				TDTypeValue td = TDTypeValueMap.map(replaceBatchColumnSession
						.getColumnData().getDataTypeName(), re
						.getReplacementValue());
				return td;
			}

		} catch (Throwable e) {
			logger.debug("Error in genReplaceBatchValueParameterValue: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException(
					"Error generating value parameter: "
							+ e.getLocalizedMessage());
		}

	}

	public static Expression genReplaceBatchOccurrencesFilter(
			OccurrencesForReplaceBatchColumnSession occurrencesSession)
			throws TDGWTServiceException {
		try {

			String validationColumnColumnId = occurrencesSession
					.getValidationColumnColumnId();
			ColumnData column = occurrencesSession.getColumnData();
			String tdId;
			if (column.getTrId().isViewTable()) {
				tdId = column.getTrId().getReferenceTargetTableId();
			} else {
				tdId = column.getTrId().getTableId();
			}
			TableId tableId = new TableId(Long.valueOf(tdId));

			if (validationColumnColumnId == null
					|| validationColumnColumnId.isEmpty()) {
				ArrayList<String> validationColumnReferences = column
						.getValidationColumnReferences();
				ArrayList<Expression> listColumnReference = new ArrayList<Expression>();
				for (String columnLocalId : validationColumnReferences) {
					ColumnReference cr = new ColumnReference(tableId,
							new ColumnLocalId(columnLocalId));
					listColumnReference.add(cr);
				}
				if (listColumnReference.size() <= 0) {
					return null;
				} else {
					if (listColumnReference.size() == 1) {
						Not not = new Not(listColumnReference.get(0));
						return not;
					} else {
						And and = new And(listColumnReference);
						Not not = new Not(and);
						return not;
					}
				}

			} else {
				ColumnReference cr = new ColumnReference(tableId,
						new ColumnLocalId(validationColumnColumnId));
				Not not = new Not(cr);
				return not;
			}

		} catch (Throwable e) {
			logger.error("Error in genReplaceBatchOccurrencesFilter: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException(
					"Error generating occurences filter: "
							+ e.getLocalizedMessage());
		}

	}

	public static Expression genEditRowParamaterCondition(
			TabularDataService service, TRId trId, String rowId)
			throws TDGWTServiceException {
		try {
			Expression exp = null;
			TableId tableId;
			if (trId.isViewTable()) {
				tableId = new TableId(
						new Long(trId.getReferenceTargetTableId()));
			} else {
				tableId = new TableId(new Long(trId.getTableId()));
			}

			Table table = service.getTable(new TableId(Long.valueOf(trId
					.getTableId())));

			List<Column> cols = table.getColumns();
			Column idCol = null;
			for (Column c : cols) {
				if (c.getColumnType() instanceof IdColumnType) {
					idCol = c;
					break;
				}
			}

			if (idCol != null) {
				ColumnReference cr = new ColumnReference(tableId,
						idCol.getLocalId());

				exp = new Equals(cr, new TDInteger(
						Integer.parseInt(rowId)));

			} else {
				logger.debug("No IdColumnType retrieved for table:"
						+ table.getId().toString());
			}
			logger.debug("genEditRowParamaterCondition() condition:" + exp);
			return exp;
		} catch (Throwable e) {
			logger.error("Error in genEditRowParamaterCondition(): "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException(
					"Error in genEditRowParamaterCondition(): "
							+ e.getLocalizedMessage());
		}

	}

}
