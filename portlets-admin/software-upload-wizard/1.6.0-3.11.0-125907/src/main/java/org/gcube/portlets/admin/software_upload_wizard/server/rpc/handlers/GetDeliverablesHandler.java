package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import java.util.ArrayList;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.ISoftwareTypeManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.exceptions.ServiceProfileUnavailableException;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.Package;
import org.gcube.portlets.admin.software_upload_wizard.shared.Deliverable;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetDeliverables;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetDeliverablesResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class GetDeliverablesHandler implements ActionHandler<GetDeliverables, GetDeliverablesResult> {

	private static final Logger log = LoggerFactory.getLogger(GetDeliverablesHandler.class);

	ImportSessionManager importSessionManager;

	@Inject
	public GetDeliverablesHandler(ImportSessionManager importSessionManager) {
		super();
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<GetDeliverables> getActionType() {
		return GetDeliverables.class;
	}

	@Override
	public GetDeliverablesResult execute(GetDeliverables action, ExecutionContext context) throws DispatchException {
		ISoftwareTypeManager manager = importSessionManager.getImportSession().getSoftwareManager();
		ArrayList<Deliverable> files = new ArrayList<Deliverable>();
		try {
			files.add(new Deliverable(Deliverable.SERVICE_PROFILE, manager.getServiceProfile(false)));
		} catch (ServiceProfileUnavailableException e) {
			// Go ahead without service profile
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
		}

		// Get Packages poms
		try {
			for (Package p : importSessionManager.getImportSession().getServiceProfile().getService().getPackages())
				files.add(new Deliverable("Package '" + p.getData().getName() + "' " + Deliverable.POM, manager
						.getPOM(p)));
		} catch (ServiceProfileUnavailableException e) {
			// Go ahead without package pom
		} catch (Exception e) {
			log.warn("Error occurred while retrieving POM for package. " + e.getMessage());
		}

		// Get Packages poms
		try {
			files.add(new Deliverable("Service archive " + Deliverable.POM, manager.getPOM(importSessionManager
					.getImportSession().getServiceProfile())));
		} catch (ServiceProfileUnavailableException e) {
			// Go ahead without service archive pom
		} catch (Exception e) {
			log.warn("Error occurred while retrieving POM for package. " + e.getMessage());
		}

		try {
			files.addAll(manager.getMiscFiles());
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}
		log.trace("Returning deliverables:\n" + files);
		return new GetDeliverablesResult(files);

	}

	@Override
	public void rollback(GetDeliverables action, GetDeliverablesResult result, ExecutionContext context)
			throws DispatchException {
	}

}
