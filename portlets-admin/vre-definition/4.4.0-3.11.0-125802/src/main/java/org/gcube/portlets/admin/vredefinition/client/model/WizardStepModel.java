package org.gcube.portlets.admin.vredefinition.client.model;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class WizardStepModel extends BaseTreeModel {

	private WizardStepType type;
	public WizardStepModel(String name, String icon, WizardStepType type) {

		setName(name);
		setType(type);
		setIcon(icon);
	}

	public String getName() {
		return get("name");
	}

	public void setName(String name) {
		set("name", name);
	}
	
	public WizardStepType getType() {
		return type;
	}
	
	public void setType(WizardStepType type) {
		this.type = type;
	}
	
	public void setIcon(String icon) {
		set("icon",icon);
	}
	
	public String getIcon() {
		return get("icon");
	}

}
