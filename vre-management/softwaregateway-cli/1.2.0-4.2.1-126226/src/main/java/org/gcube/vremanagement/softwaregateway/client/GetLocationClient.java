package org.gcube.vremanagement.softwaregateway.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.PackageCoordinates;


import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.softwaregateway.client.proxies.Proxies;

public class GetLocationClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for (int i=0; i<args.length;i++) 
			System.out.println("param N." +i + ": " + args[i]);
		
		if(args.length != 7 ){
			System.out.println("Usage:");
			System.out.println("\tjava  GetLocationClient   SoftwareGatewayEPR  scope  ServiceName   ServiceClass   ServiceVersion   PackageName   PackageVersion\n\n");
			System.out.println("Example:");
			System.out.println("\tjava  GetLocationClient   http://rcirillo-cnr.isti.cnr.it:8080/wsrf/services/gcube/vremanagement/softwaregateway/Access /gcube/devsec Test3 TestProfile3 1.00.00 TestPackage3 1.00.00  \n\n");
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
		String packageName=args[5];
		String packageVersion=args[6];
		String serviceClass=args[2];
		String serviceName=args[3];
		String serviceVersion=args[4];
		PackageCoordinates pc=new PackageCoordinates();
		pc.pn=packageName;
		pc.pv=packageVersion;
		pc.sc=serviceClass;
		pc.sn=serviceName;
		pc.sv=serviceVersion;
		String url=null;
		url=library.getLocation(pc);
		System.out.println("Remote url: \n"+url);
	}

}
