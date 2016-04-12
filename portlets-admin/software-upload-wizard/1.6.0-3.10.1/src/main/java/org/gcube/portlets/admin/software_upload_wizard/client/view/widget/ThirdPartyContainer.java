package org.gcube.portlets.admin.software_upload_wizard.client.view.widget;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

public class ThirdPartyContainer extends LayoutContainer {
	private ThirdPartyCheckbox thirdPartyCheckbox = new ThirdPartyCheckbox();

	public ThirdPartyContainer() {
		FormLayout layout = new FormLayout();
		layout.setLabelWidth(150);
		
//		initToolTip()
		
		this.setLayout(layout);
		this.add(thirdPartyCheckbox);
	}
	
	private void initToolTip(){
		DefaultTooltipConfig toolTipConfig = new DefaultTooltipConfig();
		toolTipConfig.setAnchor("right");
		toolTipConfig.setTitle("Third party software");
		toolTipConfig
				.setText("<p>If the software is from third party please select this checkbox.<br/>Third party software registration is not allowed if you are working on the \"gcube\" scope infrastructure.</p>");
		this.setToolTip(toolTipConfig);
	}
	
	public void setCheckboxValue(boolean value){
		thirdPartyCheckbox.setValue(value);
	}
	
	public boolean getValue(){
		return thirdPartyCheckbox.getValue();
	}
	
	public void setCheckboxEnabled(boolean value){
		thirdPartyCheckbox.setEnabled(value);
	}
	
	public CheckBox getCheckBox(){
		return thirdPartyCheckbox;
	}
	
}
