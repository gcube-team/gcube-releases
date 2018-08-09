package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping.dataspace;


public class ComputationData {
	
	
	public ComputationData(String name, String operator, String operatorDescription, String infrastructure, String startDate, String endDate, String status, String id, String user, String vre, String operatorId) {
		super();
		this.name = name;
		this.operator = operator;
		this.operatorDescription = operatorDescription;
		this.infrastructure = infrastructure;
		this.startDate = startDate;
		this.endDate = endDate;
		this.status = status;
		this.id = id;
		this.user=user;
		this.vre = vre;
		this.operatorId = operatorId;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getOperatorDescription() {
		return operatorDescription;
	}
	public void setOperatorDescription(String operatorDescription) {
		this.operatorDescription = operatorDescription;
	}
	public String getInfrastructure() {
		return infrastructure;
	}
	public void setInfrastructure(String infrastructure) {
		this.infrastructure = infrastructure;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setVre(String vre) {
		this.vre = vre;
	}
	public String getVre() {
		return vre;
	}
	public String getException() {
		return exception;
	}
	public void setException(String exception) {
		this.exception = exception;
	}
	public String exception;
	public String name;
	public String operator;
	public String operatorDescription;
	public String infrastructure;
	public String startDate;
	public String endDate;
	public String status;
	public String id;
	public String user;
	
	public String vre;
	public String operatorId;
	
}
