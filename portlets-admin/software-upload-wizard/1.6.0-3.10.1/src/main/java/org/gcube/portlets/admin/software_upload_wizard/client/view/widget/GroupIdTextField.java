package org.gcube.portlets.admin.software_upload_wizard.client.view.widget;

import com.extjs.gxt.ui.client.widget.form.TextField;

public class GroupIdTextField extends TextField<String> {
	public GroupIdTextField() {
		this.setFieldLabel("Group Id*");
		this.setAllowBlank(false);
		this.setRegex("[0-9a-zA-Z-_.]+");
		this.getMessages()
				.setRegexText(
						"Only alphanumerical chars and '-','_','.' symbols are allowed");
	}
}
