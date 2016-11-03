package org.gcube.portlets.widgets.githubconnector.shared.git.data.credential;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class GitHubCredential implements Serializable {

	private static final long serialVersionUID = 6198756446799134610L;
	private GitHubCredentialType type;

	public GitHubCredential() {
		super();
	}

	public GitHubCredential(GitHubCredentialType type) {
		super();
		this.type = type;
	}

	public GitHubCredentialType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "GitHubCredential [type=" + type + "]";
	}

}
