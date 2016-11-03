package org.gcube.vremanagement.resourcemanager.testsuite.reporting;


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

		if (args.length < 3)
			GetReportTest.printUsage();
		//e3327770-c487-11e1-8750-97aa514a6c68
		EndpointReferenceType endpoint = new EndpointReferenceType();
		try {
			endpoint.setAddress(new Address("http://" + args[0] + "/wsrf/services/gcube/vremanagement/resourcemanager/reporting"));
			ReportingPortType pt = new ReportingServiceAddressingLocator().getReportingPortTypePort(endpoint);
			pt = GCUBERemotePortTypeContext.getProxy(pt, GCUBEScope.getScope(args[1].trim()));
			String report = pt.getReport(args[2]);
			System.out.println("REPORT");
			System.out.println("************");
			System.out.println(report);
			System.out.println("************");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static void printUsage() {
		System.out.println("GetReportTest <host:port> <scope> <session>");
		System.out.println("or");
		System.exit(1);
	}

}
