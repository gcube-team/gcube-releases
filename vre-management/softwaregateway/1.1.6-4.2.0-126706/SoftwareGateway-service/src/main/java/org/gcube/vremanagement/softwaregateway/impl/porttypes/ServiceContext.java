package org.gcube.vremanagement.softwaregateway.impl.porttypes;

import java.io.File;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * @author Luca Frosini (ISTI-CNR)
 */
public class ServiceContext extends GCUBEServiceContext {
	
	/**
	 * JNDI Name
	 */
	public static final String JNDI_NAME = "gcube/vremanagement/softwaregateway";
	
	/**
	 * Singleton object
	 */
	private static final ServiceContext singleton = new ServiceContext();
	
	private final GCUBELog logger = new GCUBELog(ServiceContext.class);
	
//	
	/**
	 * Username for DB connection
	 */
	private String username;

	/**
	 * Password for DB connection
	 */
	private String password;

	
	/**
	 * Temporary directory
	 */
	private File tmp;
	
	/**
	 * Pending directory
	 */
	private File pending;
	
	/**
	 * Maven service directory
	 */
	private File maven;
	
	/**
	 * Maven settings file
	 */
	private String mavenSettingsFileName;
	
	/**
	 * HTTP server local path
	 */
	private File httpServerBasePath;
	
	/**
	 * HTTP server port
	 */
	private int httpServerPort;
	
	/**
	 * HTTP base url
	 */
	private String httpBaseUrl;
	
	/**
	 * HTTP relative Maven directory
	 */
	private String mavenRelativeDir;
	
	/**
	 * HTTP relative Report Directory
	 */
	private String reportRelativeDir;
	
	/**
	 * XSLT service directory
	 */
	private File xsltDir;
	
	
	private String bannedArtifactVersion;
	
	private ServiceContext(){}

	/**
	 * @return the singleton instance of the Service Context
	 */
	public static ServiceContext getContext() {
		return singleton;
	}

	/**
	 * @inheritDoc
	 * @return JNDI NAME
	 */
	@Override
	public String getJNDIName() {
		return JNDI_NAME;
	}

	/**
	 * COntext initialisation
	 */
	
	protected void onInitialisation(){
		
		logger.debug("in initialization");
		logger.debug("banned version: "+bannedArtifactVersion);
		username = (String) this.getProperty("username", true);
		password = (String) this.getProperty("password", true);
		
		String dbDir= "dbFolder";
		File dbDirFileName= this.getPersistentFile(dbDir,false);
		if (!dbDirFileName.exists()){
			dbDirFileName.mkdirs();			
		}
		tmp = this.getPersistentFile((String)this.getProperty("tmpDir", true), false);
		if (!tmp.exists()){
			tmp.mkdirs();
		}
		logger.debug("Tmp directory = " + tmp.getAbsolutePath());
		
		pending = this.getPersistentFile((String)this.getProperty("pendingDir", true), false);
		if (!pending.exists()){
			pending.mkdirs();
		}
		logger.debug("Pending directory = " + pending.getAbsolutePath());
		
		
		maven = this.getPersistentFile((String)this.getProperty("mavenDir", true), false);
		if (!maven.exists()){
			maven.mkdir();
		}
		logger.debug("Maven directory = " + maven.getAbsolutePath());
				
		mavenSettingsFileName = (String)this.getProperty("mavenSettingsFileName", true);
		logger.debug("Maven settings file path = " + mavenSettingsFileName);
		
		
		httpServerBasePath = this.getPersistentFile((String) this.getProperty("httpServerBasePath", true),false);
		logger.debug("HTTP Server Base path = " + httpServerBasePath.getAbsolutePath());
		if(!httpServerBasePath.exists()){
			httpServerBasePath.mkdirs();
		}
		
		httpServerPort = Integer.parseInt((String)this.getProperty("httpServerPort",true));
		logger.debug("HTTP Server port = " + httpServerPort);
		logger.debug("CHECK: httpServerBasePath="+httpServerBasePath+" httpServerPort"+httpServerPort);
		logger.debug("CHECK: httpServerBasePath="+httpServerBasePath+" httpServerPort"+httpServerPort);

		mavenRelativeDir = (String)this.getProperty("mavenRelativeDir",true);
		logger.debug("HTTP relative Maven directory = " + mavenRelativeDir);
		File mavenDir = new File(this.httpServerBasePath.getAbsolutePath(),this.mavenRelativeDir);
		if(!mavenDir.exists()){
			mavenDir.mkdirs();
		}
		
		reportRelativeDir = (String)this.getProperty("reportRelativeDir",true);
		logger.debug("HTTP relative Report directory = " + reportRelativeDir);
		File reportDirectory = new File(this.httpServerBasePath.getAbsolutePath(),this.reportRelativeDir);
		/* This could not work because normally the service don't have the right to create it */
		if(!reportDirectory.exists()){
			reportDirectory.mkdirs();
		}
		xsltDir = this.getFile((String)this.getProperty("xsltDir", true), false);
		logger.debug("XSLT directory = " + xsltDir);
		try {
			String webServerClass = (String) this.getProperty("webServerClass", true);
			logger.debug("webServerClass: "+webServerClass);
			Class cls=Thread.currentThread().getContextClassLoader().getClass().forName(webServerClass);
/*SERVER JETTY COMMENTED*/
//			server = (WebServer) cls.newInstance();
//			server.initDefaults(httpServerBasePath.getAbsolutePath(),httpServerPort);
//			server.startServer();
//			httpBaseUrl=server.getBaseUrl();
//			logger.debug("BASE URL: "+httpBaseUrl);
/* END COMMENT*/			
		}catch (Exception e) {
			logger.fatal("Unable to Start Web Server",e);
			return;
		}
	}
	/**
	 * @return HTTP Server local path
	 */
	public File getHttpServerBasePath() {
		return httpServerBasePath;
	}
	/**
	 * @return HTTP server port
	 */
	public int getHttpServerPort() {
		return httpServerPort;
	}
	/**
	 * @return HTTP server port
	 */
	public String getBaseUrl() {
		return httpBaseUrl;
	}
	/**
	 * @return HTTP relative Maven directory
	 */
	public String getMavenRelativeDir() {
		return mavenRelativeDir;
	}
	/**
	 * @return Temporary directory
	 */
	public File getTmp() {
		return tmp;
	}
	/**
	 * @return Maven settings file
	 */
	public String getMavenSettingsFileName() {
		return mavenSettingsFileName;
	}
	/**
	 * @return HTTP relative report Dir
	 */
	public String getReportRelativeDir() {
		return reportRelativeDir;
	}
	
}
