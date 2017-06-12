package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.codelisthelper.CodelistMappingSession;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for codelist mapping
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class OpExecution4CodelistMapping extends OpExecutionBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(OpExecution4CodelistMapping.class);

	private TabularDataService service;
	private CodelistMappingSession codelistMappingSession;
	private String storageId;

	public OpExecution4CodelistMapping(
			TabularDataService service,
			CodelistMappingSession codelistMappingSession, String storageId) {
		this.service = service;
		this.codelistMappingSession = codelistMappingSession;
		this.storageId=storageId;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		OperationExecution invocation = null;

		logger.debug(codelistMappingSession.toString());
		OperationDefinition operationDefinition;
		Map<String, Object> map = new HashMap<String, Object>();

		operationDefinition = OperationDefinitionMap.map(
				OperationsId.CodelistMappingImport.toString(), service);
		map.put(Constants.PARAMETER_ID, storageId);
		
		ColumnData columnData=codelistMappingSession.getConnectedColumn();
		long tabId;
		if(columnData.getTrId().isViewTable()){
			tabId = new Long(columnData.getTrId().getReferenceTargetTableId());
		} else {
			tabId = new Long(columnData.getTrId().getTableId());
		}
		
		TableId tableId=new TableId(new Long(tabId));
		ColumnLocalId columnId=new ColumnLocalId(columnData.getColumnId());
		ColumnReference colRef=new ColumnReference(tableId, columnId);
	
		map.put(Constants.PARAMETER_IMPORT_CODELIST_MAPPING_OLDCODES,  colRef);
		
		invocation = new OperationExecution(
				operationDefinition.getOperationId(), map);
		
		operationExecutionSpec.setOp(invocation);

	}

}
