package org.gcube.portlets.user.td.gwtservice.server.trservice;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStatus;
import org.gcube.portlets.user.td.gwtservice.shared.task.State;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TaskStateMap {
	public static State map(TaskStatus status) {
		if(status==null){
			return State.FAILED;
		}
		
		switch (status) {
		case INITIALIZING:
			return State.INITIALIZING;
		case FAILED:
			return State.FAILED;
		case ABORTED:
			return State.ABORTED;
		case IN_PROGRESS:
			return State.IN_PROGRESS;
		case SUCCEDED:
			return State.SUCCEDED;
		case STOPPED:
			return State.STOPPED;
		case VALIDATING_RULES:
			return State.VALIDATING_RULES;
		case GENERATING_VIEW:
			return State.GENERATING_VIEW;
		default:
			return State.FAILED;
		}
	}
}
