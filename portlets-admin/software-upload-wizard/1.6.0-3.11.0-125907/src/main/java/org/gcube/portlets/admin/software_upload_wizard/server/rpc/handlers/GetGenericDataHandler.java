package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetGenericData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetGenericDataResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class GetGenericDataHandler implements ActionHandler<GetGenericData, GetGenericDataResult> {

	private static final Logger log = LoggerFactory.getLogger(GetGenericDataHandler.class);

	ImportSessionManager importSessionManager;

	@Inject
	public GetGenericDataHandler(ImportSessionManager importSessionManager) {
		super();
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<GetGenericData> getActionType() {
		return GetGenericData.class;
	}

	@Override
	public GetGenericDataResult execute(GetGenericData action, ExecutionContext context) throws DispatchException {
		try {
			Object obj = importSessionManager.getImportSession().getGenericData(action.getKey());
			return new GetGenericDataResult(obj);
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}
	}

	@Override
	public void rollback(GetGenericData action, GetGenericDataResult result, ExecutionContext context)
			throws DispatchException {
	}

}
