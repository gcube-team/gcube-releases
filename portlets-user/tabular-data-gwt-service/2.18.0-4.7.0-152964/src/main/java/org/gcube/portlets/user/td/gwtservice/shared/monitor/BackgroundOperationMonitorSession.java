package org.gcube.portlets.user.td.gwtservice.shared.monitor;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class BackgroundOperationMonitorSession implements Serializable {

	private static final long serialVersionUID = 2175453217961108582L;

	private ArrayList<OperationMonitorSession> operationMonitorSessionList;

	public BackgroundOperationMonitorSession() {
		super();
		operationMonitorSessionList = new ArrayList<OperationMonitorSession>();
	}

	public BackgroundOperationMonitorSession(
			ArrayList<OperationMonitorSession> operationMonitorSessionList) {
		super();
		this.operationMonitorSessionList = operationMonitorSessionList;
	}

	public ArrayList<OperationMonitorSession> getOperationMonitorSessionList() {
		return operationMonitorSessionList;
	}

	public void setOperationMonitorSessionList(
			ArrayList<OperationMonitorSession> operationMonitorSessionList) {
		this.operationMonitorSessionList = operationMonitorSessionList;
	}

	public void addToOperationMonitorSessionList(OperationMonitorSession operationMonitorSession) {
		if (operationMonitorSession != null
				&& operationMonitorSession.getTaskId() != null
				&& !operationMonitorSession.getTaskId().isEmpty()) {
			for (OperationMonitorSession ops : operationMonitorSessionList) {
				if (ops.getTaskId().compareTo(
						operationMonitorSession.getTaskId()) == 0) {
					int index=operationMonitorSessionList.indexOf(ops);
					operationMonitorSessionList.set(index,operationMonitorSession);
					return;
				}
			}
			operationMonitorSessionList.add(operationMonitorSession);
		}
	}

	@Override
	public String toString() {
		return "BackgroundOperationMonitorSession [operationMonitorSessionList="
				+ operationMonitorSessionList + "]";
	}

}
