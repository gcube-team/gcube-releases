package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.LabelColumnSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for change label column
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class OpExecution4LabelColumn extends OpExecutionBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(OpExecution4LabelColumn.class);

	private TabularDataService service;
	private LabelColumnSession labelColumnSession;

	public OpExecution4LabelColumn(TabularDataService service,
			LabelColumnSession labelColumnSession) {
		this.service = service;
		this.labelColumnSession = labelColumnSession;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		logger.debug("LabelColumnSession :" + labelColumnSession);
	
		HashMap<ColumnData, String> columnsMaps = labelColumnSession.getMaps();

	    Map<ColumnReference, LocalizedText> labels = new HashMap<ColumnReference, LocalizedText>();
	    
	    TRId trId=labelColumnSession.getTrId();
	    
		long tabId;
		if(trId.isViewTable()){
			tabId = new Long(trId.getReferenceTargetTableId());
		} else {
			tabId = new Long(trId.getTableId());
		}
		TableId tId = new TableId(tabId);
		
		for (ColumnData key : columnsMaps.keySet()) {
			ColumnLocalId cId=new ColumnLocalId(key.getColumnId());
			ColumnReference columnReference = new ColumnReference(tId, cId);
		    
			ImmutableLocalizedText localizedText = new ImmutableLocalizedText(
					columnsMaps.get(key));

			labels.put(columnReference, localizedText);
		}
		
		OperationExecution invocation = null;
		OperationDefinition operationDefinition;
		operationDefinition = OperationDefinitionMap.map(
				OperationsId.ColumnNameAdd.toString(), service);
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put(Constants.NAME_PARAMETER_ID, labels);
		invocation = new OperationExecution(operationDefinition.getOperationId(), map);

		
		
		operationExecutionSpec.setOp(invocation);

	}

}
