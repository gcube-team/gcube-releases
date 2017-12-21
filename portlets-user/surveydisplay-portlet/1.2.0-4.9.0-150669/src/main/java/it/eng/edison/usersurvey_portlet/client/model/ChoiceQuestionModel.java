package it.eng.edison.usersurvey_portlet.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * The Class ChoiceQuestionModel.
 */
public class ChoiceQuestionModel implements IsSerializable{

    /** The id survey. */
    private Integer idSurvey;
    
    /** The number question. */
    private int numberQuestion;
    
    /** The question type. */
    private String questionType;
    
    /** The choice. */
    private String choice;
    
    /** The field 1. */
    private String field1;
    
    /** The field 2. */
    private String field2;
    
	/**
	 * Instantiates a new choice question model.
	 */
	public ChoiceQuestionModel() {
		super();
	}
	
	/**
	 * Instantiates a new choice question model.
	 *
	 * @param idSurvey the id survey
	 * @param numberQuestion the number question
	 * @param questionType the question type
	 * @param choice the choice
	 * @param field1 the field 1
	 * @param field2 the field 2
	 */
	public ChoiceQuestionModel(Integer idSurvey, int numberQuestion, String questionType, String choice, String field1,
			String field2) {
		this.idSurvey = idSurvey;
		this.numberQuestion = numberQuestion;
		this.questionType = questionType;
		this.choice = choice;
		this.field1 = field1;
		this.field2 = field2;
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
	 * Gets the number question.
	 *
	 * @return the number question
	 */
	public int getNumberQuestion() {
		return numberQuestion;
	}
	
	/**
	 * Sets the number question.
	 *
	 * @param numberQuestion the new number question
	 */
	public void setNumberQuestion(int numberQuestion) {
		this.numberQuestion = numberQuestion;
	}
	
	/**
	 * Gets the question type.
	 *
	 * @return the question type
	 */
	public String getQuestionType() {
		return questionType;
	}
	
	/**
	 * Sets the question type.
	 *
	 * @param questionType the new question type
	 */
	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}
	
	/**
	 * Gets the choice.
	 *
	 * @return the choice
	 */
	public String getChoice() {
		return choice;
	}
	
	/**
	 * Sets the choice.
	 *
	 * @param choice the new choice
	 */
	public void setChoice(String choice) {
		this.choice = choice;
	}
	
	/**
	 * Gets the field 1.
	 *
	 * @return the field 1
	 */
	public String getField1() {
		return field1;
	}
	
	/**
	 * Sets the field 1.
	 *
	 * @param field1 the new field 1
	 */
	public void setField1(String field1) {
		this.field1 = field1;
	}
	
	/**
	 * Gets the field 2.
	 *
	 * @return the field 2
	 */
	public String getField2() {
		return field2;
	}
	
	/**
	 * Sets the field 2.
	 *
	 * @param field2 the new field 2
	 */
	public void setField2(String field2) {
		this.field2 = field2;
	}
    
    
}
