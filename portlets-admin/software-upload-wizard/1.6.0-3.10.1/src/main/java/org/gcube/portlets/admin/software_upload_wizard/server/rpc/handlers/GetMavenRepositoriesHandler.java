package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import java.util.ArrayList;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.aslmanagers.ASLSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.maven.is.IMavenRepositoryIS;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetMavenRepositories;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetMavenRepositoriesResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.MavenRepositoryInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwaretypes.SoftwareTypeCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class GetMavenRepositoriesHandler implements ActionHandler<GetMavenRepositories, GetMavenRepositoriesResult> {

	private static final Logger log = LoggerFactory.getLogger(GetMavenRepositoriesHandler.class);

	IMavenRepositoryIS mavenRepositoryIS;
	ASLSessionManager aslSessionManager;
	ImportSessionManager importSessionManager;

	@Inject
	public GetMavenRepositoriesHandler(IMavenRepositoryIS mavenRepositoryIS, ASLSessionManager aslSessionManager,
			ImportSessionManager importSessionManager) {
		super();
		this.mavenRepositoryIS = mavenRepositoryIS;
		this.aslSessionManager = aslSessionManager;
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<GetMavenRepositories> getActionType() {
		return GetMavenRepositories.class;
	}

	@Override
	public GetMavenRepositoriesResult execute(GetMavenRepositories action, ExecutionContext context)
			throws DispatchException {
		String scope = aslSessionManager.getASLSession().getScope();
		String infrastructure = scope.substring(scope.indexOf("/") + 1, scope.indexOf("/", scope.indexOf("/") + 1));
		ArrayList<MavenRepositoryInfo> repos = new ArrayList<MavenRepositoryInfo>();

		try {
			if (importSessionManager.getImportSession().getSoftwareType().getCode()
					.equals(SoftwareTypeCode.AnySoftware)) {
				repos.add(new MavenRepositoryInfo(mavenRepositoryIS
						.getNexusRepository(IMavenRepositoryIS.EXTERNALS_REPO_ID)));
				return new GetMavenRepositoriesResult(repos);
			}
			if (infrastructure.equals(ASLSessionManager.GCUBE_INFRASTRUCTURE))
				repos.add(new MavenRepositoryInfo(mavenRepositoryIS
						.getNexusRepository(IMavenRepositoryIS.SNAPSHOTS_REPO_ID)));
			if (infrastructure.equals(ASLSessionManager.D4SCIENCE_INFRASTRUCTURE))
				repos.add(new MavenRepositoryInfo(mavenRepositoryIS
						.getNexusRepository(IMavenRepositoryIS.RELEASES_REPO_ID)));
			repos.add(new MavenRepositoryInfo(mavenRepositoryIS
					.getNexusRepository(IMavenRepositoryIS.EXTERNALS_REPO_ID)));
			return new GetMavenRepositoriesResult(repos);
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException("Unable to return maven repositories", e);
		}

	}

	@Override
	public void rollback(GetMavenRepositories action, GetMavenRepositoriesResult result, ExecutionContext context)
			throws DispatchException {
		// No rollback for this action

	}

}
