package org.gcube.common.vremanagement.deployer.impl.operators.common;

import org.gcube.common.vremanagement.deployer.impl.resources.BasePackage;
import org.gcube.common.vremanagement.deployer.impl.resources.KeyData;


/**
 * Generic interface for a Deployment Scheduler. 
 * A Deployment Scheduler is a generic manager for scripts of the same type defined in a Package description 
 * included in the service profile.
 * 
 * @author Manuele Simi
 *
 */
public interface Scheduler {
	
	/**
	 * Adds the scripts belonging to the given package to the scheduler.
	 * 
	 * @param target the package
	 * @throws DeployException if the methods fails when adding the script 
	 *  
	 */
	public void add(BasePackage target) throws DeployException;
			
	/**
	 * Removes all the scripts of the given package from the scheduler
	 * 
	 * @param target the package 
	 * @throws DeployException if the methods fails when removing all the scripts
	 */
	public void remove(BasePackage target) throws DeployException;
	
	/**
	 * Executes all the scripts stored in the Scheduler for the given package
	 * 
	 * @param key the package key
	 * @throws DeployException if one of the scripts fails
	 */
	public void run(KeyData key) throws DeployException;
}
