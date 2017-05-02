/**
 * 
 */
package org.gcube.portlets.user.workspace.client.workspace;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class GWTUser implements IsSerializable, Comparable<GWTUser> {
	
	protected String id;
	protected String portaLogin;
	
	public GWTUser(){}
	
	/**
	 * @param id
	 * @param portaLogin
	 */
	public GWTUser(String id, String portaLogin) {
		this.id = id;
		this.portaLogin = portaLogin;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the portaLogin
	 */
	public String getPortaLogin() {
		return portaLogin;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(GWTUser user) {
		return portaLogin.compareTo(user.portaLogin);
	}
	

}
