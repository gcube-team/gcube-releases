package org.gcube.common.vremanagement.deployer.impl.resources;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.common.core.resources.common.PlatformDescription;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.vremanagement.deployer.impl.contexts.*;

public class BasePackage implements Serializable {

	/** local logger */			
	public transient GCUBELog  logger;
	
	private static final long serialVersionUID = 8475576257671061501L;

	/** scopes to which the package is joined to */
	private Set<String> scopes = new HashSet<String>();//we do not use GCUBEScope as type, since it is not serializable		
	
	/** scopes to add after the next restart */
	private Set<String> scopesToAdd = new HashSet<String>(); //we do not use GCUBEScope as type, since it is not serializable
				
	/** The list of files installed with the package*/
	protected List<File> packageFiles = new ArrayList<File>();
	
	/** The list of installation scripts*/
	protected List<String> installScripts = new ArrayList<String>();
	
	/** The list of reboot scripts */
	protected List<String> rebootScripts = new ArrayList<String>();
	
	/** The list of uninstall scripts*/
	protected List<String> uninstallScripts = new ArrayList<String>();
	
	/** package's dependencies*/
	protected Set<KeyData> dependencies = new HashSet<KeyData>(); 
	
	/** the package unique key */
	protected KeyData key;
	
	/** package-specific properties */
	protected Map<String, String> properties = new HashMap<String, String>();
	
	/** scopes to add after the next restart */
	protected ArrayList<String> targetsToAdd = new ArrayList<String>(); //we do not use GCUBEScope as type parameter, since it is not serializable

	
	protected BasePackage() {}
			
	public BasePackage(String serviceClass, String serviceName, String serviceVersion, String packagename, String packageVersion) {
		this.key = new KeyData(serviceClass, serviceName, serviceVersion, packagename, packageVersion);
		this.logger = new GCUBELog(BasePackage.class);
	}
	
	/**
	 * Gets the package unique key.
	 * The package key holds the quintuple: 
	 *  serviceclass, servicename, serviceversion, packagename, packageversion
	 *  
	 * @return the package key
	 */
	public KeyData getKey() {
		return key;
	}
		
	
	public File getSerializationFile() {	
		return ServiceContext.getContext().getPersistentFile(this.buildSerializationFilePath(),true);			
	}
	
	private String buildSerializationFilePath() {
		return this.getKey().getServiceClass() + File.separator + this.getKey().getServiceClass() + File.separator +
		this.getKey().getServiceClass() + File.separator +
		this.getKey().getServiceName() + File.separator +
		this.getKey().getServiceVersion()+ File.separator + "package_profile.xml";
	}
	
	
	/**
	 * Gets the list of files installed with the package
	 * 
	 * @return the list of files installed with the package
	 */
	public List<File> getPackageFileList() { return this.packageFiles; }
	
	/**
	 * Adds a file to the list of package files. The file will be removed when the package is undeployed, 
	 * 
	 * @param file the new file belonging the package
	 */
	public void addFile2Package(File file) {
		this.packageFiles.add(file);
	}
	
	/**
	 * Removes a file from the list of package files
	 * 
	 * @param file the file to remove
	 */
	public void removeFileFromPackage (File file) {
		this.packageFiles.remove(file);
	}
	
	public void printFiles() {
		if (this.getPackageFileList().size() == 0) 
			return;
		logger.debug("The files belonging the package are:");
		for (File file : this.getPackageFileList())
			logger.debug(file.getAbsolutePath());
	}
	
	/**
	 * @return the installScripts
	 */
	public List<String> getInstallScripts() {
		return installScripts;
	}

	/**
	 * @param installScripts the installScripts to set
	 */
	public void addInstallScript(String installScript) {
		this.installScripts.add(installScript);
	}

	/**
	 * @return the rebootScripts
	 */
	public List<String> getRebootScripts() {
		return rebootScripts;
	}

	/**
	 * @param rebootScripts the rebootScripts to set
	 */
	public void addRebootScript(String rebootScript) {
		this.rebootScripts.add(rebootScript);
	}

	/**
	 * @return the uninstallScripts
	 */
	public List<String> getUninstallScripts() {
		return uninstallScripts;
	}

	/**
	 * @param uninstallScripts the uninstallScripts to set
	 */
	public void addUninstallScript(String uninstallScript) {
		this.uninstallScripts.add(uninstallScript);
	}

	/**
	 * Gets the target scopes to which the package is joined
	 * @return the targets scopes to which the package is joined
	 */
	public Set<GCUBEScope> getScopes() {
		Set<GCUBEScope> retScope = new HashSet<GCUBEScope>();
		for (String scope : this.scopes)
			retScope.add(GCUBEScope.getScope(scope));
		return retScope;
	}

