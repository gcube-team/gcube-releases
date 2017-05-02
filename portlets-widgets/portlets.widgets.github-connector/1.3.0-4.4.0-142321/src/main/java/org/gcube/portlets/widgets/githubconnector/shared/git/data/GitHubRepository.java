package org.gcube.portlets.widgets.githubconnector.shared.git.data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class GitHubRepository implements Serializable {
	private static final long serialVersionUID = 3024419362503115243L;
	private long id;
	private String name;
	private GitHubUser owner;
	private String description;
	private String gitUrl;
	private int watchers;
	private Date createdAt;
	private Date updatedAt;

	public GitHubRepository() {
		super();
	}

	public GitHubRepository(long id, String name, GitHubUser owner,
			String description, String gitUrl, int watchers, Date createdAt,
			Date updatedAt) {
		super();
		this.id = id;
		this.name = name;
		this.owner = owner;
		this.description = description;
		this.gitUrl = gitUrl;
		this.watchers = watchers;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public GitHubUser getOwner() {
		return owner;
	}

	public void setOwner(GitHubUser owner) {
		this.owner = owner;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGitUrl() {
		return gitUrl;
	}

	public void setGitUrl(String gitUrl) {
		this.gitUrl = gitUrl;
	}

	public int getWatchers() {
		return watchers;
	}

	public void setWatchers(int watchers) {
		this.watchers = watchers;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "GitHubRepository [id=" + id + ", name=" + name + ", owner="
				+ owner + ", description=" + description + ", gitUrl=" + gitUrl
				+ ", watchers=" + watchers + ", createdAt=" + createdAt
				+ ", updatedAt=" + updatedAt + "]";
	}

}
