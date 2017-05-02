package org.gcube.portlets.admin.accountingmanager.server.util;

import java.io.Serializable;
import java.util.Calendar;
import java.util.concurrent.Future;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class TaskInProgress implements Serializable {

	private static final long serialVersionUID = -1957012551318695316L;
	private Calendar startTime;
	private Future<TaskStatus> future;

	public TaskInProgress(Calendar startTime, Future<TaskStatus> future) {
		super();
		this.startTime = startTime;
		this.future = future;
	}

	public Calendar getStartTime() {
		return startTime;
	}

	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}

	public Future<TaskStatus> getFuture() {
		return future;
	}

	public void setFuture(Future<TaskStatus> future) {
		this.future = future;
	}

	@Override
	public String toString() {
		return "TaskInProgress [startTime=" + startTime + ", future=" + future
				+ "]";
	}

}
