package org.gcube.data.analysis.tabulardata.service.tabular;

import java.util.Calendar;
import org.gcube.data.analysis.tabulardata.model.table.TableId;

public interface HistoryStep {
	
	String getOperationDescription();
	
	TableId getResultTable();
	
	HistoryStepId getId();
	
	Calendar getExecutionDate();

}
