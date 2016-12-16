package org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.BadCoordinatesException;
import org.gcube.vremanagement.softwaregateway.impl.porttypes.ServiceContext;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.parsing.XmlParse;
import org.xml.sax.SAXException;

public class NexusRestConnector {
	
	private HttpClient client;
	private static final String TEMPORARY_POM_FILE_NAME="pom.xml";
	protected final GCUBELog logger = new GCUBELog(NexusRestConnector.class);
	
	public NexusRestConnector(){
		logger.debug("try to istantiate httpClient");
		getClient();
		logger.debug("Http Client instantiated ");
	}
	
	private HttpClient getClient(){
		if(client == null)
			client = HttpClients.createSystem();
		return client;
	}
	
	
	public void searchAllRepoMavenInfo(String baseUrl){
		client = getClient();
		String url=baseUrl+"/service/local/all_repositories";
		HttpGet method=new HttpGet(url);
		connect(method);  
		
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
	public String searchArtifact(String baseUrl, String groupName, String artifact, String extension, String ver, boolean pom) throws MalformedURLException{
		logger.trace("searchArtifact method from "+baseUrl+ " with coordinates gId: "+groupName+" aId: "+artifact+" version: "+ver+" and extension: "+extension);
		client = getClient();
		String url=baseUrl+"/service/local/data_index/repo_groups/mycompany/content?g="+groupName.trim()+"&a="+artifact.trim();
		logger.debug("GETMETHOD url: "+url);
		HttpGet method=new HttpGet(url);
    	String xml=connect(method);  
    	XmlParse p=new XmlParse();
    	String artifactUrl=null;
    	try {
    		artifactUrl=p.getURlFromSearch(xml, groupName, artifact, extension, ver, pom, "servicearchive");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		if(artifactUrl != null)
			return artifactUrl;
		else
			return null;
		
	}

	/**
	 * return the server response (XML format)
	 * @param client
	 * @param method
	 */
	private String connect(HttpGet method) {
		String result=null;
		try {
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
	 * Download a maven object in the path specified in the input parameter
	 * @param path path where the maven object is downloaded
	 * @param method instance of a get method
	 */
	private void connectAndDownload(HttpGet method, String path) {
		try {
		      // Execute the method.
			  HttpResponse response = getClient().execute(method);
		      int statusCode = response.getStatusLine().getStatusCode();
		      if (statusCode != HttpStatus.SC_OK) {
		        System.err.println("Method failed: " + statusCode);
		      }
		      // Read the response body.
		      byte[] responseBody = EntityUtils.toByteArray(response.getEntity());
		      OutputStream os=new FileOutputStream(path);
		      os.write(responseBody);
		} catch (IOException e) {
		      logger.error("Fatal transport error: " + e.getMessage());
	      e.printStackTrace();
		} finally {
		      // Release the connection.
			method.releaseConnection();
		}
	}
	
	/**
	 * Download a maven object in the path specified in input
	 * @param baseUrl: base url of a maven repository 
	 * @param path: path where the maven object is downloaded
	 */
	void getArtifact(String baseUrl, String path){

		String url=baseUrl;
		HttpGet method=new HttpGet(url);
		connectAndDownload(method, path);
	}

	/**
	 * Return a byte array of the maven object downloaded
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public byte[] getAsByteArray(URL url) throws IOException {
		logger.debug("getAsByteArray method ");
	    URLConnection connection = url.openConnection();
	    // Since you get a URLConnection, use it to get the InputStream
	    InputStream in = connection.getInputStream();
	    // Now that the InputStream is open, get the content length
	    int contentLength = connection.getContentLength();

	    // To avoid having to resize the array over and over and over as
	    // bytes are written to the array, provide an accurate estimate of
	    // the ultimate size of the byte array
	    ByteArrayOutputStream tmpOut;
	    if (contentLength != -1) {
	        tmpOut = new ByteArrayOutputStream(contentLength);
	    } else {
	        tmpOut = new ByteArrayOutputStream(16384); // Pick some appropriate size
	    }

	    byte[] buf = new byte[512];
	    while (true) {
	        int len = in.read(buf);
	        if (len == -1) {
	            break;
	        }
	        tmpOut.write(buf, 0, len);
	    }
	    in.close();
	    tmpOut.close(); // No effect, but good to do anyway to keep the metaphor alive

	    byte[] array = tmpOut.toByteArray();

	    //Lines below used to test if file is corrupt
	    //FileOutputStream fos = new FileOutputStream("C:\\abc.pdf");
	    //fos.write(array);
	    //fos.close();

	    return array;
	}
	
	/**
	 * Extracts dependencies from a list of maven repositories
	 * @param pomByte
	 * @param repositorieServers
	 * @param is 
	 * @return
	 * @throws BadCoordinatesException
	 * @throws Exception
	 */
	public String extractDepsFromPomByMavenEmb(byte[] pomByte, String [] repositorieServers) throws BadCoordinatesException, Exception {
		logger.debug("extractDepsFromPomByMavenEmb method");
    	String cfgDir= (String)ServiceContext.getContext().getProperty("configDir", false)+File.separator+System.currentTimeMillis()+"_"+Thread.currentThread().getId();
    	File dir=new File(cfgDir);
    	if(!dir.exists())
    		dir.mkdir();
		File pomFile=byteToFile(cfgDir, TEMPORARY_POM_FILE_NAME, pomByte);
		String dependenciesList=null;
		for(int i = 0; i<repositorieServers.length;i++){
			/* Scope can be null. If null no scope filter is used during resolution */
			logger.debug("Dependencies solver from server: "+repositorieServers[i]);
			dependenciesList=new MavenDependenciesSolver().dependenciesSolverFromPom(pomFile, dir, repositorieServers[i], "compile");
			if(dependenciesList!=null)
				break;

		}
		if(dir.exists())
			FileUtilsExtended.recursiveDeleteDirectory(dir);
		return dependenciesList;
	}

	/**
	 * Trasforms a byte array in a file object
	 * @param fileName
	 * @param data
	 * @return file object
	 * @throws IOException
	 */
    public File byteToFile(String cfgDir, String fileName, byte[] data){
    	logger.debug(" bytoToFile conversion ");
    	File file=null;
    	logger.debug("configDir: "+cfgDir);
    	file=new File(cfgDir, fileName);
		file.deleteOnExit();
//    	File file=new File(fileName);
    	FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	try {
			fos.write(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	try {
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	logger.info("newFile created: "+file.getAbsolutePath());
    	return file;
    }
	
}
