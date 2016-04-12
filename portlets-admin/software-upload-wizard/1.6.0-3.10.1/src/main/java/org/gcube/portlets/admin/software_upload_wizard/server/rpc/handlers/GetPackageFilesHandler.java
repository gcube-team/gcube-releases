package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import java.util.UUID;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.data.FilesContainer;
import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageFiles;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageFilesResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class GetPackageFilesHandler implements ActionHandler<GetPackageFiles, GetPackageFilesResult> {

	private static final Logger log = LoggerFactory.getLogger(GetPackageFilesHandler.class);

	ImportSessionManager importSessionManager;

	@Inject
	public GetPackageFilesHandler(ImportSessionManager importSessionManager) {
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<GetPackageFiles> getActionType() {
		return GetPackageFiles.class;
	}

	@Override
	public GetPackageFilesResult execute(GetPackageFiles action, ExecutionContext context) throws DispatchException {
		try {
			UUID packageId = UUID.fromString(action.getPackageId());
			FilesContainer files = importSessionManager.getImportSession().getServiceProfile().getService()
					.getPackage(UUID.fromString(packageId.toString())).getFilesContainer();
			log.debug("Returning package files for the package with id: " + packageId.toString());
			return new GetPackageFilesResult(files.getSoftwareFileDetails());
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}
	}

	@Override
	public void rollback(GetPackageFiles action, GetPackageFilesResult result, ExecutionContext context)
			throws DispatchException {
	}

}
