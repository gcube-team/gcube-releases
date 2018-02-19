package org.gcube.data.analysis.tabulardata.task;

import java.util.Observable;
import java.util.Observer;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.WorkerStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.ValidationStep;
import org.gcube.data.analysis.tabulardata.operation.worker.Worker;

public class ValidationStepUpdater extends ValidationStep implements Observer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7341818605669685859L;

	@Override
	public void update(Observable o, Object obj) {

		Worker<?> w = (Worker<?>) obj;
		this.status = WorkerStatus.valueOf(w.getStatus().name());
		this.progress= w.getProgress();	
		this.humanReadableStatus = w.getHumanReadableStatus();
		if (w.getException()!=null)
			this.errorMessage= w.getException();
		
	}
}