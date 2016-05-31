package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import java.util.UUID;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.RemoveMavenDependencies;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.RemoveMavenDependenciesResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class RemoveMavenDependenciesHandler implements
		ActionHandler<RemoveMavenDependencies, RemoveMavenDependenciesResult> {

	private static final Logger log = LoggerFactory.getLogger(RemoveMavenDependenciesHandler.class);

	ImportSessionManager importSessionManager;

	@Inject
	public RemoveMavenDependenciesHandler(ImportSessionManager importSessionManager) {
		super();
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<RemoveMavenDependencies> getActionType() {
		return RemoveMavenDependencies.class;
	}

	@Override
	public RemoveMavenDependenciesResult execute(RemoveMavenDependencies action, ExecutionContext context)
			throws DispatchException {
		try {
			importSessionManager.getImportSession().getServiceProfile().getService()
					.getPackage(UUID.fromString(action.getPackageId())).getMavenDependencies()
					.removeAll(action.getDependencies());
			return new RemoveMavenDependenciesResult();
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException("Error encountered while removing maven dependency. Check server logs.");
		}
	}

	@Override
	public void rollback(RemoveMavenDependencies action, RemoveMavenDependenciesResult result, ExecutionContext context)
			throws DispatchException {
		// TODO Implement rollback for this method

	}

}
