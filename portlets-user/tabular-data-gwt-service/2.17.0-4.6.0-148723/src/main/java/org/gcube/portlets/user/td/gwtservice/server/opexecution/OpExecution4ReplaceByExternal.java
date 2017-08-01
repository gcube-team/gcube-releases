package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.NotEvaluableDataTypeException;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.Equals;
import org.gcube.data.analysis.tabulardata.expression.functions.Cast;
import org.gcube.data.analysis.tabulardata.expression.logical.And;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.ColumnDataTypeMap;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.replacebyexternal.ReplaceByExternalColumnsMapping;
import org.gcube.portlets.user.td.gwtservice.shared.tr.replacebyexternal.ReplaceByExternalSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for replace by external tabular resource
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class OpExecution4ReplaceByExternal extends OpExecutionBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(OpExecution4ReplaceByExternal.class);

	private TabularDataService service;
	private ReplaceByExternalSession replaceByExternalSession;

	public OpExecution4ReplaceByExternal(TabularDataService service,
			ReplaceByExternalSession replaceByExternalSession) {
		this.service = service;
		this.replaceByExternalSession = replaceByExternalSession;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		OperationExecution invocation = null;

		logger.debug(replaceByExternalSession.toString());
		OperationDefinition operationDefinition;
		Map<String, Object> map = new HashMap<String, Object>();

		operationDefinition = OperationDefinitionMap.map(
				OperationsId.ReplaceColumnByExpression.toString(), service);

		TableId currentTableId = null;
		if (replaceByExternalSession.getCurrentTabularResource().getTrId()
				.isViewTable()) {
			currentTableId = new TableId(Long.valueOf(replaceByExternalSession
					.getCurrentTabularResource().getTrId()
					.getReferenceTargetTableId()));
		} else {
			currentTableId = new TableId(Long.valueOf(replaceByExternalSession
					.getCurrentTabularResource().getTrId().getTableId()));
		}

		TableId externalTableId = null;
		if (replaceByExternalSession.getExternalTabularResource().getTrId()
				.isViewTable()) {
			externalTableId = new TableId(Long.valueOf(replaceByExternalSession
					.getExternalTabularResource().getTrId()
					.getReferenceTargetTableId()));
		} else {
			externalTableId = new TableId(Long.valueOf(replaceByExternalSession
					.getExternalTabularResource().getTrId().getTableId()));
		}

		ArrayList<Expression> conditions = new ArrayList<Expression>();
		for (ReplaceByExternalColumnsMapping colMapping : replaceByExternalSession
				.getColumnsMatch()) {
			logger.debug("ColumnsMapping: " + colMapping);
			DataType currentColumnDataType = ColumnDataTypeMap
					.map(ColumnDataType.getColumnDataTypeFromId(colMapping
							.getCurrentColumn().getDataTypeName()));
			DataType externalColumnDataType = ColumnDataTypeMap
					.map(ColumnDataType.getColumnDataTypeFromId(colMapping
							.getExternalColumn().getDataTypeName()));

			ColumnReference currentColumn = new ColumnReference(currentTableId,
					new ColumnLocalId(colMapping.getCurrentColumn()
							.getColumnId()), currentColumnDataType);
			ColumnReference externalColumn = new ColumnReference(
					externalTableId, new ColumnLocalId(colMapping
							.getExternalColumn().getColumnId()),
					externalColumnDataType);
			logger.debug("CurrentColumn: " + currentColumn);
			logger.debug("ExternalColumn: " + externalColumn);

			if (!Cast.isCastSupported(currentColumnDataType,
					externalColumnDataType)) {
				logger.error("Error casting columns, types not compatible! Types: ["+currentColumnDataType+","+externalColumnDataType+"]");
				throw new TDGWTServiceException(
						"Error casting columns, types not compatible! Types: ["+currentColumnDataType+","+externalColumnDataType+"]");
			}

			logger.debug("Cast columns if need");
			Equals eq;
			if (currentColumnDataType.equals(externalColumnDataType)) {
				eq = new Equals(currentColumn, externalColumn);
			} else {
				if (currentColumnDataType instanceof TextType) {
					Expression externalColumnCasted;
					try {
						externalColumnCasted = new Cast(externalColumn,
								currentColumn.getReturnedDataType());
					} catch (NotEvaluableDataTypeException e) {
						logger.error("Error casting external column to Text data type! "
								+ e.getLocalizedMessage());
						e.printStackTrace();
						throw new TDGWTServiceException(
								"Error casting external column to Text data type!");
					}
					eq = new Equals(currentColumn, externalColumnCasted);
				} else {
					if (externalColumnDataType instanceof TextType) {
						Expression currentColumnCasted;
						try {
							currentColumnCasted = new Cast(currentColumn,
									externalColumn.getReturnedDataType());
						} catch (NotEvaluableDataTypeException e) {
							logger.error("Error casting current column to Text data type! "
									+ e.getLocalizedMessage());
							e.printStackTrace();
							throw new TDGWTServiceException(
									"Error casting current column to Text data type!");
						}
						eq = new Equals(currentColumnCasted, externalColumn);
					} else {
						if (currentColumnDataType instanceof IntegerType
								&& externalColumnDataType instanceof NumericType) {
							Expression currentColumnCasted;
							try {
								currentColumnCasted = new Cast(currentColumn,
										externalColumn.getReturnedDataType());
							} catch (NotEvaluableDataTypeException e) {
								logger.error("Error casting current column to Numeric data type! "
										+ e.getLocalizedMessage());
								e.printStackTrace();
								throw new TDGWTServiceException(
										"Error casting current column to Numeric data type!");
							}
							eq = new Equals(currentColumnCasted, externalColumn);
						} else {
							if (currentColumnDataType instanceof NumericType
									&& externalColumnDataType instanceof IntegerType) {
								Expression externalColumnCasted;
								try {
									externalColumnCasted = new Cast(externalColumn,
											currentColumn.getReturnedDataType());
								} catch (NotEvaluableDataTypeException e) {
									logger.error("Error casting external column to Numeric data type! "
											+ e.getLocalizedMessage());
									e.printStackTrace();
									throw new TDGWTServiceException(
											"Error casting external column to Numeric data type!");
								}
								eq = new Equals(currentColumn, externalColumnCasted);
							} else {
								logger.error("Error casting columns, types not compatible! Types: ["+currentColumnDataType+","+externalColumnDataType+"]");
								throw new TDGWTServiceException(
										"Error casting columns, types not compatible! Types: ["+currentColumnDataType+","+externalColumnDataType+"]");
							}
						}

					}
				}
			}

			conditions.add(eq);
		}

		Expression condition = null;
		if (conditions.size() <= 0) {
			logger.error("No columns selected");
			throw new TDGWTServiceException("No columns selected");
		} else {
			if (conditions.size() == 1) {
				condition = conditions.get(0);
			} else {
				And andCond = new And(conditions);
				condition = andCond;
			}
		}

		ColumnReference replaceColumn = new ColumnReference(externalTableId,
				new ColumnLocalId(replaceByExternalSession.getReplaceColumn()
						.getColumnId()));

		map.put(Constants.PARAMETER_REPLACE_BY_EXPRESSION_COLUMN_CONDITION,
				condition);
		map.put(Constants.PARAMETER_REPLACE_BY_EXPRESSION_COLUMN_VALUE,
				replaceColumn);

		invocation = new OperationExecution(replaceByExternalSession
				.getCurrentColumn().getColumnId(),
				operationDefinition.getOperationId(), map);

		operationExecutionSpec.setOp(invocation);

	}

}
