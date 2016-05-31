package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers;

import java.util.ArrayList;

import org.gcube.portlets.admin.software_upload_wizard.server.data.ImportSession;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.registrationmanagers.ISoftwareSubmissionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.scope.ScopeAvailable;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.Package;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.ServiceProfile;
import org.gcube.portlets.admin.software_upload_wizard.shared.Deliverable;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.MavenCoordinates;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwaretypes.ISoftwareTypeInfo;

public interface ISoftwareTypeManager extends ISoftwareSubmissionManager, ScopeAvailable {
	
	public void setImportSession(ImportSession importSession);
	
	public ServiceProfile generateInitialSoftwareProfile();
	
	public ISoftwareTypeInfo getSoftwareTypeInfo();

	public ArrayList<Deliverable> getMiscFiles() throws Exception;

	public String getServiceProfile(boolean b) throws Exception;
	
	public String getPOM(Package softwarePackage) throws Exception;
	
	public String getPOM(ServiceProfile serviceProfile) throws Exception;
	
	public abstract MavenCoordinates getMavenCoordinates(Package softwarePackage) throws Exception;
		
}
