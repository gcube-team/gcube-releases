package org.gcube.portlets.admin.software_upload_wizard.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface Resources extends ClientBundle {

	public static Resources INSTANCE = GWT.create(Resources.class);
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/add.png")
	public ImageResource addIcon();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/delete.png")
	public ImageResource deleteIcon();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/table_refresh.png")
	public ImageResource tableRefreshIcon();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-info_Upload.html")
	public TextResource stepInfo_Upload();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-help_Upload.html")
	public TextResource stepHelp_Upload();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-info_SoftwareTypeSelection.html")
	public TextResource stepInfo_SoftwareTypeSelection();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-help_SoftwareTypeSelection.html")
	public TextResource stepHelp_SoftwareTypeSelection();

	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-info_MaintainersAndChanges.html")
	public TextResource stepInfo_MaintainersAndChanges();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-help_MaintainersAndChanges.html")
	public TextResource stepHelp_MaintainersAndChanges();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-info_ApplicationInfo.html")
	public TextResource stepInfo_ApplicationInfo();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-help_ApplicationInfo.html")
	public TextResource stepHelp_ApplicationInfo();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-info_License.html")
	public TextResource stepInfo_License();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-help_License.html")
	public TextResource stepHelp_License();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-info_WebAppData.html")
	public TextResource stepInfo_WebAppData();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-help_WebAppData.html")
	public TextResource stepHelp_WebAppData();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-info_WebAppService.html")
	public TextResource stepInfo_WebAppService();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-help_WebAppService.html")
	public TextResource stepHelp_WebAppService();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-info_WebAppPackage.html")
	public TextResource stepInfo_WebAppPackage();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-help_WebAppPackage.html")
	public TextResource stepHelp_WebAppPackage();

	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-info_AnySoftwareData.html")
	public TextResource stepInfo_AnySoftwareData();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-help_AnySoftwareData.html")
	public TextResource stepHelp_AnySoftwareData();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-info_SoftwareRegistrationData.html")
	public TextResource stepInfo_SoftwareRegistrationData();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-help_SoftwareRegistrationData.html")
	public TextResource stepHelp_SoftwareRegistrationData();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-info_LibraryData.html")
	public TextResource stepInfo_LibraryData();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-help_LibraryData.html")
	public TextResource stepHelp_LibraryData();

	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-info_GCubePluginData.html")
	public TextResource stepInfo_GCubePluginData();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-help_GCubePluginData.html")
	public TextResource stepHelp_GCubePluginData();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-info_GCubePatchData.html")
	public TextResource stepInfo_GCubePatchData();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-help_GCubePatchData.html")
	public TextResource stepHelp_GCubePatchData();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-info_InstallNotes.html")
	public TextResource stepInfo_InstallNotes();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-help_InstallNotes.html")
	public TextResource stepHelp_InstallNotes();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-info_Submit.html")
	public TextResource stepInfo_Submit();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-help_Submit.html")
	public TextResource stepHelp_Submit();

	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-info_MavenDependencies.html")
	public TextResource stepInfo_MavenDependenciesData();

	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-help_MavenDependencies.html")
	public TextResource stepHelp_MavenDependencies();

	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-info_GCubeWebService_ServiceData.html")
	public TextResource stepInfo_GCubeWebServiceServiceData();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-help_GCubeWebService_ServiceData.html")
	public TextResource stepHelp_GCubeWebServiceServiceData();

	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-info_GCubeWebService_MainPackageData.html")
	public TextResource stepInfo_GCubeWebServiceMainPackageData();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-help_GCubeWebService_MainPackageData.html")
	public TextResource stepHelp_GCubeWebServiceMainPackageData();

	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-info_GenericLibraryPackageData.html")
	public TextResource stepInfo_GenericLibraryPackageData();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-help_GenericLibraryPackageData.html")
	public TextResource stepHelp_GenericLibraryPackageData();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-info_PackageArtifactCoordinates.html")
	public TextResource stepInfo_PackageArtifactCoordinates();
	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-help_PackageArtifactCoordinates.html")
	public TextResource stepHelp_PackageArtifactCoordinates();

	
	@Source("org/gcube/portlets/admin/software_upload_wizard/client/util/resources/step-info_SubmitCompleted.html")
	public TextResource stepInfo_SubmitCompleted();
	
}
