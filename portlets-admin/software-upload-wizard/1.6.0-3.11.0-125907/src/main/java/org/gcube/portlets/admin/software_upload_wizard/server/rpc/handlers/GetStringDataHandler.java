package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetStringData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetStringDataResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class GetStringDataHandler implements ActionHandler<GetStringData, GetStringDataResult> {

	private static final Logger log = LoggerFactory.getLogger(GetStringDataHandler.class);

	ImportSessionManager importSessionManager;

	@Inject
	public GetStringDataHandler(ImportSessionManager importSessionManager) {
		super();
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<GetStringData> getActionType() {
		return GetStringData.class;
	}

	@Override
	public GetStringDataResult execute(GetStringData action, ExecutionContext context) throws DispatchException {

		try {
			String value = (String) importSessionManager.getImportSession().getGenericData(action.getKey());
			log.trace("Returning data for key '" + action.getKey() + "': " + value);
			return new GetStringDataResult(value);
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}
	}

	@Override
	public void rollback(GetStringData action, GetStringDataResult result, ExecutionContext context)
			throws DispatchException {
		// TODO Auto-generated method stub

	}

}
