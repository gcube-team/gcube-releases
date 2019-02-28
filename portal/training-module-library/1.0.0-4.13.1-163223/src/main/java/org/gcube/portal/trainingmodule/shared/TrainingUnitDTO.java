package org.gcube.portal.trainingmodule.shared;

import java.io.Serializable;

import javax.persistence.Lob;

// TODO: Auto-generated Javadoc
/**
 * The Class TrainingUnitDTO.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 22, 2018
 */
public class TrainingUnitDTO implements Serializable{
	
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4098480375079753341L;

	/** The internal id. */
	private long internalId;
	
	/** The title. */
	private String title;
	
	/** The folder name. */
	private String workspaceFolderName;
	
	/** The description. */
	@Lob 
	private String description;
	
	/** The workspace folder id. */
	private String workspaceFolderId;
	
	/** The scope. */
	private String scope;
	
	/** The owner. */
	private String ownerLogin;
	
	
	/** The training project ref. */
	private TrainingCourseDTO trainingProjectRef;
	
	/**
	 * Instantiates a new training unit DTO.
	 */
	public TrainingUnitDTO() {
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * Instantiates a new training unit DTO.
	 *
	 * @param internalId the internal id
	 */
	public TrainingUnitDTO(long internalId) {
		this.internalId = internalId;
	}
	
	
	/**
	 * Instantiates a new training unit DTO.
	 *
	 * @param title the title
	 * @param workspaceFolderName the workspace folder name
	 * @param description the description
	 * @param workspaceFolderId the workspace folder id
	 * @param scope the scope
	 * @param ownerLogin the owner login
	 * @param trainingProjectRef the training project ref
	 */
	public TrainingUnitDTO(String title, String workspaceFolderName, String description,
			String workspaceFolderId, String scope, String ownerLogin, TrainingCourseDTO trainingProjectRef) {
		this.title = title;
		this.workspaceFolderName = workspaceFolderName;
		this.description = description;
		this.workspaceFolderId = workspaceFolderId;
		this.scope = scope;
		this.ownerLogin = ownerLogin;
		this.trainingProjectRef = trainingProjectRef;
	}

	/**
	 * Instantiates a new training unit DTO.
	 *
	 * @param internalId the internal id
	 * @param title the title
	 * @param workspaceFolderName the workspace folder name
	 * @param description the description
	 * @param workspaceFolderId the workspace folder id
	 * @param scope the scope
	 * @param ownerLogin the owner login
	 * @param trainingProjectRef the training project ref
	 */
	public TrainingUnitDTO(long internalId, String title, String workspaceFolderName, String description,
			String workspaceFolderId, String scope, String ownerLogin, TrainingCourseDTO trainingProjectRef) {
		this(internalId);
		this.title = title;
		this.workspaceFolderName = workspaceFolderName;
		this.description = description;
		this.workspaceFolderId = workspaceFolderId;
		this.scope = scope;
		this.ownerLogin = ownerLogin;
		this.trainingProjectRef = trainingProjectRef;
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
	 * Gets the training project ref.
	 *
	 * @return the training project ref
	 */
	public TrainingCourseDTO getTrainingProjectRef() {
		return trainingProjectRef;
	}

	/**
	 * Sets the training project ref.
	 *
	 * @param trainingProjectRef the new training project ref
	 */
	public void setTrainingProjectRef(TrainingCourseDTO trainingProjectRef) {
		this.trainingProjectRef = trainingProjectRef;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrainingUnitDTO [internalId=");
		builder.append(internalId);
		builder.append(", title=");
		builder.append(title);
		builder.append(", workspaceFolderName=");
		builder.append(workspaceFolderName);
		builder.append(", description=");
		builder.append(description);
		builder.append(", workspaceFolderId=");
		builder.append(workspaceFolderId);
		builder.append(", scope=");
		builder.append(scope);
		builder.append(", ownerLogin=");
		builder.append(ownerLogin);
		builder.append(", trainingProjectRef=");
		builder.append(trainingProjectRef);
		builder.append("]");
		return builder.toString();
	}
	
	

}
