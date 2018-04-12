package org.gcube.portlets.user.td.gwtservice.server.trservice;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.WorkerStatus;
import org.gcube.portlets.user.td.gwtservice.shared.task.WorkerState;

/**
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public class WorkerStateMap {
	public static WorkerState map(WorkerStatus status) {
		if(status==null){
			return WorkerState.FAILED;
		}
		
		switch (status) {
		case FAILED:
			return WorkerState.FAILED;
		case INITIALIZING:
			return WorkerState.INITIALIZING;
		case IN_PROGRESS:
			return WorkerState.IN_PROGRESS;
		case PENDING:
			return WorkerState.PENDING;
		case SUCCEDED:
			return WorkerState.SUCCEDED;
		case VALIDATING_DATA:
			return WorkerState.VALIDATING_DATA;
		case ABORTED:
			return WorkerState.ABORTED;
		default:
			return WorkerState.FAILED;
		
		}
	}
}
