package org.gcube.portlets.admin.vredeployer.server;

import org.gcube.vremanagement.vremodel.cl.stubs.types.GHN;
import org.gcube.vremanagement.vremodel.cl.stubs.types.RunningInstanceMessage;


/**
 * just a wrapper class
 * @author massi
 *
 */
public class AdditionalFuncInfo {
	/**
	 * 
	 */
	private GHN[] ghns;
	/**
	 * 
	 */
	private RunningInstanceMessage[] missingServices;
	/**
	 * 
	 */
	private RunningInstanceMessage[] foundServices;
	
	public AdditionalFuncInfo() {
		this.ghns = new GHN[0];
		this.missingServices = new RunningInstanceMessage[0];
	}

	public AdditionalFuncInfo(GHN[] ghns,
			RunningInstanceMessage[] missingServices,
			RunningInstanceMessage[] foundServices) {
		super();
		this.ghns = ghns;
		this.missingServices = missingServices;
		this.foundServices = foundServices;
	}

	public GHN[] getGhns() {
		return ghns;
	}

	public void setGhns(GHN[] ghns) {
		this.ghns = ghns;
	}

	public RunningInstanceMessage[] getMissingServices() {
		return missingServices;
	}

	public void setMissingServices(RunningInstanceMessage[] missingServices) {
		this.missingServices = missingServices;
	}

	public RunningInstanceMessage[] getFoundServices() {
		return foundServices;
	}

	public void setFoundServices(RunningInstanceMessage[] foundServices) {
		this.foundServices = foundServices;
	}
	
}
