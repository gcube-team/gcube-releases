package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.filesmanager;

import java.io.File;
import java.util.List;

import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.ServiceProfile;
import org.gcube.portlets.admin.software_upload_wizard.shared.Deliverable;

public interface FileManager {

	public File createServiveArchive(String serviceProfile, List<Deliverable> miscFiles, ServiceProfile profile) throws Exception;
	
	public File createPatchArchive(String serviceProfile, List<Deliverable> miscFiles, File patchArchive) throws Exception;
	
	public File createPomFile(String pomContent) throws Exception;
	
}
