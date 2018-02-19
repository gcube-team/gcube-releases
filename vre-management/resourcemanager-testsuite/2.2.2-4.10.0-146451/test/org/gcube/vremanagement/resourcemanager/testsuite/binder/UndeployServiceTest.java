package org.gcube.vremanagement.resourcemanager.testsuite.binder;

import java.io.IOException;
import java.util.Properties;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBEClientLog;
import org.gcube.vremanagement.resourcemanager.stubs.binder.RemoveResourcesParameters;
import org.gcube.vremanagement.resourcemanager.stubs.binder.ResourceBinderPortType;
import org.gcube.vremanagement.resourcemanager.stubs.binder.ServiceItem;
import org.gcube.vremanagement.resourcemanager.stubs.binder.ServiceList;
import org.gcube.vremanagement.resourcemanager.stubs.binder.service.ResourceBinderServiceAddressingLocator;

public class UndeployServiceTest {

	protected static Properties resources = new Properties();

	protected static GCUBEClientLog logger = new GCUBEClientLog(
			UndeployServiceTest.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if ((args.length < 2) || (args.length > 3))
			AddResourceTest.printUsage();

		try {
			resources.load(RemoveResourceTest.class.getResourceAsStream("/"
					+ args[1]));
		} catch (IOException e1) {
			e1.printStackTrace();
			Runtime.getRuntime().exit(1);

		}

		EndpointReferenceType endpoint = new EndpointReferenceType();

		try {

			endpoint.setAddress(new Address(args[0]));
			ResourceBinderPortType pt = new ResourceBinderServiceAddressingLocator()
					.getResourceBinderPortTypePort(endpoint);

			pt = GCUBERemotePortTypeContext.getProxy(pt, GCUBEScope
					.getScope(resources.getProperty("callerScope").trim()),
					90000);

			RemoveResourcesParameters params = new RemoveResourcesParameters();

			if (new Integer(resources.getProperty("numOfServicesToRemove")) > 0) {
				ServiceItem[] servicelist = new ServiceItem[new Integer(
						resources.getProperty("numOfServicesToRemove"))];
				for (int i = 1; i < (servicelist.length + 1); i++) {
					servicelist[i - 1] = new ServiceItem();
					servicelist[i - 1].setServiceClass(resources
							.getProperty("service." + i + ".class"));
					servicelist[i - 1].setServiceName(resources
							.getProperty("service." + i + ".name"));
					servicelist[i - 1].setServiceVersion(resources
							.getProperty("service." + i + ".version"));
					if (resources.getProperty("service." + i + ".GHN") != null)
						servicelist[i - 1].setGHN(resources
								.getProperty("service." + i + ".GHN"));
				}

				ServiceList l = new ServiceList();
				l.setService(servicelist);

				params.setServices(l);
			}
			params.setTargetScope(resources.getProperty("targetScope").trim());
			System.out.println("Sending the Remove Resource request....");
			String reportID = pt.removeResources(params);
			System.out.println("Returned report ID: " + reportID);

		} catch (Exception e) {
			logger.fatal("Unable to undeploy the service(s)", e);
		}
	}

}
