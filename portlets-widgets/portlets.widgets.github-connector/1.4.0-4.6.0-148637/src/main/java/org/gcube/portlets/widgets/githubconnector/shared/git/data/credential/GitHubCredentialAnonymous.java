package org.gcube.portlets.widgets.githubconnector.shared.git.data.credential;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class GitHubCredentialAnonymous extends GitHubCredential {

	private static final long serialVersionUID = 6967297424938062981L;

	public GitHubCredentialAnonymous() {
		super(GitHubCredentialType.Anonymous);
	}

	@Override
	public String toString() {
		return "GitHubCredentialAnonymous []";
	}

}
