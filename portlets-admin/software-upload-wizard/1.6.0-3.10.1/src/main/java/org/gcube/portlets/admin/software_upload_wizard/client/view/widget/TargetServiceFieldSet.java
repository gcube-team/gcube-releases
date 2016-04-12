package org.gcube.portlets.admin.software_upload_wizard.client.view.widget;

import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.Version;

public class TargetServiceFieldSet extends DefaultFieldSet {

	private ServiceClassField serviceClassField = new ServiceClassField();
	private ComponentNameField serviceNameField = new ComponentNameField("Service Name*");
	private VersionField versionField = new VersionField("Service Version*",
			new Version(1, 0, 0));
	private ComponentNameField packageNameField = new ComponentNameField("Package Name*");
	private ArtifactVersionTextField packageVersionField = new ArtifactVersionTextField();

	public TargetServiceFieldSet() {
		this.setHeading("Target service information");

		this.add(serviceClassField);
		this.add(serviceNameField);
		this.add(versionField);
		this.add(packageNameField);
		this.add(packageVersionField);
	}
	
	public String getServiceClass(){
		return serviceClassField.getValue();
	}
	
	public void setServiceClass(String value){
		serviceClassField.setRawValue(value);
	}

	public String getServiceName() {
		return serviceNameField.getValue();
	}

	public void setServiceName(String value) {
		serviceNameField.setRawValue(value);
	}

	public Version getServiceVersion() {
		return versionField.getVersion();
	}

	public void setServiceVersion(Version value) {
		versionField.setVersion(value);
	}
	
	public String getPackageName(){
		return packageNameField.getValue();
	}
	
	public void setPackagename(String value){
		packageNameField.setRawValue(value);
	}
	
	public String getPackageVersion(){
		return packageVersionField.getValue();
	}
	
	public void setPackageVersion(String value){
		packageVersionField.setRawValue(value);
	}
	
}
