/**
 *
 */
package org.gcube.datatransfer.resolver;

import org.gcube.datatransfer.resolver.GeonetworkRequestFilterParameters.MODE;
import org.gcube.datatransfer.resolver.GeonetworkRequestFilterParameters.VISIBILITY;


/**
 * The Class GeonetworkRequestCriteria.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 15, 2016
 */
public class GeonetworkRequestCriteria {

	private String scope;
	private MODE mode;
	private String owner; //This is optional
	private VISIBILITY visibility;
	/**
	 * @param scope
	 * @param mode
	 * @param owner
	 * @param visibility
	 */
	public GeonetworkRequestCriteria(
		String scope, MODE mode, String owner, VISIBILITY visibility) {

		super();
		this.scope = scope;
		this.mode = mode;
		this.owner = owner;
		this.visibility = visibility;
	}

	/**
	 * @return the scope
	 */
	public String getScope() {

		return scope;
	}

	/**
	 * @return the mode
	 */
	public MODE getMode() {

		return mode;
	}

	/**
	 * @return the owner
	 */
	public String getOwner() {

		return owner;
	}

	/**
	 * @return the visibility
	 */
	public VISIBILITY getVisibility() {

		return visibility;
	}

	/**
	 * @param scope the scope to set
	 */
	public void setScope(String scope) {

		this.scope = scope;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(MODE mode) {

		this.mode = mode;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {

		this.owner = owner;
	}

	/**
	 * @param visibility the visibility to set
	 */
	public void setVisibility(VISIBILITY visibility) {

		this.visibility = visibility;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("GeonetworkRequestCriteria [scope=");
		builder.append(scope);
		builder.append(", mode=");
		builder.append(mode);
		builder.append(", owner=");
		builder.append(owner);
		builder.append(", visibility=");
		builder.append(visibility);
		builder.append("]");
		return builder.toString();
	}



}
