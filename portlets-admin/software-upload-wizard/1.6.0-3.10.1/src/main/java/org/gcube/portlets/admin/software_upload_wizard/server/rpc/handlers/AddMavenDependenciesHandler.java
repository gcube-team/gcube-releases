package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import java.util.UUID;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.AddMavenDependencies;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.AddMavenDependenciesResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class AddMavenDependenciesHandler implements ActionHandler<AddMavenDependencies, AddMavenDependenciesResult> {

	private static final Logger log = LoggerFactory.getLogger(AddMavenDependenciesHandler.class);

	ImportSessionManager importSessionManager;

	@Inject
	public AddMavenDependenciesHandler(ImportSessionManager importSessionManager) {
		super();
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<AddMavenDependencies> getActionType() {
		return AddMavenDependencies.class;
	}

	@Override
	public AddMavenDependenciesResult execute(AddMavenDependencies action, ExecutionContext context)
			throws DispatchException {
		try {
			importSessionManager.getImportSession().getServiceProfile().getService()
					.getPackage(UUID.fromString(action.getPackageId())).getMavenDependencies()
					.addAll(action.getDependencies());
			return new AddMavenDependenciesResult();
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}
	}

	@Override
	public void rollback(AddMavenDependencies action, AddMavenDependenciesResult result, ExecutionContext context)
			throws DispatchException {
		// TODO Implement method rollback
	}

}
