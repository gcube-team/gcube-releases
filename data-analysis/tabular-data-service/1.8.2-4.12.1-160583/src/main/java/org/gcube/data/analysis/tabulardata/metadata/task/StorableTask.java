package org.gcube.data.analysis.tabulardata.metadata.task;

import java.util.Calendar;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo.TaskType;
import org.gcube.data.analysis.tabulardata.metadata.tabularresource.StorableTabularResource;
import org.gcube.data.analysis.tabulardata.task.TaskContext;

@Entity
@NamedQuery(name="TASK.getByTr", 
	query="SELECT DISTINCT task FROM StorableTabularResource str JOIN str.tasks task LEFT JOIN str.sharedWith s  WHERE str.id = :trid and ((CONCAT('u(',:user,')') = s) or (CONCAT('g(',:group,')') = s) or str.owner = :user) and :scope MEMBER OF str.scopes and str.hidden=false "
			+ " ORDER BY task.startTime DESC")
public class StorableTask {
	
	@Column
	@Id
	private String identifier;
	
	@Temporal(value=TemporalType.TIMESTAMP)
	private Calendar startTime;
	
	@Temporal(value=TemporalType.TIMESTAMP)
	private Calendar endTime;
	
	private TaskInfo storedTask;
		
	@OneToOne(cascade = CascadeType.ALL)
	private TaskContext taskContext;
	
	@ManyToOne
	private StorableTabularResource tabularResource;
	
	@Column
	private TaskType type;
		
	
	@SuppressWarnings("unused")
	private StorableTask() {
	}
	
	public StorableTask(TaskInfo task, StorableTabularResource tabularResource){
		this.storedTask = task;
		this.identifier = task.getIdentifier();
		this.startTime = task.getStartTime();
		this.endTime = task.getEndTime();
		this.type = task.getType();
		this.tabularResource = tabularResource;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.metadata.task.Task#getIdentifier()
	 */
	public String getIdentifier() {
		return identifier;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.metadata.task.Task#setIdentifier(java.lang.String)
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.metadata.task.Task#getStartTime()
	 */
	public Calendar getStartTime() {
		return startTime;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.metadata.task.Task#setStartTime(java.util.Calendar)
	 */
	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}

	
	/**
	 * @param taskContext the taskContext to set
	 */
	public void setTaskContext(TaskContext taskContext) {
		this.taskContext = taskContext;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.metadata.task.Task#getEndTime()
	 */
	public Calendar getEndTime() {
		return endTime;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.metadata.task.Task#setEndTime(java.util.Calendar)
	 */
	public void setEndTime(Calendar endTime) {
		this.endTime = endTime;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.metadata.task.Task#getStoredTask()
	 */
	public TaskInfo getStoredTask() {
		return storedTask;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.metadata.task.Task#setStoredTask(org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskInfo)
	 */
	public void setStoredTask(TaskInfo storedTask) {
		this.storedTask = storedTask;
		this.startTime = storedTask.getStartTime();
		this.endTime = storedTask.getEndTime();
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.metadata.task.Task#getTaskType()
	 */
	public TaskType getTaskType() {
		return type;
	}
	
	/**
	 * @return the taskContext
	 */
	public TaskContext getTaskContext() {
		return taskContext;
	}

	
	
	public StorableTabularResource getTabularResource() {
		return tabularResource;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "StorableTask [identifier=" + identifier + ", startTime="
				+ startTime + ", endTime=" + endTime + ", taskContext="
				+ taskContext + ", type=" + type + "]";
	}
	
}
