package org.gcube.common.vremanagement.deployer.impl.operators.deployment;

import java.io.File;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.vremanagement.deployer.impl.contexts.Configuration;
import org.gcube.common.vremanagement.deployer.impl.operators.ant.AntRunner;

public abstract class BaseExtractor implements Extractor {

	protected final GCUBELog  logger = new GCUBELog(Extractor.class);

	/** Local Ant runner*/ 
	protected AntRunner run;

	protected Downloader downloader;
	
	public BaseExtractor(Downloader downloader) throws Exception {
		this.downloader = downloader;
		this.initAntContext();
	}
	
	private void initAntContext() throws Exception {
		//initialise the ANT context
		try {					
			this.run = new AntRunner();			
			this.run.init(Configuration.DEPLOYFILE, Configuration.BASEDIR);
		} catch (Exception e) {
			logger.error("Package extractor is unable to initialize the deployment environment", e);
			throw new Exception("Package extractor is unable to initialize the deployment environment");
		}		
	}
	

	/**
	 * 
	 * @return the source file
	 */
	public File getDownloadedFile() {
		return this.downloader.downloadedFile;
	}
	
	/**
	 * 
	 * @return the service class
	 */
	public String getServiceClass() {
		return this.downloader.serviceClass;
	}

	/**
	 * 
	 * @return the service name
	 */
	public String getServiceName() {
		return this.downloader.serviceName;
	}

	/**
	 * 
	 * @return the package name
	 */
	public String getName() {
		return this.downloader.packagename;
	}

	/**
	 * 
	 * @return the package version
	 */
	public String getVersion() {
		return this.downloader.packageVersion;
	}

	/**
	 * 
	 * @return the service version
	 */
	public String getServiceVersion() {
		return this.downloader.serviceVersion;
	}
	
	/**
	 * 
	 * @return the service unique key
	 */
	public String getServiceKey() {
		return this.downloader.getServiceKey();
	}
	
	public String getPackageFilesDir() {
		return this.downloader.getPackageFilesDir();
	}

	public boolean isUpdate() {
		return this.downloader.isUpdate();
	}

	public String getPackagedir() {
		return this.downloader.getPackagedir();
	}

}
