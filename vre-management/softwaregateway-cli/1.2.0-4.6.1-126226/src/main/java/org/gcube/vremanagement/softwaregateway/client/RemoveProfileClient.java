package org.gcube.vremanagement.softwaregateway.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.LocationCoordinates;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.softwaregateway.client.proxies.Proxies;

public class RemoveProfileClient {

	
	public static void main(String[] args) throws Exception {
		
		for (int i=0; i<args.length;i++) 
			System.out.println("param N." +i + ": " + args[i]);
		
		if(args.length != 7 ){
			System.out.println("Usage:");
			System.out.println("\tjava  RemoveProfileClient   SoftwareGatewayEPR  scope  ServiceName   ServiceClass   ServiceVersion   PackageName   PackageVersion\n\n");
			System.out.println("Example:");
			System.out.println("\tjava  RemoveProfileClient  http://node2.d.d4science.research-infrastructures.eu:9001/wsrf/services/gcube/vremanagement/softwaregateway/Registration  /gcube/devsec sn sc sv pn pv  \n\n");
			return;
		}
		ScopeProvider.instance.set(args[1]);
		SGRegistrationLibrary library;
		try {
			library = Proxies.registrationService().at(new URI(args[0])).withTimeout(1, TimeUnit.MINUTES).build();
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
		LocationCoordinates location=new LocationCoordinates();
		location.sc=serviceClass;
		location.sn=serviceName;
		location.sv=serviceVersion;
		location.pn=packageName;
		location.pv=packageVersion;
		library.unregister(location);
	}
}
