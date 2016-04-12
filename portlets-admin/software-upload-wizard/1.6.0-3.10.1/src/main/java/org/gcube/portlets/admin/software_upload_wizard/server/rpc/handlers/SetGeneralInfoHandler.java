package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.shared.GeneralInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetGeneralInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetGeneralInfoResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class SetGeneralInfoHandler implements
		ActionHandler<SetGeneralInfo, SetGeneralInfoResult> {

	private static final Logger log = LoggerFactory.getLogger(SetGeneralInfoHandler.class);

	ImportSessionManager importSessionManager;

	@Inject
	public SetGeneralInfoHandler(ImportSessionManager importSessionManager) {
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<SetGeneralInfo> getActionType() {
		return SetGeneralInfo.class;
	}

	@Override
	public SetGeneralInfoResult execute(SetGeneralInfo action,
			ExecutionContext context) throws DispatchException {
		try {
			GeneralInfo generalInfo = action.getGeneralInfo();
			log.trace("Setting GeneralInfo: " + generalInfo);
			importSessionManager.getImportSession().setGeneralInfo(generalInfo);
			return new SetGeneralInfoResult();
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}
	}

	@Override
	public void rollback(SetGeneralInfo action, SetGeneralInfoResult result,
			ExecutionContext context) throws DispatchException {
		//TODO Implement rollback logic
	}

}
