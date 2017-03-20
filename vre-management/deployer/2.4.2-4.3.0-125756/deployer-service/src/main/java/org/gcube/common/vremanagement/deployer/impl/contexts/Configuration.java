package org.gcube.common.vremanagement.deployer.impl.contexts;

import java.io.File;

import org.gcube.common.core.contexts.GHNContext;

/**
 * Collector of all file-based configuration items for the Deployer service * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class Configuration {

	/**
	 * The working folder for all the deployment operation
	 */
	public static final String BASEDIR = ServiceContext.getContext().getPersistenceRoot().getAbsolutePath() + File.separator + "deployment";
	
	/**
	 * The build file including the Ant targets to use during the deployment
	 */
	public static final String DEPLOYFILE = ServiceContext.getContext().getConfigurationFileAbsolutePath("deployment" + File.separator + "deploy.xml");
	
	/**
	 * The restart file
	 */
	public static final String RESTARTFILE = ServiceContext.getContext().getConfigurationFileAbsolutePath("deployment" + File.separator + "restart.sh");
	
	/**
	 * The file created to notify to the container daemon that it's time to restart the container itself
	 */		
	public static final String RESTARTNOTIFIERFILE = GHNContext.getContext().getLocation() + File.separator + "config"+ File.separator + ".restart";
	
	/**
	 * The folder where the packages are downloaded
	 */
	public static final String BASESOURCEDIR = BASEDIR + File.separator + "tmp";
	
	/**
	 * The folder where the packages are uncompressed
	 */
	public static final String BASEDEPLOYDIR = BASEDIR + File.separator + "local-packages";
	
	/**
	 * The folder where the patches are uncompressed
	 */
	public static final String BASEPATCHDIR = BASEDIR + File.separator + "local-patches";		
	
	/**
	 * The deployment log folder
	 */
	public static final String BASEDEPLOYLOGDIR = BASEDIR + File.separator + File.separator + "log";	
	
	/**
	 * The Install scripts folder
	 */
	public static final String INSTALLSCRIPTDIR = GHNContext.getContext().getLocation() + File.separator +  "installScripts";
	
	/**
	 * The Uninstall scripts folder
	 */
	public static final String UNINSTALLSCRIPTDIR = GHNContext.getContext().getLocation() + File.separator +  "uninstallScripts";
	
	/**
	 * The Reboot scripts folder
	 */
	public static final String REBOOTSCRIPTDIR = GHNContext.getContext().getLocation() + File.separator +  "rebootScripts";
	
	/**
	 * The Deployment Reports folder
	 */
	public static final String REPORTDIR = BASEDIR + File.separator + "reports";
	
	/**
	 * The deployment timeout
	 */
	public static final int deployTimeout = 1000 * 60 * 5; 

	
	
}
