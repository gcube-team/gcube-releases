package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import java.util.UUID;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.Package;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetPackageData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetPackageDataResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PackageData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class SetPackageDataHandler implements
		ActionHandler<SetPackageData, SetPackageDataResult> {

	private static final Logger log = LoggerFactory.getLogger(SetPackageDataHandler.class);

	private ImportSessionManager importSessionManager;

	@Inject
	public SetPackageDataHandler(ImportSessionManager importSessionManager) {
		super();
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<SetPackageData> getActionType() {
		return SetPackageData.class;
	}

	@Override
	public SetPackageDataResult execute(SetPackageData action,
			ExecutionContext context) throws DispatchException {
		try {
			UUID packageId = UUID.fromString(action.getPackageId());
			PackageData data = action.getData();
			log.debug("Setting package data for the package with id: "
					+ action.getPackageId());
			Package packageToSet = importSessionManager.getImportSession().getServiceProfile()
					.getService().getPackage(packageId);
			if (packageToSet == null ) throw new Exception("Package not found with id: " + action.getPackageId());
			packageToSet.setPackageData(data);
			return new SetPackageDataResult(packageId.toString(), data);
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}
	}

	@Override
	public void rollback(SetPackageData action, SetPackageDataResult result,
			ExecutionContext context) throws DispatchException {
		// TODO Implement rollback operations
	}

}
