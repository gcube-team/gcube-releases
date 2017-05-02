package org.gcube.vremanagement.virtualplatform.model;

import java.io.FileFilter;
import java.net.URL;


/**
 * Models the behavior of an hosting platform
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 * @param <RESOURCE> the type of package managed by the platform
 */
public abstract class TargetPlatform<RESOURCE extends Package> {

	protected String user;
	protected String password;
	protected String manager;
	protected URL baseURL;
	protected String platform = "";
	protected int platformVersion = 0;
	protected int platformMinorVersion = 0;
	
	public TargetPlatform() {}
	
	/**
	 * @return the platform
	 */
	public  String getPlatform() { return this.platform;}

	/**
	 * @return the platformVersion
	 */
	public int getPlatformVersion() {return this.platformVersion;}


	/**
	 * @return the platformMinorVersion
	 */
	public int getPlatformMinorVersion() {return this.platformMinorVersion;}
	
	
	/**
	 * Deploys a resource into the platform
	 * @param resource the resource to deploy
	 * @return the deployed resource
	 * @throws Exception if the deployment fails
	 */
	public DeployedPackage deploy(RESOURCE resource) throws Exception {throw new IllegalAccessError("Not implemented");}
	
	/**
	 * Undeploys a resource from the platform
	 * @param resource the resource to undeploy
	 * @return the undeployed resource
	 * @throws Exception if the undeployment fails
	 */
	public UndeployedPackage undeploy(RESOURCE resource) throws Exception {throw new IllegalAccessError("Not implemented");}
	
	/**
	 * Activates the resource in the platform
	 * @param resource the resource to activate
	 * @return <tt>true</tt> if the resource was successfully activated, <tt>false</tt> otherwise
	 * @throws Exception if the activation fails
	 */
	public boolean activate(RESOURCE resource) throws Exception {return false;}
	
	/**
	 * Deactivates the resource on the platform
	 * @param resource the resource to deactivate
	 * @return <tt>true</tt> if the resource was successfully deactivated, <tt>false</tt> otherwise
	 * @throws Exception if the deactivation fails
	 */
	public boolean deactivate(RESOURCE resource) throws Exception {return false;}
	
	
	/**
	 * Lists all the resources deployed
	 * @return the resources
	 * @throws Exception if the listing fails
	 */
	public PackageSet<RESOURCE> list() throws Exception {return new PackageSet<RESOURCE>();}
	
	/**
	 * Initializes the platform
	 * @throws Exception
	 */
	public void initialize() throws Exception {}
	
	/**
	 * Shutdowns the platform
	 * @throws Exception
	 */
	public void shutdown() throws Exception {}
	
	/**
	 * Sets the base URL of the platform
	 * @param url
	 */
	public void setBaseURL(URL url) {this.baseURL = url;}

	/**
	 * Sets the password to use for the admin user in the target platform, if any
	 * @param password the password
	 */
	public  void setPassword(String password) {this.password = password;}

	/**
	 * Sets the admin user, if any
	 * @param user the user
	 */
	public void setUser(String user) {this.user = user;}
	
	/**
	 * States if the platform supports hot deployments, i.e. do not need a restart
	 * after a {@link #deploy(Package)} or {@link #undeploy(Package)} invocation
	 * @return true if the platform offers the support, false otherwise
	 */
	public boolean supportHotDeployment() {return false;}

	/**
	 * Gets the resource class managed by the platform
	 * @return the resource class
	 */
	public Class<RESOURCE> getResourceClass() {throw new IllegalAccessError("Not implemented");}

	/**
	 * Gets the filter to select the appropriate files to deploy on the platform.
	 * The filter is used to select the files belonging the platform in case of multiple files available in the package.
	 * @return the filter or null if no filter is available
	 */
	public FileFilter getAcceptedFilesFilter() {return null;}
	
	
	/**
	 * Checks whether the platform is available or not
	 * @return true if the platform is available, false otherwise
	 */
	public boolean isAvailable() {return false;}

	
}
