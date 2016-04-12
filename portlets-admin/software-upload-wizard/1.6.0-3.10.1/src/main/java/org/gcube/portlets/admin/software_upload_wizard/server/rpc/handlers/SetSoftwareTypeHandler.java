package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetSoftwareType;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetSoftwareTypeResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwaretypes.SoftwareTypeCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class SetSoftwareTypeHandler implements ActionHandler<SetSoftwareType, SetSoftwareTypeResult> {

	private static final Logger log = LoggerFactory.getLogger(SetSoftwareTypeHandler.class);
	
	private ImportSessionManager sessionManager;

	@Inject
	public SetSoftwareTypeHandler(ImportSessionManager sessionManager) {
		this.sessionManager=sessionManager;
	}
	
	@Override
	public Class<SetSoftwareType> getActionType() {
		return SetSoftwareType.class;
	}

	@Override
	public SetSoftwareTypeResult execute(SetSoftwareType action,
			ExecutionContext context) throws DispatchException {
		try {
			SoftwareTypeCode code = action.getCode();
			log.trace("Setting software type: " + code);
			sessionManager.getImportSession().setSoftwareType(code);
			return new SetSoftwareTypeResult(action.getCode());
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException("Unable to set software type", e);
		}
	}

	@Override
	public void rollback(SetSoftwareType action, SetSoftwareTypeResult result,
			ExecutionContext context) throws DispatchException {
		// TODO Implement rollback operations
	}

}
