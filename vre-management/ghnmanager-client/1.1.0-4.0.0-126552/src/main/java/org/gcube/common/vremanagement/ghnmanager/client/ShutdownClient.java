package org.gcube.common.vremanagement.ghnmanager.client;


import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.vremanagement.ghnmanager.client.fws.Types.ShutdownOptions;
import org.gcube.common.vremanagement.ghnmanager.client.proxies.Proxies;
import org.gcube.soa3.connector.common.security.CredentialManager;
import org.gcube.soa3.connector.common.security.impl.X509TLSCredentials;

/**
 * 
 * @author andrea
 *
 */
public class ShutdownClient {
	
	static GHNManagerLibrary library = null;
	
	public static void main (String []args){
		
		String 	hostCert = null,
				hostKey = null;
		
		if (args.length < 2){
			System.out.println("Incorrect number of params");
			return;
			}
		else if (args.length == 4)
		{
			hostCert = args [2];
			hostKey = args [3];
			System.out.println("Using cert "+hostCert);
			System.out.println("Using key "+hostKey);
		}
		else System.out.println("Using default credentials");
		

		
		ScopeProvider.instance.set(args[0]);
		
		X509TLSCredentials cred = new X509TLSCredentials(hostCert,hostKey,null,null,null);
		cred.prepareCredentials();
		
		CredentialManager.instance.set(cred);

		library = Proxies.service().at(URI.create(args[1])).withTimeout(30, TimeUnit.SECONDS).build();


		ShutdownOptions options = new ShutdownOptions();
		options.setRestart(false);
		options.setClean(false);	
		try {
			library.shutdown(options);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
