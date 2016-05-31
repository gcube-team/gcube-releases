package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import java.util.UUID;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.Package;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.DeletePackageFiles;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.DeletePackageFilesResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class DeletePackageFilesHandler implements ActionHandler<DeletePackageFiles, DeletePackageFilesResult> {

	private static final Logger log = LoggerFactory.getLogger(DeletePackageFilesHandler.class);

	private ImportSessionManager importSessionManager;

	@Inject
	public DeletePackageFilesHandler(ImportSessionManager importSessionManager) {
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<DeletePackageFiles> getActionType() {
		return DeletePackageFiles.class;
	}

	@Override
	public DeletePackageFilesResult execute(DeletePackageFiles action, ExecutionContext context)
			throws DispatchException {
		try {
			Package pack = importSessionManager.getImportSession().getServiceProfile().getService()
					.getPackage(UUID.fromString(action.getPackageId()));
			for (String filename : action.getFilenames()) {
				log.trace("Deleting file: " + filename);
				pack.getFilesContainer().deleteFile(filename);
			}
			return new DeletePackageFilesResult();
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}
	}

	@Override
	public void rollback(DeletePackageFiles action, DeletePackageFilesResult result, ExecutionContext context)
			throws DispatchException {
		// TODO Implement rollback logic
	}

}
