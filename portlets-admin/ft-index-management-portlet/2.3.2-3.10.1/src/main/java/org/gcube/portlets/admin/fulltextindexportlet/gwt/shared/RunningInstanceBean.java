/**
 * 
 */
package org.gcube.portlets.admin.fulltextindexportlet.gwt.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A class used to transport IndexManagement Running Instance information
 * between the client and the service
 * 
 * @author Spyros Boutsis, NKUA
 */
public class RunningInstanceBean implements IsSerializable, Comparable<RunningInstanceBean> {

	/** The Running Instance endpoint reference */
	String RIEPR;
	
	/** 
	 * Empty constructor 
	 **/
	public RunningInstanceBean() {}
	
	/**
	 * Sets the running instance EPR
	 * @param EPR the epr
	 */
	public void setRunningInstanceEPR(String EPR) {
		this.RIEPR = EPR;
	}
	
	/**
	 * Returns the Running instance EPR
	 * @return the epr
	 */
	public String getRunningInstanceEPR() {
		return this.RIEPR;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(RunningInstanceBean o) {
		return this.RIEPR.compareTo((o.getRunningInstanceEPR()));
	}
	
}
