package org.gcube.portlets.user.takecourse.dto;

import java.io.Serializable;
import java.util.List;

import org.gcube.portal.trainingmodule.shared.TrainingUnitDTO;

@SuppressWarnings("serial")
public class TrainingCourseWithUnits implements Serializable {

	private long id;

	/** The title. */
	private String title;

	private String description;

	/** The commitment. */
	private String commitment;

	/** The languages. */
	private String languages;

	/** The scope. */
	private String scope;

	/** The owner. */
	private TrainerDTO trainer;

	/** The workspace folder id. */
	private String workspaceFolderId;

	/** The folder name. */
	private String workspaceFolderName;
	
	/** The folder name. */
	private String createdBy;
	
	/** The course active. */
	private boolean courseActive = false;

	private List<TrainingUnitDTO> units;
	
	
	public TrainingCourseWithUnits() {
	}
	
	public TrainingCourseWithUnits(long internalId, String title, String description, String commitment, String languages,
			String scope, TrainerDTO trainer, String workspaceFolderId, String workspaceFolderName, String createdBy,
			boolean courseActive, List<TrainingUnitDTO> units) {
		super();
		this.id = internalId;
		this.title = title;
		this.description = description;
		this.commitment = commitment;
		this.languages = languages;
		this.scope = scope;
		this.trainer = trainer;
		this.workspaceFolderId = workspaceFolderId;
		this.workspaceFolderName = workspaceFolderName;
		this.createdBy = createdBy;
		this.courseActive = courseActive;
		this.units = units;
	}
	
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCommitment() {
		return commitment;
	}

	public void setCommitment(String commitment) {
		this.commitment = commitment;
	}

	public String getLanguages() {
		return languages;
	}

	public void setLanguages(String languages) {
		this.languages = languages;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public TrainerDTO getTrainer() {
		return trainer;
	}

	public void setTrainer(TrainerDTO trainer) {
		this.trainer = trainer;
	}

	public String getWorkspaceFolderId() {
		return workspaceFolderId;
	}

	public void setWorkspaceFolderId(String workspaceFolderId) {
		this.workspaceFolderId = workspaceFolderId;
	}

	public String getWorkspaceFolderName() {
		return workspaceFolderName;
	}

	public void setWorkspaceFolderName(String workspaceFolderName) {
		this.workspaceFolderName = workspaceFolderName;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public boolean isCourseActive() {
		return courseActive;
	}

	public void setCourseActive(boolean courseActive) {
		this.courseActive = courseActive;
	}

	

	public List<TrainingUnitDTO> getUnits() {
		return units;
	}

	public void setUnits(List<TrainingUnitDTO> units) {
		this.units = units;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrainingCourseWithUnits [Id=");
		builder.append(id);
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
		builder.append(", trainer=");
		builder.append(trainer);
		builder.append(", workspaceFolderId=");
		builder.append(workspaceFolderId);
		builder.append(", workspaceFolderName=");
		builder.append(workspaceFolderName);
		builder.append(", createdBy=");
		builder.append(createdBy);
		builder.append(", courseActive=");
		builder.append(courseActive);
		builder.append(", units=");
		builder.append(units);
		builder.append("]");
		return builder.toString();
	}

}
