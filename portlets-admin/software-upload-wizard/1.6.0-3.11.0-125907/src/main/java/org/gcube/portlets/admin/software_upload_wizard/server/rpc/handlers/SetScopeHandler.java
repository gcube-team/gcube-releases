package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import javax.servlet.http.HttpSession;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetScope;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetScopeResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class SetScopeHandler implements ActionHandler<SetScope, SetScopeResult> {

	Provider<HttpSession> sessionProvider;

	private static final Logger log = LoggerFactory.getLogger(SetScopeHandler.class);

	@Inject
	public SetScopeHandler(Provider<HttpSession> sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	@Override
	public SetScopeResult execute(SetScope scopeAction, ExecutionContext context) throws DispatchException {
		try {
			log.debug("Setting scope...");
			HttpSession session = sessionProvider.get();
			session.setAttribute(ScopeProvider.instance.get(), scopeAction.getScope());
			log.debug("Scope set to: " + scopeAction.getScope());
			return new SetScopeResult();
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}
	}

	@Override
	public Class<SetScope> getActionType() {
		return SetScope.class;
	}

	@Override
	public void rollback(SetScope arg0, SetScopeResult arg1, ExecutionContext arg2) throws DispatchException {
	}

}
