package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import java.lang.reflect.ParameterizedType;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetTypedData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetTypedDataResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class GetTypedDataHandler<T> implements ActionHandler<GetTypedData<T>, GetTypedDataResult<T>> {

	private static final Logger log = LoggerFactory.getLogger(GetTypedDataHandler.class);
	
	ImportSessionManager importSessionManager;

	@Inject
	public GetTypedDataHandler(ImportSessionManager importSessionManager) {
		super();
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<GetTypedData<T>> getActionType() {
		ParameterizedType superclass = (ParameterizedType) GetTypedData.class.getGenericSuperclass();
		return (Class<GetTypedData<T>>) superclass.getActualTypeArguments()[0];
	}

	@Override
	public GetTypedDataResult<T> execute(GetTypedData<T> action, ExecutionContext context) throws DispatchException {

		try {
			Object value = importSessionManager.getImportSession().getGenericData(action.getKey());
			return new GetTypedDataResult<T>((T) value);
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}
	}

	@Override
	public void rollback(GetTypedData<T> action, GetTypedDataResult<T> result, ExecutionContext context)
			throws DispatchException {
	}

}
