package org.gcube.portlets.widgets.githubconnector.shared.git.data.credential;

/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
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
