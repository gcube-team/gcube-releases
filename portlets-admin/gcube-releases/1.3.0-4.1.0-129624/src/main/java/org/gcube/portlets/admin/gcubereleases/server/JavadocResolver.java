/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.server;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.io.IOUtils;
import org.gcube.portlets.admin.gcubereleases.server.database.EntityManagerFactoryCreator;
import org.gcube.portlets.admin.gcubereleases.server.exception.DatabaseServiceException;
import org.gcube.portlets.admin.gcubereleases.server.persistence.JavadocUriPersistence;
import org.gcube.portlets.admin.gcubereleases.server.persistence.PackagePersistence;
import org.gcube.portlets.admin.gcubereleases.server.util.HttpDownloadUtility;
import org.gcube.portlets.admin.gcubereleases.shared.JavadocHtmlUri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class JavadocResolver.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class JavadocResolver extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -447932120357509793L;
//	public static final String JAVADOC_JAR = "javadocJar";
	public static final String ARTIFACT_ID = "artifactID";
	public static final String RELEASE_ID = "releaseID";
	public static final String GROUP_ID = "groupID";
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(JavadocResolver.class);


	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {

		String releaseID = req.getParameter(RELEASE_ID);
		String artifactID = req.getParameter(ARTIFACT_ID);
		String groupID = req.getParameter(GROUP_ID);

		
		if (releaseID == null || releaseID.equals("")) {
			logger.debug(RELEASE_ID + " not found");
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, RELEASE_ID
					+ " not found or empty");
			return;
		}

		if (artifactID == null || artifactID.equals("")) {
			logger.debug(ARTIFACT_ID + " not found");
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, ARTIFACT_ID
					+ " not found or empty");
			return;
		}

		if (groupID == null || groupID.equals("")) {
			logger.debug(GROUP_ID + " not found");
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, GROUP_ID
					+ " not found or empty");
			return;
		}
		
