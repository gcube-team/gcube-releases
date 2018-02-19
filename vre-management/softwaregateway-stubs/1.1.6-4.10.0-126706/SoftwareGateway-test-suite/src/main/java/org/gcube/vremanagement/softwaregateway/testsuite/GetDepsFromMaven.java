package org.gcube.vremanagement.softwaregateway.testsuite;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.webdav.lib.properties.GetContentLengthProperty;
import org.gcube.common.core.informationsystem.ISException;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.GCubeCoordinates;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.MavenCoordinates;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.BadCoordinatesException;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.ServiceNotAvaiableFault;
import org.gcube.vremanagement.softwaregateway.impl.is.ISProxy;
import org.gcube.vremanagement.softwaregateway.impl.porttypes.ServiceContext;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.RepositoryManager;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.RepositoryManagerFactory;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.parsing.XmlParse;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class GetDepsFromMaven {

	
	
	String serviceName;
	String serviceClass;
	String serviceVersion;
	String packageName;
	String packageVersion;
	List resolved;
	List missing;
	RepositoryManager rm;
	ISProxy is;
	GCUBEScope scope;
	MavenCoordinates mc;
	HttpClient client;
	String baseUrl;
	  String groupName;
	  String artifact; 
	  String extension; 
	  String ver; 
	  boolean pom=true; 
	  String classifier;
	  
	@Before
	public void initialize(){
		 serviceClass="org.gcube";//"VREManagement";
		 serviceName="tree-manager-sample-plg";//"SoftwareRepository";
		 serviceVersion="1.0.0";
		 packageName="tree-manager-sample-plg";//"org.gcube.execution.MadgikCommons"; //"SoftwareRepository-service";
		 packageVersion="1.0.0-SNAPSHOT";
		  String gCubeScope="/gcube";
		  scope =ServiceContext.getContext().getScope();
		  baseUrl=" http://maven.research-infrastructures.eu/nexus/service/local/data_index/repo_groups/mycompany/content?";
		  groupName="org.gcube.data.access";
		  artifact="streams"; 
		  extension="jar"; 
		  ver="1.0.0-SNAPSHOT"; 
		  pom=false; 
		  classifier=null;
	}

	
	public void searchArtifact(){
		System.out.println("searchArtifact method from "+baseUrl+ " with coordinates gId: "+groupName+" aId: "+artifact+" version: "+ver+" and extension: "+extension+" and classifier "+classifier);
//		String result=cache.searchArtifact(baseUrl, groupName, artifact, extension, ver, pom);
//		if(result != null)
//			return result;
		client = getClient();
		StringBuffer param=new StringBuffer();
		if(ver!=null)
			param.append("g="+groupName.trim()+"&a="+artifact.trim()+"&v="+ver);
		else{
			param.append("g="+groupName.trim()+"&a="+artifact.trim()+"&v=LATEST");
		}
		if(classifier!=null){
			param.append("&c="+classifier);
		}
//		String url=baseUrl+"/service/local/data_index/repositories/releases/content?g="+groupName;
//		String url=baseUrl+"/service/local/data_index/repo_groups/mycompany/content?g="+groupName;
//		String localUrl=baseUrl+"/service/local/data_index/repo_groups/mycompany/content?g="+groupName.trim()+"&a="+artifact.trim();
		String localUrl=baseUrl+"/service/local/data_index/repo_groups/mycompany/content?"+param.toString();
		String centralUrl=null;
//		if(ver!=null){
////			centralUrl=baseUrl+"/service/local/artifact/maven/redirect?r=central&g="+groupName.trim()+"&a="+artifact.trim()+"&v="+ver;
//			centralUrl=baseUrl+"/service/local/artifact/maven/redirect?r=central&g="+groupName.trim()+"&a="+artifact.trim()+"&v="+ver;
//		}else{
////			centralUrl=baseUrl+"/service/local/artifact/maven/redirect?r=central&g="+groupName.trim()+"&a="+artifact.trim()+"&v=LATEST";
//			centralUrl=baseUrl+"/service/local/artifact/maven/redirect?r=central&g="+groupName.trim()+"&a="+artifact.trim()+"&v=LATEST";
//		}
		centralUrl=baseUrl+"/service/local/artifact/maven/redirect?r=central&"+param.toString();
		System.out.println("getMethod with url: "+localUrl);
		GetMethod method=new GetMethod(localUrl);
		System.out.println("connect to server ");
    	String xml=connect(method);  
    	System.out.println(" server response: "+xml);
    	XmlParse p=new XmlParse();
    	String artifactUrl=null;
    	try {
    		artifactUrl=p.getURlFromSearch(xml, groupName, artifact, extension, ver, pom, classifier);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		if(artifactUrl != null){
//			String cacheCoordinates=groupName+cache.CACHE_STRING_SEPARATOR+artifact+cache.CACHE_STRING_SEPARATOR+ver+cache.CACHE_STRING_SEPARATOR+extension;
			System.out.println(" artifactUrl: "+artifactUrl);
		}else{
	// check in maven central
			method=new GetMethod(centralUrl);
			try {
				int statusCode = getClient().executeMethod(method);
				if(statusCode == HttpStatus.SC_OK){
					System.out.println("found artifact in maven Central, status code returned: "+statusCode);	
					System.out.println(" artifactUrl: "+centralUrl);
				}
					
			} catch (HttpException e) {
				System.out.println(" HttpException in maven Central "+e.getMessage());
			} catch (IOException e) {
				System.out.println(" IOException in maven Central "+e.getMessage());
			}
			
		}
	}
	
	
	
	
	public void getDependencies() throws ServiceNotAvaiableFault{
		String result=null;
		try{
			result=findDeps(serviceName, serviceClass, serviceVersion, packageName,	packageVersion);
		}catch(Exception e){
			throw new ServiceNotAvaiableFault();
		}
		System.out.println("OUT: "+result);	
//		AnswerBuild answer=new AnswerBuild();
//		String xml = answer.constructAnswer(resolved, missing, null, null);
//		return xml;
	}

	private HttpClient getClient(){
		if(client == null)
			client = new HttpClient();
		return client;
	}

	
	private String connect(GetMethod method) {
		
		String result=null;
		try {
		      // Execute the method.
		      int statusCode = getClient().executeMethod(method);

		      if (statusCode != HttpStatus.SC_OK) {
		        System.out.println("Method failed: " + method.getStatusCode());
		      }

		      // Read the response body.
		      byte[] responseBody = method.getResponseBody();

		      // Deal with the response.
		      // Use caution: ensure correct character encoding and is not binary data
		      result=new String(responseBody);
//		      System.out.println(result);

	    } catch (HttpException e) {
	      System.out.println("Fatal protocol violation: " + e.getMessage());
	      e.printStackTrace();
	    } catch (IOException e1) {
	      System.out.println("Fatal transport error: " + e1.getMessage());
	      e1.printStackTrace();
	    } catch (Exception e2){
	    	e2.printStackTrace();
	    }finally {
	      // Release the connection.
		  method.releaseConnection();
	    }
	    return result;
	}
	
	
	/**
	 * @param serviceName
	 * @param serviceClass
	 * @param serviceVersion
	 * @param packageName
	 * @param packageVersion
	 * @throws Exception 
	 * @throws BadCoordinatesException
	 */
	@Test
	private String findDeps(String serviceName, String serviceClass,
			String serviceVersion, String packageName, String packageVersion)
			throws Exception {
		List<Package> list=null;
		String url=null;
		// build object gCube Coordinates
		GCubeCoordinates gcubeC=null;
		try{
			gcubeC= new GCubeCoordinates(serviceName, serviceClass, serviceVersion, packageName, packageVersion);
		}catch(BadCoordinatesException e){
			throw new BadCoordinatesException(" gcube coordinates");
		}
		// retrieve maven coordinates
		MavenCoordinates mavenC = getMavenCoordinates(gcubeC);
		// Call to RepositoryManager with maven coordinates
		RepositoryManagerFactory rmf= new RepositoryManagerFactory();
		try{
			rm= rmf.getRepositoryManager(is.getMavenServerList(scope), false);
			url=rm.get(mavenC, "pom", null);			
		}catch(Exception e){
			throw new ServiceNotAvaiableFault(e.getMessage());
		}
		if(url !=null)
			return rm.extractDepsFromMavenEmb(url.toString());
		return null;
//		//extract Dependencies
//		List<MavenCoordinates> mavenCoordList;
//		try {
//			mavenCoordList = rm.extractDependencies(url.toString());
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			throw new ServiceNotAvaiableFault("pom problem ");
//		}
//		// add Coordinates List extracted to relative List of Dependencies	
//		for(Iterator it=mavenCoordList.iterator(); it.hasNext();){
//			MavenCoordinates mCoord=(MavenCoordinates)it.next();
//			try{
//				url=(URL)rm.get(mavenC, "jar");
//			}catch(Exception e){
//				System.out.println("Problem to retrieve coordinates gId= "+mavenC.getGroupId()+" aId: "+mavenC.getArtifactId());
//				url=null;
//			}
//			if(url!=null){
//				if(resolved==null)
//					resolved=new ArrayList();
// // check if the IS contains the mavenC coordinates 
//	// if yes, extract coordinates gCube from IS
//	// if no converts the mavenCoordinates in gCube Coordinates			
//				gcubeC=getGCubeCoordinates(mavenC);
//				resolved.add(gcubeC);
//				findDeps(gcubeC.getServiceName(), gcubeC.getServiceClass(), gcubeC.getServiceVersion(), gcubeC.getPackageName(), gcubeC.getPackageVersion());				
//			}else{
//				if(missing==null)
//					missing=new ArrayList();
//				gcubeC=getGCubeCoordinates(mavenC);
//				missing.add(gcubeC);
//				findDeps(gcubeC.getServiceName(), gcubeC.getServiceClass(), gcubeC.getServiceVersion(), gcubeC.getPackageName(), gcubeC.getPackageVersion());
//			}
//		}
	}

	/**
	 * @param gcubeC
	 * @return
	 * @throws BadCoordinatesException
	 */
	private MavenCoordinates getMavenCoordinates(GCubeCoordinates gcubeC)
			throws BadCoordinatesException {
   // try to get coordinates from IS
		MavenCoordinates mavenC=null;
		try{	
			mavenC = is.getMavenCoordinates(gcubeC);
		}catch(ISException e){
//			System.out.println("IS EXCEPTION CATCHED");
		}
   //if the is return is null, then converts coordinates from gCubeCoordinates to MavenCoordinates		
		if(mavenC==null){
			try{
				mavenC = (MavenCoordinates)gcubeC.convert();
			}catch(BadCoordinatesException e){
				throw new BadCoordinatesException(" convert from gcube to maven");
			}
		}
   // else extract from service profile the list of resolved Dependencies
		else{
			
		}
		return mavenC;
	}
	
	private GCubeCoordinates getGCubeCoordinates(MavenCoordinates mavenC) throws BadCoordinatesException {
		GCubeCoordinates gcubeC=null;
		try{
			gcubeC= is.getGcubeCoordinates(mavenC);
		}catch(ISException e){
//			System.out.println("IS EXCEPTION CATCHED");
		}
		if(gcubeC==null){
			try{
				gcubeC = (GCubeCoordinates)mavenC.convert();
			}catch(BadCoordinatesException e){
				throw new BadCoordinatesException(" convert from maven to gcube "); 
			}
		}
		return gcubeC;
	}

	
	
}
