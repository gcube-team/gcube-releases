package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.util.ScopeUtil;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetThirdPartyCapability;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetThirdPartyCapabilityResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class GetThirdPartyCapabilityHandler implements
		ActionHandler<GetThirdPartyCapability, GetThirdPartyCapabilityResult> {
	
	private static final Logger log = LoggerFactory.getLogger(GetThirdPartyCapabilityHandler.class);
	
	private ImportSessionManager importSessionManager;
	
	@Inject
	public GetThirdPartyCapabilityHandler(ImportSessionManager importSessionManager) {
		this.importSessionManager=importSessionManager;
	}

	@Override
	public Class<GetThirdPartyCapability> getActionType() {
		return GetThirdPartyCapability.class;
	}

	@Override
	public GetThirdPartyCapabilityResult execute(GetThirdPartyCapability action,
			ExecutionContext context) throws DispatchException {
		try {
			boolean allowsThirdParty;
			if (ScopeUtil.getInfrastructure(importSessionManager.getImportSession().getScope()).equals("gcube"))
				allowsThirdParty = false;
			else
				allowsThirdParty = true;
			log.trace("Returning third party software upload capability: "
					+ allowsThirdParty);
			return new GetThirdPartyCapabilityResult(allowsThirdParty);
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}
	}

	@Override
	public void rollback(GetThirdPartyCapability action,
			GetThirdPartyCapabilityResult result, ExecutionContext context)
			throws DispatchException {
	}

}
