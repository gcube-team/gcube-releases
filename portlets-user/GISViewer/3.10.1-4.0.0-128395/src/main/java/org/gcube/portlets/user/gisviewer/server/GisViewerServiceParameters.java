/**
 *
 */
package org.gcube.portlets.user.gisviewer.server;


/**
 * The Class GisViewerServiceParameters.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 2, 2016
 */
public class GisViewerServiceParameters {

	protected String geoServerUrl;
	protected String geoServerUser;
	protected String geoServerPwd;

	protected String geoNetworkUrl;
	protected String geoNetworkUser;
	protected String geoNetworkPwd;

	protected String transectUrl;
	protected String scope;

	protected String dataMinerUrl;

	/**
	 * Instantiates a new gis viewer service parameters.
	 */

	public GisViewerServiceParameters(){}


	/**
	 * Instantiates a new gis viewer service parameters.
	 *
	 * @param geoServerUrl the geo server url
	 * @param geoServerUser the geo server user
	 * @param geoServerPwd the geo server pwd
	 * @param geoNetworkUrl the geo network url
	 * @param geoNetworkUser the geo network user
	 * @param geoNetworkPwd the geo network pwd
	 * @param transectUrl the transect url
	 * @param dataMinerUrl the data miner url
	 * @param scope the scope
	 */
	public GisViewerServiceParameters(String geoServerUrl, String geoServerUser, String geoServerPwd, String geoNetworkUrl, String geoNetworkUser,
			String geoNetworkPwd, String transectUrl, String dataMinerUrl, String scope) {
		this.geoServerUrl = geoServerUrl;
		this.geoServerUser = geoServerUser;
		this.geoServerPwd = geoServerPwd;
		this.geoNetworkUrl = geoNetworkUrl;
		this.geoNetworkUser = geoNetworkUser;
		this.geoNetworkPwd = geoNetworkPwd;
		this.transectUrl = transectUrl;
		this.scope = scope;
		this.dataMinerUrl = dataMinerUrl;
	}

	/**
	 * Gets the geo server url.
	 *
	 * @return the geoServerUrl
	 */
	public String getGeoServerUrl() {
		return geoServerUrl;
	}

	/**
	 * Sets the geo server url.
	 *
	 * @param geoServerUrl the geoServerUrl to set
	 */
	public void setGeoServerUrl(String geoServerUrl) {
		this.geoServerUrl = geoServerUrl;
	}

	/**
	 * Gets the geo server user.
	 *
	 * @return the geoServerUser
	 */
	public String getGeoServerUser() {
		return geoServerUser;
	}

	/**
	 * Sets the geo server user.
	 *
	 * @param geoServerUser the geoServerUser to set
	 */
	public void setGeoServerUser(String geoServerUser) {
		this.geoServerUser = geoServerUser;
	}

	/**
	 * Gets the geo server pwd.
	 *
	 * @return the geoServerPwd
	 */
	public String getGeoServerPwd() {
		return geoServerPwd;
	}

	/**
	 * Sets the geo server pwd.
	 *
	 * @param geoServerPwd the geoServerPwd to set
	 */
	public void setGeoServerPwd(String geoServerPwd) {
		this.geoServerPwd = geoServerPwd;
	}

	/**
	 * Gets the geo network url.
	 *
	 * @return the geoNetworkUrl
	 */
	public String getGeoNetworkUrl() {
		return geoNetworkUrl;
	}

	/**
	 * Sets the geo network url.
	 *
	 * @param geoNetworkUrl the geoNetworkUrl to set
	 */
	public void setGeoNetworkUrl(String geoNetworkUrl) {
		this.geoNetworkUrl = geoNetworkUrl;
	}

	/**
	 * Gets the geo network user.
	 *
	 * @return the geoNetworkUser
	 */
	public String getGeoNetworkUser() {
		return geoNetworkUser;
	}

	/**
	 * Sets the geo network user.
	 *
	 * @param geoNetworkUser the geoNetworkUser to set
	 */
	public void setGeoNetworkUser(String geoNetworkUser) {
		this.geoNetworkUser = geoNetworkUser;
	}

	/**
	 * Gets the geo network pwd.
	 *
	 * @return the geoNetworkPwd
	 */
	public String getGeoNetworkPwd() {
		return geoNetworkPwd;
	}

	/**
	 * Sets the geo network pwd.
	 *
	 * @param geoNetworkPwd the geoNetworkPwd to set
	 */
	public void setGeoNetworkPwd(String geoNetworkPwd) {
		this.geoNetworkPwd = geoNetworkPwd;
	}

	/**
	 * Gets the transect url.
	 *
	 * @return the transectUrl
	 */
	public String getTransectUrl() {
		return transectUrl;
	}

	/**
	 * Sets the transect url.
	 *
	 * @param transectUrl the transectUrl to set
	 */
	public void setTransectUrl(String transectUrl) {
		this.transectUrl = transectUrl;
	}

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * Sets the scope.
	 *
	 * @param scope the scope to set
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}


	/**
	 * Gets the data miner url.
	 *
	 * @return the dataMinerUrl
	 */
	public String getDataMinerUrl() {

		return dataMinerUrl;
	}

	/**
	 * Sets the data miner url.
	 *
	 * @param dataMinerUrl the dataMinerUrl to set
	 */
	public void setDataMinerUrl(String dataMinerUrl) {

		this.dataMinerUrl = dataMinerUrl;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("GisViewerServiceParameters [geoServerUrl=");
		builder.append(geoServerUrl);
		builder.append(", geoServerUser=");
		builder.append(geoServerUser);
		builder.append(", geoServerPwd=");
		builder.append(geoServerPwd);
		builder.append(", geoNetworkUrl=");
		builder.append(geoNetworkUrl);
		builder.append(", geoNetworkUser=");
		builder.append(geoNetworkUser);
		builder.append(", geoNetworkPwd=");
		builder.append(geoNetworkPwd);
		builder.append(", transectUrl=");
		builder.append(transectUrl);
		builder.append(", scope=");
		builder.append(scope);
		builder.append(", dataMinerUrl=");
		builder.append(dataMinerUrl);
		builder.append("]");
		return builder.toString();
	}

}
