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
 * Filename: RunningInstanceManager.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.managers.resources;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.Software;
import org.gcube.common.resources.gcore.Software.Profile.ServicePackage;
import org.gcube.common.resources.gcore.Software.Profile.SoftwarePackage;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.resourcemanagement.support.server.exceptions.AbstractResourceException;
import org.gcube.resourcemanagement.support.server.exceptions.ResourceAccessException;
import org.gcube.resourcemanagement.support.server.exceptions.ResourceOperationException;
import org.gcube.resourcemanagement.support.server.exceptions.ResourceParameterException;
import org.gcube.resourcemanagement.support.server.types.AllowedResourceTypes;
import org.gcube.resourcemanagement.support.server.utils.Assertion;
import org.gcube.resourcemanagement.support.server.utils.ServerConsole;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.vremanagement.resourcemanager.client.RMBinderLibrary;
import org.gcube.vremanagement.resourcemanager.client.RMReportingLibrary;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.AddResourcesParameters;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.PackageItem;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.RemoveResourcesParameters;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.ResourceItem;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.ResourceList;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.SoftwareList;

/**
 * @author Daniele Strollo (ISTI-CNR)
 * @author Massimiliano Assante (ISTI-CNR)
 */
public class RunningInstanceManager extends AbstractResourceManager {
	// Used internally to require static functionalities (e.g. deploy).
	private static final String LOG_PREFIX = "[RI-MGR]";

	/**
	 * @deprecated discouraged use. With no ID some operations cannot be accessed.
	 */
	public RunningInstanceManager()
			throws ResourceParameterException, ResourceAccessException {
		super(AllowedResourceTypes.RunningInstance);
	}

	public RunningInstanceManager(final String id)
			throws ResourceParameterException, ResourceAccessException {
		super(id, AllowedResourceTypes.RunningInstance);
	}

	public RunningInstanceManager(final String id, final String name)
			throws ResourceParameterException, ResourceAccessException {
		super(id, name, AllowedResourceTypes.RunningInstance);
	}

	public RunningInstanceManager(final String id, final String name, final String subType)
			throws ResourceParameterException, ResourceAccessException {
		super(id, name, AllowedResourceTypes.RunningInstance, subType);
	}
	/**
	 * 
	 * @param scope
	 * @param ghnsID
	 * @param servicesID
	 * @return
	 * @throws ResourceParameterException
	 * @throws ResourceOperationException
	 */
	public final String deploy(final ScopeBean scope, final String[] ghnsID, final String[] servicesID) throws ResourceParameterException, ResourceOperationException {
		Assertion<ResourceParameterException> checker = new Assertion<ResourceParameterException>();
		checker.validate(servicesID != null && servicesID.length != 0,
				new ResourceParameterException("Invalid servicesID parameter. It cannot be null or empty."));
		checker.validate(scope != null,
				new ResourceParameterException("Cannot retrieve the scope."));

		ArrayList<PackageItem> serviceProfiles = new ArrayList<PackageItem>();

		try {

			SimpleQuery query = null;
			DiscoveryClient<Software> client = clientFor(Software.class);

			prepareServices: for (String serviceID : servicesID) {
				query = queryFor(Software.class);
				query.addCondition("$resource/Profile/ID/text() eq '" + serviceID + "'");
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
			RMBinderLibrary manager = ResourceFactory.createResourceManager(AllowedResourceTypes.Service).getResourceManager(scope.toString());
			String curr = ScopeProvider.instance.get();
			ScopeProvider.instance.set(scope.toString());
			id = manager.addResources(addResourcesParameters);
			ScopeProvider.instance.set(curr);
			return id;
		} catch (Exception e) {
			ServerConsole.error(LOG_PREFIX, "Error during deployment.", e);
			throw new ResourceOperationException("Software deployment failure: " + e.getMessage());
		}
	}
	/**
	 * 
	 * @param scope
	 * @return
	 * @throws AbstractResourceException
	 */
	public final String undeploy(final ScopeBean scope)	throws AbstractResourceException {
		Assertion<AbstractResourceException> checker = new Assertion<AbstractResourceException>();
		checker.validate(scope != null,
				new ResourceParameterException("Cannot retrieve the scope."));
		checker.validate(this.getID() != null,
				new ResourceOperationException("Invalid ID."));

		try {
			RMBinderLibrary rm = ResourceFactory.createResourceManager(AllowedResourceTypes.Service).getResourceManager(scope.toString());
			//prepare the parameters
			RemoveResourcesParameters params = new RemoveResourcesParameters();
			ResourceItem[] resourcelist = new ResourceItem[1];
			resourcelist[0] = new ResourceItem();
			resourcelist[0].id = this.getID();
			resourcelist[0].type = this.getType().name();
			ResourceList r = new ResourceList();
			ArrayList<ResourceItem> temp =new ArrayList<ResourceItem>();
			temp.add(resourcelist[0]);
			r.setResource(temp);
			params.resources = r;
			params.targetScope = scope.toString();

			//sending the request
			ServerConsole.info(LOG_PREFIX, "Sending the Remove Resource request....");
			String reportID = rm.removeResources(params);
			ServerConsole.info(LOG_PREFIX, "Returned report ID: " + reportID);
			return reportID;
		} catch (Exception e) {
			ServerConsole.error(LOG_PREFIX, "Error during deployment.", e);
			throw new ResourceOperationException("Software deployment failure: " + e.getMessage());
		}
	}

	public final String checkDeployStatus(final ScopeBean scope, final String deployID)
			throws AbstractResourceException {
		Assertion<ResourceParameterException> checker = new Assertion<ResourceParameterException>();
		checker.validate(scope != null,
				new ResourceParameterException("Invalid scope passed"));
		checker.validate(deployID != null && deployID.trim().length() > 0,
				new ResourceParameterException("Invalid reportID passed"));

		RMReportingLibrary vreManagerPortType = this.getReportResourceManager(scope.toString());

		try {
			return vreManagerPortType.getReport(deployID);
		} catch (Exception e) {
			ServerConsole.error(LOG_PREFIX, e);
			throw new ResourceOperationException("Cannot retrieve the report: " + deployID + " " + e.getMessage());
		}
	}

	@Override
	protected final Resource buildResource(final String xmlRepresentation) throws AbstractResourceException {
		try {
			JAXBContext ctx = JAXBContext.newInstance(GCoreEndpoint.class);
			Unmarshaller unmarshaller = ctx.createUnmarshaller();
			StringReader reader = new StringReader(xmlRepresentation);
			GCoreEndpoint deserialised = (GCoreEndpoint) unmarshaller.unmarshal(reader);
			return deserialised;
		} catch (Exception e) {
			throw new ResourceAccessException("Cannot load the stub for resource " + this.getType(), e);
		}			

	}
}
