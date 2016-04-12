package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import java.util.UUID;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.Package;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageDataResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PackageData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class GetPackageDataHandler implements ActionHandler<GetPackageData, GetPackageDataResult> {

	private static final Logger log = LoggerFactory.getLogger(GetPackageDataHandler.class);

	private ImportSessionManager importSessionManager;

	@Inject
	public GetPackageDataHandler(ImportSessionManager importSessionManager) {
		super();
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<GetPackageData> getActionType() {
		return GetPackageData.class;
	}

	@Override
	public GetPackageDataResult execute(GetPackageData action, ExecutionContext context) throws DispatchException {
		try {
			UUID packageId = UUID.fromString(action.getPackageId());
			Package softwarePackage = importSessionManager.getImportSession().getServiceProfile().getService()
					.getPackage(packageId);
			if (softwarePackage == null)
				throw new Exception("Package not found with id: " + action.getPackageId());
			PackageData data = softwarePackage.getData();
			log.debug("Returning package data for package with id: " + packageId.toString());
			return new GetPackageDataResult(data);
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}
	}

	@Override
	public void rollback(GetPackageData action, GetPackageDataResult result, ExecutionContext context)
			throws DispatchException {
	}

}
