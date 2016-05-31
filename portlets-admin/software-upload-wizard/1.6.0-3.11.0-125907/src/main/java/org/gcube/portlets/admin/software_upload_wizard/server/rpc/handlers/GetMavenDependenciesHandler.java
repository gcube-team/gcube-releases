package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetMavenDependencies;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetMavenDependenciesResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.MavenCoordinates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class GetMavenDependenciesHandler implements ActionHandler<GetMavenDependencies, GetMavenDependenciesResult> {

	private static final Logger log = LoggerFactory.getLogger(GetMavenDependenciesHandler.class);

	ImportSessionManager importSessionManager;

	@Inject
	public GetMavenDependenciesHandler(ImportSessionManager importSessionManager) {
		super();
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<GetMavenDependencies> getActionType() {
		return GetMavenDependencies.class;
	}

	@Override
	public GetMavenDependenciesResult execute(GetMavenDependencies action, ExecutionContext context)
			throws DispatchException {
		try {
			Collection<MavenCoordinates> dependencies = importSessionManager.getImportSession().getServiceProfile()
					.getService().getPackage(UUID.fromString(action.getPackageId())).getMavenDependencies();
			return new GetMavenDependenciesResult(new ArrayList<MavenCoordinates>(dependencies));
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}
	}

	@Override
	public void rollback(GetMavenDependencies action, GetMavenDependenciesResult result, ExecutionContext context)
			throws DispatchException {
	}

}
