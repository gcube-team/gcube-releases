package org.gcube.portlets.user.trainingcourse.shared;

import java.util.List;

import org.gcube.portal.trainingmodule.shared.TrainingCourseDTO;


// TODO: Auto-generated Javadoc
/**
 * The Class TrainingCourseObj.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 1, 2018
 */
public class TrainingCourseObj extends TrainingCourseDTO{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4342164119873659648L;
	
	/** The user shared with. */
	private List<TrainingContact> userSharedWith;
	
	/** The user shared with. */
	private List<TrainingContact> groupSharedWith;
	
	/**
	 * Instantiates a new training course obj.
	 */
	public TrainingCourseObj() {
		// TODO Auto-generated constructor stub
	}

	

	/**
	 * Instantiates a new training course obj.
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
	public TrainingCourseObj(long internalId, String title, String description, String commitment, String languages,
			String scope, String ownerLogin, String workspaceFolderId, String workspaceFolderName, String createdBy,
			boolean courseActive, List<String> sharedWith) {
		super(internalId, title, description, commitment, languages, scope, ownerLogin, workspaceFolderId, workspaceFolderName,
				createdBy, courseActive, sharedWith);
	}



	/**
	 * Gets the user shared with.
	 *
	 * @return the user shared with
	 */
	public List<TrainingContact> getUserSharedWith() {
		return userSharedWith;
	}
	

	/**
	 * Gets the group shared with.
	 *
	 * @return the group shared with
	 */
	public List<TrainingContact> getGroupSharedWith() {
		return groupSharedWith;
	}

	/**
	 * Sets the user shared with.
	 *
	 * @param userSharedWith the new user shared with
	 */
	public void setUserSharedWith(List<TrainingContact> userSharedWith) {
		this.userSharedWith = userSharedWith;
	}
	
	

	public void setGroupSharedWith(List<TrainingContact> groupSharedWith) {
		this.groupSharedWith = groupSharedWith;
	}



	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrainingCourseObj [userSharedWith=");
		builder.append(userSharedWith);
		builder.append(", groupSharedWith=");
		builder.append(groupSharedWith);
		builder.append(", getInternalId()=");
		builder.append(getInternalId());
		builder.append(", getTitle()=");
		builder.append(getTitle());
		builder.append(", getDescription()=");
		builder.append(getDescription());
		builder.append(", getCommitment()=");
		builder.append(getCommitment());
		builder.append(", getLanguages()=");
		builder.append(getLanguages());
		builder.append(", getScope()=");
		builder.append(getScope());
		builder.append(", getOwnerLogin()=");
		builder.append(getOwnerLogin());
		builder.append(", getWorkspaceFolderId()=");
		builder.append(getWorkspaceFolderId());
		builder.append(", getWorkspaceFolderName()=");
		builder.append(getWorkspaceFolderName());
		builder.append(", getCreatedBy()=");
		builder.append(getCreatedBy());
		builder.append(", isCourseActive()=");
		builder.append(isCourseActive());
		builder.append(", getSharedWith()=");
		builder.append(getSharedWith());
		builder.append(", toString()=");
		builder.append(super.toString());
		builder.append(", getClass()=");
		builder.append(getClass());
		builder.append(", hashCode()=");
		builder.append(hashCode());
		builder.append("]");
		return builder.toString();
	}

	
}