	/**
	 * Sets the target scopes to which the package is joined
	 *  
	 * @param scopes the targets to set
	 */
	public void setScopes(Set<GCUBEScope> scopes) {
		for (GCUBEScope scope : scopes )
			this.scopes.add(scope.toString());
	}
	
	/**
	 * Adds a new scope to the package
	 * 
	 * @param scope the new scope
	 */
	public void addScope(GCUBEScope scope) {
		this.scopes.add(scope.toString());
	}
	
	/**
	 * Removes a scope from the package
	 * 
	 * @param scope the scope to remove
	 */
	public void removeScope(GCUBEScope scope) {
		this.scopes.remove(scope.toString());
	}
	
	/**
	 * Removes the given scopes from the package
	 * 
	 * @param scopes the scopes to remove
	 */
	public void removeScopes(Set<GCUBEScope> scopes) {
		for (GCUBEScope scope : scopes )
			this.scopes.remove(scope.toString());
	}

	/**
	 * Gets the target scopes to add to the package at the next restart
	 * 
	 * @return the scopes to add
	 */
	public Set<GCUBEScope> getScopesToAdd() {
		Set<GCUBEScope> retScope = new HashSet<GCUBEScope>();
		for (String scope : this.scopesToAdd)
			retScope.add(GCUBEScope.getScope(scope));
		return retScope;
	}

	/**
	 * Sets the target scopes to add to the package at the next restart
	 *  
	 * @param scopesToAdd the scopesToAdd to set
	 */
	public void setScopesToAdd(Set<GCUBEScope> scopesToAdd) {
		for (GCUBEScope scope : scopesToAdd)
			this.scopesToAdd.add(scope.toString());		
	}
	
	/**
	 * Gets the list of dependencies of the package
	 * 
	 * @return the dependencies
	 */
	public Set<KeyData> getDependencies() {
		return this.dependencies;
	}

	/**
	 * Adds new depencencies to the package 
	 * 
	 * @param dependencies the dependencies to add
	 */
	public void addDependencies(Set<KeyData> dependencies) {
		this.dependencies.addAll(dependencies);
	}

	/**
	 * Adds a property to the packge
	 * 
	 * @param name the property to set
	 * @param value the value of the property
	 */
	public void setProperty(String name, String value) {
		this.properties.put(name, value);
	}

	/**
	 * Gets a property from the package
	 * 
	 * @return the value of the property
	 */
	public String getProperty(String name) {
		return properties.get(name);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BasePackage [key=" + key + "]";
	}

	/**
	 * Gets the target platform of this package
	 * @return the platform on which the package is supposed to run
	 */
	public PlatformDescription getTargetPlatform() {
		if (!this.properties.containsKey("PlatformName")) {
			logger.warn("No platform information available for the package " + this.getKey());
			return null;
		}
		PlatformDescription desc = new PlatformDescription();
		desc.setName(this.getProperty("PlatformName"));
		if (this.properties.containsKey("PlatformVersion"))
			desc.setVersion(Short.valueOf(this.getProperty("PlatformVersion")));
		if (this.properties.containsKey("PlatformMinorVersion"))
			desc.setMinorVersion(Short.valueOf(this.getProperty("PlatformMinorVersion")));
		return desc;
	}
	
	/**
	 * Sets the target platform of this package
	 * @param description the description of the target platform
	 */
	public void setTargetPlatform(PlatformDescription description) {
		this.setProperty("PlatformName", description.getName());
		this.setProperty("PlatformVersion", String.valueOf(description.getVersion()));
		this.setProperty("PlatformMinorVersion", String.valueOf(description.getMinorVersion()));

	}
	
	/**
	 * Sets the target scopes to add to the package at the next restart
	 * 
	 * @return the targetsToAdd
	 */
	public final List<GCUBEScope> getTargetsToAdd() {
		List<GCUBEScope> ret = new ArrayList<GCUBEScope>();
		for (String scope : this.targetsToAdd) ret.add(GCUBEScope.getScope(scope));
		return ret;		
	}

	/**
	 * Sets the target scopes to add to the package at the next restart
	 * 
	 * @param targetsToAdd the targets scopes 
	 */
	public final void setTargetsToAdd(List<GCUBEScope> targetsToAdd) {
		for (GCUBEScope scope : targetsToAdd) 
			this.targetsToAdd.add(scope.toString());		
	}
	
	/**
	 * Notifies to the package that it has been added to the given target scopes
	 * 
	 * @param targets the target scope
	 */
	public final void notifiyTargetsAdded(List<GCUBEScope> targets) {
		for (GCUBEScope scope : targets)
			this.targetsToAdd.remove(scope.toString());
	}
}
