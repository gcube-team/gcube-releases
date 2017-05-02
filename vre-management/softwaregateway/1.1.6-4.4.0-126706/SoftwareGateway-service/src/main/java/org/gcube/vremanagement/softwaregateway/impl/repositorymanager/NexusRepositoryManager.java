package org.gcube.vremanagement.softwaregateway.impl.repositorymanager;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.Coordinates;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.MavenCoordinates;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.ServiceNotAvaiableFault;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.cache.NexusCache;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven.NexusRestConnector;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.parsing.XmlParse;
import org.xml.sax.SAXException;

public class NexusRepositoryManager extends RepositoryManager {

	public static final String TEMPORARY_SA_FILE_NAME="sa_tar_file.tar.gz";
	protected final GCUBELog logger = new GCUBELog(NexusRepositoryManager.class);
	private HttpClient client;
	public NexusCache cache=null;
	public static NexusRepositoryManager singleton;
	
	
	public NexusRepositoryManager(String[] mavenServerList, boolean cacheEnabled) {
		super(mavenServerList);
		this.cacheEnabled=cacheEnabled;
		if(cacheEnabled)
			cache=NexusCache.getInstance(mavenServerList);
	}

	
	public String get(Object mavenC, String extension, String classifier) throws MalformedURLException, ServiceNotAvaiableFault{
		logger.trace("get method: get maven object with extension "+extension+"");	
		MavenCoordinates mc=(MavenCoordinates)mavenC;
		logger.debug("mavenCoordinates "+mc.getGroupId()+" "+mc.getArtifactId()+" "+mc.getVersion());
		String url=null;
		if(cacheEnabled){
			logger.debug("cache enabled ");
			url=cache.get(mavenC, extension, classifier);
		}
		if(url!=null){
			logger.debug("url found in cache");
			return url;
		}
		logger.debug("and coordinates: g: "+mc.getGroupId()+ " a: "+mc.getArtifactId()+" v: "+mc.getVersion());
		if(servers == null)
			logger.debug(" list of servers is null ");
		else
			logger.debug("list of servers is not null "+servers);
		logger.debug("number of servers founded: "+servers.length);
		for(int i=0; i<servers.length; i++){
			logger.debug(" get method search artifact: "+mc.getArtifactId()+"  with extension: "+extension);
			if(extension.equalsIgnoreCase("pom"))
				url=searchArtifact(servers[i], mc.getGroupId(), mc.getArtifactId(), "jar", mc.getVersion(), true, classifier);
			else
				url=searchArtifact(servers[i], mc.getGroupId(), mc.getArtifactId(), extension, mc.getVersion(), false, classifier);
			if((url !=null && !url.isEmpty()) && (cacheEnabled)){
				String cacheCoordinates=cache.buildMavenCoordinatesCacheInputString(mc, extension, classifier);
				cache.put(cacheCoordinates, url);
				break;
			}
		}
		logger.debug("get Method url returned: "+url);
		if(url==null)
			throw new ServiceNotAvaiableFault("url is null ");
		return url;
	}

	
	/**
	 * Download pom file and extract Dependencies from maven repositories
	 * @param url: url of the pom file
	 */
	@Override
	public String extractDepsFromMavenEmb(String url) throws ServiceNotAvaiableFault{
		logger.trace("extractDepsFromMavenEmb method with url: "+url);
		NexusRestConnector nc=new NexusRestConnector();
		String result=null;
		if(cacheEnabled)
			result=cache.extractDepsFromMavenEmb(url);
		if(result != null)
			return result;
	//recover pom file from url	
		try{
			byte[] pom=nc.getAsByteArray(new URL(url));
			logger.debug("pom converted in byte array");
			logger.debug("extract deps from pom: "+pom+" and first server"+servers[0]);
			result=nc.extractDepsFromPomByMavenEmb(pom, servers);
			if(cacheEnabled)
				cache.put(url, result);
			return result;
		}catch(Exception e){
			logger.error("Failed to get dependencies from the configured Maven Repositories", e);
			throw new ServiceNotAvaiableFault(e.getMessage());
		}
	}
	
	
	/**
	 * Search a maven object (pom, jar, tar.gz) from a maven repository
	 * @param baseUrl base url of a maven repository
	 * @param groupName groupID
	 * @param artifact artifactID
	 * @param extension type of extension: jar, pom, tar.gz
	 * @param ver version of the maven object 
	 * @param pom true if the maven object searched is a pom file
	 * @return the url of the maven object if founded otherwise null
	 * @throws MalformedURLException
	 */
	public String searchArtifact(String baseUrl, String groupName, String artifact, String extension, String ver, boolean pom, String classifier) throws MalformedURLException{
		logger.trace("searchArtifact method from "+baseUrl+ " with coordinates gId: "+groupName+" aId: "+artifact+" version: "+ver+" and extension: "+extension+" and classifier "+classifier);
		client = getClient();
		StringBuffer param=new StringBuffer();
		String artifactId=artifact;
		String classifierId=classifier;
		if(artifact.contains("#")){
			String[] splitting = artifact.split("\\#");
			artifactId =splitting[0];
			classifierId=splitting[1];
		}
		if(ver!=null)
			param.append("g="+groupName.trim()+"&a="+artifactId.trim()+"&v="+ver);
		else{
			param.append("g="+groupName.trim()+"&a="+artifactId.trim()+"&v=LATEST");
		}
		String localUrl=baseUrl+"/service/local/data_index/repo_groups/mycompany/content?"+param.toString();
		String centralUrl=null;
		centralUrl=baseUrl+"/service/local/artifact/maven/redirect?r=central&"+param.toString();
		logger.debug("getMethod with url: "+localUrl);
		HttpGet method=new HttpGet(localUrl);
		logger.debug("connect to server ");
    	String xml=connect(method);  
    	logger.debug(" server response: "+xml);
    	XmlParse p=new XmlParse();
    	String artifactUrl=null;
    	try {
    		artifactUrl=p.getURlFromSearch(xml, groupName, artifactId, extension, ver, pom, classifierId);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		if(artifactUrl != null){
			return artifactUrl;
		}else{
// try to get the url without classifier only if the classifier is servicearchive. In this case i try to get the jar 	
			if(classifier.equalsIgnoreCase("servicearchive")){
				try {
		    		artifactUrl=p.getURlFromSearch(xml, groupName, artifactId, extension, ver, pom, null);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				}
			}
			if(artifactUrl!=null)
				return artifactUrl;
			else{
				// check in maven central
				method=new HttpGet(centralUrl);
				try {
					int statusCode = getClient().execute(method).getStatusLine().getStatusCode();
					if(statusCode == HttpStatus.SC_OK){
						logger.info("found artifact in maven Central, status code returned: "+statusCode);	
						return centralUrl;
					}
						
				} catch (IOException e) {
					logger.error(" IOException in maven Central "+e.getMessage());
				}

			}
			
		}
		return null;	
	}

	
	private HttpClient getClient(){
		if(client == null)
			client = HttpClients.createSystem();
		return client;
	}

	/**
	 * return the server response (XML format)
	 * @param client
	 * @param method
	 */
	private String connect(HttpGet method) {
		logger.debug("Try to connect to the server: connect method");
		String result=null;
		try {
		      // Execute the method.
			  HttpResponse response = getClient().execute(method);
		      int statusCode = response.getStatusLine().getStatusCode();

		      if (statusCode != HttpStatus.SC_OK) {
		        logger.error("Method failed: " + statusCode);
		      }

		      // Read the response body.
		      // Deal with the response.
		      // Use caution: ensure correct character encoding and is not binary data
		      result=EntityUtils.toString(response.getEntity());
//		      System.out.println(result);

	    } catch (IOException e1) {
	      logger.error("Fatal transport error: " + e1.getMessage());
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
 * @param tmpTargetDirectory
 * @param mcList
 * @return
 * @throws MalformedURLException
 * @throws ServiceNotAvaiableFault
 * @throws IOException
 * @throws Exception
 */
		
	@Override
	public String getSALocation(File tmpTargetDirectory, List<MavenCoordinates> mcList,
			Coordinates coordinates) throws MalformedURLException,
			ServiceNotAvaiableFault, IOException, Exception {
		logger.trace("getSALocation method ");
		String url=null;
		if(cacheEnabled)
			url=cache.getSALocation(tmpTargetDirectory, mcList, coordinates);
		if(url != null){
			logger.info("CACHE FETCHED "+url);
			return url;
		}
		logger.debug("not element retrieved in cache ");
		
		url=(String)get(mcList.get(0), "tar.gz", SERVICE_ARCHIVE_IDENTIFIER);	
/*SECTION FOR UPLOAD PROFILE E JETTY PUBLICATION*/
//		File sa;
//	//download archive	
//		sa=downloadSA(tmpTargetDirectory, url);
//	//TEST		
////		sa= new File (tmpTargetDirectory, NexusRepositoryManager.TEMPORARY_SA_FILE_NAME);
//	//ENDTEST		
//		ArchiveManagement arcManager=new ArchiveManagement();
//		arcManager.unTarGz(sa);
//	//retrieve profile	
//		File profile=arcManager.extractFileFromArchive(sa, "profile.xml");
////	if it contains not only the package identify by pn pv 
//		boolean onePackage=false; //verifyPackageNumber(mavenC );
//		if(!onePackage){
//			logger.debug("founded more than one package");
//			profile = new XmlParse().updateProfile(profile, coordinates);
//			arcManager.replaceFilesToTarGz(sa, profile);
//			url=getLocalSAURL(sa, profile, arcManager, coordinates);
//		}
/*END SECTION UPLOAD PROFILE*/		
		if((url != null) && (cacheEnabled)){
	// insert entry in cache
			logger.info("insert entry in cache ");
			String cacheString= cache.buildGCubeCoordinatesCacheInputString(coordinates, "tar.gz", "servicearchive");
			cache.put(cacheString, url);
		}
		return url;
	}

}
