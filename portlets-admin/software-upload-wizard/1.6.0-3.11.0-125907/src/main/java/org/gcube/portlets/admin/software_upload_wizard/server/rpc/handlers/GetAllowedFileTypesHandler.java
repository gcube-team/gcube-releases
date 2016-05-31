package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import java.util.ArrayList;
import java.util.UUID;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.FileType;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetAllowedFileTypes;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetAllowedFileTypesResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class GetAllowedFileTypesHandler implements
		ActionHandler<GetAllowedFileTypes, GetAllowedFileTypesResult> {

	private static final Logger log = LoggerFactory.getLogger(GetAllowedFileTypesHandler.class);

	ImportSessionManager importSessionManager;

	@Inject
	public GetAllowedFileTypesHandler(ImportSessionManager importSessionManager) {
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<GetAllowedFileTypes> getActionType() {
		return GetAllowedFileTypes.class;
	}

	@Override
	public GetAllowedFileTypesResult execute(GetAllowedFileTypes action,
			ExecutionContext context) throws DispatchException {
		try {
			ArrayList<FileType> allowedFileTypes = importSessionManager
					.getImportSession().getServiceProfile().getService()
					.getPackage(UUID.fromString(action.getPackageId()))
					.getAllowedFileTypes();
			log.debug("Returning allowed file types for package with id: "
					+ action.getPackageId());
			return new GetAllowedFileTypesResult(allowedFileTypes);
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}
	}

	@Override
	public void rollback(GetAllowedFileTypes action,
			GetAllowedFileTypesResult result, ExecutionContext context)
			throws DispatchException {
		// TODO Auto-generated method stub

	}

}
