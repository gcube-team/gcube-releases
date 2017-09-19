package it.eng.edison.usersurvey_portlet.client.model;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * The Class SurveyModel.
 */
public class SurveyModel implements IsSerializable{
	
    /** The idsurvey. */
    private Integer idsurvey;
    
    /** The titlesurvey. */
    private String titlesurvey;
    
    /** The id user creator. */
    private Integer idUserCreator;
    
    /** The group id. */
    private Long groupId;
    
    /** The date survay. */
    private Date dateSurvay;
    
    /** The expired date survay. */
    private Date expiredDateSurvay;
    
    /** The is anonymous. */
    private Boolean isAnonymous;
    
    /** The creator fullname. */
    private String creatorFullname;
    
	/**
	 * Instantiates a new survey model.
	 */
	public SurveyModel() {
	}
	
	/**
	 * Gets the idsurvey.
	 *
	 * @return the idsurvey
	 */
	public Integer getIdsurvey() {
		return idsurvey;
	}
	
	/**
	 * Sets the idsurvey.
	 *
	 * @param idsurvey the new idsurvey
	 */
	public void setIdsurvey(Integer idsurvey) {
		this.idsurvey = idsurvey;
	}
	
	/**
	 * Gets the titlesurvey.
	 *
	 * @return the titlesurvey
	 */
	public String getTitlesurvey() {
		return titlesurvey;
	}
	
	/**
	 * Sets the titlesurvey.
	 *
	 * @param titlesurvey the new titlesurvey
	 */
	public void setTitlesurvey(String titlesurvey) {
		this.titlesurvey = titlesurvey;
	}
	
	/**
	 * Gets the id user creator.
	 *
	 * @return the id user creator
	 */
	public Integer getIdUserCreator() {
		return idUserCreator;
	}
	
	/**
	 * Sets the id user creator.
	 *
	 * @param idUserCreator the new id user creator
	 */
	public void setIdUserCreator(Integer idUserCreator) {
		this.idUserCreator = idUserCreator;
	}
	
	/**
	 * Gets the date survay.
	 *
	 * @return the date survay
	 */
	public Date getDateSurvay() {
		return dateSurvay;
	}
	
	/**
	 * Sets the date survay.
	 *
	 * @param dateSurvay the new date survay
	 */
	public void setDateSurvay(Date dateSurvay) {
		this.dateSurvay = dateSurvay;
	}
	
	/**
	 * Gets the expired date survay.
	 *
	 * @return the expired date survay
	 */
	public Date getExpiredDateSurvay() {
		return expiredDateSurvay;
	}

	/**
	 * Sets the expired date survay.
	 *
	 * @param expiredDateSurvay the new expired date survay
	 */
	public void setExpiredDateSurvay(Date expiredDateSurvay) {
		this.expiredDateSurvay = expiredDateSurvay;
	}
	
	/**
	 * Gets the checks if is anonymous.
	 *
	 * @return the checks if is anonymous
	 */
	public Boolean getIsAnonymous() {
		return isAnonymous;
	}
	
	/**
	 * Sets the checks if is anonymous.
	 *
	 * @param isAnonymous the new checks if is anonymous
	 */
	public void setIsAnonymous(Boolean isAnonymous) {
		this.isAnonymous = isAnonymous;
	}

	/**
	 * Gets the group id.
	 *
	 * @return the group id
	 */
	public Long getGroupId() {
		return groupId;
	}

	/**
	 * Sets the group id.
	 *
	 * @param groupId the new group id
	 */
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	/**
	 * Gets the creator fullname.
	 *
	 * @return the creator fullname
	 */
	public String getCreatorFullname() {
		return creatorFullname;
	}

	/**
	 * Sets the creator fullname.
	 *
	 * @param creatorFullname the new creator fullname
	 */
	public void setCreatorFullname(String creatorFullname) {
		this.creatorFullname = creatorFullname;
	}
    
}
