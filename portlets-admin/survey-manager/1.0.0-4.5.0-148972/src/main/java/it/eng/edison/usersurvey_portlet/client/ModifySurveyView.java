package it.eng.edison.usersurvey_portlet.client;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import it.eng.edison.usersurvey_portlet.client.model.SurveyQuestionModel;

/**
 * The Class ModifySurveyView.
 */
public class ModifySurveyView extends Composite {

	private static ModifySurveyViewUiBinder uiBinder = GWT.create(ModifySurveyViewUiBinder.class);

	/**
	 * The Interface ModifySurveyViewUiBinder.
	 */
	interface ModifySurveyViewUiBinder extends UiBinder<Widget, ModifySurveyView> {
	}
	
	/** The vertical panel. */
	@UiField VerticalPanel verticalPanel;
	
	/** The survey question model list. */
	private List<SurveyQuestionModel> surveyQuestionModelList;
	
	/** The manage survey view. */
	private ManageSurveyView manageSurveyView;
	
	/** The title survay. */
	private String titleSurvay;
	
	/** The is anonymous. */
	private boolean isAnonymous;
	
	/** The id survey selected. */
	private int idSurveySelected;
	
	/** The expired survey date. */
	private Date expiredSurveyDate;
	
	/** The number of members filled survey. */
	private int numberOfMembersFilledSurvey;
	
	/** The user DTO. */
	private UserDTO userDTO;
	
	/**
	 * Instantiates a new modify survey view.
	 */
	public ModifySurveyView() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	/**
	 * Instantiates a new modify survey view.
	 *
	 * @param surveyQuestionModelList the survey question model list
	 */
	public ModifySurveyView(List<SurveyQuestionModel> surveyQuestionModelList) {
		this();
		this.surveyQuestionModelList = surveyQuestionModelList;
		addManageSurveyView();
	}
	
	/**
	 * Instantiates a new modify survey view.
	 *
	 * @param idSurveySelected the id survey selected
	 * @param titleSurvay the title survay
	 * @param isAnonymous the is anonymous
	 * @param expiredSurveyDate the expired survey date
	 * @param numberOfMembersFilledSurvey the number of members filled survey
	 * @param surveyQuestionModelList the survey question model list
	 */
	public ModifySurveyView(int idSurveySelected, String titleSurvay, boolean isAnonymous, Date expiredSurveyDate, int numberOfMembersFilledSurvey, List<SurveyQuestionModel> surveyQuestionModelList, UserDTO userDTO) {
		this();
		this.idSurveySelected = idSurveySelected;
		this.titleSurvay = titleSurvay;
		this.isAnonymous = isAnonymous;
		this.expiredSurveyDate = expiredSurveyDate;
		this.numberOfMembersFilledSurvey = numberOfMembersFilledSurvey;
		this.surveyQuestionModelList = surveyQuestionModelList;
		this.userDTO = userDTO;
		
		addManageSurveyView();
	}

	/**
	 * Adds the manage survey view.
	 */
	private void addManageSurveyView(){
		
		ManageSurveyView manageSurveyView = new ManageSurveyView(idSurveySelected, titleSurvay, isAnonymous, expiredSurveyDate, numberOfMembersFilledSurvey, getSurveyQuestionModelList(), getUserDTO());
		verticalPanel.add(manageSurveyView);
	}
	

	/**
	 * Gets the survey model list.
	 *
	 * @return the survey model list
	 */
	public List<SurveyQuestionModel> getSurveyModelList() {
		return surveyQuestionModelList;
	}

	/**
	 * Sets the survey model list.
	 *
	 * @param surveyModelList the new survey model list
	 */
	public void setSurveyModelList(List<SurveyQuestionModel> surveyModelList) {
		this.surveyQuestionModelList = surveyModelList;
	}

	/**
	 * Gets the survey question model list.
	 *
	 * @return the survey question model list
	 */
	public List<SurveyQuestionModel> getSurveyQuestionModelList() {
		return surveyQuestionModelList;
	}

	/**
	 * Sets the survey question model list.
	 *
	 * @param surveyQuestionModelList the new survey question model list
	 */
	public void setSurveyQuestionModelList(List<SurveyQuestionModel> surveyQuestionModelList) {
		this.surveyQuestionModelList = surveyQuestionModelList;
	}

	/**
	 * Gets the manage survey view.
	 *
	 * @return the manage survey view
	 */
	public ManageSurveyView getManageSurveyView() {
		return manageSurveyView;
	}

	/**
	 * Sets the manage survey view.
	 *
	 * @param manageSurveyView the new manage survey view
	 */
	public void setManageSurveyView(ManageSurveyView manageSurveyView) {
		this.manageSurveyView = manageSurveyView;
	}

	/**
	 * Gets the title survay.
	 *
	 * @return the title survay
	 */
	public String getTitleSurvay() {
		return titleSurvay;
	}

	/**
	 * Sets the title survay.
	 *
	 * @param titleSurvay the new title survay
	 */
	public void setTitleSurvay(String titleSurvay) {
		this.titleSurvay = titleSurvay;
	}

	/**
	 * Checks if is anonymous.
	 *
	 * @return true, if is anonymous
	 */
	public boolean isAnonymous() {
		return isAnonymous;
	}

	/**
	 * Sets the anonymous.
	 *
	 * @param isAnonymous the new anonymous
	 */
	public void setAnonymous(boolean isAnonymous) {
		this.isAnonymous = isAnonymous;
	}

	/**
	 * Gets the id survey selected.
	 *
	 * @return the id survey selected
	 */
	public int getIdSurveySelected() {
		return idSurveySelected;
	}

	/**
	 * Sets the id survey selected.
	 *
	 * @param idSurveySelected the new id survey selected
	 */
	public void setIdSurveySelected(int idSurveySelected) {
		this.idSurveySelected = idSurveySelected;
	}

	/**
	 * Gets the expired survey date.
	 *
	 * @return the expired survey date
	 */
	public Date getExpiredSurveyDate() {
		return expiredSurveyDate;
	}

	/**
	 * Sets the expired survey date.
	 *
	 * @param expiredSurveyDate the new expired survey date
	 */
	public void setExpiredSurveyDate(Date expiredSurveyDate) {
		this.expiredSurveyDate = expiredSurveyDate;
	}

	/**
	 * Gets the number of members filled survey.
	 *
	 * @return the number of members filled survey
	 */
	public int getNumberOfMembersFilledSurvey() {
		return numberOfMembersFilledSurvey;
	}

	/**
	 * Sets the number of members filled survey.
	 *
	 * @param numberOfMembersFilledSurvey the new number of members filled survey
	 */
	public void setNumberOfMembersFilledSurvey(int numberOfMembersFilledSurvey) {
		this.numberOfMembersFilledSurvey = numberOfMembersFilledSurvey;
	}

	public UserDTO getUserDTO() {
		return userDTO;
	}

	public void setUserDTO(UserDTO userDTO) {
		this.userDTO = userDTO;
	}
}
