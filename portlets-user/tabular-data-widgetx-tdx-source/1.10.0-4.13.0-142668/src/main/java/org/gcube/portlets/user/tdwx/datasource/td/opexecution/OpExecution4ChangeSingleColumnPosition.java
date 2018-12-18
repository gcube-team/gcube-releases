package org.gcube.portlets.user.tdwx.datasource.td.opexecution;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.tdwx.datasource.td.exception.OperationException;
import org.gcube.portlets.user.tdwx.shared.ColumnsReorderingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for change single column on grid.
 * This operation is not supported on TDM beacause ordering of columns on view table
 * is not allowed. 
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class OpExecution4ChangeSingleColumnPosition extends OpExecutionBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(OpExecution4ChangeSingleColumnPosition.class);

	//private TabularDataService service;
	private ColumnsReorderingConfig columnsReorderingConfig;

	public OpExecution4ChangeSingleColumnPosition(TabularDataService service,
			ColumnsReorderingConfig columnsReorderingConfig) {
		//this.service = service;
		this.columnsReorderingConfig = columnsReorderingConfig;
	}

	@Override
	public void buildOpEx() throws OperationException {
		logger.debug("ColumnsReorderingConfig :" + columnsReorderingConfig);
		
		
		OperationExecution invocation = null;
		/*
		  
		OperationDefinition operationDefinition;
		
		operationDefinition = OperationDefinitionMap.map(
				OperationsId.ChangeSingleColumnPosition.toString(), service);
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put(OpCostants.PARAMETER_CHANGE_COLUMN_POSITION_POSITION, new Integer(columnsReorderingConfig.getColumnIndex()));
		
		invocation = new OperationExecution(columnsReorderingConfig.getColumnDefinition().getColumnLocalId(),operationDefinition.getOperationId(), map);
		*/
		operationExecutionSpec.setOp(invocation);

	}

}
