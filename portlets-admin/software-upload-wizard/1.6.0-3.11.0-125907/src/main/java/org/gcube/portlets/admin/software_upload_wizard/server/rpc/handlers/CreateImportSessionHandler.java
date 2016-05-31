package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.aslmanagers.ASLSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.data.ImportSession;
import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.CreateImportSession;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.CreateImportSessionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class CreateImportSessionHandler implements ActionHandler<CreateImportSession, CreateImportSessionResult> {

	private static final Logger log = LoggerFactory.getLogger(CreateImportSessionHandler.class);

	private ASLSessionManager aslManager;
	private ImportSessionManager importManager;

	@Inject
	public CreateImportSessionHandler(ASLSessionManager aslManager, ImportSessionManager importManager) {
		this.aslManager = aslManager;
		this.importManager = importManager;
	}

	@Override
	public Class<CreateImportSession> getActionType() {
		return CreateImportSession.class;
	}

	@Override
	public CreateImportSessionResult execute(CreateImportSession action, ExecutionContext context)
			throws DispatchException {
		try {
			ImportSession newSession = new ImportSession();
			newSession.setScope(aslManager.getASLSession().getScope());
			importManager.setImportSession(newSession);
			log.debug("Created new import session with id: " + newSession.getSessionId().getId());
			return new CreateImportSessionResult(newSession.getSessionId());
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}
	}

	@Override
	public void rollback(CreateImportSession action, CreateImportSessionResult result, ExecutionContext context)
			throws DispatchException {
		// TODO Unimplemented
	}

}
