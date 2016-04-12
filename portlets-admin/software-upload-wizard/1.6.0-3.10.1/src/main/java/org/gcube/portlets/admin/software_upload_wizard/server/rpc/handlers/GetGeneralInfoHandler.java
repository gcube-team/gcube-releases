package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.shared.GeneralInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetGeneralInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetGeneralInfoResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class GetGeneralInfoHandler implements ActionHandler<GetGeneralInfo, GetGeneralInfoResult> {

	private static final Logger log = LoggerFactory.getLogger(GetGeneralInfoHandler.class);
	
	@Inject
	private ImportSessionManager importSessionManager;
	
	@Inject
	public GetGeneralInfoHandler(ImportSessionManager importSessionManager) {
		super();
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<GetGeneralInfo> getActionType() {
		return GetGeneralInfo.class;
	}

	@Override
	public GetGeneralInfoResult execute(GetGeneralInfo action,
			ExecutionContext context) throws DispatchException {
		try {
			GeneralInfo generalInfo = importSessionManager.getImportSession()
					.getGeneralInfo();
			log.trace("Returning General Software Data");
			return new GetGeneralInfoResult(generalInfo);
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}
	}

	@Override
	public void rollback(GetGeneralInfo action, GetGeneralInfoResult result,
			ExecutionContext context) throws DispatchException {
	}

}
