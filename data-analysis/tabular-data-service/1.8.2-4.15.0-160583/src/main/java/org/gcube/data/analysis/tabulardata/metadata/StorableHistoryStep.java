package org.gcube.data.analysis.tabulardata.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.metadata.tabularresource.StorableTabularResource;


@Entity
public class StorableHistoryStep implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	@Column
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long id;
	
	
	@Column
	private Long tableId;
	
	
	@Column
	boolean containsDiff =false;
	
	
	private OperationExecution operationExecution;
	
	@Column
	boolean tableComplete = true;
	
	@ManyToMany(mappedBy="historySteps", cascade = CascadeType.ALL)
	private List<StorableTabularResource> tabularResources = new ArrayList<StorableTabularResource>();
	
	@Lob
	@Column
	private String operationDescription;
	
	@Column
	@Temporal(value=TemporalType.TIMESTAMP)
	private Calendar date;
	
	public StorableHistoryStep(Long tableId,
			OperationExecution operationExecution, String operationDescription) {
		super();
		this.tableId = tableId;
		this.operationExecution = operationExecution;
		this.operationDescription = operationDescription;
		this.date = Calendar.getInstance();
	}

	protected StorableHistoryStep(){}
		
		
	/**
	 * @return the tableId
	 */
	public Long getTableId() {
		return tableId;
	}


	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	

	/**
	 * @return the date
	 */
	public Calendar getDate() {
		return date;
	}

	/**
	 * @param tableId the tableId to set
	 */
	public void setTableId(long tableId) {
		this.tableId = tableId;
	}
	

	/**
	 * @param date the date to set
	 */
	public void setDate(Calendar date) {
		this.date = date;
	}

	/**
	 * @return the tabularResource
	 */
	public List<StorableTabularResource> getTabularResources() {
		return tabularResources;
	}

	/**
	 * @param tabularResource the tabularResource to set
	 */
	public void addTabularResource(StorableTabularResource tabularResource) {
		this.tabularResources.add(tabularResource);
	}

	

	public boolean isTableComplete() {
		return tableComplete;
	}

	public void setTableComplete(boolean tableComplete) {
		this.tableComplete = tableComplete;
	}

	public boolean isContainsDiff() {
		return containsDiff;
	}

	public void setContainsDiff(boolean containsDiff) {
		this.containsDiff = containsDiff;
	}

	public String getOperationDescription() {
		return operationDescription;
	}

	public OperationExecution getOperationInvocation() {
		return operationExecution;
	}
	
	@Override
	public String toString() {
		return "StorableHistoryStep [id=" + id + ", operationExecution="
				+ operationExecution + ", operationDescription="
				+ operationDescription + ", date=" + date + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (containsDiff ? 1231 : 1237);
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StorableHistoryStep other = (StorableHistoryStep) obj;
		if (containsDiff != other.containsDiff)
			return false;
		if (id != other.id)
			return false;
		return true;
	}
	
	
	
}
