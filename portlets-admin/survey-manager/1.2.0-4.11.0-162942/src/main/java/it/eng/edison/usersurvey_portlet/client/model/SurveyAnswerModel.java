package it.eng.edison.usersurvey_portlet.client.model;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * The Class SurveyAnswerModel.
 */
public class SurveyAnswerModel implements IsSerializable {

	/** The id user answer. */
	private Integer idUserAnswer;
	
	/** The id survey. */
	private Integer idSurvey;
	
	/** The numberquestion. */
	private int numberquestion;
	
	/** The answer 1. */
	private String answer1;
    
    /** The answer 2. */
    private String answer2;
    
    /** The answer 3. */
    private String answer3;
    
    /** The answer 4. */
    private String answer4;
    
    /** The answer 5. */
    private String answer5;
    
    /** The answer 6. */
    private String answer6;
    
    /** The answer 7. */
    private String answer7;
    
    /** The answer 8. */
    private String answer8;
    
    /** The answer 9. */
    private String answer9;
    
    /** The answer 10. */
    private String answer10;
    
    /** The date. */
    private Date date;
    
    /** The multiple choice list. */
    private List<String> multipleChoiceList;
    
	/**
	 * Instantiates a new survey answer model.
	 */
	public SurveyAnswerModel() {
	}
	

	/**
	 * Instantiates a new survey answer model.
	 *
	 * @param idUserAnswer the id user answer
	 * @param idSurvey the id survey
	 * @param numberquestion the numberquestion
	 * @param answer1 the answer 1
	 * @param answer2 the answer 2
	 * @param answer3 the answer 3
	 * @param answer4 the answer 4
	 * @param answer5 the answer 5
	 * @param answer6 the answer 6
	 * @param answer7 the answer 7
	 * @param answer8 the answer 8
	 * @param answer9 the answer 9
	 * @param answer10 the answer 10
	 */
	public SurveyAnswerModel(Integer idUserAnswer, Integer idSurvey, int numberquestion, String answer1, String answer2,
			String answer3, String answer4, String answer5, String answer6, String answer7, String answer8,
			String answer9, String answer10) {
		this.idUserAnswer = idUserAnswer;
		this.idSurvey = idSurvey;
		this.numberquestion = numberquestion;
		this.answer1 = answer1;
		this.answer2 = answer2;
		this.answer3 = answer3;
		this.answer4 = answer4;
		this.answer5 = answer5;
		this.answer6 = answer6;
		this.answer7 = answer7;
		this.answer8 = answer8;
		this.answer9 = answer9;
		this.answer10 = answer10;
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
	 * Gets the numberquestion.
	 *
	 * @return the numberquestion
	 */
	public int getNumberquestion() {
		return numberquestion;
	}

	/**
	 * Sets the numberquestion.
	 *
	 * @param numberquestion the new numberquestion
	 */
	public void setNumberquestion(int numberquestion) {
		this.numberquestion = numberquestion;
	}

	/**
	 * Gets the answer 1.
	 *
	 * @return the answer 1
	 */
	public String getAnswer1() {
		return answer1;
	}

	/**
	 * Sets the answer 1.
	 *
	 * @param answer1 the new answer 1
	 */
	public void setAnswer1(String answer1) {
		this.answer1 = answer1;
	}

	/**
	 * Gets the answer 2.
	 *
	 * @return the answer 2
	 */
	public String getAnswer2() {
		return answer2;
	}

	/**
	 * Sets the answer 2.
	 *
	 * @param answer2 the new answer 2
	 */
	public void setAnswer2(String answer2) {
		this.answer2 = answer2;
	}

	/**
	 * Gets the answer 3.
	 *
	 * @return the answer 3
	 */
	public String getAnswer3() {
		return answer3;
	}

	/**
	 * Sets the answer 3.
	 *
	 * @param answer3 the new answer 3
	 */
	public void setAnswer3(String answer3) {
		this.answer3 = answer3;
	}

	/**
	 * Gets the answer 4.
	 *
	 * @return the answer 4
	 */
	public String getAnswer4() {
		return answer4;
	}

	/**
	 * Sets the answer 4.
	 *
	 * @param answer4 the new answer 4
	 */
	public void setAnswer4(String answer4) {
		this.answer4 = answer4;
	}

	/**
	 * Gets the answer 5.
	 *
	 * @return the answer 5
	 */
	public String getAnswer5() {
		return answer5;
	}

	/**
	 * Sets the answer 5.
	 *
	 * @param answer5 the new answer 5
	 */
	public void setAnswer5(String answer5) {
		this.answer5 = answer5;
	}

	/**
	 * Gets the answer 6.
	 *
	 * @return the answer 6
	 */
	public String getAnswer6() {
		return answer6;
	}

	/**
	 * Sets the answer 6.
	 *
	 * @param answer6 the new answer 6
	 */
	public void setAnswer6(String answer6) {
		this.answer6 = answer6;
	}

	/**
	 * Gets the answer 7.
	 *
	 * @return the answer 7
	 */
	public String getAnswer7() {
		return answer7;
	}

	/**
	 * Sets the answer 7.
	 *
	 * @param answer7 the new answer 7
	 */
	public void setAnswer7(String answer7) {
		this.answer7 = answer7;
	}

	/**
	 * Gets the answer 8.
	 *
	 * @return the answer 8
	 */
	public String getAnswer8() {
		return answer8;
	}

	/**
	 * Sets the answer 8.
	 *
	 * @param answer8 the new answer 8
	 */
	public void setAnswer8(String answer8) {
		this.answer8 = answer8;
	}

	/**
	 * Gets the answer 9.
	 *
	 * @return the answer 9
	 */
	public String getAnswer9() {
		return answer9;
	}

	/**
	 * Sets the answer 9.
	 *
	 * @param answer9 the new answer 9
	 */
	public void setAnswer9(String answer9) {
		this.answer9 = answer9;
	}

	/**
	 * Gets the answer 10.
	 *
	 * @return the answer 10
	 */
	public String getAnswer10() {
		return answer10;
	}

	/**
	 * Sets the answer 10.
	 *
	 * @param answer10 the new answer 10
	 */
	public void setAnswer10(String answer10) {
		this.answer10 = answer10;
	}


	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}


	/**
	 * Sets the date.
	 *
	 * @param date the new date
	 */
	public void setDate(Date date) {
		this.date = date;
	}


	/**
	 * Gets the multiple choice list.
	 *
	 * @return the multiple choice list
	 */
	public List<String> getMultipleChoiceList() {
		return multipleChoiceList;
	}


	/**
	 * Sets the multiple choice list.
	 *
	 * @param multipleChoiceList the new multiple choice list
	 */
	public void setMultipleChoiceList(List<String> multipleChoiceList) {
		this.multipleChoiceList = multipleChoiceList;
	}
    
}
