package org.gcube.portlets.widgets.githubconnector.shared.git.data.credential;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class GitHubCredentialOAuth2 extends GitHubCredential {

	private static final long serialVersionUID = 6967297424938062981L;
	private String token;

	public GitHubCredentialOAuth2() {
		super(GitHubCredentialType.OAuth2);
	}

	public GitHubCredentialOAuth2(String token) {
		super(GitHubCredentialType.OAuth2);
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "GitHubCredentialOAuth2 [token=" + token + "]";
	}

}
