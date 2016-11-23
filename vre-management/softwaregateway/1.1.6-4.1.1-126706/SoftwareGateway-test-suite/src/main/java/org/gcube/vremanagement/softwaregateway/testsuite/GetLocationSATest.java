package org.gcube.vremanagement.softwaregateway.testsuite;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.ISException;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.GCubeCoordinates;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.MavenCoordinates;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.BadCoordinatesException;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.ServiceNotAvaiableFault;
import org.gcube.vremanagement.softwaregateway.impl.is.ISProxy;
import org.gcube.vremanagement.softwaregateway.impl.packages.GCubePackage;
import org.gcube.vremanagement.softwaregateway.impl.porttypes.ServiceContext;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.RepositoryManager;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.RepositoryManagerFactory;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.parsing.XmlParse;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.util.ArchiveManagement;
import org.junit.Before;
import org.junit.Test;

public class GetLocationSATest {
	
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
	 serviceClass="Execution";//"DataAccess";//"VREManagement";
	 serviceName="ResourceRegistry";//"streams";//"SoftwareRepository";
	 serviceVersion="1.0.0";
	 packageName="ResourceRegistry";//"streams";//"org.gcube.execution.MadgikCommons"; //"SoftwareRepository-service";
	 packageVersion="1.2.1";
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
		File sa=null;
		System.out.println("Create a temporary directory: "+ coordinates.getServiceClass()+" in directory: "+"target");
		File tmpTargetDirectory = new File("target", coordinates.getServiceClass());
		try{
			MavenCoordinates mavenC = getSAMavenCoordinates();
			List<MavenCoordinates> mcList=new ArrayList<MavenCoordinates>();
			mcList.add(mavenC);

	// Call to RepositoryManager with maven coordinates to retrieve the url
			RepositoryManagerFactory rmf= new RepositoryManagerFactory();		
			rm= rmf.getRepositoryManager(servers, false);
			url=(String)rm.get(mcList, "tar.gz", "servicearchive");			
			url = rm.getSALocation(tmpTargetDirectory, mcList, coordinates);		
		}catch(Exception e){
			throw new ServiceNotAvaiableFault(e.getMessage());
		}finally{
			if(url==null)
				throw new ServiceNotAvaiableFault("url is null ");
		}
		System.out.println("URL: "+url);
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
	
	/**
	 * query to IS the SA maven coordinates, if not found, try to convert in gCube coordinate follow specific description
	 * @param gcubeC
	 * @return
	 * @throws BadCoordinatesException
	 */
	private MavenCoordinates getSAMavenCoordinates()
			throws BadCoordinatesException {
   // try to get coordinates from IS
		MavenCoordinates mavenC=null;
		System.out.println("getSAMavenCoordinates method: with maven Coordinates: g "+coordinates.getGroupId()+" a "+coordinates.getArtifactId()+" v "+coordinates.getVersion());
		if((coordinates.getGroupId()== null) ||(coordinates.getArtifactId()==null) || (coordinates.getVersion()==null)){
			try{	
				mavenC = is.getSAMavenCoordinates(coordinates);
			}catch(ISException e){
				System.out.println("IS EXCEPTION CATCHED");
			}
	   //if the is return is null, then converts coordinates from gCubeCoordinates to MavenCoordinates		
			if(mavenC==null){
				System.out.println(" no maven coordinates founded in profile. Try to convert from gCube to Maven");
				try{
					mavenC = (MavenCoordinates)coordinates.convert();
				}catch(BadCoordinatesException e){
					throw new BadCoordinatesException(" convert from gcube to maven");
				}
			}else{
				System.out.println("founded maven Coordinates in profile: gid: "+mavenC.getGroupId()+" aid: "+mavenC.getArtifactId()+" v: "+mavenC.getVersion());
			}
		}else{
			System.out.println("maven coordinates already setted ");
			mavenC=new MavenCoordinates(coordinates.getGroupId(), coordinates.getArtifactId(), coordinates.getVersion());
		}
		return mavenC;
	}
	
	
	
/**
 * create a local SA archive with only the package requested,
 * re-tar the SA and put in a local (cachable folder)
 * @param tmpTargetDirectory
 * @param profile
 * @param arcManager
 * @return the URL from which the local SA can be downloaded
 */
	private String getLocalSAURL(File tar, File profile, ArchiveManagement arcManager) {		
	//get http base dir	
		File urlDirectory = new File(ServiceContext.getContext().getHttpServerBasePath().getAbsolutePath());
		if(!urlDirectory.exists())
			urlDirectory.mkdir();
		String hostName = GHNContext.getContext().getHostname();
		StringBuilder sb = new StringBuilder();
	// build url	
		sb.append("http://").append(hostName).append(":"+ServiceContext.getContext().getHttpServerPort()).append("/" + ServiceContext.getContext().getMavenRelativeDir() +"/");
		System.out.println(" local url for sa created: "+sb.toString());
		return sb.toString();
	}

	/**True if the profile contains only the package corresponded to the mc input parameter
	 * 
	 * @param mc maven coordinates
	 * @return
	 * @throws ServiceNotAvaiableFault
	 */
	private boolean verifyPackageNumber(MavenCoordinates mc) throws ServiceNotAvaiableFault {
		boolean onePackage=false;
//		List<GCubePackage> listCoord=getPackages();
//		if(listCoord==null)
//			throw new ServiceNotAvaiableFault();
//		else if(listCoord.size() == 1){
//			GCubePackage mCoord=listCoord.get(0);
//			if((mc.getArtifactId().equals(mCoord.getCoordinates().getArtifactId()) && (mc.getGroupId().equals(mCoord.getCoordinates().getGroupId())))){
//				onePackage=true;
//			}
//		}
		return onePackage;
	}
	
}
