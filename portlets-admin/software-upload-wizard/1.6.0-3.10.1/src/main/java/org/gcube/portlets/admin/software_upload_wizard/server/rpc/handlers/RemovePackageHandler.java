package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import java.util.UUID;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.Package;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.RemovePackage;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.RemovePackageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class RemovePackageHandler implements
		ActionHandler<RemovePackage, RemovePackageResult> {

	private static final Logger log = LoggerFactory.getLogger(RemovePackageHandler.class);

	private ImportSessionManager importSessionManager;

	@Inject
	public RemovePackageHandler(ImportSessionManager importSessionManager) {
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<RemovePackage> getActionType() {
		return RemovePackage.class;
	}

	@Override
	public synchronized RemovePackageResult execute(RemovePackage action,
			ExecutionContext context) throws DispatchException {
		String packageId = action.getPackageId();
		try {
			importSessionManager.getImportSession().getServiceProfile()
					.getService()
					.removePackage(UUID.fromString(packageId));
			log.debug("Removed package with id: " + packageId );
			
			log.trace("Packages in session '"
					+ importSessionManager.getImportSession().getSessionId()
							.getId() + "': ");
			int i = 0;
			for (Package obj : importSessionManager.getImportSession()
					.getServiceProfile().getService().getPackages()) {
				log.trace("Package #" + i + " with id: "
						+ obj.getUuid().toString());
				i++;
			}
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(
					"Unable to remove package with the given ID", e);
		}
		return new RemovePackageResult();
	}

	@Override
	public void rollback(RemovePackage action, RemovePackageResult result,
			ExecutionContext context) throws DispatchException {
		// TODO Auto-generated method stub

	}

}
