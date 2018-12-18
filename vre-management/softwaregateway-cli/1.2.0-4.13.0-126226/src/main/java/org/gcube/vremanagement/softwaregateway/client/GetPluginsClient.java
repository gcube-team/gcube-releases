package org.gcube.vremanagement.softwaregateway.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.LocationItem;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.PluginCoordinates;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.getPluginResponse;
import org.gcube.vremanagement.softwaregateway.client.proxies.Proxies;

public class GetPluginsClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for (int i=0; i<args.length;i++) 
			System.out.println("param N." +i + ": " + args[i]);
		
		if(args.length != 7 ){
			System.out.println("Usage:");
			System.out.println("\tjava  GetPluginsClient   SoftwareGatewayEPR  scope  ServiceName   ServiceClass   ServiceVersion PackageName PackageVersion\n\n");
			System.out.println("Example:");
			System.out.println("\tjava  GetPluginsClient  http://node2.d.d4science.research-infrastructures.eu:9001/wsrf/services/gcube/vremanagement/softwaregateway/Access /gcube/devsec sn sc sv pn pv\n\n");
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
		String packageName=args[5];
		String packageVersion=args[6];
		PluginCoordinates pc=new PluginCoordinates();
		pc.sc=serviceClass;
		pc.sn=serviceName;
		pc.sv=serviceVersion;
		pc.pn=packageName;
		pc.pv=packageVersion;
		getPluginResponse response=null;
		response=library.getPlugins(pc);
//		try {
//			response = stub.getPlugins(pc);
//		} catch (GCUBEFault e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		if(response != null){
			List<LocationItem>  items=response.items;
			if(items != null){
				System.out.println("Packages found: \n"+items.size());
				for (LocationItem item : items){
					System.out.println("    package name: "+item.pn+"  version: "+item.pv+ " in service: sc: "+item.sc+ " sn: "+item.sn+" sv: "+item.sv );
				}
			}
		}
	}

}
