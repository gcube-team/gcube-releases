package org.gcube.portlets.admin.software_upload_wizard.client.view.widget;

import com.extjs.gxt.ui.client.widget.form.CheckBox;

public class ThirdPartyCheckbox extends CheckBox {	

	public ThirdPartyCheckbox() {
		this.setFieldLabel("Is third party software?");
		this.setBoxLabel("");
		this.setValue(false);	
	}

}
