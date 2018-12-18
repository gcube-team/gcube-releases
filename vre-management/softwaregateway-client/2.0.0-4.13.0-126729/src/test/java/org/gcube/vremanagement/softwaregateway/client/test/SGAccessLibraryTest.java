package org.gcube.vremanagement.softwaregateway.client.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.DependenciesCoordinates;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.LocationItem;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.PackageCoordinates;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.PluginCoordinates;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.SACoordinates;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.ServiceCoordinates;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.getPackagesResponse;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.getPluginResponse;
import org.gcube.vremanagement.softwaregateway.client.proxies.Proxies;
import org.gcube.vremanagement.softwaregateway.client.SGAccessLibrary;
import org.junit.Before;
import org.junit.Test;

public class SGAccessLibraryTest {
	
	public static SGAccessLibrary library=null;
	public static String sgHost="node4.d.d4science.research-infrastructures.eu";
	public static String scope="/gcube";
	public static ArrayList<String> ghnList;
	public static PackageCoordinates coord;
	public static SACoordinates saCoord;
	public static ServiceCoordinates serviceCoord;
	public static PluginCoordinates plCoord;
	public static DependenciesCoordinates location;
	
	@Before
	public void initialize(){
		ScopeProvider.instance.set("/gcube/devsec");
		library=Proxies.accessService().build();
		coord=new PackageCoordinates();
		coord.sc="ContentManagement";
		coord.sn="storage-manager-core";
		coord.sv="1.0.0";
		coord.pn="storage-manager-core";
		coord.pv="2.0.2-SNAPSHOT";
		location=new DependenciesCoordinates();
		location.sc="ContentManagement";
		location.sn="storage-manager-core";
		location.sv="1.0.0";
		location.pn="storage-manager-core";
		location.pv="2.0.2-SNAPSHOT";
		saCoord=new SACoordinates();
		saCoord.sc="ContentManagement";
		saCoord.sn="storage-manager-core";
		saCoord.sv="1.0.0";
		saCoord.pn="storage-manager-core";
		saCoord.pv="2.0.2-SNAPSHOT";
		serviceCoord=new ServiceCoordinates();
		serviceCoord.sc="ContentManagement";
		serviceCoord.sn="storage-manager-core";
		serviceCoord.sv="1.0.0";
		plCoord=new PluginCoordinates();
		plCoord.sc="ContentManagement";
		plCoord.sn="storage-manager-core";
		plCoord.sv="1.0.0";
		plCoord.pn="storage-manager-core";
		plCoord.pv="2.0.2-SNAPSHOT";

	}

	@Test
	public void getLocationTest(){
		String result=library.getLocation(coord);
		assertNotNull(result);
		System.out.println("getLocation test. Location found: "+result);
	}
	
	
	@Test
	public void getSALocationTest(){
		String result=library.getSALocation(saCoord);
		assertNotNull(result);
		System.out.println("getSALocation test. Location found: "+result);
	}
	
	@Test
	public void getDependenciesTest(){
		String result=library.getDependencies(location);
		assertNotNull(result);
		System.out.println("getDependencies test. Location found: "+result);
	}
	
	@Test
	public void getPackagesTest(){
		getPackagesResponse result=library.getPackages(serviceCoord);
		assertNotNull(result);
		System.out.println("getPackages test");
		for(LocationItem c : result.items){
			System.out.println("sc: "+c.sc);
			System.out.println("sn: "+c.sn);
			System.out.println("sv: "+c.sv);
			System.out.println("pn: "+c.pn);
			System.out.println("pv: "+c.pv);
		}
		
	}
	
	@Test
	public void getPluginTest(){
		getPluginResponse result=library.getPlugins(plCoord);
		assertNotNull(result);
		System.out.println("getPlugins test");
		if(result.items!=null ){
			for(LocationItem c : result.items){
				System.out.println("sc: "+c.sc);
				System.out.println("sn: "+c.sn);
				System.out.println("sv: "+c.sv);
				System.out.println("pn: "+c.pn);
				System.out.println("pv: "+c.pv);
			}
		}
		
	}
	
}
