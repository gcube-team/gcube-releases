package org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.forms;

import java.util.Map;

import com.google.gwt.user.client.ui.Widget;

public interface StepDefinition {

	public Widget getWidget();
	public boolean isStepValid();
	public Map<String,String> getDefinedFields();
	public String getMessage();
	public void setStatus(Map<String,String> status);
	
	public void onShowStep();
	
	public String getTitle();
}
