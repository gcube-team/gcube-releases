/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: ManagementUtils.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.managers.resources;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.Software;
import org.gcube.common.resources.gcore.Software.Profile.ServicePackage;
import org.gcube.common.resources.gcore.Software.Profile.SoftwarePackage;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.resourcemanagement.support.server.exceptions.AbstractResourceException;
import org.gcube.resourcemanagement.support.server.exceptions.ResourceAccessException;
import org.gcube.resourcemanagement.support.server.exceptions.ResourceOperationException;
import org.gcube.resourcemanagement.support.server.exceptions.ResourceParameterException;
import org.gcube.resourcemanagement.support.server.managers.scope.ScopeManager;
import org.gcube.resourcemanagement.support.server.types.AllowedResourceTypes;
import org.gcube.resourcemanagement.support.server.utils.Assertion;
import org.gcube.resourcemanagement.support.server.utils.ServerConsole;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.vremanagement.resourcemanager.client.RMBinderLibrary;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.AddResourcesParameters;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.PackageItem;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.ResourceItem;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.ResourceList;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.SoftwareList;


/**
 * A support class containing operations to manage multiple resources.
 * Here are provided the functionalities to delete/addToScope/deploy
 * groups of homogeneous resources.
 * @author Daniele Strollo (ISTI-CNR)
 */
public class ManagementUtils {
	private static final String LOG_PREFIX = "[MANAGEMENT-UTILS]";

	/**
	 * Applies the add to scope to multiple resources having the same type.
	 * @param type
	 * @param resources
	 * @param sourceScope
	 * @param targetScope
	 * @return the generated report ID
	 */
	public static final synchronized String addToExistingScope(AllowedResourceTypes type, String[] resourceIDs, ScopeBean sourceScope, ScopeBean targetScope) throws Exception {
		ServerConsole.trace(
				LOG_PREFIX,
				"[ADD-ToExistingScope] Adding from scope [" +
						sourceScope.toString() +
						"] to existing scope [" +
						targetScope.toString() +
						"] resources having type " + type.name());

		// 1 - If not RI or GHN and the scopes are sibling and VO copyFromToVO
		if (!(type == AllowedResourceTypes.GHN) &&
				!(type == AllowedResourceTypes.RunningInstance) &&
				sourceScope.type() == Type.VO && targetScope.type() == Type.VO) {
			// Phase 1. retrieve the resource to copy
			//GCUBEResource resStub = this.getGCUBEResource(sourceScope);

			// Phase 2. Before to register the resource, the scope must be
			// bound to the local GCUBEResource
			String retval = bindToScope(type, resourceIDs, targetScope);

			// Phase 3. Register to the new VO through the ISPublisher
			// applies a copy of old descriptors bound in other scopes.
			try {
				for (String id : resourceIDs) {
					AbstractResourceManager res = ResourceFactory.createResourceManager(type, id);
					Resource resStub = res.getResource(sourceScope);

					ScopeProvider.instance.set(targetScope.toString());
					res.getRegistryPublisher().update(resStub);
	
				}
			} catch (Exception e) {
				throw new ResourceAccessException(e.getMessage());
			}
			return retval;
		}

		// Add a gCube Resource to
		// (i) a VRE scope from the parent VO or
		// (ii) a VO scope from the infrastructure scope
		if (sourceScope.toString().contains(targetScope.toString())) {
			throw new ResourceOperationException(
					"You are not allowed to apply to this scope. Target scope is not enclosed in the source one.");
		}

		// 2 - Applies the normal scope binding
		return bindToScope(type, resourceIDs, targetScope);
	}

	/**
	 * Applies the remove from scope
	 * @param type
	 * @param resources
	 * @param sourceScope
	 * @param targetScope
	 * @return the generated report ID
	 */
	public static final synchronized String removeFromExistingScope(final AllowedResourceTypes type, final String[] resourceIDs, 
			final ScopeBean sourceScope, final ScopeBean targetScope)	throws Exception {

		ServerConsole.trace(
				LOG_PREFIX,
				"[REMOVE-FromExistingScope] Removing scope [" +
						targetScope.toString() +
						"] from existing scope [" +
						sourceScope.toString() +
						"] resources having type " + type.name());
		// cannot remove a Scope if its the same
		if (targetScope.toString().compareTo(sourceScope.toString()) == 0) {
			return "You are not allowed to remove this scope. Current and Target scope are the same.";
		}
		String toReturn = "";
		AbstractResourceManager manager = ResourceFactory.createResourceManager(type);
		for (String id : resourceIDs) {
			try {
				manager.setID(id);
				toReturn = manager.delete(targetScope);
			} catch (AbstractResourceException e) {
				ServerConsole.error(LOG_PREFIX, e);
			}
		}

		return toReturn;
	}

	/**
	 *
	 * @param type
	 * @param resourceIDs
	 * @param targetScope
	 * @return the ID of generated report
	 * @throws AbstractResourceException
	 */
	private static synchronized String bindToScope(final AllowedResourceTypes type,	final String[] resourceIDs,	final ScopeBean targetScope)throws AbstractResourceException {
		AddResourcesParameters addParam = new AddResourcesParameters();
		RMBinderLibrary manager = ResourceFactory.createResourceManager(type).getResourceManager(targetScope.toString());
		ArrayList<ResourceItem> resToBind = new ArrayList<ResourceItem>();

		for (String id : resourceIDs) {
			ResourceItem toAdd = new ResourceItem();
			toAdd.id = id;
			toAdd.setType(type.name());
			resToBind.add(toAdd);
		}
		ResourceList r = new ResourceList();

		r.setResource(resToBind);
		addParam.setResources(r);
		addParam.setTargetScope(targetScope.toString());

		try {
			String reportID =  manager.addResources(addParam);

			ServerConsole.trace(
					LOG_PREFIX,
					"[BIND-SCOPE-EXIT] Applyed Adding of resources " + type.name() + " to scope [" +
							targetScope.toString() + "]... reportID: " + reportID);

			return reportID;
		} catch (Exception e) {
			ServerConsole.trace(
					LOG_PREFIX,
					"[BIND-SCOPE-EXIT] [FAILURE]");
			throw new ResourceOperationException("During resource::addToScope: " + e.getMessage());
		}
	}

