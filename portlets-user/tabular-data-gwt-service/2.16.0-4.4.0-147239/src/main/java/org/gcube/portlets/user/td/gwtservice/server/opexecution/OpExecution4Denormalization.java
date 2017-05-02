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
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.normalization.DenormalizationSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for Denormalization
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class OpExecution4Denormalization extends OpExecutionBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(OpExecution4Denormalization.class);

	private TabularDataService service;
	private DenormalizationSession denormalizationSession;

	public OpExecution4Denormalization(TabularDataService service,
			DenormalizationSession denormalizationSession) {
		this.service = service;
		this.denormalizationSession = denormalizationSession;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		OperationExecution invocation = null;

		logger.debug(denormalizationSession.toString());
		OperationDefinition operationDefinition;
		Map<String, Object> map = new HashMap<String, Object>();

		TRId trId = denormalizationSession.getTrId();
		logger.debug("trID: " + trId);
		if(trId==null){
			logger.error("Error in DenormalizationSession: trId is null");
			throw new TDGWTServiceException("No tabular resource set");
		}
		
		long tabId;
		if(trId.isViewTable()){
			tabId = new Long(trId.getReferenceTargetTableId());
		} else {
			tabId = new Long(trId.getTableId());
			
		}
		TableId tId = new TableId(tabId);
		
		ColumnData valueCol=denormalizationSession.getValueColumn();
		logger.debug("Value Column: "+valueCol);
		if(valueCol==null){
			logger.error("Error in DenormalizationSession: Value Column is null");
			throw new TDGWTServiceException("No value column set");
		}
		ColumnLocalId valueId = new ColumnLocalId(valueCol.getColumnId());
		ColumnReference valueColumnReference = new ColumnReference(tId, valueId);
		
		ColumnData attributeCol=denormalizationSession.getAttributeColumn();
		logger.debug("Attribute Column: "+valueCol);
		if(attributeCol==null){
			logger.error("Error in DenormalizationSession: Attribute Column is null");
			throw new TDGWTServiceException("No attribute column set");
		}
		
		ColumnLocalId attributeId = new ColumnLocalId(attributeCol.getColumnId());
		ColumnReference attributeColumnReference = new ColumnReference(tId, attributeId);
		
		
		operationDefinition = OperationDefinitionMap.map(
				OperationsId.Denormalize.toString(), service);

		map.put(Constants.PARAMETER_DENORMALIZATION_VALUE_COLUMN, valueColumnReference);
		map.put(Constants.PARAMETER_DENORMALIZATION_ATTRIBUTE_COLUMN, attributeColumnReference);
		
		invocation = new OperationExecution(
				operationDefinition.getOperationId(), map);

		operationExecutionSpec.setOp(invocation);

	}

}
