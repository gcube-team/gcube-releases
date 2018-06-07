package it.eng.edison.usersurvey_portlet.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * The Class TokenModel.
 */
public class TokenModel implements IsSerializable{
	
	/** The id user answer. */
	private Integer idUserAnswer;
	
	/** The id survey. */
	private Integer idSurvey;
	
	/** The token. */
	private String token;
	
	/** The uuid. */
	private String UUID;
	
	/** The email. */
	private String email;
	
	/**
	 * Instantiates a new token model.
	 */
	public TokenModel() {
	}
	
	/**
	 * Instantiates a new token model.
	 *
	 * @param idUserAnswer the id user answer
	 * @param idSurvey the id survey
	 * @param token the token
	 * @param email the email
	 */
	public TokenModel(Integer idUserAnswer, Integer idSurvey, String token, String email) {
		this.idUserAnswer = idUserAnswer;
		this.idSurvey = idSurvey;
		this.token = token;
		this.email = email;
	}
	
	/**
	 * Gets the id user answer.
	 *
	 * @return the id user answer
	 */
	public Integer getIdUserAnswer() {
		return idUserAnswer;
	}
	
	/**
	 * Sets the id user answer.
	 *
	 * @param idUserAnswer the new id user answer
	 */
	public void setIdUserAnswer(Integer idUserAnswer) {
		this.idUserAnswer = idUserAnswer;
	}
	
	/**
	 * Gets the id survey.
	 *
	 * @return the id survey
	 */
	public Integer getIdSurvey() {
		return idSurvey;
	}
	
	/**
	 * Sets the id survey.
	 *
	 * @param idSurvey the new id survey
	 */
	public void setIdSurvey(Integer idSurvey) {
		this.idSurvey = idSurvey;
	}
	
	/**
	 * Gets the token.
	 *
	 * @return the token
	 */
	public String getToken() {
		return token;
	}
	
	/**
	 * Sets the token.
	 *
	 * @param token the new token
	 */
	public void setToken(String token) {
		this.token = token;
	}
	
	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	
	/**
	 * Sets the email.
	 *
	 * @param email the new email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the uuid.
	 *
	 * @return the uuid
	 */
	public String getUUID() {
		return UUID;
	}

	/**
	 * Sets the uuid.
	 *
	 * @param uUID the new uuid
	 */
	public void setUUID(String uUID) {
		UUID = uUID;
	}

}
