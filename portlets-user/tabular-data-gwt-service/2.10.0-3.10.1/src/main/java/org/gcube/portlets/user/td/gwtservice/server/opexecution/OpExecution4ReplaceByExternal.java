package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.Equals;
import org.gcube.data.analysis.tabulardata.expression.logical.And;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.replacebyexternal.ReplaceByExternalColumnsMapping;
import org.gcube.portlets.user.td.gwtservice.shared.tr.replacebyexternal.ReplaceByExternalSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for replace by external tabular resource
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class OpExecution4ReplaceByExternal extends OpExecutionBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(OpExecution4ReplaceByExternal.class);

	private TabularDataService service;
	private ReplaceByExternalSession replaceByExternalSession;

	public OpExecution4ReplaceByExternal(
			TabularDataService service,
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
		
		TableId currentTableId=null;
		if(replaceByExternalSession.getCurrentTabularResource().getTrId().isViewTable()){
			currentTableId=new TableId(Long.valueOf(replaceByExternalSession.getCurrentTabularResource().getTrId().getReferenceTargetTableId()));
		} else {
			currentTableId=new TableId(Long.valueOf(replaceByExternalSession.getCurrentTabularResource().getTrId().getTableId()));
		}
		
		TableId externalTableId=null;
		if(replaceByExternalSession.getExternalTabularResource().getTrId().isViewTable()){
			externalTableId=new TableId(Long.valueOf(replaceByExternalSession.getExternalTabularResource().getTrId().getReferenceTargetTableId()));
		} else {
			externalTableId=new TableId(Long.valueOf(replaceByExternalSession.getExternalTabularResource().getTrId().getTableId()));
		}
		
		ArrayList<Expression> conditions=new ArrayList<Expression>();
		for(ReplaceByExternalColumnsMapping colMapping:replaceByExternalSession.getColumnsMatch()){
			ColumnReference currentColumn=new ColumnReference(currentTableId, new ColumnLocalId(colMapping.getCurrentColumn().getColumnId()));
			ColumnReference externalColumn=new ColumnReference(externalTableId, new ColumnLocalId(colMapping.getExternalColumn().getColumnId()));
			Equals eq=new Equals(currentColumn, externalColumn);
			conditions.add(eq);
		}
		
		Expression condition=null;
		if(conditions.size()<=0){
			logger.error("No columns selected");
			throw new TDGWTServiceException("No columns selected");
		} else {
			if(conditions.size()==1){
				condition=conditions.get(0);
			} else {
				And andCond=new And(conditions);
				condition=andCond;
			}
		}
	
		ColumnReference replaceColumn=new ColumnReference(externalTableId, new ColumnLocalId(replaceByExternalSession.getReplaceColumn().getColumnId()));
		
		map.put(Constants.PARAMETER_REPLACE_BY_EXPRESSION_COLUMN_CONDITION,
				condition);
		map.put(Constants.PARAMETER_REPLACE_BY_EXPRESSION_COLUMN_VALUE,
				replaceColumn);

		
		invocation = new OperationExecution(replaceByExternalSession.getCurrentColumn().getColumnId(),
				operationDefinition.getOperationId(), map);

		
		operationExecutionSpec.setOp(invocation);

	}

}
