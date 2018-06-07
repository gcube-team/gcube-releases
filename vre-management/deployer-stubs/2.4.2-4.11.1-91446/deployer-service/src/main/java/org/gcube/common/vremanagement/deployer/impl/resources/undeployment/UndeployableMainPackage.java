package org.gcube.common.vremanagement.deployer.impl.resources.undeployment;


import java.io.File;
import java.util.HashMap;
import java.util.Set;

import org.gcube.common.vremanagement.deployer.impl.contexts.Configuration;
import org.gcube.common.vremanagement.deployer.impl.operators.ant.AntInterfaceException;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.contexts.GCUBEServiceContext.IllegalStateTransitionException;
import org.gcube.common.core.contexts.GCUBEServiceContext.StateTransitionException;
import org.gcube.common.core.contexts.GCUBEServiceContext.Status;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.vremanagement.deployer.impl.operators.ant.AntRunner;
import org.gcube.common.vremanagement.deployer.impl.operators.common.DeployException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.InvalidPackageArchiveException;
import org.gcube.common.vremanagement.deployer.impl.resources.BaseTypedPackage;

/**
 * Undeployable Main Package
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class UndeployableMainPackage extends UndeployablePackage {

	
	private static final long serialVersionUID = -3848609283797739276L;
	
	/**	Local Ant runner */ 
	protected transient AntRunner run;
	
	public UndeployableMainPackage(BaseTypedPackage base) {
		super(base);
		//initialise the ANT context
		try {					
			this.run = new AntRunner();			
			this.run.init(Configuration.DEPLOYFILE, Configuration.BASEDIR);
			
		} catch (Exception e) {
			logger.error("Package deployer is unable to initialize the deployment environment ", e);			
		}				
	}	


	/**
	 * {@inheritDoc}
	 */
	public void packageUndeploy(Set<GCUBEScope> scopes, boolean cleanState) throws DeployException,
			InvalidPackageArchiveException {
		logger.trace("Undeploy operation called on a " + this.getType().name() + " package");		
				
		//set the state of the RI DOWN, otherwise the RIprofile.xml will be written again after the cleanup, due to the GHN shutdown event propagated to
		//all the local RIs
		try {
				GHNContext.getContext().getServiceContext(this.getKey().getServiceClass(), this.getKey().getServiceName()).setStatus(Status.DOWN);
		} catch (IllegalStateTransitionException e) {
				logger.warn("Cannot move the RI to DOWN status from the current status");
		} catch (StateTransitionException e) {
				logger.warn("Failed to move the RI to DOWN status");
		} catch (Exception e) {
				throw new DeployException (e.getMessage());
		}
		
		try {	
			//undeploy the related Gar
			HashMap<String, String> properties = new HashMap<String, String>();
			properties.put("gar.name", this.getProperty("gar.name"));
			this.run.setProperties(properties, true);
			this.run.runTarget("undeployWSRFService");
			
			//clean the state if needed
			if (cleanState) {
				File state = new File(GHNContext.getContext().getStorageRoot() + File.separator + this.getKey().getServiceName());
				logger.debug("Cleaning up the package state in " + GHNContext.getContext().getStorageRoot() + File.separator + this.getKey().getServiceName() );
				if (state.exists()) {
					if (!deleteState(state)) {
						logger.error("Unable to clean up the package state");
						throw new DeployException ("Unable to clean up the package state");
					}
				} else 	
					logger.debug("The package has no state to clean up");				
			} else
				logger.debug("The client has requested to keep the actual state of the package");	
		} catch (AntInterfaceException aie) {
			logger.error("Unable to undeploy the package ", aie);
			throw new DeployException (aie.getMessage());
		} catch (Exception e) {
			throw new DeployException (e.getMessage());
		}
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean verify() throws InvalidPackageArchiveException {
		logger.debug("Verifying folder " + GHNContext.getContext().getLocation() + File.separator + "etc" + File.separator + this.getProperty("gar.name"));
		if ( new File(GHNContext.getContext().getLocation() + File.separator + "etc" + File.separator + this.getProperty("gar.name")).exists()) 
			return false;
		else
			return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean requireRestart() {
		return true;
	}

	/** Deletes the package state
	 * 
	 * @param state the root folder of the state
	 * @return true if the state has been successfully deleted, false otherwise
	 */
	private boolean deleteState(File state) {
	    if( state.exists() ) {
	    	for (File file : state.listFiles()) {		    		      
		         if(file.isDirectory()) 
		        	 deleteState(file);	         
		         else {
		        	 logger.trace("Deleting state file " + file.getAbsolutePath());
		        	 file.delete();
		         }
	    	}
	    }
	    return state.delete();
	 }

}
