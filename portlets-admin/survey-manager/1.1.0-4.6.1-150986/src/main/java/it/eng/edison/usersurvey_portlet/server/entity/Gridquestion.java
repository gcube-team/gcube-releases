package it.eng.edison.usersurvey_portlet.server.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the gridquestion database table.
 * 
 */
@Entity
@NamedQuery(name="Gridquestion.findAll", query="SELECT g FROM Gridquestion g")
public class Gridquestion implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name="field1")
	private String field1;

	@Column(name="field2")
	private String field2;

	@Column(name="gridlabel")
	private String gridlabel;

	@Column(name="id_survey")
	private Integer idSurvey;

	@Column(name="numberquestion")
	private Integer numberquestion;

	@Column(name="questiontype")
	private String questiontype;

	@Column(name="roworcolumnlabel")
	private String roworcolumnlabel;

	public Gridquestion() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getGridlabel() {
		return this.gridlabel;
	}

	public void setGridlabel(String gridlabel) {
		this.gridlabel = gridlabel;
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

	public String getRoworcolumnlabel() {
		return this.roworcolumnlabel;
	}

	public void setRoworcolumnlabel(String roworcolumnlabel) {
		this.roworcolumnlabel = roworcolumnlabel;
	}

}