//		if(!javadocJar.startsWith("http://"))
//			javadocJar = "http://"+javadocJar;

		List<JavadocHtmlUri> rows = null;
		EntityManagerFactory factory = getDBFactory();
		
		String publicJavadocUri = "";
		
		try{
			
			if(factory==null)
				throw new DatabaseServiceException("Factory is null");
			
			try {
				rows = fetchJavodocHtmlUriFromDatabase(factory, releaseID, artifactID);
			}catch (DatabaseServiceException e) {
				//silent
			}

			logger.info(ARTIFACT_ID+": "+artifactID);
			logger.info(RELEASE_ID+": "+releaseID);
			logger.info(GROUP_ID+": "+groupID);

			
			if (rows ==null || rows.size() == 0) {
				
				logger.info("JavadocHtmlUri not found into DB");
			
				try{
					List<org.gcube.portlets.admin.gcubereleases.shared.Package> pcks = fetchPackageFromDatabase(factory, releaseID, artifactID, groupID);
					
					if(pcks!=null && pcks.size()>0){
						
						String javadocJar = pcks.get(0).getJavadoc();
						logger.info("Javadoc: "+javadocJar);

						publicJavadocUri = returnJavadocHtmlFolder(req, resp, releaseID, artifactID, javadocJar);
						
						try{
							logger.info("Stroring data into into DB..");
							JavadocUriPersistence jp = new JavadocUriPersistence(factory);
							JavadocHtmlUri jd = new JavadocHtmlUri(javadocJar, artifactID, releaseID, groupID, publicJavadocUri);
							jp.insert(jd);
							logger.info("Stored "+jd);
							
//							jp.getEntityManagerFactory().close();
						}catch(Exception e){
							logger.error("Db exception error: ", e);
						}finally{
//							closeDBFactory(factory);
						}
						
					}else{
//						closeDBFactory(factory);
						sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Error on reconvering Javadoc with parameters releaseID: "+releaseID +", artifactID: "+artifactID);
					}
				} catch (Exception e) {
//					closeDBFactory(factory);
					logger.error("Exception, sending error: ", e);
					sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Error on reconvering Javadoc with parameters releaseID: "+releaseID +", artifactID: "+artifactID);
				}
				
			} else {
				logger.info("JavadocHtmlUri found");
				publicJavadocUri = rows.get(0).getJavadocHtmlUri();
//				closeDBFactory(factory);
			}	

		} catch (Exception e) {
			logger.error("sending http error -> Error on reconvering Javadoc, caused: ", e);
			sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error on reconvering Javadoc");
		} 
	
		String serverURL = getRequestURL(req);
		String redirectTo = serverURL + "/"+publicJavadocUri;
		urlRedirect(req, resp, redirectTo);
	}
	
	/**
	 * Gets the DB factory.
	 *
	 * @return the DB factory
	 */
	private EntityManagerFactory getDBFactory(){
		return EntityManagerFactoryCreator.getEntityManagerFactory();
	}
	
	/*
	private void closeDBFactory(EntityManagerFactory factory){
		
		try{
			if(factory.isOpen()){
				factory.close();
				logger.info("Db Factory closed correctly");
			}
		}catch(Exception e){
			logger.error("An error occurred when closing the factory ", e);
		}
	}*/
	/**
	 * Fetch package from database.
	 *
	 * @param factory the factory
	 * @param releaseID the release id
	 * @param artifactID the artifact id
	 * @param groupID the group id
	 * @return the list
	 * @throws DatabaseServiceException the database service exception
	 */
	private List<org.gcube.portlets.admin.gcubereleases.shared.Package> fetchPackageFromDatabase(EntityManagerFactory factory, String releaseID, String artifactID, String groupID) throws DatabaseServiceException {

		try {
			PackagePersistence jp = new PackagePersistence(factory);
			Map<String, String> andFilterMap = new HashMap<String, String>();
			andFilterMap.put(org.gcube.portlets.admin.gcubereleases.shared.Package.RELEASE_ID_REF, releaseID);
			andFilterMap.put(org.gcube.portlets.admin.gcubereleases.shared.Package.GROUPID, groupID);
			andFilterMap.put(org.gcube.portlets.admin.gcubereleases.shared.Package.ARTIFACTID, artifactID);
			return jp.getRowsFiltered(andFilterMap);
			
		} catch (DatabaseServiceException e) {
			logger.error("fetchFromDatabase DatabaseServiceException: ", e);
			throw new DatabaseServiceException("DatabaseServiceException error");
		} catch (Exception e) {
			logger.error("fetchFromDatabase error: ", e);
			throw new DatabaseServiceException("fetchFromDatabase error");
		}
	}
	
	/**
	 * Fetch javodoc html uri from database.
	 *
	 * @param factory the factory
	 * @param releaseID the release id
	 * @param artifactID the artifact id
	 * @return the list
	 * @throws DatabaseServiceException the database service exception
	 */
	private List<JavadocHtmlUri> fetchJavodocHtmlUriFromDatabase(EntityManagerFactory factory, String releaseID, String artifactID) throws DatabaseServiceException {

		try {

			JavadocUriPersistence jp = new JavadocUriPersistence(factory);
			Map<String, String> andFilterMap = new HashMap<String, String>();
			andFilterMap.put(JavadocHtmlUri.RELEASE_ID, releaseID);
//			andFilterMap.put(JavadocHtmlUri.JAVADOC_JAR, javadocJar);
			andFilterMap.put(JavadocHtmlUri.ARTIFACT_ID, artifactID);
			return jp.getRowsFiltered(andFilterMap);
			
		} catch (DatabaseServiceException e) {
			logger.error("fetchFromDatabase DatabaseServiceException: ", e);
			throw new DatabaseServiceException("DatabaseServiceException error");
		} catch (Exception e) {
			logger.error("fetchFromDatabase error: ", e);
			throw new DatabaseServiceException("fetchFromDatabase error");
		}
	}
	
	/**
	 * Gets the request url.
	 *
	 * @param req the req
	 * @return the request url
	 */
	public static String getRequestURL(HttpServletRequest req) {

	    String scheme = req.getScheme();             // http
	    String serverName = req.getServerName();     // hostname.com
	    int serverPort = req.getServerPort();        // 80
	    String contextPath = req.getContextPath();   // /mywebapp
//	    String servletPath = req.getServletPath();   // /servlet/MyServlet
//	    String pathInfo = req.getPathInfo();         // /a/b;c=123
//	    String queryString = req.getQueryString();          // d=789

	    // Reconstruct original requesting URL
	    StringBuffer url =  new StringBuffer();
	    url.append(scheme).append("://").append(serverName);

	    if ((serverPort != 80) && (serverPort != 443)) {
	        url.append(":").append(serverPort);
	    }

	    logger.trace("server: "+url);
	    logger.trace("omitted contextPath: "+contextPath);
//	    logger.trace("servletPath: "+servletPath);
//	    url.append(contextPath).append(servletPath);
	    
//	    if (pathInfo != null) {
//	        url.append(pathInfo);
//	    }
//	    if (queryString != null) {
//	        url.append("?").append(queryString);
//	    }
	    return url.toString();
	}

	/**
	 * Return javadoc html folder.
	 *
	 * @param req the req
	 * @param resp the resp
	 * @param releaseID the release id
	 * @param artifactID the artifact id
	 * @param javadocJar the javadoc jar
	 * @return the string
	 * @throws Exception the exception
	 */
	private String returnJavadocHtmlFolder(HttpServletRequest req, HttpServletResponse resp, String releaseID, String artifactID, String javadocJar) throws Exception {
		
		try {

			logger.info("Requesting jar..");
			File file = HttpDownloadUtility.downloadFile(javadocJar);
			String destination = getTomcatFolder()+"webapps/";
			String publicDestination = "javodocrelease/"+releaseID+"/"+artifactID;
			if(file!=null){
				try {
					logger.info("Trying unzip jar..");
				    ZipFile zipFile = new ZipFile(file);
				    String fullPath = destination+publicDestination;
				    logger.info("Extrall all in: "+fullPath);
				    zipFile.extractAll(fullPath);
				    logger.info("Unziped file: "+zipFile.getFile().getAbsolutePath());
//				    return publicDestination + "/index.html";
				    return publicDestination;
				} catch (ZipException e) {
					logger.error("ZipException error: ", e);
					sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							"Error on reconvering data");
				}
				
			}

			// return daoManager.getDaoViewer().getRows();
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw new Exception("Exception error");
		}
		return null;

	}

	/**
	 * Gets the tomcat folder.
	 *
	 * @return the tomcat folder
	 */
    public static String getTomcatFolder(){
    	
    	String catalinaHome = System.getenv("CATALINA_HOME") != null ? System.getenv("CATALINA_HOME") : System.getProperty("catalina.home");
    	
    	if(catalinaHome == null || catalinaHome.isEmpty())
    		logger.error("CATALINA_HOME ENVIROMENT NOT FOUND -  RETURNED / PATH");
    	
    	
    	String tomcatFolder = catalinaHome.endsWith("/") ? catalinaHome : catalinaHome+"/";
    	logger.info("CATALINA_HOME: "+tomcatFolder);
    	return tomcatFolder;
    }

	/**
	 * Send error.
	 *
	 * @param response the response
	 * @param status the status
	 * @param message the message
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected  void sendError(HttpServletResponse response, int status,
			String message) throws IOException {
		// response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		response.setStatus(status);
		logger.info("error message: " + message);
		logger.info("writing response...");
		StringReader sr = new StringReader(message);
		IOUtils.copy(sr, response.getOutputStream());

		// response.getWriter().write(resultMessage.toString());
		logger.info("response writed");
		response.flushBuffer();
	}

	/**
	 * Url redirect.
	 *
	 * @param req the req
	 * @param response the response
	 * @param redirectURI the redirect uri
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected static void urlRedirect(HttpServletRequest req, HttpServletResponse response, String redirectURI) throws IOException {
		try{
			logger.info("Url redirecting to: "+redirectURI);
			response.sendRedirect(response.encodeRedirectURL(redirectURI));
		}catch(Exception e){
			logger.error("Redirect exception to: "+redirectURI, e);
			return;
		}
		return;
	}
	
//	public static void main(String[] args) {
//		String javadocJar = "http://maven.research-infrastructures.eu/nexus/service/local/artifact/maven/redirect?r=gcube-staging&g=org.cotrix&a=cotrix-gcube-portlet&v=0.1.0-3.2.0&e=jar&c=javadoc";
//		String artifactID = "cotrix-gcube-portlet";
//		String releaseID = "org.gcube.3-2-0";
//		String groupID = "";
//		try {
//			
//			List<Package> pcks = fetchPackageFromDatabase(releaseID, artifactID, groupID);
//			logger.trace(pcks.toString());
			
//			List<JavadocHtmlUri> rows = fetchFromDatabase(releaseID, artifactID, javadocJar);
//			String javodocURI;
//			if (rows.size() == 0) {
//				logger.info("JavadocHtmlUri not found into DB");
//				javodocURI = returnJavadocHtmlIndex(null, null, releaseID, artifactID, javadocJar);
//				EntityManagerFactory factory = null;
//				try {
//					
//					factory = getFactory();
//					factory.createEntityManager();
//					logger.info("Stroring data into into DB");
//					JavadocUriPersistence jp = new JavadocUriPersistence(factory);
//					jp.insert(new JavadocHtmlUri(javadocJar, artifactID, releaseID, javodocURI));
//					
//				} catch (Exception e) {
//					logger.error("fetchFromDatabase error: ", e);
//					throw new Exception("fetchFromDatabase error");
//				}finally{
//					if(factory!=null)
//						factory.close();
//				}
//			} else {
//				logger.info("JavadocHtmlUri found");
//				javodocURI = rows.get(0).getJavadocHtmlUri();
//			}	
//			
//			logger.info("Url redirect to: "+javodocURI);
//			urlRedirect(req, resp, javodocURI);
			
//		} catch (Exception e) {
////			sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
////					"Error on reconvering Javadoc");
//		}
//	}
}
