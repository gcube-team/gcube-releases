package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import java.util.ArrayList;
import java.util.Collection;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.gcube.portlets.admin.software_upload_wizard.server.aslmanagers.ASLSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.ISoftwareTypeManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.SoftwareTypeFactory;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.maven.is.IMavenRepositoryIS;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetAvailableSoftwareTypes;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetAvailableSoftwareTypesResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwaretypes.ISoftwareTypeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.allen_sauer.gwt.log.client.Log;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;

public class GetAvailableSoftwareTypesHandler implements
		ActionHandler<GetAvailableSoftwareTypes, GetAvailableSoftwareTypesResult> {

	private static final Logger log = LoggerFactory.getLogger(GetAvailableSoftwareTypesHandler.class);

	@Inject
	private IMavenRepositoryIS mavenRepositoryIS;

	private ASLSessionManager aslSessionManager;

	@Inject
	public GetAvailableSoftwareTypesHandler(ASLSessionManager aslSessionManager) {
		super();
		this.aslSessionManager = aslSessionManager;
	}

	@Override
	public Class<GetAvailableSoftwareTypes> getActionType() {
		return GetAvailableSoftwareTypes.class;
	}

	@Override
	public GetAvailableSoftwareTypesResult execute(GetAvailableSoftwareTypes action, ExecutionContext context)
			throws DispatchException {
		try {
			ArrayList<ISoftwareTypeManager> list = SoftwareTypeFactory.getAvailableSoftwareTypes();
			Log.trace("Filtering software managers with session scope...");
			// Filter non eligible collections
			Collection<ISoftwareTypeManager> managers = Collections2.filter(list,
					new Predicate<ISoftwareTypeManager>() {

						@Override
						public boolean apply(ISoftwareTypeManager obj) {
							return obj.isAvailableForScope(aslSessionManager.getASLSession().getScope());
						}

					});

			Log.trace("Remaining software type managers: " + managers);

			// Return the right type list
			Collection<ISoftwareTypeInfo> infosList = Collections2.transform(managers,
					new Function<ISoftwareTypeManager, ISoftwareTypeInfo>() {

						@Override
						public ISoftwareTypeInfo apply(ISoftwareTypeManager manager) {
							return manager.getSoftwareTypeInfo();
						}
					});
			Log.trace("Returning software types info list: " + infosList);
			return new GetAvailableSoftwareTypesResult(new ArrayList<ISoftwareTypeInfo>(infosList));
		} catch (Exception e) {
			HandlerExceptionLogger.logHandlerException(log, e);
			throw new ActionException("A problem occurred while retrieving available software types");
		}
	}

	@Override
	public void rollback(GetAvailableSoftwareTypes action, GetAvailableSoftwareTypesResult result,
			ExecutionContext context) throws DispatchException {
	}

}
