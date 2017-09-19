package org.gcube.common.vremanagement.deployer.impl.operators.common;

import java.util.List;

import org.gcube.common.vremanagement.deployer.impl.resources.BasePackage;

/**
 * Scheduler to execute the packages' installation scripts. 
 * 
 * @author Manuele Simi (ISTI-CNR)
 * @see Scheduler
 */
public class InstallScheduler extends BaseScheduler {					
	
	protected static InstallScheduler scheduler;		
	
	private InstallScheduler() {}
	
	/**
	 * Creates and returns a valid scheduler
	 * @return an install scheduler instance
	 * @throws DeployException if the Scheduler initialization fails
	 */
	public static Scheduler getScheduler() throws DeployException {
		if (scheduler == null)
			scheduler = new InstallScheduler();
		
		return scheduler;
	}

	/** {@inheritDoc} */
	@Override
	protected List<String> getScriptsToSchedule(BasePackage target) {
		return target.getInstallScripts();
	}

}
