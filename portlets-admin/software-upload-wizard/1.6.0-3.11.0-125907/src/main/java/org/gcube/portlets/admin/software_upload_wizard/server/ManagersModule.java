package org.gcube.portlets.admin.software_upload_wizard.server;

import org.gcube.portlets.admin.software_upload_wizard.server.aslmanagers.ASLSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.aslmanagers.DefaultASLSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ASLImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.filesmanager.DefaultFileManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.filesmanager.FileManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.filesmanager.TarGzManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.maven.deploy.ConsoleMavenDeployer;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.maven.deploy.IMavenDeployer;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.maven.is.IMavenRepositoryIS;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.maven.is.MavenRepositoryISClient;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.softwaregateway.DefaultSG;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.softwaregateway.DefaultSoftwareGatewayRegistrationManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.softwaregateway.ISoftwareGatewayRegistrationManager;

import com.google.inject.AbstractModule;

public class ManagersModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ASLSessionManager.class).to(DefaultASLSessionManager.class);
		bind(ImportSessionManager.class).to(ASLImportSessionManager.class);
//		bind(IMavenRepositoryIS.class).to(MavenRepositoryInlineIS.class);
		bind(IMavenRepositoryIS.class).to(MavenRepositoryISClient.class);
		bind(IMavenDeployer.class).to(ConsoleMavenDeployer.class);
		bind(FileManager.class).to(DefaultFileManager.class);
		bind(TarGzManager.class);
//		bind(ISoftwareGatewayRegistrationManager.class).to(VOSoftwareGatewayRegistrationManager.class);
		bind(ISoftwareGatewayRegistrationManager.class).to(DefaultSoftwareGatewayRegistrationManager.class);
		bind(ISoftwareGatewayRegistrationManager.class).annotatedWith(DefaultSG.class).to(DefaultSoftwareGatewayRegistrationManager.class);
	} 

}
