package org.gcube.portal.trainingmodule.dao;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.eclipse.persistence.annotations.CascadeOnDelete;

// TODO: Auto-generated Javadoc
/**
 * The Class TrainingUnitQuestionnaire.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 25, 2018
 */
@Entity
@CascadeOnDelete
public class TrainingUnitQuestionnaire implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4428289679721444265L;

	/** The interna id. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long internalId;
	
	/** The title. */
	private String title;
	
	/** The description. */
	@Lob 
	private String description;
	
	
	/** The questionnaire id. */
	private String questionnaireId;
	

	/** The questionnaire URL. */
	@Lob
	private String questionnaireURL;
	

	
	/**
	 * Instantiates a new training unit questionnaire.
	 */
	public TrainingUnitQuestionnaire() {
	}



	/**
	 * Instantiates a new training unit questionnaire.
	 *
	 * @param title the title
	 * @param description the description
	 * @param questionnaireId the questionnaire id
	 * @param questionnaireURL the questionnaire URL
	 */
	public TrainingUnitQuestionnaire(String title, String description, String questionnaireId,
			String questionnaireURL) {
		super();
		this.title = title;
		this.description = description;
		this.questionnaireId = questionnaireId;
		this.questionnaireURL = questionnaireURL;
	}
	
	


	/**
	 * Instantiates a new training unit questionnaire.
	 *
	 * @param internalId the internal id
	 * @param title the title
	 * @param description the description
	 * @param questionnaireId the questionnaire id
	 * @param questionnaireURL the questionnaire URL
	 */
	public TrainingUnitQuestionnaire(long internalId, String title, String description, String questionnaireId,
			String questionnaireURL) {
		super();
		this.internalId = internalId;
		this.title = title;
		this.description = description;
		this.questionnaireId = questionnaireId;
		this.questionnaireURL = questionnaireURL;
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
	 * Gets the questionnaire id.
	 *
	 * @return the questionnaire id
	 */
	public String getQuestionnaireId() {
		return questionnaireId;
	}



	/**
	 * Sets the questionnaire id.
	 *
	 * @param questionnaireId the new questionnaire id
	 */
	public void setQuestionnaireId(String questionnaireId) {
		this.questionnaireId = questionnaireId;
	}



	/**
	 * Gets the questionnaire URL.
	 *
	 * @return the questionnaire URL
	 */
	public String getQuestionnaireURL() {
		return questionnaireURL;
	}



	/**
	 * Sets the questionnaire URL.
	 *
	 * @param questionnaireURL the new questionnaire URL
	 */
	public void setQuestionnaireURL(String questionnaireURL) {
		this.questionnaireURL = questionnaireURL;
	}



	/**
	 * Gets the internal id.
	 *
	 * @return the internal id
	 */
	public long getInternalId() {
		return internalId;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrainingUnitQuestionnaire [internalId=");
		builder.append(internalId);
		builder.append(", title=");
		builder.append(title);
		builder.append(", description=");
		builder.append(description);
		builder.append(", questionnaireId=");
		builder.append(questionnaireId);
		builder.append(", questionnaireURL=");
		builder.append(questionnaireURL);
		builder.append("]");
		return builder.toString();
	}

}
