package org.gcube.vremanagement.softwaregateway.testsuite;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.gcube.common.core.informationsystem.ISException;
import org.gcube.common.core.informationsystem.client.ISClient.ISMalformedQueryException;
import org.gcube.common.core.informationsystem.client.ISClient.ISUnsupportedQueryException;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.GCubeCoordinates;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.MavenCoordinates;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.BadCoordinatesException;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.ServiceNotAvaiableFault;
import org.gcube.vremanagement.softwaregateway.impl.is.ISProxy;
import org.gcube.vremanagement.softwaregateway.impl.porttypes.ServiceContext;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.RepositoryManager;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.RepositoryManagerFactory;
import org.junit.Before;
import org.junit.Test;


public class GetLocationTest {

	String serviceName;
	String serviceClass;
	String serviceVersion;
	String packageName;
	String packageVersion;
	ISProxy is;
	GCUBEScope scope;
	RepositoryManager rm;
	GCubeCoordinates coordinates;
	String[] servers={"http://146.48.122.71/nexus"};
	@Before
	public void initialize(){
		 serviceClass="org.gcube";//"VREManagement";
		 serviceName="SoftwareRepository";
		 serviceVersion="1.0.1";
		 packageName="org.gcube.execution.MadgikCommons"; //"SoftwareRepository-service";
		 packageVersion="1.0.1";
		 String gCubeScope="/gcube";
		 scope =ServiceContext.getContext().getScope();
		 is=new ISProxy(scope, false);
		 coordinates=null;
		try{
			coordinates= new GCubeCoordinates(serviceName, serviceClass, serviceVersion, packageName, packageVersion);
		}catch(BadCoordinatesException e){
			
		}
	}
	
	@Test
	public void getLocation() throws ServiceNotAvaiableFault{
		String url=null;
		// build object gCube Coordinates
		// retrieve or convert in maven Coordinates		
		MavenCoordinates mavenC = getMavenCoordinates();
		List<MavenCoordinates> mcList=new ArrayList<MavenCoordinates>();
		mcList.add(mavenC);
// Call to RepositoryManager with maven coordinates
		RepositoryManagerFactory rmf= new RepositoryManagerFactory();		
		try {
			rm= rmf.getRepositoryManager(servers, false);//is.getMavenServerList(scope));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			url=(String)rm.get(mcList, "jar", null);
		}catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		System.out.println("URL RETURNED: "+url);
	}
	
	private MavenCoordinates getMavenCoordinates()
			throws BadCoordinatesException {
   // try to get coordinates from IS
		MavenCoordinates mavenC=null;
//		try{	
//			mavenC = is.getMavenCoordinates(coordinates);
//		}catch(ISException e){
//			System.out.println("IS EXCEPTION CATCHED");
//		}
   //if the is return is null, then converts coordinates from gCubeCoordinates to MavenCoordinates		
		if(mavenC==null){
			try{
				mavenC = (MavenCoordinates)coordinates.convert();
			}catch(BadCoordinatesException e){
				throw new BadCoordinatesException(" convert from gcube to maven");
			}
		}
		return mavenC;
	}
}
