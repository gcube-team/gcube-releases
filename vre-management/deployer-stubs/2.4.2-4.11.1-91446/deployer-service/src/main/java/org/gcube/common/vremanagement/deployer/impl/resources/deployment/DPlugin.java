package org.gcube.common.vremanagement.deployer.impl.resources.deployment;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.resources.service.Package;
import org.gcube.common.core.resources.service.Plugin;
import org.gcube.common.core.resources.service.Plugin.TargetService;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.vremanagement.deployer.impl.contexts.Configuration;
import org.gcube.common.vremanagement.deployer.impl.operators.ant.AntInterfaceException;
import org.gcube.common.vremanagement.deployer.impl.operators.ant.AntRunner;
import org.gcube.common.vremanagement.deployer.impl.operators.common.DeployException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.InvalidPackageArchiveException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.PackageAldreadyDeployedException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.UpdateException;
import org.gcube.common.vremanagement.deployer.impl.operators.deployment.PackageExtractor;
import org.gcube.common.vremanagement.deployer.stubs.deployer.UpdateParameters;


/**
 * A deployable plugin
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class DPlugin extends DeployablePackage {

	private static final long serialVersionUID = -5582413387483432642L;

	protected String baseTargetDir = GHNContext.getContext().getLocation() + File.separator + "lib" + File.separator;
	
	protected transient Plugin packageprofile;
	
	/**
	 * @param packageprofile
	 * @param downloader
	 * @throws Exception 
	 */
	public DPlugin(Plugin packageprofile, PackageExtractor extractor) throws Exception {
		super(packageprofile, extractor);
		this.packageprofile = packageprofile;
		this.setType(TYPE.PLUGIN);
		this.analysePackage();
		StringWriter writer = new StringWriter();
		try {
			this.getServiceProfile().store(writer);
			this.properties.put("SerializedProfile", writer.toString());
		} catch (Exception e) {
			logger.error("Unable to serialize the Service profile", e);
			throw e;
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deployPackage(Set<GCUBEScope> targets) throws DeployException, InvalidPackageArchiveException {
		logger.debug("Deploying the plugin package " + this.getKey().getPackageName() + " in scope(s) " + targets.toString());
		this.deployPlugin(this.packageprofile.getFiles());
		this.setScopesToAdd(targets); //TODO: this will allow post restart management, that's not nice, to be refined
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Package getPackageProfile() {
		return this.packageprofile;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean requireRestart() {	
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean verify() throws InvalidPackageArchiveException {		
		for (File pluginFile : this.getPackageFileList()) {
			logger.warn("Verifying library file "+ pluginFile.getName() + "...");	
			if (!pluginFile.exists()) {
				try {
					logger.warn("Plugin file "+ pluginFile.getCanonicalPath() + " not correctly deployed");
				} catch (IOException e) {
					throw new InvalidPackageArchiveException("Unable to check library file " + pluginFile.getName());
				}
				return false;
			}
		}
		return true;
	}
	/**
	 * Moves the plugin files to the local GHN /lib folder
	 * 
	 * @param pluginfiles the list of files forming the plugin
	 * @throws InvalidPackageArchiveException if the Service profile is not valid
	 * @throws Exception if the deploy operation fails 
	 */
	private void deployPlugin(List<String> pluginfiles) throws DeployException, InvalidPackageArchiveException {
		
		//deploy a standard library included in the tar.gz and stored on the PR
		for (String file : pluginfiles) {
			try {					
				this.run = new AntRunner();			
				this.run.init(Configuration.DEPLOYFILE, Configuration.BASEDIR);
				
			} catch (Exception e) {
				logger.error("Package deployer is unable to initialize the deployment environment for " + this.key.getPackageName(), e);
				throw new DeployException ("Package deployer is unable to initialize the deployment environment for " + this.key.getPackageName());
			}
			//this.buildJarNames(file);
			Map<String, String> properties = new HashMap<String, String>();
			properties.put("service.id", this.extractor.getServiceKey());
			properties.put("package.name", this.getKey().getPackageName());
			properties.put("package.file", this.extractor.getDownloadedFile().getName());		
			properties.put("package.source.dir", Configuration.BASESOURCEDIR );
			properties.put("base.deploy.dir", Configuration.BASEDEPLOYDIR );
			properties.put("jar.name", file.trim());
			try {
				this.run.setProperties(properties, true);
				this.run.runTarget("deployLibrary");
			} catch (AntInterfaceException aie) {			
				throw new DeployException (aie.getMessage());
			}
			String[] filetokens = file.split(File.separator);
			this.addFile2Package(new File(baseTargetDir + filetokens[filetokens.length-1]));
			
			//hold the target service information for the plugin registration after the restart
			TargetService service = this.packageprofile.getTargetService();
			this.properties.put("Class", service.getClazz());
			this.properties.put("Name", service.getName());
			this.properties.put("Version", service.getVersion());
			this.properties.put("TargetPackage", service.getTargetPackage());
			this.properties.put("TargetVersion", service.getTargetVersion());			
			
		}
	}

}
