package org.gcube.vremanagement.softwaregateway.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.softwaregateway.client.proxies.Proxies;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.LocationItem;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.ServiceCoordinates;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.getPackagesResponse;

public class GetPackagesClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for (int i=0; i<args.length;i++) 
			System.out.println("param N." +i + ": " + args[i]);
		
		if(args.length != 5 ){
			System.out.println("Usage:");
			System.out.println("\tjava  GetPackagesClient   SoftwareGatewayEPR  scope  ServiceName   ServiceClass   ServiceVersion \n\n");
			System.out.println("Example:");
			System.out.println("\tjava  GetPackagesClient  http://node2.d.d4science.research-infrastructures.eu:9001/wsrf/services/gcube/vremanagement/softwaregateway/Registration /gcube/devsec Test3 TestProfile3 1.00.00 \n\n");
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
//		AccessPortType stub;
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
		String serviceClass=args[2];
		String serviceName=args[3];
		String serviceVersion=args[4];
		ServiceCoordinates sc=new ServiceCoordinates();
		sc.sc=serviceClass;
		sc.sn=serviceName;
		sc.sv=serviceVersion;
		
		
		getPackagesResponse response=null;
		response=library.getPackages(sc);
		
//		try {
//			response = stub.getPackages(sc);
//		} catch (GCUBEFault e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		if(response != null){
			List<LocationItem> items=response.items;
			if(items != null){
				System.out.println("Packages found: \n"+items.size());
				for (LocationItem item : items){
					System.out.println("  package name: "+item.pn+"  version: "+item.pv);
				}
			}
		}
	}

}
