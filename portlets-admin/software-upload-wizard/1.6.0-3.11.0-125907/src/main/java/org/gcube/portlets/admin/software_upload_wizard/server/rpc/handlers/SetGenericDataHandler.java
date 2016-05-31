package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetGenericData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetGenericDataResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class SetGenericDataHandler implements
		ActionHandler<SetGenericData, SetGenericDataResult> {

	ImportSessionManager importSessionManager;
	
	private static final Logger log = LoggerFactory.getLogger(SetGenericDataHandler.class);

	@Inject
	public SetGenericDataHandler(ImportSessionManager importSessionManager) {
		super();
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<SetGenericData> getActionType() {
		return SetGenericData.class;
	}

	@Override
	public SetGenericDataResult execute(SetGenericData action,
			ExecutionContext context) throws DispatchException {
		try {
			importSessionManager.getImportSession()
					.setGenericData(action.getKey(),action.getValue());
			return new SetGenericDataResult();
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}
	}

	@Override
	public void rollback(SetGenericData action, SetGenericDataResult result,
			ExecutionContext context) throws DispatchException {
		// TODO rollback logic

	}
}
