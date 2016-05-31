package org.gcube.portlets.admin.wftemplates.client.view;

import com.google.gwt.user.client.ui.HTML;
/**
 * <code> WfStep </code> class is the view component of a Workflow Step
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version May 2011 (0.1) 
 */
public class WfStep extends HTML {
	String description;
	public WfStep(String toDiplay, String desc) {
		setStyleName("wf-step");		
		setSize("78px", "50px");
		setHTML("<table width=\"100%\"><tr height=\"45px\"><td valign=\"middle\" align=\"center\">"+toDiplay+"</td></tr></table>");
		this.description = desc;
		setTitle(description);
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}	
}
