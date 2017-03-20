package org.gcube.vremanagement.resourcemanager.testsuite.reporting;

import java.util.Calendar;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.vremanagement.resourcemanager.stubs.reporting.ReportingPortType;
import org.gcube.vremanagement.resourcemanager.stubs.reporting.service.ReportingServiceAddressingLocator;

/**
 * Tester for the <code>getReport</code> operation
 * @author manuele simi (CNR)
 *
 */
public class GetReportTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if ((args.length < 3) || (args.length > 4))
			GetReportTest.printUsage();

		EndpointReferenceType endpoint = new EndpointReferenceType();

		try {
			endpoint.setAddress(new Address(args[0]));
			ReportingPortType pt = new ReportingServiceAddressingLocator()
					.getReportingPortTypePort(endpoint);

			pt = GCUBERemotePortTypeContext.getProxy(pt,
					GCUBEScope.getScope(args[1]), 90000);

			System.out.println(Calendar.getInstance().getTime().toString());
			String report = pt.getReport(args[2]);
			System.out.println(Calendar.getInstance().getTime().toString());
			System.out.println("REPORT");
			System.out.println("************");
			System.out.println(report);
			System.out.println("************");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static void printUsage() {
		System.out
				.println("GetReportTest http://host:port/wsrf/services/gcube/vremanagement/ResourceManager <scope> <session>");
		System.out.println("or");
		System.out
				.println("GetReportTest https://host:port/wsrf/services/gcube/vremanagement/ResourceManager <scope> <session> <identity>");

		System.exit(1);
	}

}
