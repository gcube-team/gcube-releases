package org.gcube.portlets.admin.authportletmanager.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.gcube.portlets.admin.authportletmanager.shared.Caller.TypeCaller;

/**
 * PolicyAuth usage for project
 * @author "Alessandro Pieve " <a
 *         href="mailto:alessandro.pieve@isti.cnr.it">alessandro.pieve@isti.cnr.it</a>
 * 
 */
public class PolicyAuth implements Serializable  {

	private static final long serialVersionUID = 5453609273197782507L;
	private Long idPolicy;
	private List<Caller> caller;
	private Boolean excludesCaller;

	private Service service;
	//private String access;
	// private Action access;

	private Access access;

	private Date dataInsert;
	private Date dataUpdate;

	public PolicyAuth() {
		super();
	}

	public PolicyAuth(List<Caller> caller, Service service, Access access) {	
		super();
		this.caller=caller;
		this.service = service;
		this.access = access;
	}

	public PolicyAuth(Long idPolicy, List<Caller> caller,Service service,Access access) {
		super();
		this.idPolicy = idPolicy;
		this.caller=caller;
		this.service = service;
		this.access = access;
	}

	public PolicyAuth(Long idPolicy, List<Caller> caller,Service service,Access access, Date dataInsert, Date dataUpdate) {
		super();
		this.idPolicy = idPolicy;
		this.caller=caller;
		this.service = service;
		this.access = access;
		this.dataInsert=dataInsert;
		this.dataUpdate=dataUpdate;
	}

	
	public PolicyAuth(Long idPolicy, List<Caller> caller,Service service,Access access, Date dataInsert) {
		super();
		this.idPolicy = idPolicy;
		this.caller=caller;
		this.service = service;
		this.access = access;
		this.dataInsert=dataInsert;

	}

	public PolicyAuth(Long idPolicy,List<Caller> caller,Boolean excludeCallers,Service service,Access access, Date dataInsert, Date dataUpdate) {
		super();
		this.idPolicy = idPolicy;
		this.caller=caller;
		this.excludesCaller=excludeCallers;
		this.service = service;
		this.access = access;
		this.dataInsert=dataInsert;
		this.dataUpdate=dataUpdate;
	}

	public long getIdpolicy() {
		return idPolicy;
	}

	public void setIdpolicy(Long idPolicy) {
		this.idPolicy = idPolicy;
	}

	public List<Caller> getCaller() {
		return caller;
	}

	public void setCaller(List<Caller> caller) {
		this.caller = caller;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public Access getAccess() {
		return access;
	}
	public String getAccessString() {
		return access.toString();
	}

	
	public void setAccess(Access access) {
		this.access = access;
	}

	public Service getService() {
		return service;
	}
	/*
	 * Get full name 
	 */
	public String getServiceAsString(){
		return service.getServiceClass()+":"+service.getServiceName()+":"+service.getServiceId(); 
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
			if (this.caller.size()==1){
				callerTypeString=caller.getTypecaller().toString();
			}
			else{
				callerTypeString=callerTypeString+" "+caller.getTypecaller().toString();
			}
		}
		return callerTypeString;
	}
	
	/**
	 * 
	 * @return
	 */
	public TypeCaller getCallerType(){
		TypeCaller callerTypeString =null;

		for (Caller caller:this.caller){
				callerTypeString=caller.getTypecaller();
			
		}
		return callerTypeString;
	}
	
	/*
	 * Get full type caller
	 */
	public String getCallerTypeAsDataGrid(){
		String callerTypeString = "";

		for (Caller caller:this.caller){
				callerTypeString=caller.getTypecaller().toString();
			
		}
		return callerTypeString;
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	public String[] getCallerExecptAsString(){
		String[] callerNameString = new String[this.caller.size()-1];
		int count =0;
		for (Caller caller:this.caller){
			if (!caller.getCallerName().equals("ALL")){
				callerNameString[count]=caller.getCallerName();
			
				count++;
			}
		}
		return callerNameString;
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
	public Boolean getExcludesCaller() {
		return excludesCaller;
	}
	public void setExcludesCaller(Boolean excludesCaller) {
		this.excludesCaller = excludesCaller;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((access == null) ? 0 : access.hashCode());
		result = prime * result + ((caller == null) ? 0 : caller.hashCode());
		result = prime * result
				+ ((idPolicy == null) ? 0 : idPolicy.hashCode());
		result = prime * result + ((service == null) ? 0 : service.hashCode());
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
		PolicyAuth other = (PolicyAuth) obj;
		if (access == null) {
			if (other.access != null)
				return false;
		} else if (!access.equals(other.access))
			return false;
		if (caller == null) {
			if (other.caller != null)
				return false;
		} else if (!caller.equals(other.caller))
			return false;
		if (idPolicy == null) {
			if (other.idPolicy != null)
				return false;
		} else if (!idPolicy.equals(other.idPolicy))
			return false;
		if (service == null) {
			if (other.service != null)
				return false;
		} else if (!service.equals(other.service))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "PolicyAuth [idPolicy=" + idPolicy + ", caller=" + caller
				+ ", excludesCaller=" + excludesCaller + ", service=" + service
				+ ", access=" + access + ", dataInsert=" + dataInsert
				+ ", dataUpdate=" + dataUpdate + "]";
	}


}
