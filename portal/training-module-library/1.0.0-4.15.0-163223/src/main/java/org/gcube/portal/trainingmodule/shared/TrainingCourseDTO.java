package org.gcube.portal.trainingmodule.shared;

import java.io.Serializable;
import java.util.List;


// TODO: Auto-generated Javadoc
/**
 * The Class TrainingCourseDTO.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 22, 2018
 */
public class TrainingCourseDTO implements Serializable{
	
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2249997323662668401L;

	/** The internal id. */
	private long internalId;

	/** The title. */
	private String title;

	/** The description. */
	private String description;

	/** The commitment. */
	private String commitment;

	/** The languages. */
	private String languages;

	/** The scope. */
	private String scope;

	/** The owner. */
	private String ownerLogin;

	/** The workspace folder id. */
	private String workspaceFolderId;

	/** The folder name. */
	private String workspaceFolderName;
	
	/** The folder name. */
	private String createdBy;
	
	/** The course active. */
	private boolean courseActive = false;

	/** The shared with. */
	private List<String> sharedWith;
	
	
	/**
	 * Instantiates a new training course DTO.
	 */
	public TrainingCourseDTO() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Instantiates a new training course DTO.
	 *
	 * @param internalId the internal id
	 */
	public TrainingCourseDTO(long internalId) {
		this.internalId = internalId;
	}
	
	
	/**
	 * Instantiates a new training course DTO.
	 *
	 * @param title the title
	 * @param description the description
	 * @param commitment the commitment
	 * @param languages the languages
	 * @param scope the scope
	 * @param ownerLogin the owner login
	 * @param workspaceFolderId the workspace folder id
	 * @param workspaceFolderName the workspace folder name
	 * @param createdBy the created by
	 * @param courseActive the course active
	 * @param sharedWith the shared with
	 */
	public TrainingCourseDTO(String title, String description, String commitment, String languages,
			String scope, String ownerLogin, String workspaceFolderId, String workspaceFolderName, String createdBy,
			boolean courseActive, List<String> sharedWith) {
		this.title = title;
		this.description = description;
		this.commitment = commitment;
		this.languages = languages;
		this.scope = scope;
		this.ownerLogin = ownerLogin;
		this.workspaceFolderId = workspaceFolderId;
		this.workspaceFolderName = workspaceFolderName;
		this.createdBy = createdBy;
		this.courseActive = courseActive;
		this.sharedWith = sharedWith;
	}
	
	/**
	 * Instantiates a new training course DTO.
	 *
	 * @param internalId the internal id
	 * @param title the title
	 * @param description the description
	 * @param commitment the commitment
	 * @param languages the languages
	 * @param scope the scope
	 * @param ownerLogin the owner login
	 * @param workspaceFolderId the workspace folder id
	 * @param workspaceFolderName the workspace folder name
	 * @param createdBy the created by
	 * @param courseActive the course active
	 * @param sharedWith the shared with
	 */
	public TrainingCourseDTO(long internalId, String title, String description, String commitment, String languages,
			String scope, String ownerLogin, String workspaceFolderId, String workspaceFolderName, String createdBy,
			boolean courseActive, List<String> sharedWith) {
		this(internalId);
		this.title = title;
		this.description = description;
		this.commitment = commitment;
		this.languages = languages;
		this.scope = scope;
		this.ownerLogin = ownerLogin;
		this.workspaceFolderId = workspaceFolderId;
		this.workspaceFolderName = workspaceFolderName;
		this.createdBy = createdBy;
		this.courseActive = courseActive;
		this.sharedWith = sharedWith;
	}

	/**
	 * Gets the internal id.
	 *
	 * @return the internal id
	 */
	public long getInternalId() {
		return internalId;
	}

	/**
	 * Sets the internal id.
	 *
	 * @param internalId the new internal id
	 */
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title.
	 *
	 * @param title the new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the commitment.
	 *
	 * @return the commitment
	 */
	public String getCommitment() {
		return commitment;
	}

	/**
	 * Sets the commitment.
	 *
	 * @param commitment the new commitment
	 */
	public void setCommitment(String commitment) {
		this.commitment = commitment;
	}

	/**
	 * Gets the languages.
	 *
	 * @return the languages
	 */
	public String getLanguages() {
		return languages;
	}

	/**
	 * Sets the languages.
	 *
	 * @param languages the new languages
	 */
	public void setLanguages(String languages) {
		this.languages = languages;
	}

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * Sets the scope.
	 *
	 * @param scope the new scope
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	/**
	 * Gets the owner login.
	 *
	 * @return the owner login
	 */
	public String getOwnerLogin() {
		return ownerLogin;
	}

	/**
	 * Sets the owner login.
	 *
	 * @param ownerLogin the new owner login
	 */
	public void setOwnerLogin(String ownerLogin) {
		this.ownerLogin = ownerLogin;
	}

	/**
	 * Gets the workspace folder id.
	 *
	 * @return the workspace folder id
	 */
	public String getWorkspaceFolderId() {
		return workspaceFolderId;
	}

	/**
	 * Sets the workspace folder id.
	 *
	 * @param workspaceFolderId the new workspace folder id
	 */
	public void setWorkspaceFolderId(String workspaceFolderId) {
		this.workspaceFolderId = workspaceFolderId;
	}

	/**
	 * Gets the workspace folder name.
	 *
	 * @return the workspace folder name
	 */
	public String getWorkspaceFolderName() {
		return workspaceFolderName;
	}

	/**
	 * Sets the workspace folder name.
	 *
	 * @param workspaceFolderName the new workspace folder name
	 */
	public void setWorkspaceFolderName(String workspaceFolderName) {
		this.workspaceFolderName = workspaceFolderName;
	}

	/**
	 * Gets the created by.
	 *
	 * @return the created by
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * Sets the created by.
	 *
	 * @param createdBy the new created by
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * Checks if is course active.
	 *
	 * @return true, if is course active
	 */
	public boolean isCourseActive() {
		return courseActive;
	}

	/**
	 * Sets the course active.
	 *
	 * @param courseActive the new course active
	 */
	public void setCourseActive(boolean courseActive) {
		this.courseActive = courseActive;
	}

	/**
	 * Gets the shared with.
	 *
	 * @return the shared with
	 */
	public List<String> getSharedWith() {
		return sharedWith;
	}

	/**
	 * Sets the shared with.
	 *
	 * @param sharedWith the new shared with
	 */
	public void setSharedWith(List<String> sharedWith) {
		this.sharedWith = sharedWith;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrainingCourseDTO [internalId=");
		builder.append(internalId);
		builder.append(", title=");
		builder.append(title);
		builder.append(", description=");
		builder.append(description);
		builder.append(", commitment=");
		builder.append(commitment);
		builder.append(", languages=");
		builder.append(languages);
		builder.append(", scope=");
		builder.append(scope);
		builder.append(", ownerLogin=");
		builder.append(ownerLogin);
		builder.append(", workspaceFolderId=");
		builder.append(workspaceFolderId);
		builder.append(", workspaceFolderName=");
		builder.append(workspaceFolderName);
		builder.append(", createdBy=");
		builder.append(createdBy);
		builder.append(", courseActive=");
		builder.append(courseActive);
		builder.append(", sharedWith=");
		builder.append(sharedWith);
		builder.append("]");
		return builder.toString();
	}


}
