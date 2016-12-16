package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.normalization.NormalizationSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for Normalization
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class OpExecution4Normalization extends OpExecutionBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(OpExecution4Normalization.class);

	private TabularDataService service;
	private NormalizationSession normalizationSession;

	public OpExecution4Normalization(TabularDataService service,
			NormalizationSession normalizationSession) {
		this.service = service;
		this.normalizationSession = normalizationSession;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		OperationExecution invocation = null;

		logger.debug(normalizationSession.toString());
		OperationDefinition operationDefinition;
		Map<String, Object> map = new HashMap<String, Object>();

		ArrayList<ColumnData> cols = normalizationSession.getColumns();
		logger.debug("Column To Set: " + cols);
		ArrayList<ColumnReference> columnReferences = new ArrayList<ColumnReference>();

		for (ColumnData col : cols) {
			ColumnLocalId cId = new ColumnLocalId(col.getColumnId());
			TRId trId = col.getTrId();
			logger.debug("trID: " + trId);
			long tabId;
			if(trId.isViewTable()){
				tabId = new Long(trId.getReferenceTargetTableId());
			} else {
				tabId = new Long(trId.getTableId());
				
			}
			
			TableId tId = new TableId(tabId);
			ColumnReference columnReference = new ColumnReference(tId, cId);
			columnReferences.add(columnReference);
		}

		String name=normalizationSession.getNormalizedColumnName();
		String value=normalizationSession.getValueColumnName();
		
		ImmutableLocalizedText nameText = new ImmutableLocalizedText(
				name);
		ImmutableLocalizedText valueText = new ImmutableLocalizedText(
				value);
		
		operationDefinition = OperationDefinitionMap.map(
				OperationsId.Normalize.toString(), service);

		map.put(Constants.PARAMETER_NORMALIZATION_TO_NORMALIZE, columnReferences);
		map.put(Constants.PARAMETER_NORMALIZATION_NORM_LABEL, nameText);
		map.put(Constants.PARAMETER_NORMALIZATION_QUANT_LABEL, valueText);
		
		invocation = new OperationExecution(
				operationDefinition.getOperationId(), map);

		operationExecutionSpec.setOp(invocation);

	}

}
