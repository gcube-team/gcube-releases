package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SubmitSoftwareRegistration;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SubmitSoftwareRegistrationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class SubmitSoftwareRegistrationHandler implements ActionHandler<SubmitSoftwareRegistration, SubmitSoftwareRegistrationResult> {

	private static final Logger log = LoggerFactory.getLogger(SubmitSoftwareRegistrationHandler.class);
	
	private ImportSessionManager importSessionManager;
	
	@Inject
	public SubmitSoftwareRegistrationHandler(
			ImportSessionManager importSessionManager) {
		super();
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<SubmitSoftwareRegistration> getActionType() {
		return SubmitSoftwareRegistration.class;
	}

	@Override
	public SubmitSoftwareRegistrationResult execute(
			SubmitSoftwareRegistration action, ExecutionContext context)
			throws DispatchException {
		try {
			importSessionManager.getImportSession().getSoftwareManager().submitSoftware();
			log.debug("Software submission started succesfully");
			return new SubmitSoftwareRegistrationResult();
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException("Error encountered while submitting software");
		}
	}

	@Override
	public void rollback(SubmitSoftwareRegistration action,
			SubmitSoftwareRegistrationResult result, ExecutionContext context)
			throws DispatchException {
		//TODO Implement rollback logic
	}

}
