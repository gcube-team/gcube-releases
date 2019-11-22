package org.gcube.portlets.admin.vredeployer.shared;

import java.io.Serializable;

public class GHNMemory implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String virtualAvailable;
	String localAvailableSpace;
	public GHNMemory() {
		super();
		// TODO Auto-generated constructor stub
	}
	public GHNMemory(String virtualAvailable, String localAvailableSpace) {
		super();
		this.virtualAvailable = virtualAvailable + " MB";
		this.localAvailableSpace = localAvailableSpace + " MB";
	}
	public String getVirtualAvailable() {
		return virtualAvailable;
	}
	public void setVirtualAvailable(String virtualAvailable) {
		this.virtualAvailable = virtualAvailable;
	}
	public String getLocalAvailableSpace() {
		return localAvailableSpace;
	}
	public void setLocalAvailableSpace(String localAvailableSpace) {
		this.localAvailableSpace = localAvailableSpace;
	}
	
	
}
