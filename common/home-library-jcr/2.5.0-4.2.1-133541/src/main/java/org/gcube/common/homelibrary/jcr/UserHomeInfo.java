package org.gcube.common.homelibrary.jcr;

public class UserHomeInfo {

	protected String userId;
	protected String homeRoot;
	protected String dataRoot;
	protected String workspaceRoot;
	protected String dataAreaContentId;
	
	/**
	 * Create a new UserHomeInfo.
	 * @param userId the user id.
	 * @param homeRoot the user home root.
	 * @param dataRoot the user data root.
	 * @param workspaceRoot the user workspace root.
	 * @param dataAreaContentId the data area content id.
	 */
	public UserHomeInfo(String userId, String homeRoot, String dataRoot, String workspaceRoot, String dataAreaContentId) {
		this.userId = userId;
		this.homeRoot = homeRoot;
		this.dataRoot = dataRoot;
		this.workspaceRoot = workspaceRoot;
		this.dataAreaContentId = dataAreaContentId;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @return the homeRoot
	 */
	public String getHomeRoot() {
		return homeRoot;
	}

	/**
	 * @return the dataRoot
	 */
	public String getDataRoot() {
		return dataRoot;
	}

	/**
	 * @return the workspaceRoot
	 */
	public String getWorkspaceRoot() {
		return workspaceRoot;
	}

	/**
	 * @return the dataAreaContentId
	 */
	public String getDataAreaContentId() {
		return dataAreaContentId;
	}
}
