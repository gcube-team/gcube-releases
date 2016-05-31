package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.softwaregateway;


public interface ISoftwareGatewayRegistrationManager {

	public void registerProfile(String serviceProfileContent, String scope) throws Exception;
	
}
