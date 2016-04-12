package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetThirdParty;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetThirdPartyResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class GetThirdPartyHandler implements ActionHandler<GetThirdParty, GetThirdPartyResult> {

	private static final Logger log = LoggerFactory.getLogger(GetThirdPartyHandler.class);

	private ImportSessionManager importSessionManager;

	@Inject
	public GetThirdPartyHandler(ImportSessionManager importSessionManager) {
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<GetThirdParty> getActionType() {
		return GetThirdParty.class;
	}

	@Override
	public GetThirdPartyResult execute(GetThirdParty action, ExecutionContext context) throws DispatchException {
		try {
			boolean thirdParty = importSessionManager.getImportSession().getServiceProfile().isThirdPartySoftware();
			log.trace("Returning third party value: " + thirdParty);
			return new GetThirdPartyResult(thirdParty);
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}
	}

	@Override
	public void rollback(GetThirdParty action, GetThirdPartyResult result, ExecutionContext context)
			throws DispatchException {
	}

}
