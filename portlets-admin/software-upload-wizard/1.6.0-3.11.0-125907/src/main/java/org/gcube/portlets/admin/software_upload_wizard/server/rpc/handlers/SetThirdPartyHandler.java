package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetThirdParty;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetThirdPartyResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class SetThirdPartyHandler implements ActionHandler<SetThirdParty, SetThirdPartyResult> {

	private static final Logger log = LoggerFactory.getLogger(SetThirdPartyHandler.class);
	
	ImportSessionManager importSessionManager;
	
	@Inject
	public SetThirdPartyHandler(ImportSessionManager importSessionManager) {
		super();
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<SetThirdParty> getActionType() {
		return SetThirdParty.class;
	}

	@Override
	public SetThirdPartyResult execute(SetThirdParty action,
			ExecutionContext context) throws DispatchException {
		try {
			boolean thirdParty = action.isThirdParty();
			log.trace("Setting third party value: " + thirdParty);
			importSessionManager.getImportSession().getServiceProfile()
					.setThirdPartySoftware(thirdParty);
			return new SetThirdPartyResult(thirdParty);
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}
	}

	@Override
	public void rollback(SetThirdParty action, SetThirdPartyResult result,
			ExecutionContext context) throws DispatchException {
		//TODO Implement rollback operations 
	}

}
