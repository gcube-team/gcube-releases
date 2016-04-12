package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import java.lang.reflect.ParameterizedType;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetTypedData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetTypedDataResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class SetTypedDataHandler<T> implements ActionHandler<SetTypedData<T>, SetTypedDataResult<T>> {

	private static final Logger log = LoggerFactory.getLogger(SetTypedDataHandler.class);

	ImportSessionManager importSessionManager;

	@Inject
	public SetTypedDataHandler(ImportSessionManager importSessionManager) {
		super();
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<SetTypedData<T>> getActionType() {
		ParameterizedType superclass = (ParameterizedType) SetTypedData.class.getGenericSuperclass();
		return (Class<SetTypedData<T>>) superclass.getActualTypeArguments()[0];
	}

	@Override
	public SetTypedDataResult<T> execute(SetTypedData<T> action, ExecutionContext context) throws DispatchException {
		try {
			importSessionManager.getImportSession().setGenericData(action.getKey(), action.getValue());
			return new SetTypedDataResult<T>();
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}

	}

	@Override
	public void rollback(SetTypedData<T> action, SetTypedDataResult<T> result, ExecutionContext context)
			throws DispatchException {
		// TODO Auto-generated method stub

	}

}
