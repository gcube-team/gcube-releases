package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.shared.IOperationProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetSubmitProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetSubmitProgressResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class GetSubmitProgressHandler implements
		ActionHandler<GetSubmitProgress, GetSubmitProgressResult> {

	private static final Logger log = LoggerFactory.getLogger(GetSubmitProgressHandler.class);

	private ImportSessionManager importSessionManager;

	@Inject
	public GetSubmitProgressHandler(ImportSessionManager importSessionManager) {
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<GetSubmitProgress> getActionType() {
		return GetSubmitProgress.class;
	}

	@Override
	public GetSubmitProgressResult execute(GetSubmitProgress action,
			ExecutionContext context) throws DispatchException {
		try {
			IOperationProgress operationProgress = importSessionManager
					.getImportSession().getSubmitProgress();
			log.trace("Returning upload progress: "+ operationProgress.getElaboratedLenght() + "/" + operationProgress.getTotalLenght() + "\t" + operationProgress.getState() );
			return new GetSubmitProgressResult(operationProgress);
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}
	}

	@Override
	public void rollback(GetSubmitProgress action,
			GetSubmitProgressResult result, ExecutionContext context)
			throws DispatchException {
	}

}
