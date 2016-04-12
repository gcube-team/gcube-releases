package org.gcube.portlets.admin.software_upload_wizard.client.view.widget;

import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.MavenVersionRule;

public class MavenCoordinatesFieldSet extends DefaultFieldSet {
	
	private GroupIdTextField groupIdTextField = new GroupIdTextField();
	private ArtifactIdTextField artifactIdTextField = new ArtifactIdTextField();
	private ArtifactVersionTextField versionField = new ArtifactVersionTextField();

	public MavenCoordinatesFieldSet() {
		this.setHeading("Artifact Coordinates");
		
		this.add(groupIdTextField);
		this.add(artifactIdTextField);
		this.add(versionField);
	}
	
	public String getGroupId(){
		return groupIdTextField.getValue();
	}
	
	public void setGroupId(String value){
		groupIdTextField.setRawValue(value);
	}
	
	
	public String getArtifactId(){
		return artifactIdTextField.getValue();
	}
	
	public void setArtifactId(String value){
		artifactIdTextField.setRawValue(value);
	}
	
	public String getVersion(){
		return versionField.getValue();
	}
	
	public void setVersion(String value){
		versionField.setRawValue(value);
	}
	
	public void setVersionFieldBehavior(MavenVersionRule rule){
		switch (rule){
		case ALLOW_ALL:
			versionField.setRegex("^[0-9]{1,2}\\.[0-9]{1,2}\\.[0-9]{1,2}(-SNAPSHOT)?");
			versionField.getMessages()
			.setRegexText(
					"Must be a valid snapshot or non-snapshot version number");
			return;
		case ONLY_SNAPSHOT:
			versionField.setRegex("^[0-9]{1,2}\\.[0-9]{1,2}\\.[0-9]{1,2}-SNAPSHOT");
			versionField.getMessages()
			.setRegexText(
					"Must be a valid snapshot version number");
			return;
		case NO_SNAPSHOT:
			versionField.setRegex("^[0-9]{1,2}\\.[0-9]{1,2}\\.[0-9]{1,2}$");
			versionField.getMessages()
			.setRegexText(
					"Must be a valid non-snapshot version number");
			return;
		}
	}
	
}
