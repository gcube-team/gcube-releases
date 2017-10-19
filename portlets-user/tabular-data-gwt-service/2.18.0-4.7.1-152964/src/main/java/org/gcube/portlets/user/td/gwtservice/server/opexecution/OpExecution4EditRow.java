package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.ExpressionGenerator;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.server.trservice.ValueMap;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.rows.EditRowSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for edit and add row
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class OpExecution4EditRow extends OpExecutionBuilder {
	private static final String ITEM_CREATE_ROW = "NewRow";
	private static Logger logger = LoggerFactory
			.getLogger(OpExecution4EditRow.class);

	private TabularDataService service;
	private EditRowSession editRowSession;

	public OpExecution4EditRow(TabularDataService service, EditRowSession editRowSession) {
		this.service = service;
		this.editRowSession = editRowSession;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		logger.debug(editRowSession.toString());
		
			
		
		
		ArrayList<OperationExecution> invocations = new ArrayList<OperationExecution>();
		
		ValueMap valueMap = new ValueMap();
		
		if (editRowSession.isNewRow()) {
			logger.debug("Is a add row");
			OperationDefinition operationDefinition= OperationDefinitionMap.map(
					OperationsId.AddRow.toString(), service);

			HashMap<String,String> fieldsMap=editRowSession.getRowsMaps().get(ITEM_CREATE_ROW);
					
			ArrayList<Map<String, Object>> compositeValue = valueMap
					.genValueMap(editRowSession.getTrId(), editRowSession.getColumns(), fieldsMap);
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(Constants.PARAMETER_ADD_ROW_COMPOSITE, compositeValue);

			OperationExecution invocation = new OperationExecution(
					operationDefinition.getOperationId(), map);
			invocations.add(invocation);

		} else {
			logger.debug("Is a edit row");
			
			for (String rowId : editRowSession.getRowsId()) {
				logger.debug("EDIT OPERATION ROWID: "+rowId);
				HashMap<String,String> fieldsMap=editRowSession.getRowsMaps().get(rowId);
				
				ArrayList<Map<String, Object>> compositeValue = valueMap
						.genValueMap(editRowSession.getTrId(),editRowSession.getColumns(), fieldsMap);

				Expression exp = ExpressionGenerator.genEditRowParamaterCondition(
						service, editRowSession.getTrId(), rowId);
				logger.debug("EDIT OPERATION EXP: "+exp);
				OperationDefinition operationDefinition = OperationDefinitionMap.map(
						OperationsId.ModifyTuplesValuesByExpression.toString(),
						service);

				Map<String, Object> map = new HashMap<String, Object>();
				map.put(Constants.PARAMETER_ADD_ROW_COMPOSITE, compositeValue);
				map.put(Constants.PARAMETER_EDIT_ROW_CONDITION, exp);
								
				OperationExecution invocation = new OperationExecution(
						operationDefinition.getOperationId(), map);
				invocations.add(invocation);
			}

		}

		

		operationExecutionSpec.setOps(invocations);


	}

}
