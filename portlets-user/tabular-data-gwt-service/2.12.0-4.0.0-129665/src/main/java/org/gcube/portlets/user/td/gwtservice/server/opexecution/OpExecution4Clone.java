package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.clone.CloneTabularResourceSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for delete column
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class OpExecution4Clone extends OpExecutionBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(OpExecution4Clone.class);

	private TabularDataService service;
	private CloneTabularResourceSession cloneTabularResourceSession;

	public OpExecution4Clone(TabularDataService service,
			CloneTabularResourceSession cloneTabularResourceSession) {
		this.service = service;
		this.cloneTabularResourceSession = cloneTabularResourceSession;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		logger.debug(cloneTabularResourceSession.toString());
		
		TRId trId=cloneTabularResourceSession.getTrId();
		TableId tableId;
		if(trId.isViewTable()){
			tableId=new TableId(Long.valueOf(trId.getReferenceTargetTableId()));
		} else {
			tableId=new TableId(Long.valueOf(trId.getTableId()));
		}
		
		
		OperationDefinition operationDefinition;
		operationDefinition = OperationDefinitionMap.map(
				OperationsId.CLONE.toString(), service);
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put(Constants.PARAMETER_CLONE_TABLE, tableId);
	
		OperationExecution invocation = new OperationExecution(
				operationDefinition.getOperationId(), map);

		operationExecutionSpec.setOp(invocation);
	}

}
