package org.gcube.portlets.admin.software_upload_wizard.client.view.widget;

import com.extjs.gxt.ui.client.widget.form.TextArea;

public class ComponentDescriptionField extends TextArea {
	public ComponentDescriptionField() {
		this("Description");
		
	}
	
	public ComponentDescriptionField(String label){
		this.setFieldLabel(label);
		this.setHeight(60);
		// descriptionField.setEmptyText("");
		this.setAllowBlank(true);
		this.setSelectOnFocus(true);
	}
}
