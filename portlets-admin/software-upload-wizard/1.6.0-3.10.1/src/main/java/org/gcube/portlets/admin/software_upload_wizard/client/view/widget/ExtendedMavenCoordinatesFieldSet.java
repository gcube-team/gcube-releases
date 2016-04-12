package org.gcube.portlets.admin.software_upload_wizard.client.view.widget;

import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.Version;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

public class ExtendedMavenCoordinatesFieldSet extends FieldSet {

	private ArtifactIdTextField artifactIdTextField = new ArtifactIdTextField();
	private GroupIdTextField groupIdTextField = new GroupIdTextField();
	private VersionField versionField = new VersionField("Version*");
	private SnapshotCheckbox snapshotCheckbox = new SnapshotCheckbox();
	private FilenameTextBox filenameTextBox = new FilenameTextBox();

	public ExtendedMavenCoordinatesFieldSet() {
		this.setHeading("Maven artifact coordinates");

		FormLayout layout = new FormLayout();
		layout.setLabelWidth(150);
		this.setLayout(layout);
		
		this.add(groupIdTextField);
		this.add(artifactIdTextField);
		this.add(versionField);
		this.add(snapshotCheckbox);
		this.add(filenameTextBox);

		bind();
	}

	private void bind() {

		snapshotCheckbox.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				Log.trace("Snapshot checkbox has changed. New value: "
						+ be.getValue());
				if ((Boolean) be.getValue()) {
					Log.trace("Setting Filename textbox to editable");
					ExtendedMavenCoordinatesFieldSet.this.filenameTextBox.setVisible(true);
					ExtendedMavenCoordinatesFieldSet.this.filenameTextBox.setAllowBlank(false);
				} else {
					Log.trace("Setting Filename textbox to non-editable");
					ExtendedMavenCoordinatesFieldSet.this.filenameTextBox
							.setVisible(false);
					ExtendedMavenCoordinatesFieldSet.this.filenameTextBox.setAllowBlank(true);
				}
				ExtendedMavenCoordinatesFieldSet.this.filenameTextBox.setRawValue("");
			}
		});
	}

	public String getArtifactId() {
		return artifactIdTextField.getValue();
	}

	public void setArtifactId(String value) {
		artifactIdTextField.setRawValue(value);
	}

	public String getGroupId() {
		return groupIdTextField.getValue();
	}

	public void setGroupId(String value) {
		groupIdTextField.setRawValue(value);
	}

	public Version getVersion() {
		return versionField.getVersion();
	}

	public void setVersion(Version version) {
		versionField.setVersion(version);
	}

	public boolean getSnapshot() {
		return snapshotCheckbox.getValue();
	}

	public void setSnapshot(boolean value) {
		snapshotCheckbox.setValue(value);
	}
	
	public String getFilename(){
		return filenameTextBox.getValue();
	}
	
	public void setFilename(String value){
		filenameTextBox.setRawValue(value);
	}

	private class SnapshotCheckbox extends CheckBox {
		public SnapshotCheckbox() {
			setBoxLabel("");
			setFieldLabel("Snapshot");
			setValue(false);
		}
	}

	private class FilenameTextBox extends TextField<String> {

		public FilenameTextBox() {
			setRawValue("");
			setFieldLabel("Jar archive filename*");
			setVisible(false);
			setRegex(".*\\.jar$");
			getMessages().setRegexText("Filename must end with '.jar'");
			setAllowBlank(true);
		}

	}

}
