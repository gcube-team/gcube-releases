package org.gcube.portlets.admin.wfdocviewer.client.view;

import java.util.ArrayList;

import org.gcube.portlets.admin.wfdocslibrary.shared.Step;
import org.gcube.portlets.admin.wfdocviewer.shared.RoleStep;

import com.google.gwt.user.client.ui.VerticalPanel;

public class InnerTabPanel extends VerticalPanel {
	private  ArrayList<RoleStep> roles;
	private  Step myStep;

	public InnerTabPanel() {}
	
	public InnerTabPanel( Step myStep, ArrayList<RoleStep> roles) {
		super();
		this.myStep = myStep;
		this.roles = roles;
	}
	

	public ArrayList<RoleStep> getRoles() {
		return roles;
	}

	public void setRoles(ArrayList<RoleStep> roles) {
		this.roles = roles;
	}

	public Step getMyStep() {
		return myStep;
	}

	public void setMyStep(Step myStep) {
		this.myStep = myStep;
	}
	
}
