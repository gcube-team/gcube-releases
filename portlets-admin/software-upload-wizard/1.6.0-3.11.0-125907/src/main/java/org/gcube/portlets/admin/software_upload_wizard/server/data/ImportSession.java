package org.gcube.portlets.admin.software_upload_wizard.server.data;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.ISoftwareTypeManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.SoftwareTypeFactory;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.ServiceProfile;
import org.gcube.portlets.admin.software_upload_wizard.shared.GeneralInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.IOperationProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.ImportSessionId;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwaretypes.ISoftwareTypeInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwaretypes.SoftwareTypeCode;

public class ImportSession {

	private ImportSessionId sessionId;

	private ISoftwareTypeManager softwareType = null;
	private ServiceProfile serviceProfile = new ServiceProfile();
	private GeneralInfo generalInfo = new GeneralInfo();
	private ConcurrentHashMap<String,Object> genericDataMap = new ConcurrentHashMap<String, Object>();


	private IOperationProgress uploadProgress = null;
	private IOperationProgress submitProgress = null;

	private String scope = null;

	public ImportSession() {
		this.sessionId = new ImportSessionId(UUID.randomUUID().toString());
	}

	public ImportSession(ServiceProfile profile) {
		this.serviceProfile = profile;
	}

	public ImportSessionId getSessionId() {
		return sessionId;
	}

	public void setSessionId(ImportSessionId sessionId) {
		this.sessionId = sessionId;
	}

	public ISoftwareTypeManager getSoftwareManager() {
		return softwareType;
	}

	public ISoftwareTypeInfo getSoftwareType() {
		return softwareType.getSoftwareTypeInfo();
	}

	public void setSoftwareType(SoftwareTypeCode softwareTypeCode)
			throws Exception {
		this.softwareType = SoftwareTypeFactory
				.getSoftwareManager(softwareTypeCode);
		softwareType.setImportSession(this);
		setServiceProfile(softwareType.generateInitialSoftwareProfile());
	}

	public ServiceProfile getServiceProfile() {
		return serviceProfile;
	}

	public void setServiceProfile(ServiceProfile softwareProfile) {
		this.serviceProfile = softwareProfile;
	}

	public GeneralInfo getGeneralInfo() {
		return generalInfo;
	}

	public void setGeneralInfo(GeneralInfo generalInfo) {
		this.generalInfo = generalInfo;
	}

	public Object getGenericData(String key){
		return genericDataMap.get(key);
	}

	public void setGenericData(String key, Object value) {
		genericDataMap.put(key, value);
	}

	public String getStringData(String key){
		return (String) genericDataMap.get(key);
	}

	public void setStringData(String key, String value){
		genericDataMap.put(key, value);
	}

	public IOperationProgress getSubmitProgress() {
		return submitProgress;
	}

	public void setSubmitProgress(IOperationProgress submitProgress) {
		this.submitProgress = submitProgress;
	}

	public IOperationProgress getUploadProgress() {
		return uploadProgress;
	}

	public void setUploadProgress(IOperationProgress uploadProgress) {
		this.uploadProgress = uploadProgress;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

}
