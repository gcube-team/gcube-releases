package org.gcube.portlets.widgets.githubconnector.shared.git;

import java.io.Serializable;

import org.gcube.portlets.widgets.githubconnector.shared.git.data.credential.GitHubCredential;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class GitHubCloneSession implements Serializable {
	private static final long serialVersionUID = 4995026750312087485L;
	private String destinationFolderId;
	private GitHubCredential gitHubCredential;
	private String repositoryOwner;
	private String repositoryName;

	public GitHubCloneSession() {
		super();
	}

	/**
	 * 
	 * @param destinationFolderId
	 *            destination folder id
	 */
	public GitHubCloneSession(String destinationFolderId) {
		super();
		this.destinationFolderId = destinationFolderId;
	}

	/**
	 * 
	 * @param destinationFolderId
	 *            destination folder id
	 * @param gitHubCredential
	 *            git hub credential
	 * @param repositoryOwner
	 *            repository owner
	 * @param repositoryName
	 *            repository name
	 */
	public GitHubCloneSession(String destinationFolderId, GitHubCredential gitHubCredential, String repositoryOwner,
			String repositoryName) {
		super();
		this.destinationFolderId = destinationFolderId;
		this.gitHubCredential = gitHubCredential;
		this.repositoryOwner = repositoryOwner;
		this.repositoryName = repositoryName;
	}

	public GitHubCredential getGitHubCredential() {
		return gitHubCredential;
	}

	public void setGitHubCredential(GitHubCredential gitHubCredential) {
		this.gitHubCredential = gitHubCredential;
	}

	public String getDestinationFolderId() {
		return destinationFolderId;
	}

	public void setDestinationFolderId(String destinationFolderId) {
		this.destinationFolderId = destinationFolderId;
	}

	public String getRepositoryOwner() {
		return repositoryOwner;
	}

	public void setRepositoryOwner(String repositoryOwner) {
		this.repositoryOwner = repositoryOwner;
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	@Override
	public String toString() {
		return "GitHubCloneSession [destinationFolderId=" + destinationFolderId + ", gitHubCredential="
				+ gitHubCredential + ", repositoryOwner=" + repositoryOwner + ", repositoryName=" + repositoryName
				+ "]";
	}

}
