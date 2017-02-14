package org.gcube.common.vremanagement.deployer.impl.operators.common;

import java.util.List;

import org.gcube.common.vremanagement.deployer.impl.resources.BasePackage;

/**
 * Scheduler to execute the packages' uninstallation scripts. 
 * 
 * @author Manuele Simi (ISTI-CNR)
 * @see Scheduler
 */
public class UninstallScheduler extends BaseScheduler {

	/** the singleton instance of the scheduler */
	protected static UninstallScheduler ref;
			
	private UninstallScheduler() {}
	
	/**
	 * Creates and returns a valid scheduler
	 * @return an uninstall scheduler instance
	 * @throws DeployException if the scheduler initialization fails
	 */
	public static synchronized Scheduler getScheduler() throws DeployException {
      if (ref == null)  
    	  ref = new UninstallScheduler();           
      return ref;
    }
	
	/** {@inheritDoc} */
	@Override
	protected List<String> getScriptsToSchedule(BasePackage target) {
		return target.getUninstallScripts();
	}

}
