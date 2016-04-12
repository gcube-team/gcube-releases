package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetServiceData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetServiceDataResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.ServiceData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class GetServiceDataHandler implements ActionHandler<GetServiceData, GetServiceDataResult> {

	private static final Logger log = LoggerFactory.getLogger(GetServiceDataHandler.class);

	private ImportSessionManager importSessionManager;

	@Inject
	public GetServiceDataHandler(ImportSessionManager importSessionManager) {
		super();
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<GetServiceData> getActionType() {
		return GetServiceData.class;
	}

	@Override
	public GetServiceDataResult execute(GetServiceData action, ExecutionContext context) throws DispatchException {
		try {
			ServiceData data = importSessionManager.getImportSession().getServiceProfile().getService().getData();
			log.trace("Returning service data");
			return new GetServiceDataResult(data);
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException("An error occurred while retrieving service data.");
		}
	}

	@Override
	public void rollback(GetServiceData action, GetServiceDataResult result, ExecutionContext context)
			throws DispatchException {
	}

}
