package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import java.util.UUID;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.Package;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetPackageArtifactCoordinates;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetPackageArtifactCoordinatesResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class SetPackageArtifactCoordinatesHandler implements
		ActionHandler<SetPackageArtifactCoordinates, SetPackageArtifactCoordinatesResult> {

	private static final Logger log = LoggerFactory.getLogger(SetPackageArtifactCoordinatesHandler.class);
	
	private ImportSessionManager importSessionManager;
	
	@Inject
	public SetPackageArtifactCoordinatesHandler(ImportSessionManager importSessionManager) {
		super();
		this.importSessionManager = importSessionManager;
	}
	
	@Override
	public Class<SetPackageArtifactCoordinates> getActionType() {
		return SetPackageArtifactCoordinates.class;
	}

	@Override
	public SetPackageArtifactCoordinatesResult execute(
			SetPackageArtifactCoordinates action, ExecutionContext context)
			throws DispatchException {
		try {
			Package softwarePackage = importSessionManager.getImportSession()
					.getServiceProfile().getService()
					.getPackage(UUID.fromString(action.getPackageId()));
			softwarePackage.setArtifactCoordinates(action.getArtifactCoordinates());
			return new SetPackageArtifactCoordinatesResult();
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}
	}

	@Override
	public void rollback(SetPackageArtifactCoordinates action,
			SetPackageArtifactCoordinatesResult result, ExecutionContext context)
			throws DispatchException {
		// TODO Implement roolback operations
		
	}

}
