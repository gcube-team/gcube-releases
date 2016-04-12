package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.Package;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageIds;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageIdsResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;

public class GetPackageIdsHandler implements ActionHandler<GetPackageIds, GetPackageIdsResult> {

	private static final Logger log = LoggerFactory.getLogger(GetPackageIdsHandler.class);

	private ImportSessionManager sessionManager;

	@Inject
	public GetPackageIdsHandler(ImportSessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	@Override
	public Class<GetPackageIds> getActionType() {
		return GetPackageIds.class;
	}

	@Override
	public GetPackageIdsResult execute(GetPackageIds action, ExecutionContext context) throws DispatchException {
		ArrayList<Package> packages = sessionManager.getImportSession().getServiceProfile().getService().getPackages();

		try {
			Collection<String> collection = Collections2
					.transform(
							packages,
							new Function<org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.Package, String>() {

								@Override
								public String apply(Package arg0) {
									return arg0.getUuid().toString();
								}

							});
			log.trace("Returning packages ids:");
			Iterator<String> i = collection.iterator();
			String tmp;
			int k = 0;
			while (i.hasNext()) {
				tmp = i.next();
				log.trace("Package #" + k + ": " + tmp);
				k++;
			}
			return new GetPackageIdsResult(new ArrayList<String>(collection));
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException(e);
		}

	}

	@Override
	public void rollback(GetPackageIds action, GetPackageIdsResult result, ExecutionContext context)
			throws DispatchException {
		// TODO Auto-generated method stub
	}

}
