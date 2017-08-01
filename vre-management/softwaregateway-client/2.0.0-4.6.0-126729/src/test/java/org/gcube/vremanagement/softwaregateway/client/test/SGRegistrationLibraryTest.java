package org.gcube.vremanagement.softwaregateway.client.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.softwaregateway.client.SGRegistrationLibrary;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.LocationCoordinates;
import org.gcube.vremanagement.softwaregateway.client.proxies.Proxies;
import org.junit.Before;
import org.junit.Test;

public class SGRegistrationLibraryTest {

	public static SGRegistrationLibrary library=null;
	public static String sgHost="node4.d.d4science.research-infrastructures.eu";
	public static String scope="/gcube/devsec";
	public static ArrayList<String> ghnList;
	public static LocationCoordinates coord;
	
	@Before
	public void initialize(){
		ScopeProvider.instance.set("/gcube");
		library=Proxies.registrationService()/*.at("node4.d.d4science.research-infrastructures.eu", 8080).withTimeout(1, TimeUnit.MINUTES)*/.build();
		coord=new LocationCoordinates();
		coord.sc="TestProfile";
		coord.sn="TestSGClient";
		coord.sv="1.0.0";
		coord.pn="TestPackageName";
		coord.pv="1.0.0";
	}

	@Test
	public void registerTest(){
		try {
			InputStream is=SGRegistrationLibraryTest.class.getResourceAsStream("/profile.xml");
			String profile=getStringFromInputStream(is);
			String result=library.register(profile);
			assertNotNull(result);
			System.out.println("register return: "+result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void unregisterTest() {
		System.out.println("Unregistered test ");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		library.unregister(coord);
		
	}

	// convert InputStream to String
		private static String getStringFromInputStream(InputStream is) {
	 
			BufferedReader br = null;
			StringBuilder sb = new StringBuilder();
	 
			String line;
			try {
	 
				br = new BufferedReader(new InputStreamReader(is));
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
	 
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
	 
			return sb.toString();
	 
		}
	
}
