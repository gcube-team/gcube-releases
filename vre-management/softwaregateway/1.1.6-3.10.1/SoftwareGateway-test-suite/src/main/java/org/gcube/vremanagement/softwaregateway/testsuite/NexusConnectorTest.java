package org.gcube.vremanagement.softwaregateway.testsuite;

import java.net.MalformedURLException;

import org.gcube.vremanagement.softwaregateway.impl.coordinates.Coordinates;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.GCubeCoordinates;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.MavenCoordinates;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.BadCoordinatesException;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.util.NexusRestConnector;
import org.junit.Before;
import org.junit.Test;

public class NexusConnectorTest {

	String extension="";
	Object mavenC;
	String [] servers={"http://maven.research-infrastructures.eu/nexus"};
	@Before
	public void initialize() {
		try {
			mavenC=new MavenCoordinates("org.gcube", "tree-manager-stubs", "1.0.0-SNAPSHOT");
		} catch (BadCoordinatesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		servers={"http://maven.research-infrastructures.eu/nexus"};
	}
	
	@Test
	public void get(){
		System.out.println("get method: get maven object with extension "+extension+"");	
		MavenCoordinates mc=(MavenCoordinates)mavenC;
		System.out.println("and coordinates: g: "+mc.getGroupId()+ " a: "+mc.getArtifactId()+" v: "+mc.getVersion());
		System.out.println("number of servers founded: "+servers.length);
		String url=null;
		System.out.println("Nexus Connector start");
		NexusRestConnector nc =new NexusRestConnector();
		System.out.println("Nexus Connector end");
		for(int i=0; i<servers.length; i++){
			System.out.println(" get method search artifact: "+mc.getArtifactId()+"  with extension: "+extension);
			if(extension.equalsIgnoreCase("pom"))
				try {
					url=nc.searchArtifact(servers[i], mc.getGroupId(), mc.getArtifactId(), "jar", mc.getVersion(), true);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			else
				try {
					url=nc.searchArtifact(servers[i], mc.getGroupId(), mc.getArtifactId(), extension, mc.getVersion(), false);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if(url !=null)
				break;
		}
	}
}
