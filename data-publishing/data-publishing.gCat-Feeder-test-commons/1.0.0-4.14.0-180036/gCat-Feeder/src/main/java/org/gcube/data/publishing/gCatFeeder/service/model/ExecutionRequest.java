package org.gcube.data.publishing.gCatFeeder.service.model;

import java.util.HashSet;
import java.util.Set;

public class ExecutionRequest {

	private Set<String> toInvokeCollectors=new HashSet<>();
	private Set<String> toInvokeControllers=new HashSet<>();
	
	private String callerID;
	private String context;
	private String encryptedToken;
	
	
	public ExecutionRequest addCollectorId(String id) {
		this.toInvokeCollectors.add(id);
		return this;
	}
	
	
	public ExecutionRequest addControllerId(String id) {
		this.toInvokeControllers.add(id);
		return this;
	}
	
	public Set<String> getToInvokeCollectors() {
		return toInvokeCollectors;
	}
	
	public Set<String> getToInvokeControllers() {
		return toInvokeControllers;
	}
	
	
	public void setToInvokeCollectors(Set<String> toInvokeCollectors) {
		this.toInvokeCollectors = toInvokeCollectors;
	}
	
	public void setToInvokeControllers(Set<String> toInvokeControllers) {
		this.toInvokeControllers = toInvokeControllers;
	}


	public String getCallerID() {
		return callerID;
	}


	public void setCallerID(String callerID) {
		this.callerID = callerID;
	}


	public String getContext() {
		return context;
	}


	public void setContext(String context) {
		this.context = context;
	}


	public String getEncryptedToken() {
		return encryptedToken;
	}


	public void setEncryptedToken(String encryptedToken) {
		this.encryptedToken = encryptedToken;
	}
	
	
}
