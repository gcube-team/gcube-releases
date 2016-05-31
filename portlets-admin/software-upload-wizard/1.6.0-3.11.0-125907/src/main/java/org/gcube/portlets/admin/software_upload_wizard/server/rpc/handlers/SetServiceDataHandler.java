package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetServiceData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetServiceDataResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.ServiceData;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwaretypes.SoftwareTypeCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class SetServiceDataHandler implements ActionHandler<SetServiceData, SetServiceDataResult> {
	
	private static final Logger log = LoggerFactory.getLogger(SetServiceDataHandler.class);
	
	private ImportSessionManager importSessionManager;

	@Inject
	public SetServiceDataHandler(ImportSessionManager importSessionManager) {
		super();
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<SetServiceData> getActionType() {
		return SetServiceData.class;
	}

	@Override
	public SetServiceDataResult execute(SetServiceData action,
			ExecutionContext context) throws DispatchException {
		try {
			ServiceData data = action.getData();
			log.trace("Setting service data");
			importSessionManager.getImportSession().getServiceProfile()
					.getService().setServiceData(data);
			
			//Set stub description for GCubeWebService case
			//TODO: handling the problem here is a bad solution, need to find another design, maybe using hooks or events
			if (importSessionManager.getImportSession().getSoftwareType().getCode()==SoftwareTypeCode.gCubeWebService){
				importSessionManager.getImportSession().getServiceProfile().getService().getPackages().get(1).getData().setDescription("Stubs for service " + data.getName());
			}
			
			
			return new SetServiceDataResult(data);
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}
	}

	@Override
	public void rollback(SetServiceData action, SetServiceDataResult result,
			ExecutionContext context) throws DispatchException {
		//TODO Implement rollback operations
	}

	

}
