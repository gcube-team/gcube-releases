package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import java.util.UUID;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.aslmanagers.ASLSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.Package;
import org.gcube.portlets.admin.software_upload_wizard.server.util.ScopeUtil;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageArtifactCoordinates;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageArtifactCoordinatesResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.MavenVersionRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class GetPackageArtifactCoordinatesHandler implements
		ActionHandler<GetPackageArtifactCoordinates, GetPackageArtifactCoordinatesResult> {

	private static final Logger log = LoggerFactory.getLogger(GetPackageArtifactCoordinatesHandler.class);

	private ImportSessionManager importSessionManager;

	@Inject
	public GetPackageArtifactCoordinatesHandler(ImportSessionManager importSessionManager) {
		super();
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<GetPackageArtifactCoordinates> getActionType() {
		return GetPackageArtifactCoordinates.class;
	}

	@Override
	public GetPackageArtifactCoordinatesResult execute(GetPackageArtifactCoordinates action, ExecutionContext context)
			throws DispatchException {
		try {
			Package softwarePackage = importSessionManager.getImportSession().getServiceProfile().getService()
					.getPackage(UUID.fromString(action.getPackageId()));

			MavenVersionRule rule = null;

			// gcube infrastructure
			if (ScopeUtil.getInfrastructure(importSessionManager.getImportSession().getScope()).equals(
					ASLSessionManager.GCUBE_INFRASTRUCTURE))
				rule = MavenVersionRule.ONLY_SNAPSHOT;

			// d4science infrastructure
			if (ScopeUtil.getInfrastructure(importSessionManager.getImportSession().getScope()).equals(
					ASLSessionManager.D4SCIENCE_INFRASTRUCTURE))
				rule = MavenVersionRule.NO_SNAPSHOT;

			if (rule == null)
				throw new Exception("Unable to evaluate maven version rule with the given scope infrastructure");

			return new GetPackageArtifactCoordinatesResult(importSessionManager.getImportSession().getSoftwareManager()
					.getMavenCoordinates(softwarePackage), rule);
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}

	}

	@Override
	public void rollback(GetPackageArtifactCoordinates action, GetPackageArtifactCoordinatesResult result,
			ExecutionContext context) throws DispatchException {
		// No rollback for this handler

	}

}
