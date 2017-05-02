package it.eng.edison.usersurvey_portlet.server.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the choicequestion database table.
 * 
 */
@Entity
@NamedQuery(name="Choicequestion.findAll", query="SELECT c FROM Choicequestion c")
public class Choicequestion implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name="choice")
	private String choice;

	@Column(name="field1")
	private String field1;

	@Column(name="field2")
	private String field2;

	@Column(name="id_survey")
	private Integer idSurvey;
	
	@Column(name="numberquestion")
	private Integer numberquestion;

	@Column(name="questiontype")
	private String questiontype;

	public Choicequestion() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getChoice() {
		return this.choice;
	}

	public void setChoice(String choice) {
		this.choice = choice;
	}

	public String getField1() {
		return this.field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	public String getField2() {
		return this.field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}

	public Integer getIdSurvey() {
		return this.idSurvey;
	}

	public void setIdSurvey(Integer idSurvey) {
		this.idSurvey = idSurvey;
	}

	public Integer getNumberquestion() {
		return this.numberquestion;
	}

	public void setNumberquestion(Integer numberquestion) {
		this.numberquestion = numberquestion;
	}

	public String getQuestiontype() {
		return this.questiontype;
	}

	public void setQuestiontype(String questiontype) {
		this.questiontype = questiontype;
	}

}