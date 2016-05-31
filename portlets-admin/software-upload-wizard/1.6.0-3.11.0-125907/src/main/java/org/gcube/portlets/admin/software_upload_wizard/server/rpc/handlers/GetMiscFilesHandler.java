package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import java.util.ArrayList;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.ISoftwareTypeManager;
import org.gcube.portlets.admin.software_upload_wizard.shared.Deliverable;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetMiscFiles;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetMiscFilesResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class GetMiscFilesHandler implements ActionHandler<GetMiscFiles, GetMiscFilesResult> {

	private static final Logger log = LoggerFactory.getLogger(GetMiscFilesHandler.class);

	ImportSessionManager importSessionManager;

	@Inject
	public GetMiscFilesHandler(ImportSessionManager importSessionManager) {
		super();
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<GetMiscFiles> getActionType() {
		return GetMiscFiles.class;
	}

	@Override
	public GetMiscFilesResult execute(GetMiscFiles action, ExecutionContext context) throws DispatchException {
		try {

			ISoftwareTypeManager manager = importSessionManager.getImportSession().getSoftwareManager();
			ArrayList<Deliverable> files = manager.getMiscFiles();
			log.trace("Returning misc files:\n" + files);
			return new GetMiscFilesResult(files);
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}
	}

	@Override
	public void rollback(GetMiscFiles action, GetMiscFilesResult result, ExecutionContext context)
			throws DispatchException {
	}

}
