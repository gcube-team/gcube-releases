package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetStringData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetStringDataResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class SetStringDataHandler implements
		ActionHandler<SetStringData, SetStringDataResult> {

	private static final Logger log = LoggerFactory.getLogger(SetStringDataHandler.class);

	ImportSessionManager importSessionManager;

	@Inject
	public SetStringDataHandler(ImportSessionManager importSessionManager) {
		super();
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<SetStringData> getActionType() {
		return SetStringData.class;
	}

	@Override
	public SetStringDataResult execute(SetStringData action,
			ExecutionContext context) throws DispatchException {
		try {
			log.trace("Setting value for key '" + action.getKey() + "': "
					+ action.getValue());
			importSessionManager.getImportSession().setGenericData(
					action.getKey(), action.getValue());
			log.trace("Value " + action.getValue() + " set for key "
					+ action.getKey());
			return new SetStringDataResult();
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}

	}

	@Override
	public void rollback(SetStringData action, SetStringDataResult result,
			ExecutionContext context) throws DispatchException {
		// TODO Implement rollback logic
	}

}
