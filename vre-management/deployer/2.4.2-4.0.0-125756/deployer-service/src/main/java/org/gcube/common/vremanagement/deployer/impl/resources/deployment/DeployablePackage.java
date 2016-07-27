package org.gcube.common.vremanagement.deployer.impl.resources.deployment;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.io.File;

import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.vremanagement.deployer.impl.contexts.Configuration;
import org.gcube.common.vremanagement.deployer.impl.operators.ant.AntInterfaceException;
import org.gcube.common.vremanagement.deployer.impl.operators.ant.AntRunner;
import org.gcube.common.vremanagement.deployer.impl.operators.common.DeployException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.InstallScheduler;
import org.gcube.common.vremanagement.deployer.impl.operators.common.InvalidPackageArchiveException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.PackageAldreadyDeployedException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.RebootScheduler;
import org.gcube.common.vremanagement.deployer.impl.operators.deployment.PackageExtractor;
import org.gcube.common.vremanagement.deployer.impl.resources.BaseTypedPackage;
import org.gcube.common.vremanagement.deployer.impl.resources.KeyData;
import org.gcube.common.core.resources.service.Dependency;
import org.gcube.common.core.resources.service.Package;
import org.gcube.common.core.scope.GCUBEScope;

/**
 * Base deployable package
 * 
 * @author Manuele Simi (CNR-ISTI)
 *
 */
abstract public class DeployablePackage extends BaseTypedPackage implements Deployable {
		
	private static final long serialVersionUID = -3579799875441746994L;

	/**The extractor managing the package */
	protected transient PackageExtractor extractor;			
	
	/**	Local Ant runner */ 
	protected transient AntRunner run;

	/** service profile extracted from the deployable package*/
	protected transient GCUBEService serviceprofile;

	/** the service identifier to which the package belongs to*/
	protected String serviceID;
	
	/** scopes to which the package is joined to */
	//protected ArrayList<String> targets = new ArrayList<String>(); //we do not use GCUBEScope as type parameter, since it is not serializable
	
	/** scopes to add after the next restart */
	//protected ArrayList<String> targetsToAdd = new ArrayList<String>(); //we do not use GCUBEScope as type parameter, since it is not serializable
		
	protected java.util.Calendar deploymentTime;
	
	/**
	 * Non public constructor
	 * @throws Exception  if the environment initialisation fails
	 */
	DeployablePackage(Package packagefile, PackageExtractor extractor) throws Exception {
		super(extractor.getServiceProfile().getServiceClass(), extractor.getServiceProfile().getServiceName(),
				extractor.getServiceProfile().getVersion(), packagefile.getName(), packagefile.getVersion());
		this.extractor = extractor;
		this.key = new KeyData(this.extractor.getServiceProfile().getServiceClass(), this.extractor.getServiceProfile().getServiceName(),
				this.extractor.getServiceProfile().getVersion(), packagefile.getName(), packagefile.getVersion());				
		this.serviceID = this.extractor.getServiceProfile().getID();		
		logger.debug("DeployablePackage created for: " + packagefile.getName());
		this.serviceprofile = this.extractor.getServiceProfile();
		//initialise the ANT context
		try {					
			this.run = new AntRunner();			
			this.run.init(Configuration.DEPLOYFILE, Configuration.BASEDIR);
			
		} catch (Exception e) {
			logger.error("Package deployer is unable to initialize the deployment environment for " + this.key.getPackageName(), e);
			throw new Exception("Package deployer is unable to initialize the deployment environment for " + this.key.getPackageName());
		}				
	}


	protected final void analysePackage() {
		this.extractInstallScripts();
		this.extractsRebootScripts();
		this.extractsUninstallScripts();
		this.extractDependencies();
		if (this.getPackageProfile().getTargetPlatform() != null)
			this.setTargetPlatform(this.getPackageProfile().getTargetPlatform());
		
	}
	
	
	public final void deploy(Set<GCUBEScope> targets) throws PackageAldreadyDeployedException, DeployException, InvalidPackageArchiveException {
		this.preDeploy();
		this.deployPackage(targets);
		//join the package to the target scopes
		this.setScopes(targets);
		//manage install and reboot scripts
		this.manageScripts();
		this.postDeploy();		
		//notify the completeness 
		this.notifyDeployCompleted();
	}
	

	abstract public void deployPackage(Set<GCUBEScope> targets) throws PackageAldreadyDeployedException, DeployException, InvalidPackageArchiveException;			
	
	/**
	 * Gets the package profile
	 * @return the package profile
	 */
	protected abstract Package getPackageProfile();

		
	/**
	 * Gets the service profile
	 * @return the service profile
	 */
	public GCUBEService getServiceProfile() {			
		return this.serviceprofile;
	}
		
	
	/**
	 * Executes the installation scripts for the package
	 * 
	 * @throws DeployException if an error occurs during the execution of one of the given scripts
	 * @throws InvalidPackageArchiveException if the format of the downloaded package is not valid
	 */
	protected void runInstallScripts() throws DeployException, InvalidPackageArchiveException {		
		List<String> scripts = this.getInstallScripts();
		if (scripts == null)
			return;		
	}
	
