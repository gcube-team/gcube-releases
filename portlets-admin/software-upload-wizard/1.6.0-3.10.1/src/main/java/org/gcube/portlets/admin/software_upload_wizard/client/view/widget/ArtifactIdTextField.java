package org.gcube.portlets.admin.software_upload_wizard.client.view.widget;

import com.extjs.gxt.ui.client.widget.form.TextField;

public class ArtifactIdTextField extends TextField<String> {
	public ArtifactIdTextField() {
		this.setFieldLabel("Artifact Id*");
		this.setAllowBlank(false);
		this.setRegex("[0-9a-zA-Z-]+");
		this.getMessages().setRegexText(
				"Only alphanumerical chars and '-' symbol are allowed");
	}
}
