package it.eng.edison.usersurvey_portlet.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



/**
 * The Class SurveyQuestionModel.
 */
@SuppressWarnings("serial")
public class SurveyQuestionModel implements Serializable {
	
    /** The idsurvey. */
    private Integer idsurvey;
    
    /** The numberquestion. */
    private int numberquestion;
    
    /** The ismandatory. */
    private Boolean ismandatory;
    
    /** The questiontype. */
    private String questiontype;
    
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
    
    /** The date answer. */
    private Date dateAnswer;
    
    /** The image file name. */
    private String imageFileName;
    
    /** The folder id image. */
    private Long folderIdImage;
    
    /** The section Title. */
    private String sectionTitle;
    
    /** The section Description. */
    private String sectionDescription;
    
    
    /** The multiple choice list. */
    private List<String> multipleChoiceList;
    
	/** The row grid list. */
	private List<String> rowGridList = null; 
	
	/** The column grid list. */
	private List<String> columnGridList = null; 
    
	/**
	 * Instantiates a new survey question model.
	 */
	public SurveyQuestionModel() {
		multipleChoiceList = new ArrayList<>();
	}

	/**
	 * Instantiates a new survey question model.
	 *
	 * @param idsurvey the idsurvey
	 * @param numberquestion the numberquestion
	 * @param questiontype the questiontype
	 * @param question the question
	 * @param answer1 the answer 1
	 */
	public SurveyQuestionModel(Integer idsurvey, int numberquestion, String questiontype, String question,
			String answer1) {
		this.idsurvey = idsurvey;
		this.numberquestion = numberquestion;
		this.questiontype = questiontype;
		this.question = question;
		this.answer1 = answer1;
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
	 * Gets the ismandatory.
	 *
	 * @return the ismandatory
	 */
	public Boolean getIsmandatory() {
		return ismandatory;
	}

	/**
	 * Sets the ismandatory.
	 *
	 * @param ismandatory the new ismandatory
	 */
	public void setIsmandatory(Boolean ismandatory) {
		this.ismandatory = ismandatory;
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
	 * Gets the image file name.
	 *
	 * @return the image file name
	 */
	public String getImageFileName() {
		return imageFileName;
	}

	/**
	 * Sets the image file name.
	 *
	 * @param imageFileName the new image file name
	 */
	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}

	/**
	 * Gets the folder id image.
	 *
	 * @return the folder id image
	 */
	public Long getFolderIdImage() {
		return folderIdImage;
	}

	/**
	 * Sets the folder id image.
	 *
	 * @param folderIdImage the new folder id image
	 */
	public void setFolderIdImage(Long folderIdImage) {
		this.folderIdImage = folderIdImage;
	}

	/**
	 * Gets the section title.
	 *
	 * @return the section title
	 */
	public String getSectionTitle() {
		return sectionTitle;
	}

	/**
	 * Sets the section title.
	 *
	 * @param sectionTitle the new section title
	 */
	public void setSectionTitle(String sectionTitle) {
		this.sectionTitle = sectionTitle;
	}

	/**
	 * Gets the section description.
	 *
	 * @return the section description
	 */
	public String getSectionDescription() {
		return sectionDescription;
	}

	/**
	 * Sets the section description.
	 *
	 * @param sectionDescription the new section description
	 */
	public void setSectionDescription(String sectionDescription) {
		this.sectionDescription = sectionDescription;
	}

	/**
	 * Gets the row grid list.
	 *
	 * @return the row grid list
	 */
	public List<String> getRowGridList() {
		return rowGridList;
	}

	/**
	 * Sets the row grid list.
	 *
	 * @param rowGridList the new row grid list
	 */
	public void setRowGridList(List<String> rowGridList) {
		this.rowGridList = rowGridList;
	}

	/**
	 * Gets the column grid list.
	 *
	 * @return the column grid list
	 */
	public List<String> getColumnGridList() {
		return columnGridList;
	}

	/**
	 * Sets the column grid list.
	 *
	 * @param columnGridList the new column grid list
	 */
	public void setColumnGridList(List<String> columnGridList) {
		this.columnGridList = columnGridList;
	}

	@Override
	public String toString() {
		return "SurveyQuestionModel [idsurvey=" + idsurvey + ", numberquestion=" + numberquestion + ", ismandatory="
				+ ismandatory + ", questiontype=" + questiontype + ", question=" + question + ", answer1=" + answer1
				+ ", answer2=" + answer2 + ", answer3=" + answer3 + ", dateAnswer=" + dateAnswer + ", imageFileName=" + imageFileName
				+ ", folderIdImage=" + folderIdImage + ", sectionTitle=" + sectionTitle + ", sectionDescription="
				+ sectionDescription + ", multipleChoiceList=" + multipleChoiceList + ", rowGridList=" + rowGridList
				+ ", columnGridList=" + columnGridList + "]";
	}


}
