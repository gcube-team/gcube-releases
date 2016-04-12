package org.gcube.portlets.admin.software_upload_wizard.client.view.widget;

import com.extjs.gxt.ui.client.widget.form.TextField;

public class ServiceClassField extends TextField<String> {
	public ServiceClassField() {
		this.setFieldLabel("Service Class*");
		this.setAllowBlank(false);
		this.setRegex("[a-zA-Z-]+");
		this.getMessages()
				.setRegexText(
						"Only alphabetical characters and \"-\" symbol are allowed");
		this.setSelectOnFocus(true);
	}
}
