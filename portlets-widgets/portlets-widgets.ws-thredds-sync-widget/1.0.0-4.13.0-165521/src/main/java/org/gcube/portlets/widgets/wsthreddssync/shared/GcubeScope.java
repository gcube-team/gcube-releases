package org.gcube.portlets.widgets.wsthreddssync.shared;


import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;


// TODO: Auto-generated Javadoc
/**
 * The Class GcubeVRE.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 14, 2018
 */
public class GcubeScope implements Serializable, IsSerializable, Comparable<GcubeScope>{


	/**
	 *
	 */
	private static final long serialVersionUID = -6427520549519606384L;


	private GcubeScopeType scopeType;


	private String scopeTitle;


	private String scopeName;

	/**
	 * Instantiates a new gcube VRE.
	 */
	public GcubeScope() {

	}

	/**
	 * Instantiates a new gcube scope.
	 *
	 * @param scopeTitle the scope title
	 * @param scopeName the scope name
	 * @param scopeType the scope type
	 */
	public GcubeScope(String scopeTitle, String scopeName, GcubeScopeType scopeType) {

		super();
		this.scopeTitle = scopeTitle;
		this.scopeName = scopeName;
		this.scopeType = scopeType;
	}


	/**
	 * @return the scopeType
	 */
	public GcubeScopeType getScopeType() {

		return scopeType;
	}


	/**
	 * @return the scopeTitle
	 */
	public String getScopeTitle() {

		return scopeTitle;
	}


	/**
	 * @return the scopeName
	 */
	public String getScopeName() {

		return scopeName;
	}


	/**
	 * @param scopeType the scopeType to set
	 */
	public void setScopeType(GcubeScopeType scopeType) {

		this.scopeType = scopeType;
	}


	/**
	 * @param scopeTitle the scopeTitle to set
	 */
	public void setScopeTitle(String scopeTitle) {

		this.scopeTitle = scopeTitle;
	}


	/**
	 * @param scopeName the scopeName to set
	 */
	public void setScopeName(String scopeName) {

		this.scopeName = scopeName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(GcubeScope o) {

		if(o==null)
			return -1;

		return this.getScopeName().compareTo(o.getScopeName());

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("GcubeScope [scopeType=");
		builder.append(scopeType);
		builder.append(", scopeTitle=");
		builder.append(scopeTitle);
		builder.append(", scopeName=");
		builder.append(scopeName);
		builder.append("]");
		return builder.toString();
	}


}