	public static synchronized void delete(final AllowedResourceTypes type,	final String[] resourceIDs,	final ScopeBean scope) throws AbstractResourceException {
		AbstractResourceManager resource = ResourceFactory.createResourceManager(type);
		for (String id : resourceIDs) {
			try {
				resource.setID(id);
				resource.delete(scope);
			} catch (AbstractResourceException e) {
				ServerConsole.error(LOG_PREFIX, e);
			}
		}
	}

	/**
	 * Makes the deployment of software on a list of ghns.
	 * @param ghnsID
	 * @param servicesID
	 * @return the generated report ID
	 * @throws Exception
	 */
	public static final synchronized String deploy(final ScopeBean scope, final String[] ghnsID, final String[] servicesID)	throws Exception {
		Assertion<Exception> checker = new Assertion<Exception>();
		checker.validate(ghnsID != null && ghnsID.length != 0, new ResourceParameterException("Invalid ghnsID parameter. It cannot be null or empty."));
		checker.validate(servicesID != null && servicesID.length != 0, new ResourceParameterException("Invalid servicesID parameter. It cannot be null or empty."));
		checker.validate(scope != null, new Exception("Cannot retrieve the scope."));

	
		RMBinderLibrary manager = ResourceFactory.createResourceManager(AllowedResourceTypes.Service).getResourceManager(scope.toString());

		System.out.println("\n\n**** These are the service ids to deploy on SCOPE " + scope);
		for (String sid : servicesID) {
			System.out.println(sid);
		}
		System.out.println("\n\n**** These are the gHNs ids to deploy on SCOPE " + scope);
		for (String ghn : ghnsID) {
			System.out.println(ghn);
		}

		ArrayList<PackageItem> serviceProfiles = new ArrayList<PackageItem>();


		// Retrieves the profiles of services
		SimpleQuery query = null;
		DiscoveryClient<Software> client = clientFor(Software.class);

		prepareServices: for (String serviceID : servicesID) {
			System.out.println("\n\n**** Query the ICClient to get the profile");
			query = queryFor(Software.class);
			query.addCondition("$resource/ID/text() eq '" + serviceID + "'");

			System.out.println("**** Query : " + query.toString());
			String curr = ScopeProvider.instance.get();
			ScopeProvider.instance.set(scope.toString());
			List<Software> results = client.submit(query);
			ScopeProvider.instance.set(curr);
			System.out.println("**** results received : " + results.size());

			Software ret = null;
			if (results != null && results.size() > 0) {
				ret = results.get(0);
			} else {
				continue prepareServices;
			}

			if (ret == null ||
					ret.profile() == null ||
					ret.profile().softwareClass() == null ||
					ret.profile().softwareName() == null) {
				ServerConsole.error(LOG_PREFIX, "found an invalid service profile");
				continue;
			}

			PackageItem toAdd = new PackageItem();

			toAdd.serviceClass = ret.profile().softwareClass();
			toAdd.serviceName = ret.profile().softwareName();
			toAdd.serviceVersion ="1.0.0";
			if (ret.profile().packages().size() == 1) {
				toAdd.packageName = ret.profile().packages().iterator().next().name();
				toAdd.packageVersion = ret.profile().packages().iterator().next().version();
			} else {
				for (SoftwarePackage p : ret.profile().packages()) {
					if (p.getClass().isAssignableFrom(ServicePackage.class)) {
						toAdd.packageName = p.name();
						toAdd.packageVersion = p.version();
						break;
					}
				}
			}

			serviceProfiles.add(toAdd);
		}

		SoftwareList serviceList = new SoftwareList();
		ArrayList<String> arrayGHNSids = new ArrayList<String>();
		for (int i = 0; i < ghnsID.length; i++) {
			arrayGHNSids.add(ghnsID[i]);
		}
		serviceList.suggestedTargetGHNNames = arrayGHNSids;
		serviceList.software = serviceProfiles;
				
		AddResourcesParameters addResourcesParameters = new AddResourcesParameters();
		addResourcesParameters.softwareList = serviceList;
		addResourcesParameters.setTargetScope(scope.toString());

		System.out.println("\n\n**** These is the ServiceList i pass to ResourceManagerPortType: ");
		for (int i = 0; i <  serviceList.software.size(); i++) {
			System.out.println(serviceList.software.get(i));
		}

		String id = "";
		id = manager.addResources(addResourcesParameters);
		ServerConsole.debug(LOG_PREFIX, "Report ID = " + id);
		System.out.println("Returning.... no exceptions");
		return id;
	}

	public static final void main(final String[] args) {
		List<String> ids = new Vector<String>();
		ids.add(null);
		ids.add("id2");
		ids.add(null);

		ScopeManager.setScopeConfigFile("test-suite" + File.separator + "scopes" + File.separator + "scopedata.xml");

		try {


			ManagementUtils.delete(AllowedResourceTypes.GenericResource,
					new String[]{
					"3f7384a0-d51c-11df-80cc-ece35605c26c",
					"975419b0-d2e1-11df-b0ed-f8e6e669b8ad",
					null,
					"test"
			},
			new ScopeBean("/gcube/devsec/devVRE"));
		} catch (Exception e) {
			ServerConsole.error(LOG_PREFIX, e);
		}
	}
}
