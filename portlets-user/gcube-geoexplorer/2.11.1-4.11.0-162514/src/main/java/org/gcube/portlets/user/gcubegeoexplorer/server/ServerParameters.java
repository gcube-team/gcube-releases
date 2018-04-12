/**
 * 
 */
package org.gcube.portlets.user.gcubegeoexplorer.server;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class ServerParameters {
	
	protected String url;
	protected String user;
	protected String password;
	protected String workspaces;
	
	public ServerParameters(){}
	
	/**
	 * @param url
	 * @param user
	 * @param password
	 */
	public ServerParameters(String url, String user, String password, String workspaces) {
		this.url = url;
		this.user = user;
		this.password = password;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServerParameters [url=");
		builder.append(url);
		builder.append(", user=");
		builder.append(user);
		builder.append(", password=");
		builder.append("************");
		builder.append("]");
		return builder.toString();
	}

	/**
	 * @return the workspaces
	 */
	public String getWorkspaces() {
		return workspaces;
	}
	
	/**
	 * @param workspaces the workspaces to set
	 */
	public void setWorkspaces(String workspaces) {
		this.workspaces = workspaces;
	}
}
