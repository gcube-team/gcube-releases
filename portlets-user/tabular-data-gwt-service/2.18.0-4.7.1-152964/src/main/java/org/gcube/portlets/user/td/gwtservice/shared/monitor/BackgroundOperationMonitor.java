/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.monitor;

import java.io.Serializable;
import java.util.Date;

import org.gcube.portlets.user.td.gwtservice.shared.task.State;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.UIOperationsId;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class BackgroundOperationMonitor implements Serializable {

	private static final long serialVersionUID = 5378053063599667767L;

	private String taskId;
	private float progress;
	private State state;
	private Throwable errorCause;
	private String submitter;
	private Date startTime;
	private Date endTime;
	private boolean inBackground;
	private boolean abort;
	private boolean hidden;
	private UIOperationsId operationId;
	private TRId trId;
	private String tabularResourceId;
	private String tabularResourceName;

	public BackgroundOperationMonitor() {
		super();
	}

	public BackgroundOperationMonitor(String taskId, float progress, State state, Throwable errorCause,
			String submitter, Date startTime, Date endTime, boolean inBackground, boolean abort, boolean hidden,
			UIOperationsId operationId, TRId trId, String tabularResourceId, String tabularResourceName) {
		super();
		this.taskId = taskId;
		this.progress = progress;
		this.state = state;
		this.errorCause = errorCause;
		this.submitter = submitter;
		this.startTime = startTime;
		this.endTime = endTime;
		this.inBackground = inBackground;
		this.abort = abort;
		this.hidden = hidden;
		this.operationId = operationId;
		this.trId = trId;
		this.tabularResourceId = tabularResourceId;
		this.tabularResourceName = tabularResourceName;
	}

	public UIOperationsId getOperationId() {
		return operationId;
	}

	public void setOperationId(UIOperationsId operationId) {
		this.operationId = operationId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public boolean isInBackground() {
		return inBackground;
	}

	public void setInBackground(boolean inBackground) {
		this.inBackground = inBackground;
	}

	public boolean isAbort() {
		return abort;
	}

	public void setAbort(boolean abort) {
		this.abort = abort;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public String getTabularResourceName() {
		return tabularResourceName;
	}

	public void setTabularResourceName(String tabularResourceName) {
		this.tabularResourceName = tabularResourceName;
	}

	public float getProgress() {
		return progress;
	}

	public void setProgress(float progress) {
		this.progress = progress;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Throwable getErrorCause() {
		return errorCause;
	}

	public void setErrorCause(Throwable errorCause) {
		this.errorCause = errorCause;
	}

	public String getSubmitter() {
		return submitter;
	}

	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getTabularResourceId() {
		return tabularResourceId;
	}

	public void setTabularResourceId(String tabularResourceId) {
		this.tabularResourceId = tabularResourceId;
	}

	@Override
	public String toString() {
		return "BackgroundOperationMonitor [taskId=" + taskId + ", progress=" + progress + ", state=" + state
				+ ", errorCause=" + errorCause + ", submitter=" + submitter + ", startTime=" + startTime + ", endTime="
				+ endTime + ", inBackground=" + inBackground + ", abort=" + abort + ", hidden=" + hidden
				+ ", operationId=" + operationId + ", trId=" + trId + ", tabularResourceId=" + tabularResourceId
				+ ", tabularResourceName=" + tabularResourceName + "]";
	}

}
