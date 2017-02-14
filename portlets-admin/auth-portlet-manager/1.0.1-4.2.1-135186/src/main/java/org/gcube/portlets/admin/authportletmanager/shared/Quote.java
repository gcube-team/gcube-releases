package org.gcube.portlets.admin.authportletmanager.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.List;



public class Quote implements Serializable  {

	private static final long serialVersionUID = 5453609273197782507L;

	private Long idQuote;


	private List<Caller> caller;
	
	
	
	private String target;

	//private String manager;

	//private String timeInterval;


	public enum ManagerType {
		STORAGE(0),SERVICE(1),PORTLET(2);     
		@SuppressWarnings("unused")
		private int value;
		private ManagerType(int value){
			this.value=value;
		}
	}



	protected ManagerType managerType;

	public enum TimeInterval {
		DAILY,WEEKLY,MONTHLY,YEARLY
	}
	protected TimeInterval timeInterval;

	private Double quota;

	private Date dataInsert;

	private Date dataUpdate;


	public Quote() {
		super();
	}
	public Quote(List<Caller> caller, String target,ManagerType managerType,TimeInterval timeInterval,Double quota) {
		super();
		this.caller=caller;
		this.target=target;
		this.managerType=managerType;
		this.timeInterval=timeInterval;
		this.quota=quota;

	}
	public Quote(Long idquote,List<Caller> caller, String target,ManagerType managerType,TimeInterval timeInterval,Double quota) {
		super();
		this.idQuote=idquote;
		this.caller=caller;
		this.target=target;
		this.managerType=managerType;
		this.timeInterval=timeInterval;
		this.quota=quota;

	}



	public Long getIdQuote() {
		return idQuote;
	}
	public void setIdQuote(Long idQuote) {
		this.idQuote = idQuote;
	}
	public List<Caller> getCaller() {
		return caller;
	}
	
	/*
	 * Get full name caller
	 */
	public String getCallerAsString(){
		String callerNameString = new String();
		for (Caller caller:this.caller){
			callerNameString=caller.getCallerName()+" "+ callerNameString;
		}
		return callerNameString;
	}
	/*
	 * Get full type caller
	 */
	public String getCallerTypeAsString(){
		String callerTypeString = "";
		for (Caller caller:this.caller){
			callerTypeString=callerTypeString+" "+caller.getTypecaller();
		}
		return callerTypeString;
	}

	
	
	
	public void setCaller(List<Caller> caller) {
		this.caller = caller;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public ManagerType getManager() {
		return managerType;
	}
	public void setManager(ManagerType manager) {
		this.managerType = manager;
	}
	public TimeInterval getTimeInterval() {
		return timeInterval;
	}
	public void setTimeInterval(TimeInterval timeInterval) {
		this.timeInterval = timeInterval;
	}
	public Double getQuota() {
		return quota;
	}
	public void setQuota(Double quota) {
		this.quota = quota;
	}
	public Date getDataInsert() {
		return dataInsert;
	}
	public void setDataInsert(Date dataInsert) {
		this.dataInsert = dataInsert;
	}
	public Date getDataUpdate() {
		return dataUpdate;
	}
	public void setDataUpdate(Date dataUpdate) {
		this.dataUpdate = dataUpdate;
	}

	@Override
	public String toString() {
		return "Quote [idQuote=" + idQuote + ", caller=" + caller + ", target="
				+ target + ", manager=" + managerType + ", timeInterval="
				+ timeInterval + ", quota=" + quota + ", dataInsert="
				+ dataInsert + ", dataUpdate=" + dataUpdate + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((caller == null) ? 0 : caller.hashCode());
		result = prime * result
				+ ((dataInsert == null) ? 0 : dataInsert.hashCode());
		result = prime * result
				+ ((dataUpdate == null) ? 0 : dataUpdate.hashCode());
		result = prime * result + ((idQuote == null) ? 0 : idQuote.hashCode());
		result = prime * result + ((managerType == null) ? 0 : managerType.hashCode());
		result = prime * result + ((quota == null) ? 0 : quota.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result
				+ ((timeInterval == null) ? 0 : timeInterval.hashCode());
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
		Quote other = (Quote) obj;
		if (caller == null) {
			if (other.caller != null)
				return false;
		} else if (!caller.equals(other.caller))
			return false;
		if (dataInsert == null) {
			if (other.dataInsert != null)
				return false;
		} else if (!dataInsert.equals(other.dataInsert))
			return false;
		if (dataUpdate == null) {
			if (other.dataUpdate != null)
				return false;
		} else if (!dataUpdate.equals(other.dataUpdate))
			return false;
		if (idQuote == null) {
			if (other.idQuote != null)
				return false;
		} else if (!idQuote.equals(other.idQuote))
			return false;
		if (managerType == null) {
			if (other.managerType != null)
				return false;
		} else if (!managerType.equals(other.managerType))
			return false;
		if (quota == null) {
			if (other.quota != null)
				return false;
		} else if (!quota.equals(other.quota))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		if (timeInterval == null) {
			if (other.timeInterval != null)
				return false;
		} else if (!timeInterval.equals(other.timeInterval))
			return false;
		return true;
	}

}
