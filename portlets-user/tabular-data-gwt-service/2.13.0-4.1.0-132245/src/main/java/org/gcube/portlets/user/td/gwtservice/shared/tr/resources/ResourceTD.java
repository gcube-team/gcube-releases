package org.gcube.portlets.user.td.gwtservice.shared.tr.resources;

import java.io.Serializable;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ResourceTD implements Serializable {

	private static final long serialVersionUID = -6324769323093791963L;

	private String stringValue;

	public ResourceTD() {
	}
	
	/**
	 * 
	 * @param stringValue
	 */
	public ResourceTD(String stringValue) {
		super();
		this.stringValue = stringValue;
	}



	public String getStringValue() {
		return stringValue;
	}

	

	@Override
	public String toString() {
		return "ResourceTD [stringValue=" + stringValue + "]";
	}

}
