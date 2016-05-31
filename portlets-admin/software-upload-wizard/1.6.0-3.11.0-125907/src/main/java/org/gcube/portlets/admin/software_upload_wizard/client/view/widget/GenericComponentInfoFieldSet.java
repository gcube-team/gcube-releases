package org.gcube.portlets.admin.software_upload_wizard.client.view.widget;

import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.Version;

import com.extjs.gxt.ui.client.widget.layout.FormData;

public class GenericComponentInfoFieldSet extends DefaultFieldSet {
	
	protected ComponentNameField nameField = new ComponentNameField();
	protected ComponentDescriptionField descriptionField = new ComponentDescriptionField();
	protected VersionField versionField = new VersionField("Version*",
			new Version(1, 0, 0));

	public GenericComponentInfoFieldSet(String componentName) {
		this.setHeading(componentName + " data");

		this.add(nameField);
		this.add(descriptionField, new FormData("-20"));
		this.add(versionField);
	}

	public String getName() {
		return nameField.getValue();
	}

	public void setName(String value) {
		nameField.setRawValue(value);
	}

	public String getDescription() {
		return descriptionField.getValue();
	}

	public void setDescription(String value) {
		descriptionField.setRawValue(value);
	}

	public Version getVersion() {
		return versionField.getVersion();
	}

	public void setVersion(Version value) {
		versionField.setVersion(value);
	}

}
