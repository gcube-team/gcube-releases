package org.gcube.portlets.admin.wfdocviewer.shared;

import java.io.Serializable;

import org.gcube.portlets.admin.wfdocslibrary.shared.Step;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfRole;

@SuppressWarnings("serial")
public class RoleStep implements Serializable{
	private WfRole role;
	private Step step;
	
	public RoleStep() {	}
	
	public RoleStep(WfRole role, Step step) {
		this.role = role;
		this.step = step;
	}
	public String getRolename() {
		return role.getRolename();
	}
	
	public WfRole getRole() {
		return role;
	}
	public void setRole(WfRole role) {
		this.role = role;
	}
	public Step getStep() {
		return step;
	}
	public void setStep(Step step) {
		this.step = step;
	}
}
