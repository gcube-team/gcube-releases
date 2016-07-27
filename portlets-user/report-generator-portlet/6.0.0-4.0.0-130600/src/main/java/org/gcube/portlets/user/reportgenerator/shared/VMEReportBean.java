package org.gcube.portlets.user.reportgenerator.shared;

import com.extjs.gxt.ui.client.data.BaseModel;

@SuppressWarnings("serial")
public class VMEReportBean  extends BaseModel {
	
	public VMEReportBean() {}

	public VMEReportBean(String id, String rfmo, String name) {
		super();
		set("id", id);
		set("rfmo", rfmo);
		set("name", name);		
	}

	public String getId() {
		return (String) get("id");
	}
	
	public String getRfmo() {
		return (String) get("rfmo");
	}

	public String getName() {
		return (String) get("name");
	}
}
