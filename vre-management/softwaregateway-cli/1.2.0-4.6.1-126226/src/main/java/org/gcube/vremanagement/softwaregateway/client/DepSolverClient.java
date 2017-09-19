package org.gcube.vremanagement.softwaregateway.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;

//import javax.xml.rpc.ServiceException;
//
//import org.apache.axis.message.addressing.AttributedURI;
//import org.apache.axis.message.addressing.EndpointReferenceType;
//import org.apache.axis.types.URI.MalformedURIException;
//import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
//import org.gcube.common.core.faults.GCUBEFault;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.common.core.scope.GCUBEScope.MalformedScopeExpressionException;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.DependenciesCoordinates;
import org.gcube.vremanagement.softwaregateway.client.proxies.Proxies;
//import org.gcube.vremanagement.softwaregateway.stubs.AccessPortType;
//import org.gcube.vremanagement.softwaregateway.stubs.DependenciesCoordinates;
//import org.gcube.vremanagement.softwaregateway.stubs.RegistrationPortType;
//import org.gcube.vremanagement.softwaregateway.stubs.service.AccessServiceAddressingLocator;
//import org.gcube.vremanagement.softwaregateway.stubs.service.RegistrationServiceAddressingLocator;

public class DepSolverClient {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws MalformedScopeExpressionException 
	 */
	public static void main(String[] args){

		for (int i=0; i<args.length;i++) 
			System.out.println("param N." +i + ": " + args[i]);
		
		if((args.length != 7 ) && (args.length != 5 )){
			System.out.println("Usage:");
			System.out.println("\tjava  DepSolverClient   SoftwareGatewayEPR  scope  ServiceName   ServiceClass   ServiceVersion   PackageName   PackageVersion\n\n");
			System.out.println("Example:");
			System.out.println("\tjava  DepSolverClient  http://dlib25.isti.cnr.it:8080/wsrf/services/gcube/vremanagement/softwaregateway/Access  /gcube/devsec sn sc sv pn pv  \n\n");
			return;
		}
		ScopeProvider.instance.set(args[1]);
		SGAccessLibrary library;
		try {
			library = Proxies.accessService().at(new URI(args[0])).withTimeout(1, TimeUnit.MINUTES).build();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
//		EndpointReferenceType endpoint = new EndpointReferenceType();
//		try {
//			endpoint.setAddress(new AttributedURI(args[0]));
//		} catch (MalformedURIException e) {
//			e.printStackTrace();
//			return;
//		}
//		
//		AccessPortType stub;
//		try {
//			stub = new AccessServiceAddressingLocator().getAccessPortTypePort(endpoint);
//		} catch (ServiceException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return;
//		}
//		try {
//			stub=GCUBERemotePortTypeContext.getProxy(stub,GCUBEScope.getScope(args[1]), 180000);
//		} catch (MalformedScopeExpressionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return;
//		}
		
//		String packageName=args[5];
//		String packageVersion=args[6];
		String serviceClass=args[2];
		String serviceName=args[3];
		String serviceVersion=args[4];
		String packageName=null;
		String packageVersion=null;
		if(args.length == 7 ){
			packageName=args[5];
			packageVersion=args[6];
			
		}
		DependenciesCoordinates pc=new DependenciesCoordinates();
//		pc.setPackageName(packageName);
//		pc.setPackageVersion(packageVersion);
//		pc.setServiceClass(serviceClass);
//		pc.setServiceName(serviceName);
//		pc.setServiceVersion(serviceVersion);
		pc.pn=packageName;
		pc.pv=packageVersion;
		pc.sc=serviceClass;
		pc.sn=serviceName;
		pc.sv=serviceVersion;

		String xml=null;
		xml=library.getDependencies(pc);
//		try {
//			xml = stub.getDependencies(pc);
//		} catch (GCUBEFault e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		System.out.println("Dependencies founded: \n"+xml);
	}

}
