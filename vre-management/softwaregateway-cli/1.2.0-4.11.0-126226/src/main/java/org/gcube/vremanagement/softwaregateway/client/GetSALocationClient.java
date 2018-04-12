package org.gcube.vremanagement.softwaregateway.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.softwaregateway.client.proxies.Proxies;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.SACoordinates;


public class GetSALocationClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for (int i=0; i<args.length;i++) 
			System.out.println("param N." +i + ": " + args[i]);
		
		if(args.length != 7 ){
			System.out.println("Usage:");
			System.out.println("\tjava  GetSALocationClient   SoftwareGatewayEPR  scope  ServiceName   ServiceClass   ServiceVersion   PackageName   PackageVersion\n\n");
			System.out.println("Example:");
			System.out.println("\tjava  GetSALocationClient  http://node2.d.d4science.research-infrastructures.eu:9001/wsrf/services/gcube/vremanagement/softwaregateway/Access  /gcube/devsec sn sc sv pn pv  \n\n");
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
//
//		try {
//			stub = new AccessServiceAddressingLocator().getAccessPortTypePort(endpoint);
//		} catch (ServiceException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return;
//		}
//		try {
//			stub=GCUBERemotePortTypeContext.getProxy(stub,GCUBEScope.getScope(args[1]));
//		} catch (MalformedScopeExpressionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return;
//		}
		
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
		
		String serviceClass=args[2];
		String serviceName=args[3];
		String serviceVersion=args[4];
		String packageName=args[5];
		String packageVersion=args[6];

		SACoordinates saCoordinates=new SACoordinates();
		saCoordinates.pn=packageName;
		saCoordinates.pv=packageVersion;
		saCoordinates.sc=serviceClass;
		saCoordinates.sn=serviceName;
		saCoordinates.sv=serviceVersion;
		String url=null;
		url=library.getSALocation(saCoordinates);
//		try {
//			url = stub.getSALocation(pc);
//		} catch (GCUBEFault e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		System.out.println("Remote url: \n"+url);

	}

}
