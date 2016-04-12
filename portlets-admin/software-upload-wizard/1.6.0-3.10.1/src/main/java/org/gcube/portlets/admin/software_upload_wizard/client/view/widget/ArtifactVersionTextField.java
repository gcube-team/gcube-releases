package org.gcube.portlets.admin.software_upload_wizard.client.view.widget;

import com.extjs.gxt.ui.client.widget.form.TextField;

public class ArtifactVersionTextField extends TextField<String> {
	public ArtifactVersionTextField() {
		this.setFieldLabel("Artifact version*");
		this.setAllowBlank(false);
		this.setRegex("^[0-9]{1,2}\\.[0-9]{1,2}\\.[0-9]{1,2}(-SNAPSHOT)?$");
		this.getMessages()
				.setRegexText(
						"Must be a valid snapshot or non-snapshot version number");
	}
}
