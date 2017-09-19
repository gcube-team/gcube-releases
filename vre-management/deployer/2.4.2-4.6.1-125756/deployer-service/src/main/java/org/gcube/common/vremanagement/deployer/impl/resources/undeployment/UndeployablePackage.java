package org.gcube.common.vremanagement.deployer.impl.resources.undeployment;


import java.io.File;
import java.util.List;
import java.util.Set;

import org.gcube.common.core.resources.common.PlatformDescription;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.vremanagement.deployer.impl.operators.common.DeployException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.InvalidPackageArchiveException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.RebootScheduler;
import org.gcube.common.vremanagement.deployer.impl.operators.common.UninstallScheduler;
import org.gcube.common.vremanagement.deployer.impl.resources.BasePackage;
import org.gcube.common.vremanagement.deployer.impl.resources.BaseTypedPackage;
import org.gcube.common.vremanagement.deployer.impl.resources.KeyData;


/**
 * Base undeployable package
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public abstract class UndeployablePackage extends BaseTypedPackage implements Undeployable {
	

	private static final long serialVersionUID = 5845468400815728987L;
			
	private BaseTypedPackage base;		
	
	public UndeployablePackage(BaseTypedPackage base) {
		super(base.getKey().getServiceClass(), base.getKey().getServiceName(), base.getKey().getServiceVersion(),
				base.getKey().getPackageName(), base.getKey().getPackageVersion());
		this.base = base;
		this.base.logger = new GCUBELog(BasePackage.class);
		this.logger = new GCUBELog(BasePackage.class);
	}
	
	/**
	 * Performs the following post-undeployment operations
	 * <ul>
	 * 	<li> execute the uninstall scripts from the unistall
	 * 	<li> remove all the reboot scripts from the reboot 
	 * </ul>
	 *
	 */
	public void postUndeploy() throws InvalidPackageArchiveException, DeployException {		
		
		//schedule & execute all the uninstall scripts		
		UninstallScheduler.getScheduler().add(this);
		UninstallScheduler.getScheduler().run(this.getKey());
		
		//remove the package's reboot scripts
		RebootScheduler.getScheduler().remove(this);		
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.undeployment.Undeployable#preUndeploy()
	 */
	/** {@inheritDoc} */
	public void preUndeploy() throws InvalidPackageArchiveException, DeployException {}
		
	/** {@inheritDoc} */
	public void undeploy(Set<GCUBEScope> scopes, boolean cleanState) throws DeployException, InvalidPackageArchiveException {		
		this.preUndeploy();
		this.packageUndeploy(scopes, cleanState);
		this.postUndeploy();		
	}

	/**
	 * Performs package-specific undeployment actions
	 * 
	 * @param scopes scopes from which the package has to be undeployed
	 * @param cleanState states if the package's state must be also removed after undeployment
	 * @throws DeployException
	 * @throws InvalidPackageArchiveException
	 */
	protected abstract void packageUndeploy(Set<GCUBEScope> scopes, boolean cleanState) throws DeployException,InvalidPackageArchiveException;

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.BasePackage#addDependencies(java.util.Set)
	 */
	/** {@inheritDoc} */
	@Override
	public void addDependencies(Set<KeyData> dependencies) {
		this.base.addDependencies(dependencies);
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.BasePackage#addFile2Package(java.io.File)
	 */
	/** {@inheritDoc} */
	@Override
	public void addFile2Package(File file) {
		this.base.addFile2Package(file);
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.BasePackage#addInstallScript(java.lang.String)
	 */
	/** {@inheritDoc} */
	@Override
	public void addInstallScript(String installScript) {
		this.base.addInstallScript(installScript);
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.BasePackage#addRebootScript(java.lang.String)
	 */
	/** {@inheritDoc} */
	@Override
	public void addRebootScript(String rebootScript) {
		this.base.addRebootScript(rebootScript);
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.BasePackage#addUninstallScript(java.lang.String)
	 */
	/** {@inheritDoc} */
	@Override
	public void addUninstallScript(String uninstallScript) {
		this.base.addUninstallScript(uninstallScript);
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.BasePackage#getDependencies()
	 */
	/** {@inheritDoc} */
	@Override
	public Set<KeyData> getDependencies() {
		return this.base.getDependencies();
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.BasePackage#getInstallScripts()
	 */
	/** {@inheritDoc} */
	@Override
	public List<String> getInstallScripts() {
		return this.base.getInstallScripts();
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.BasePackage#getKey()
	 */
	/** {@inheritDoc} */
	@Override
	public KeyData getKey() {
		return this.base.getKey();
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.BasePackage#getPackageFileList()
	 */
	/** {@inheritDoc} */
	@Override
	public List<File> getPackageFileList() {
		return this.base.getPackageFileList();
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.BasePackage#getRebootScripts()
	 */
	/** {@inheritDoc} */
	@Override
	public List<String> getRebootScripts() {
		return this.base.getRebootScripts();
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.BasePackage#getScopes()
	 */
	/** {@inheritDoc} */
	@Override
	public Set<GCUBEScope> getScopes() {
		return this.base.getScopes();
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.BasePackage#getScopesToAdd()
	 */
	/** {@inheritDoc} */
	@Override
	public Set<GCUBEScope> getScopesToAdd() {
		return this.base.getScopesToAdd();
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.BasePackage#getSerializationFile()
	 */
	/** {@inheritDoc} */
	@Override
	public File getSerializationFile() {
		return this.base.getSerializationFile();
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.BaseTypedPackage#getType()
	 */
	/** {@inheritDoc} */
	@Override
	public TYPE getType() {
		return this.base.getType();
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.BasePackage#getUninstallScripts()
	 */
	/** {@inheritDoc} */
	@Override
	public List<String> getUninstallScripts() {
		return this.base.getUninstallScripts();
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.BasePackage#printFiles()
	 */
	/** {@inheritDoc} */
	@Override
	public void printFiles() {
		this.base.printFiles();
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.BasePackage#removeFileFromPackage(java.io.File)
	 */
	/** {@inheritDoc} */
	@Override
	public void removeFileFromPackage(File file) {
		this.base.removeFileFromPackage(file);
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.BasePackage#removeScopes(java.util.Set)
	 */
	/** {@inheritDoc} */
	@Override
	public void removeScopes(Set<GCUBEScope> scopes) {
		this.base.removeScopes(scopes);
	}


	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.BasePackage#setScopes(java.util.Set)
	 */
	/** {@inheritDoc} */
	@Override
	public void setScopes(Set<GCUBEScope> scopes) {
		this.base.setScopes(scopes);
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.BasePackage#setScopesToAdd(java.util.Set)
	 */
	/** {@inheritDoc} */
	@Override
	public void setScopesToAdd(Set<GCUBEScope> scopesToAdd) {
		this.base.setScopesToAdd(scopesToAdd);
	}

	/** {@inheritDoc} */
	@Override
	public void addScope(GCUBEScope scope) {
		this.base.addScope(scope);
	}
	
	/** {@inheritDoc} */
	@Override
	public void removeScope(GCUBEScope scope) {
		this.base.removeScope(scope);
	}
	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.BaseTypedPackage#setType(org.gcube.common.vremanagement.deployer.impl.resources.BaseTypedPackage.TYPE)
	 */
	/** {@inheritDoc} */
	@Override
	public void setType(TYPE type) {
		this.base.setType(type);
	}

	/** {@inheritDoc} */
	@Override
	public void setProperty(String name, String value) {
		this.base.setProperty(name, value);
	}

	/** {@inheritDoc} */
	@Override
	public String getProperty(String name) {
		return this.base.getProperty(name);
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.undeployment.Undeployable#verify()
	 */
	@Override
	public boolean verify() throws InvalidPackageArchiveException {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.undeployment.Undeployable#requireRestart()
	 */
	@Override
	public boolean requireRestart() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.BasePackage#getTargetPlatform()
	 */
	@Override
	public PlatformDescription getTargetPlatform() {
		return this.base.getTargetPlatform();
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.BasePackage#setTargetPlatform(org.gcube.common.core.resources.common.PlatformDescription)
	 */
	@Override
	public void setTargetPlatform(PlatformDescription description) {
		this.base.setTargetPlatform(description);
	}
}
