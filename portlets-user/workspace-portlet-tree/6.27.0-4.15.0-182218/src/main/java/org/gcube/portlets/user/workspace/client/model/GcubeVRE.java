/**
 *
 */
package org.gcube.portlets.user.workspace.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.user.client.rpc.IsSerializable;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Sep 27, 2017
 */
public class GcubeVRE extends BaseModelData implements IsSerializable{


	/**
	 *
	 */
	private static final long serialVersionUID = 4852709833439398009L;
	private String vreName;
	private String vreScope;

	/**
	 *
	 */
	protected GcubeVRE() {

	}

	
	public GcubeVRE(String vreName, String vreScope) {

		super();
		this.vreName = vreName;
		setName(vreName);
		this.vreScope = vreScope;
		setScope(vreScope);
	}

	public String getScope() {
		return get("scope");
	}

	public void setScope(String id) {
		set("scope", id);
	}

	public String getName() {
		return get("name");
	}

	public void setName(String name) {
		set("name", name);
	}

	/**
	 * @return the vreName
	 */
	public String getVreName() {

		return vreName;
	}


	/**
	 * @return the vreScope
	 */
	public String getVreScope() {

		return vreScope;
	}


	/**
	 * @param vreName the vreName to set
	 */
	public void setVreName(String vreName) {

		this.vreName = vreName;
	}


	/**
	 * @param vreScope the vreScope to set
	 */
	public void setVreScope(String vreScope) {

		this.vreScope = vreScope;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("GcubeVRE [vreName=");
		builder.append(vreName);
		builder.append(", vreScope=");
		builder.append(vreScope);
		builder.append("]");
		return builder.toString();
	}



}
