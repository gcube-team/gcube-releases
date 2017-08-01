package org.gcube.common.vremanagement.deployer.impl.resources.deployment;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.vremanagement.deployer.impl.contexts.Configuration;
import org.gcube.common.vremanagement.deployer.impl.operators.ant.AntInterfaceException;
import org.gcube.common.vremanagement.deployer.impl.operators.ant.AntRunner;
import org.gcube.common.vremanagement.deployer.impl.operators.common.DeployException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.InvalidPackageArchiveException;
import org.gcube.common.vremanagement.deployer.impl.operators.deployment.ExternalPackageExtractor;
import org.gcube.common.vremanagement.deployer.impl.resources.BaseTypedPackage;

public abstract class ExternalDeployablePackage extends BaseTypedPackage implements Deployable {

	private static final long serialVersionUID = -3579799875441746995L;

	/** Local Ant runner*/ 
	protected transient AntRunner run;
	
	protected transient ExternalPackageExtractor extractor;
	
	ExternalDeployablePackage(ExternalPackageExtractor extractor) throws Exception  {
		super(extractor.getServiceClass(), extractor.getServiceName(),
				extractor.getServiceVersion(), extractor.getName(), extractor.getVersion());
	
		logger.debug("ExternalDeployablePackage created for: " + this.getKey());
		this.extractor = extractor;
		this.setType(TYPE.EXTERNAL);
		//initialise the ANT context
		try {					
			this.run = new AntRunner();			
			this.run.init(Configuration.DEPLOYFILE, Configuration.BASEDIR);			
		} catch (Exception e) {
			logger.error("Package deployer is unable to initialize the deployment environment for " + this.extractor.getName(), e);
			throw new Exception("Package deployer is unable to initialize the deployment environment for " + this.extractor.getName());
		}		
	 }
	 

	@Override
	public boolean verify() throws InvalidPackageArchiveException {
		return true;
	}

	@Override
	public void preDeploy() throws InvalidPackageArchiveException, DeployException {}

	@Override
	public void postDeploy() throws InvalidPackageArchiveException, DeployException {}

	@Override
	public void clean() throws DeployException {
		try {
			Map<String, String> properties = new HashMap<String, String>();			
			properties.put("package.source.dir", Configuration.BASESOURCEDIR);
			properties.put("base.deploy.dir", Configuration.BASEDEPLOYDIR);
			this.run.setProperties(properties, true);
			this.run.runTarget("deleteTempFiles");									
		} catch (AntInterfaceException aie) {			
			throw new DeployException("Unable to cleanup the package " + this.key.getPackageName() + ": " + aie.getMessage());
		}
	}

	@Override
	public boolean requireRestart() {return false;}

	@Override
	public BaseTypedPackage getSourcePackage() {return this;}
	
	public final void deploy(Set<GCUBEScope> targets) throws DeployException,InvalidPackageArchiveException {
		this.preDeploy();
		this.deployPackage(targets);
		//join the package to the target scopes
		this.setScopes(targets);
		this.postDeploy();		
		
	}

	abstract public void deployPackage(Set<GCUBEScope> targets) throws DeployException, InvalidPackageArchiveException;			

}
