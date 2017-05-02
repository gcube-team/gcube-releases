package org.gcube.portlets.widgets.githubconnector.shared.git.data.credential;


/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public enum GitHubCredentialType {
	Anonymous("Anonymous"),
	OAuth2("OAuth2"), 
	Login("Login");
	
	
	/**
	 * @param text
	 */
	private GitHubCredentialType(final String id) {
		this.id = id;
	}

	private final String id;
	
	@Override
	public String toString() {
		return id;
	}
	
	public String getLabel() {
		return id;
	}
	
	
	public static GitHubCredentialType getFromId(String id) {
		for (GitHubCredentialType type : values()) {
			if (type.id.compareToIgnoreCase(id) == 0) {
				return type;
			}
		}
		return null;
	}
}
