package org.gcube.portal.trainingmodule.dao;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

import org.eclipse.persistence.annotations.CascadeOnDelete;

// TODO: Auto-generated Javadoc
/**
 * The Class TrainingProject.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 10, 2018
 */
@Entity
@CascadeOnDelete
//@EntityListeners(PackageEntityListener.class)
public class TrainingUnit implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7762192161324748042L;

	/** The interna id. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	private TrainingProject trainingProjectRef;

	/** The list video. */
	@OneToMany(cascade=CascadeType.PERSIST, fetch=FetchType.EAGER, orphanRemoval=true)
	private List<TrainingVideo> listVideo = new ArrayList<TrainingVideo>();
	
	/** The list questionnaire. */
	@OneToMany(cascade=CascadeType.PERSIST, fetch=FetchType.EAGER, orphanRemoval=true)
	private List<TrainingUnitQuestionnaire> listQuestionnaire = new ArrayList<TrainingUnitQuestionnaire>();
	
	
	/**
	 * Instantiates a new training unit.
	 */
	public TrainingUnit() {

	}

	/**
	 * Instantiates a new training unit.
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
	public TrainingUnit(long internalId, String title, String workspaceFolderName, String description,
			String workspaceFolderId, String scope, String ownerLogin, TrainingProject trainingProjectRef) {
		super();
		this.internalId = internalId;
		this.title = title;
		this.workspaceFolderName = workspaceFolderName;
		this.description = description;
		this.workspaceFolderId = workspaceFolderId;
		this.scope = scope;
		this.ownerLogin = ownerLogin;
		this.trainingProjectRef = trainingProjectRef;
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
	public TrainingProject getTrainingProjectRef() {
		return trainingProjectRef;
	}


	/**
	 * Sets the training project ref.
	 *
	 * @param trainingProjectRef the new training project ref
	 */
	public void setTrainingProjectRef(TrainingProject trainingProjectRef) {
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
	 * Adds the questionnaire.
	 *
	 * @param qst the qst
	 */
	public void addQuestionnaire(TrainingUnitQuestionnaire qst) {
		this.listQuestionnaire.add(qst);
	}
	
	
	/**
	 * Gets the questionnaire.
	 *
	 * @param questionnaireId the questionnaire id
	 * @return the questionnaire
	 */
	public TrainingUnitQuestionnaire getQuestionnaireFor(String questionnaireId) {
		
		if(questionnaireId==null)
			return null;
		
		for (TrainingUnitQuestionnaire trainingUnitQuestionnaire : listQuestionnaire) {
			String qId = trainingUnitQuestionnaire.getQuestionnaireId();
			if(qId!=null && qId.compareTo(questionnaireId)==0) {
				return trainingUnitQuestionnaire;
			}
		}
		
		return null;
	}
	

	/**
	 * Adds the video.
	 *
	 * @param video the video
	 */
	public void addVideo(TrainingVideo video) {
		this.listVideo.add(video);
	}
	
	

	/**
	 * Gets the video for URL.
	 *
	 * @param videoURL the video URL
	 * @return the video for URL
	 */
	public TrainingVideo getVideoForURL(String videoURL) {
		
		if(videoURL==null || videoURL.isEmpty())
			return null;
		
		for (TrainingVideo video : listVideo) {
			if(videoURL.compareTo(video.getUrl())==0) {
				return video;
			}
		}
		
		return null;
	}
	
	

	/**
	 * Gets the list questionnaire.
	 *
	 * @return the list questionnaire
	 */
	public List<TrainingUnitQuestionnaire> getListQuestionnaire() {
		return listQuestionnaire;
	}
	
	

	/**
	 * Gets the list video.
	 *
	 * @return the list video
	 */
	public List<TrainingVideo> getListVideo() {
		return listVideo;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrainingUnit [internalId=");
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
		builder.append(trainingProjectRef!=null?trainingProjectRef.getInternalId():"null");
		builder.append(", listQuestionnaire size=");
		builder.append(listQuestionnaire.size());
		builder.append("]");
		return builder.toString();
	}
	
	



	
}