	/**
	 * Extracts the list of install scripts declared in the service profile
	 * 
	 * @return the list of install scripts for this package
	 * @throws InvalidPackageArchiveException if an error occurs when reading the service profile
	 */
	private void extractInstallScripts() {
		if (this.extractor == null)
			return;
		
		String dir = this.extractor.getPackageFilesDir();				
		List<String> profile_scripts = this.getPackageProfile().getInstallScripts();
		if (profile_scripts == null)
			return;
		
		for (String script : profile_scripts) {
			logger.debug("Scheduling " + dir + File.separator + script  + " as install script for " + this.key.getPackageName() +"...");		
			this.addInstallScript(dir + File.separator + script );
		}
	}
		
		
	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.deployment.Deployable#clean()
	 */
	public final void clean() throws DeployException {
		try {
			logger.trace("Cleaning up for package " + this.key.getPackageName());
			Map<String, String> properties = new HashMap<String, String>();			
			properties.put("package.source.dir", Configuration.BASESOURCEDIR);
			properties.put("base.deploy.dir", Configuration.BASEDEPLOYDIR);
			this.run.setProperties(properties, true);
			this.run.runTarget("deleteTempFiles");									
		} catch (AntInterfaceException aie) {			
			throw new DeployException("Unable to cleanup the package " + this.key.getPackageName() + ": " + aie.getMessage());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.deployment.Deployable#preDeploy()
	 */
	public void preDeploy() throws  InvalidPackageArchiveException, DeployException {
			//give a chance to subclasses to manage custom pre-deployment operations
	}	

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.deployment.Deployable#postDeploy()
	 */
	public void postDeploy() throws  InvalidPackageArchiveException, DeployException {
		//give a chance to subclasses to manage custom post-deployment operations
	}
	
	/**
	 * @return the serviceID
	 */
	public String getServiceID() {
		return serviceID;
	}
	

	/**
	 * @return the deploymentTime
	 */
	public final java.util.Calendar getDeploymentTime() {
		return deploymentTime;
	}
	
	/**
	 * Does the latest stuffs and cleanup after a successful deployment
	 */
	protected final void notifyDeployCompleted() {
		try {
			Calendar calendar = new GregorianCalendar();
		    calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		    this.deploymentTime = calendar;
		} catch (Exception e) {
		    logger.warn("unable to detect the current time in the default time zone with the default locale ", e);
		}
	}	

	/**
	 * Extracts the list of reboot scripts declared in the service profile
	 * 
	 * @return the list of reboot scripts for this package
	 */
	private void extractsRebootScripts() {		
		if (this.extractor == null)
			return;
		
		String dir = this.extractor.getPackageFilesDir();		
		List<String> profile_scripts = this.getPackageProfile().getRebootScripts();
		if (profile_scripts == null)
			return;
		
		for (String script : profile_scripts) {
			logger.debug("package deployer is scheduling " + dir + File.separator + script  + " as reboot script for " + this.key.getPackageName() +"...");		
			this.addRebootScript(dir + File.separator + script );
		}
	}

	/**
	 * Extracts the list of uninstall scripts declared in the service profile
	 * 
	 * @return the list of uninstall scripts for this package
	 */
	private void extractDependencies() {
		Set<KeyData> deps = new HashSet<KeyData>();
		for (Dependency  dep : this.getPackageProfile().getDependencies()) {			
			deps.add( new KeyData(dep.getService().getClazz(), dep.getService().getName(),
					dep.getService().getVersion(), dep.getPackage(),dep.getVersion()));
		}
		this.addDependencies(deps);
	}
	
	private void manageScripts() throws InvalidPackageArchiveException, DeployException {
		//get and run the install scripts
		InstallScheduler.getScheduler().add(this);		
		InstallScheduler.getScheduler().run(this.getKey());		
		//schedule all the reboot scripts		
		RebootScheduler.getScheduler().add(this);
	}	

	/**
	 * Extracts the list of uninstall scripts declared in the service profile
	 * 
	 * @return the list of uninstall scripts for this package
	 */
	private void extractsUninstallScripts() {
		
		if (this.extractor == null)
			return;
		
		String dir = this.extractor.getPackageFilesDir();				
		List<String> profile_scripts = this.getPackageProfile().getUninstallScripts();
		if (profile_scripts == null)
			return;
		
		for (String script : profile_scripts) {
			logger.debug("package deployer is scheduling " + dir + File.separator + script  + " as unistall script for " + this.key.getPackageName() +"...");
			this.addUninstallScript(dir + File.separator + script );
		}	
	}

	public String deployApp(Set<GCUBEScope> targets) throws PackageAldreadyDeployedException, DeployException, InvalidPackageArchiveException {
		this.deploy(targets); //unless it is overwritten... it has the same behavior of the deploy
		return "";
	}


	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.deployment.Deployable#getSourcePackage()
	 */
	@Override
	public BaseTypedPackage getSourcePackage() {
		return this;
	}
	
}
