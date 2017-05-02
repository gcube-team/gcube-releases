package org.gcube.portlets.user.td.gwtservice.server.trservice;

import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.tr.RefColumn;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi"
 * 
 */
public class ExtractReferences {
	private static Logger logger = LoggerFactory
			.getLogger(ExtractReferences.class);

	public static RefColumn extract(OperationExecution op) {
		logger.debug("ExtractReferences OperationExecution: "+op);
		RefColumn refColumn = null;
		Long opId = op.getOperationId();
		
		if (opId.compareTo(OperationsId.ChangeToDimensionColumn.toLong())==0) {
			refColumn = retrieveRefColumn(op);
		}

		logger.debug("ExtractReferences: " + refColumn);
		return refColumn;

	}

	protected static RefColumn retrieveRefColumn(OperationExecution op) {
		RefColumn refColumn = null;
		Map<String, Object> parameters = op.getParameters();
		ColumnReference columnReference = (ColumnReference) parameters
				.get(Constants.PARAMETER_REFERENCE_COLUMN);
		logger.debug("Parameter " + Constants.PARAMETER_REFERENCE_COLUMN + ": "
				+ columnReference);
		if (columnReference != null && columnReference.getColumnId() != null
				&& columnReference.getTableId() != null) {
			refColumn = new RefColumn(String.valueOf(columnReference
					.getTableId().getValue()), columnReference.getColumnId()
					.getValue());

		}
		return refColumn;
	}
}
