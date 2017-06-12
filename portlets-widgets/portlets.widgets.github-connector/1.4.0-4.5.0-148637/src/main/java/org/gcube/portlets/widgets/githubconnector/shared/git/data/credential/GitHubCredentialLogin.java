package org.gcube.portlets.widgets.githubconnector.shared.git.data.credential;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class GitHubCredentialLogin extends GitHubCredential {

	private static final long serialVersionUID = 6967297424938062981L;
	private String user;
	private String password;

	public GitHubCredentialLogin() {
		super(GitHubCredentialType.Login);
	}

	public GitHubCredentialLogin(String user, String password) {
		super(GitHubCredentialType.Login);
		this.user = user;
		this.password = password;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "GitHubCredentialLogin [user=" + user + ", password=" + password
				+ "]";
	}

}
