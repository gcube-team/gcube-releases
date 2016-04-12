package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetXmlProfile;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetXmlProfileResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class GetXmlProfileHandler implements ActionHandler<GetXmlProfile, GetXmlProfileResult> {

	private static final Logger log = LoggerFactory.getLogger(GetXmlProfileHandler.class);
	
	private ImportSessionManager importSessionManager;
	
	@Inject
	public GetXmlProfileHandler(ImportSessionManager importSessionManager) {
		this.importSessionManager=importSessionManager;
	}
	
	@Override
	public Class<GetXmlProfile> getActionType() {
		return GetXmlProfile.class;
	}

	@Override
	public GetXmlProfileResult execute(GetXmlProfile action,
			ExecutionContext context) throws DispatchException {
		try {
			String xmlDocument = importSessionManager.getImportSession().getSoftwareManager().getServiceProfile(false);
			return new GetXmlProfileResult(xmlDocument);
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}
	}

	@Override
	public void rollback(GetXmlProfile action, GetXmlProfileResult result,
			ExecutionContext context) throws DispatchException {
	}

}
