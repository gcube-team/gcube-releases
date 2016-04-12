package org.gcube.portlets.admin.software_upload_wizard.server;

import net.customware.gwt.dispatch.server.BatchActionHandler;
import net.customware.gwt.dispatch.server.guice.ActionHandlerModule;
import net.customware.gwt.dispatch.shared.BatchAction;

import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.AddMavenDependenciesHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.AddPackageHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.CreateImportSessionHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.DeletePackageFilesHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.GetAllowedFileTypesHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.GetAvailableSoftwareTypesHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.GetDeliverablesHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.GetGeneralInfoHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.GetGenericDataHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.GetMavenDependenciesHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.GetMavenRepositoriesHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.GetMiscFilesHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.GetPackageArtifactCoordinatesHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.GetPackageDataHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.GetPackageFilesHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.GetPackageIdsHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.GetServiceDataHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.GetStringDataHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.GetSubmitProgressHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.GetThirdPartyCapabilityHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.GetThirdPartyHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.GetUploadProgressHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.GetXmlProfileHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.RemoveMavenDependenciesHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.RemovePackageHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.SetGeneralInfoHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.SetGenericDataHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.SetPackageArtifactCoordinatesHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.SetPackageDataHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.SetScopeHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.SetServiceDataHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.SetSoftwareTypeHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.SetStringDataHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.SetThirdPartyHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.SubmitSoftwareRegistrationHandler;
import org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers.ValidateUploadedFilesHandler;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.AddMavenDependencies;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.AddPackage;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.CreateImportSession;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.DeletePackageFiles;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetAllowedFileTypes;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetAvailableSoftwareTypes;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetDeliverables;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetGeneralInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetGenericData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetMavenDependencies;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetMavenRepositories;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetMiscFiles;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageArtifactCoordinates;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageFiles;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageIds;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetServiceData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetStringData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetSubmitProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetThirdParty;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetThirdPartyCapability;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetUploadProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetXmlProfile;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.RemoveMavenDependencies;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.RemovePackage;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetGeneralInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetGenericData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetPackageArtifactCoordinates;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetPackageData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetScope;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetServiceData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetSoftwareType;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetStringData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetThirdParty;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SubmitSoftwareRegistration;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.ValidateUploadedFiles;

public class ActionsModule extends ActionHandlerModule {

	@Override
	protected void configureHandlers() {
		bindHandler(CreateImportSession.class, CreateImportSessionHandler.class);
		bindHandler(GetAvailableSoftwareTypes.class, GetAvailableSoftwareTypesHandler.class);
		bindHandler(SetSoftwareType.class, SetSoftwareTypeHandler.class);
		
		bindHandler(GetThirdPartyCapability.class, GetThirdPartyCapabilityHandler.class);
		
		bindHandler(GetThirdParty.class, GetThirdPartyHandler.class);
		bindHandler(SetThirdParty.class, SetThirdPartyHandler.class);
		
		bindHandler(GetServiceData.class, GetServiceDataHandler.class);
		bindHandler(SetServiceData.class, SetServiceDataHandler.class);
		
		bindHandler(GetPackageData.class, GetPackageDataHandler.class);
		bindHandler(SetPackageData.class, SetPackageDataHandler.class);
		bindHandler(GetPackageIds.class, GetPackageIdsHandler.class);
		bindHandler(GetPackageFiles.class, GetPackageFilesHandler.class);
		
		bindHandler(DeletePackageFiles.class, DeletePackageFilesHandler.class);
		bindHandler(ValidateUploadedFiles.class, ValidateUploadedFilesHandler.class);
		
		bindHandler(GetGeneralInfo.class, GetGeneralInfoHandler.class);
		bindHandler(SetGeneralInfo.class, SetGeneralInfoHandler.class);
		
		bindHandler(GetMavenDependencies.class, GetMavenDependenciesHandler.class);
		bindHandler(AddMavenDependencies.class, AddMavenDependenciesHandler.class);
		bindHandler(RemoveMavenDependencies.class, RemoveMavenDependenciesHandler.class);
		bindHandler(GetMavenRepositories.class, GetMavenRepositoriesHandler.class);

		bindHandler(GetAllowedFileTypes.class, GetAllowedFileTypesHandler.class);
		
		bindHandler(GetUploadProgress.class, GetUploadProgressHandler.class);
		bindHandler(GetSubmitProgress.class, GetSubmitProgressHandler.class);
		
		bindHandler(GetXmlProfile.class, GetXmlProfileHandler.class);
		bindHandler(GetMiscFiles.class,GetMiscFilesHandler.class);
		bindHandler(GetDeliverables.class, GetDeliverablesHandler.class);
		
		bindHandler(SubmitSoftwareRegistration.class, SubmitSoftwareRegistrationHandler.class);
		
		bindHandler(GetGenericData.class, GetGenericDataHandler.class);
		bindHandler(SetGenericData.class, SetGenericDataHandler.class);
		
		bindHandler(GetStringData.class, GetStringDataHandler.class);
		bindHandler(SetStringData.class, SetStringDataHandler.class);
		
//		ParameterizedType getTypedDataSuperclass = (ParameterizedType) GetTypedData.class
//				.getGenericSuperclass();
//		ParameterizedType getTypedDataHandlerSuperclass = (ParameterizedType) GetTypedDataHandler.class
//				.getGenericSuperclass();
//		ParameterizedType setTypedDataSuperclass = (ParameterizedType) SetTypedData.class
//				.getGenericSuperclass();
//		ParameterizedType setTypedDataHandlerSuperclass = (ParameterizedType) SetTypedDataHandler.class
//				.getGenericSuperclass();
		
//		bindHandler((Class<GetTypedData<T>>) getTypedDataSuperclass.getActualTypeArguments()[0], (Class<GetTypedDataHandler<T>>) getTypedDataHandlerSuperclass.getActualTypeArguments()[0]);
//		bindHandler((Class<SetTypedData<T>>) setTypedDataSuperclass.getActualTypeArguments()[0], (Class<SetTypedDataHandler<T>>) setTypedDataHandlerSuperclass.getActualTypeArguments()[0]);
		
		bindHandler(GetPackageArtifactCoordinates.class, GetPackageArtifactCoordinatesHandler.class);
		bindHandler(SetPackageArtifactCoordinates.class, SetPackageArtifactCoordinatesHandler.class);
		
		bindHandler(AddPackage.class, AddPackageHandler.class);
		bindHandler(RemovePackage.class, RemovePackageHandler.class);
		
		bindHandler(BatchAction.class, BatchActionHandler.class);
		
		bindHandler(SetScope.class, SetScopeHandler.class);
	}

}
