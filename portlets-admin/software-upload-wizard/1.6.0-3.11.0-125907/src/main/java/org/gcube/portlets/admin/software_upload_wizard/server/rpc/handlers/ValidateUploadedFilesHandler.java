package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import java.util.UUID;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.Package;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.FileType;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.ValidateUploadedFiles;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.ValidateUploadedFilesResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class ValidateUploadedFilesHandler implements
		ActionHandler<ValidateUploadedFiles, ValidateUploadedFilesResult> {

	private static final Logger log = LoggerFactory.getLogger(ValidateUploadedFilesHandler.class);

	ImportSessionManager importSessionManager;

	@Inject
	public ValidateUploadedFilesHandler(
			ImportSessionManager importSessionManager) {
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<ValidateUploadedFiles> getActionType() {
		return ValidateUploadedFiles.class;
	}

	@Override
	public ValidateUploadedFilesResult execute(ValidateUploadedFiles action,
			ExecutionContext context) throws DispatchException {
		try {
			// Checks if all mandatory files are uploaded
			Package pack = importSessionManager.getImportSession()
					.getServiceProfile().getService()
					.getPackage(UUID.fromString(action.getPackageId()));
			for (FileType fileType : pack.getAllowedFileTypes()) {
				if (fileType.isMandatory()) {
					if (!pack.getFilesContainer().hasFileWithFileType(
							fileType.getName())) {
						log.debug("Returning invalid set of files");
						return new ValidateUploadedFilesResult(false);
					}
				}
			}
			log.debug("Returning valid set of files");
			return new ValidateUploadedFilesResult(true);
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}
	}

	@Override
	public void rollback(ValidateUploadedFiles action,
			ValidateUploadedFilesResult result, ExecutionContext context)
			throws DispatchException {
	}

}
