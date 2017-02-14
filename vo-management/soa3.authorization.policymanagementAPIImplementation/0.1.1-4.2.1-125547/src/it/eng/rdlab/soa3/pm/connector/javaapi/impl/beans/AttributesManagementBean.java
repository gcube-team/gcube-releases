package it.eng.rdlab.soa3.pm.connector.javaapi.impl.beans;

import it.eng.rdlab.soa3.pm.connector.beans.AttributeBean;

import java.util.List;

public class AttributesManagementBean 
{
	
	private List<AttributeBean> subjectAttributes;
	private String resourceId;
	private String actionId;
	private boolean moveAfter;
	
	public List<AttributeBean> getSubjectAttributes() {
		return subjectAttributes;
	}
	public void setSubjectAttributes(List<AttributeBean> subjectAttributes) {
		this.subjectAttributes = subjectAttributes;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public String getActionId() {
		return actionId;
	}
	public void setActionId(String actionId) {
		this.actionId = actionId;
	}
	public boolean isMoveAfter() {
		return moveAfter;
	}
	public void setMoveAfter(boolean moveAfter) {
		this.moveAfter = moveAfter;
	}
	
	

}
