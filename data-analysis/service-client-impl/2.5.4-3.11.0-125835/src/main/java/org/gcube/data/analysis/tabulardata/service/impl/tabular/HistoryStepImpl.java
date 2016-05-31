package org.gcube.data.analysis.tabulardata.service.impl.tabular;

import java.util.Calendar;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.HistoryData;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.service.operation.JobClassifier;
import org.gcube.data.analysis.tabulardata.service.tabular.HistoryStep;
import org.gcube.data.analysis.tabulardata.service.tabular.HistoryStepId;

public class HistoryStepImpl implements HistoryStep {

	private HistoryData historyData;
	
	protected HistoryStepImpl(HistoryData historyData){
		this.historyData = historyData;
	}
	
	public String getOperationDescription() {
		return historyData.getOperationDescription();
	}

	public TableId getResultTable() {
		return historyData.getResultTableId();
	}

	public JobClassifier getOperationClassifier() {
		return JobClassifier.PROCESSING;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HistoryStepImpl [historyData=" + historyData + "]";
	}

	@Override
	public HistoryStepId getId() {
		return new HistoryStepId(historyData.getId());
	}

	@Override
	public Calendar getExecutionDate() {
		return historyData.getDate();
	}
	
	
}
