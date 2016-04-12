package org.gcube.portlets.admin.wfdocviewer.shared;

import java.io.Serializable;

import org.gcube.portlets.admin.wfdocslibrary.shared.WfGraph;

@SuppressWarnings("serial")
public class WfDocument implements Serializable {

	public String id;
	public String name;
	public String currentStatus;
	public WfGraph workflow;
	
	public WfDocument() {	}
	
	/**
	 * 
	 * @param id the workflow identifier
	 * @param name the workflow name
	 * @param workflow workflow represented as an adjacency matrix
	 */
	public WfDocument(String id, String name, String status, WfGraph workflow) {
		super();
		this.id = id;
		this.name = name;
		this.currentStatus = status;
		this.workflow = workflow;
	}
//Getters and setters
	public String getId() {	return id; }
	public void setId(String id) {	this.id = id;}
	public String getName() {	return name;	}
	public void setName(String name) {	this.name = name;	}
	public String getCurrentStatus() {	return currentStatus;	}
	public void setCurrentStatus(String status) {	this.currentStatus = status; }
	public WfGraph getWorkflow() {	return workflow;	}
	public void setWorkflow(WfGraph workflow) {	this.workflow = workflow; }
}
