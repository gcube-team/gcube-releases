package org.gcube.common.vremanagement.deployer.impl.operators;

import org.apache.axis.types.URI;
import java.util.HashMap;
import java.util.Map;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.handlers.GCUBEHandler;
import org.gcube.common.vremanagement.deployer.impl.contexts.Configuration;
import org.gcube.common.vremanagement.deployer.impl.operators.ant.AntInterfaceException;
import org.gcube.common.vremanagement.deployer.impl.operators.ant.AntRunner;
import org.gcube.common.vremanagement.deployer.impl.operators.common.DeployException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.Report;
import org.gcube.common.vremanagement.deployer.impl.operators.deployment.Downloader;
import org.gcube.common.vremanagement.deployer.stubs.common.PackageInfo;
import org.gcube.common.vremanagement.deployer.impl.operators.patch.PatchExtractor;

/**
 * Applies a patch to local files
 * 
 * @author manuele simi (CNR-ISTI)
 *
 */
public class PatchOperator extends  GCUBEHandler<GCUBEHandler<?>> {

	private PackageInfo packageinfo;
	
	private  URI uri; 	
	
	private static final String SCRIPT = "apply.sh";

	private String callbackID = "";
	
	private boolean restart = false;

	private EndpointReferenceType callbackEPR = null;
	
	private GCUBEScope callerScope = null;
	
	/** 
	 * @param packageinfo information about the package to patch
	 * @param uri the URI from with the patch will be downloaded
	 * @param callbackEPR the EPR to notify about the result of the patch operation
	 * @param callbackID the ID of the operation as passed as input by the caller
	 */
	public PatchOperator(PackageInfo packageinfo, URI uri, EndpointReferenceType callbackEPR, String callbackID, boolean restart, GCUBEScope callerScope) {
		this.packageinfo = packageinfo;
		this.uri = uri;
		this.callbackID = callbackID;
		this.callbackEPR = callbackEPR;
		this.restart = restart;
		this.callerScope = callerScope;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override public void run() throws Exception {
		logger.info("Starting the patcher");
		
		//prepare the report to send back to the callback EPR
		Report finalreport = new Report(this.callbackEPR, this.callbackID,	1, Report.TYPE.PATCH, this.callerScope);
		
		//block until condition holds				
		DeployerOperator.deployLock.lock();
		
		try {
			Downloader downloader = new Downloader(this.packageinfo, true);
			downloader.downloadPatch(new java.net.URI(this.uri.toString()));								
			this.apply(Configuration.BASESOURCEDIR, new PatchExtractor(downloader).getPatchFolder());
			logger.info("patch succcessfully applied to " + this.packageinfo.getName());
		} catch (Exception e) {
			logger.error("failed to apply the patch to " + this.packageinfo.getName(), e);
			finalreport.addPackage(this.packageinfo, Report.PACKAGESTATUS.FAILED, 0);
		}
		try {
			finalreport.addPackage(this.packageinfo, Report.PACKAGESTATUS.PATCHED, 0);			
			//restart and send back the report
			finalreport.close();
			finalreport.send();		
			finalreport.save();		
		} catch (Exception e) {/*to avoid exception when the deployer is caller directly, without the VREManager mediation*/}
		
		if (restart) 
			GHNContext.getContext().restart();
		
		//release the deployment lock
		DeployerOperator.deployLock.unlock();

	}
	
	
	/**
	 * Applies the patch
	 * 
	 * @param srcDir the patch source folder
	 * @param file the patch file
	 * @throws DeployException
	 */
     private void apply(String srcDir, String destDir) throws DeployException {
		
    	Map<String, String> properties = new HashMap<String, String>();    	
    	    	
    	AntRunner local_run;
    	try {
			local_run = new org.gcube.common.vremanagement.deployer.impl.operators.ant.AntRunner();			
			local_run.init(Configuration.DEPLOYFILE, Configuration.BASEDIR);
		} catch (Exception e) {
			logger.error("Deployer is unable to initialize the patching environment", e);
			throw new DeployException("Deployer is unable to initialize the patching environment");
		}
		//apply the patch
		properties = new HashMap<String, String>();
		properties.put("base.script.dir", destDir );
		properties.put("exec.name", SCRIPT);
		
		try {						
			local_run.setProperties(properties, true);
			local_run.runTarget("runScript");
		} catch (AntInterfaceException aie) {			
			throw new DeployException (aie.getMessage());
		}
		//remove the dest folder
		properties = new HashMap<String, String>();
		properties.put("folder", destDir);
		try {									
			local_run.setProperties(properties, true);
			local_run.runTarget("deleteFolder");
		} catch (AntInterfaceException aie) {			
			throw new DeployException (aie.getMessage());
		}
		
		properties = new HashMap<String, String>();
		properties.put("folder", srcDir);
		try {									
			local_run.setProperties(properties, true);
			local_run.runTarget("deleteFolder");
		} catch (AntInterfaceException aie) {			
			throw new DeployException (aie.getMessage());
		}
		
		properties = new HashMap<String, String>();
		properties.put("base.deploy.dir", Configuration.BASESOURCEDIR);
		properties.put("package.source.dir",  Configuration.BASESOURCEDIR);
		try {									
			local_run.setProperties(properties, true);
			local_run.runTarget("deleteTempFiles");
		} catch (AntInterfaceException aie) {			
			throw new DeployException (aie.getMessage());
		}
	}
}
