package org.gcube.data.analysis.tabulardata.operation.factories.types;

import org.gcube.data.analysis.tabulardata.operation.OperationType;

public abstract class ExportWorkerFactory extends TableResourceCreatorWorkerFactory {
	
	@Override
	protected OperationType getOperationType() {
	 return OperationType.EXPORT;
	}
	
	
}
