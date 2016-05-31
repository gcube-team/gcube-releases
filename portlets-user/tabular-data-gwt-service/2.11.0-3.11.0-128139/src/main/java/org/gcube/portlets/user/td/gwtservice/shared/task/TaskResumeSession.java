package org.gcube.portlets.user.td.gwtservice.shared.task;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.tr.column.mapping.ColumnMappingData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author "Giancarlo Panichi"
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TaskResumeSession implements Serializable {

	private static final long serialVersionUID = -4503878699159491057L;

	protected TRId trId;
	protected String taskId;
	protected ColumnData column;
	protected ArrayList<ColumnMappingData> columnMapping;
	protected InvocationS invocationS;
	
	public TaskResumeSession() {

	}

	public TaskResumeSession(TRId trId, String taskId) {
		this.trId = trId;
		this.taskId = taskId;
		this.columnMapping = null;
	}

	public TaskResumeSession(TRId trId, String taskId,
			ArrayList<ColumnMappingData> columnMapping, ColumnData column, InvocationS invocationS) {
		this.trId = trId;
		this.taskId = taskId;
		this.columnMapping = columnMapping;
		this.column=column;
		this.invocationS=invocationS;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public ArrayList<ColumnMappingData> getColumnMapping() {
		return columnMapping;
	}

	public void setColumnMapping(ArrayList<ColumnMappingData> columnMapping) {
		this.columnMapping = columnMapping;
	}

	public ColumnData getColumn() {
		return column;
	}

	public void setColumn(ColumnData column) {
		this.column = column;
	}

	public InvocationS getInvocationS() {
		return invocationS;
	}

	public void setInvocationS(InvocationS invocationS) {
		this.invocationS = invocationS;
	}

	@Override
	public String toString() {
		return "TaskResumeSession [trId=" + trId + ", taskId=" + taskId
				+ ", column=" + column + ", columnMapping=" + columnMapping
				+ ", invocationS=" + invocationS + "]";
	}

	
	

	

}
