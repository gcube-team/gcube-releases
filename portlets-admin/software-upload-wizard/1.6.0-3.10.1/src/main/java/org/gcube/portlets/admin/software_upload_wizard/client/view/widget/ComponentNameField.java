package org.gcube.portlets.admin.software_upload_wizard.client.view.widget;

import com.extjs.gxt.ui.client.widget.form.TextField;

public class ComponentNameField extends TextField<String> {

	public ComponentNameField() {
		this("Name*");
	}

	public ComponentNameField(String label) {
		this.setFieldLabel(label);
		this.setRegex("[a-zA-Z0-9-]+");
		this.getMessages()
				.setRegexText(
						"Only alphanumerical charaters and \"-\" symbol are allowed (no space or special chars)");
		this.setAllowBlank(false);
		this.setSelectOnFocus(true);
	}

}
