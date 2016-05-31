package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import java.util.ArrayList;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.Package;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.FileType;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.JarFileType;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.AddPackage;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.AddPackageResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PackageData.PackageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class AddPackageHandler implements
		ActionHandler<AddPackage, AddPackageResult> {

	private static final Logger log = LoggerFactory.getLogger(AddPackageHandler.class);

	private ImportSessionManager importSessionManager;

	@Inject
	public AddPackageHandler(ImportSessionManager importSessionManager) {
		this.importSessionManager = importSessionManager;
	}

	@Override
	public Class<AddPackage> getActionType() {
		return AddPackage.class;
	}

	@Override
	public synchronized AddPackageResult execute(AddPackage action, ExecutionContext context)
			throws DispatchException {

		try {
			ArrayList<FileType> allowedFileTypes = new ArrayList<FileType>();
			allowedFileTypes.add(new JarFileType());
			Package newPackage = new Package(PackageType.Software, allowedFileTypes);
			importSessionManager.getImportSession().getServiceProfile().getService().getPackages().add(newPackage);
			log.debug("Added package with id: " + newPackage.getUuid());
			log.trace("Packages in session '" + importSessionManager.getImportSession().getSessionId().getId() + "': ");
			int i = 0;
			for (Package obj : importSessionManager.getImportSession().getServiceProfile().getService().getPackages()) {
				log.trace("Package #" + i + " with id: " + obj.getUuid().toString());
				i++;
			}
			return new AddPackageResult(newPackage.getUuid().toString());
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}
	}

	@Override
	public void rollback(AddPackage action, AddPackageResult result,
			ExecutionContext context) throws DispatchException {
		// TODO Implement rollback logic
	}

}
