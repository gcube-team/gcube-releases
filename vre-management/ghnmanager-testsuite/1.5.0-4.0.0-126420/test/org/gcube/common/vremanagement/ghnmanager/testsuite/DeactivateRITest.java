package org.gcube.common.vremanagement.ghnmanager.testsuite;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManagerImpl;
import org.gcube.common.vremanagement.ghnmanager.stubs.GHNManagerPortType;
import org.gcube.common.vremanagement.ghnmanager.stubs.RIData;
import org.gcube.common.vremanagement.ghnmanager.stubs.service.GHNManagerServiceAddressingLocator;

/**
 * Deactivation test
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class DeactivateRITest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 5)
			DeactivateRITest.printUsage();

		GCUBESecurityManagerImpl managerSec = new GCUBESecurityManagerImpl() {
			public boolean isSecurityEnabled() {
				return false;
			}
		};

		EndpointReferenceType endpoint = new EndpointReferenceType();
		try {
			endpoint.setAddress(new Address("http://" + args[0] + ":" + args[1]
					+ "/wsrf/services/gcube/common/vremanagement/GHNManager"));
			GHNManagerServiceAddressingLocator locator = new GHNManagerServiceAddressingLocator();
			GHNManagerPortType pt = GCUBERemotePortTypeContext.getProxy(
					locator.getGHNManagerPortTypePort(endpoint),
					GCUBEScope.getScope(args[2]), managerSec);
			RIData params = new RIData();
			params.setClazz(args[3]);
			params.setName(args[4]);
			System.out.println("Deactivating RI..");
			pt.deactivateRI(params);
			System.out.println("done");
		} catch (MalformedURIException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void printUsage() {
		System.err
				.println("DeactivateRI <host> <port> <scope> <serviceName> <serviceClass>");
		Runtime.getRuntime().exit(1);
	}

}
