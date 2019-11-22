package it.eng.edison.usersurvey_portlet.client.model;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * The Class SurveyUserAnswerModel.
 */
public class SurveyUserAnswerModel implements IsSerializable{
	
	/** The iduseranswer. */
	private Integer iduseranswer;
    
    /** The idsurvey. */
    private Integer idsurvey;
    
    /** The numberquestion. */
    private int numberquestion;
    
    /** The question. */
    private String question;
    
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
    
    /** The grid answer list. */
    private List<String> gridAnswerList;
    
    /** The date answer. */
    private Date dateAnswer;
    
    /** The multiple choice list. */
    private List<String> multipleChoiceList;
    
    /** The questiontype. */
    private String questiontype;
    
	/**
	 * Instantiates a new survey user answer model.
	 */
	public SurveyUserAnswerModel() {
	}
	
	/**
	 * Instantiates a new survey user answer model.
	 *
	 * @param iduseranswer the iduseranswer
	 * @param idsurvey the idsurvey
	 * @param numberquestion the numberquestion
	 * @param question the question
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
	 * @param dateAnswer the date answer
	 */
	public SurveyUserAnswerModel(Integer iduseranswer, Integer idsurvey, int numberquestion, String question,
			String answer1, String answer2, String answer3, String answer4, String answer5, String answer6,
			String answer7, String answer8, String answer9, String answer10, Date dateAnswer) {
		this.iduseranswer = iduseranswer;
		this.idsurvey = idsurvey;
		this.numberquestion = numberquestion;
		this.question = question;
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
		this.dateAnswer = dateAnswer;
	}
	
	/**
	 * Gets the iduseranswer.
	 *
	 * @return the iduseranswer
	 */
	public Integer getIduseranswer() {
		return iduseranswer;
	}
	
	/**
	 * Sets the iduseranswer.
	 *
	 * @param iduseranswer the new iduseranswer
	 */
	public void setIduseranswer(Integer iduseranswer) {
		this.iduseranswer = iduseranswer;
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
	 * Gets the question.
	 *
	 * @return the question
	 */
	public String getQuestion() {
		return question;
	}
	
	/**
	 * Sets the question.
	 *
	 * @param question the new question
	 */
	public void setQuestion(String question) {
		this.question = question;
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
	 * Gets the date answer.
	 *
	 * @return the date answer
	 */
	public Date getDateAnswer() {
		return dateAnswer;
	}
	
	/**
	 * Sets the date answer.
	 *
	 * @param dateAnswer the new date answer
	 */
	public void setDateAnswer(Date dateAnswer) {
		this.dateAnswer = dateAnswer;
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
	
	/**
	 * Gets the questiontype.
	 *
	 * @return the questiontype
	 */
	public String getQuestiontype() {
		return questiontype;
	}

	/**
	 * Sets the questiontype.
	 *
	 * @param questiontype the new questiontype
	 */
	public void setQuestiontype(String questiontype) {
		this.questiontype = questiontype;
	}

	/**
	 * Gets the grid answer list.
	 *
	 * @return the grid answer list
	 */
	public List<String> getGridAnswerList() {
		return gridAnswerList;
	}

	/**
	 * Sets the grid answer list.
	 *
	 * @param gridAnswerList the new grid answer list
	 */
	public void setGridAnswerList(List<String> gridAnswerList) {
		this.gridAnswerList = gridAnswerList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SurveyUserAnswerModel [iduseranswer=");
		builder.append(iduseranswer);
		builder.append(", idsurvey=");
		builder.append(idsurvey);
		builder.append(", numberquestion=");
		builder.append(numberquestion);
		builder.append(", question=");
		builder.append(question);
		builder.append(", answer1=");
		builder.append(answer1);
		builder.append(", answer2=");
		builder.append(answer2);
		builder.append(", answer3=");
		builder.append(answer3);
		builder.append(", answer4=");
		builder.append(answer4);
		builder.append(", answer5=");
		builder.append(answer5);
		builder.append(", answer6=");
		builder.append(answer6);
		builder.append(", answer7=");
		builder.append(answer7);
		builder.append(", answer8=");
		builder.append(answer8);
		builder.append(", answer9=");
		builder.append(answer9);
		builder.append(", answer10=");
		builder.append(answer10);
		builder.append(", gridAnswerList=");
		builder.append(gridAnswerList);
		builder.append(", dateAnswer=");
		builder.append(dateAnswer);
		builder.append(", multipleChoiceList=");
		builder.append(multipleChoiceList);
		builder.append(", questiontype=");
		builder.append(questiontype);
		builder.append("]");
		return builder.toString();
	}
	
	
}
