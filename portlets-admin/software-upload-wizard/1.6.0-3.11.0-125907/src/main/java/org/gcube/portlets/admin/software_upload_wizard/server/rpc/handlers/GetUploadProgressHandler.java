package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.shared.IOperationProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetUploadProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetUploadProgressResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class GetUploadProgressHandler implements
		ActionHandler<GetUploadProgress, GetUploadProgressResult> {

	private static final Logger log = LoggerFactory.getLogger(GetUploadProgressHandler.class);

	ImportSessionManager importSessionManager;

	@Inject
	public GetUploadProgressHandler(ImportSessionManager manager) {
		this.importSessionManager = manager;
	}

	@Override
	public Class<GetUploadProgress> getActionType() {
		return GetUploadProgress.class;
	}

	@Override
	public GetUploadProgressResult execute(GetUploadProgress action,
			ExecutionContext context) throws DispatchException {
		try {
			IOperationProgress operationProgress = importSessionManager
					.getImportSession().getUploadProgress();
			log.trace("Returning upload progress: "+ operationProgress.getElaboratedLenght() + "/" + operationProgress.getTotalLenght() + "\t" + operationProgress.getState() );
			return new GetUploadProgressResult(operationProgress);
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}
	}

	@Override
	public void rollback(GetUploadProgress action,
			GetUploadProgressResult result, ExecutionContext context)
			throws DispatchException {
	}

}
