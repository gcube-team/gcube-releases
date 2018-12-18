/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.edison.usersurvey_portlet.server.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "surveyuseranswer")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Surveyuseranswer.findAll", query = "SELECT s FROM Surveyuseranswer s"),
    @NamedQuery(name = "Surveyuseranswer.findById", query = "SELECT s FROM Surveyuseranswer s WHERE s.id = :id"),
    @NamedQuery(name = "Surveyuseranswer.findByIduseranswer", query = "SELECT s FROM Surveyuseranswer s WHERE s.iduseranswer = :iduseranswer"),
    @NamedQuery(name = "Surveyuseranswer.findByNumberquestion", query = "SELECT s FROM Surveyuseranswer s WHERE s.numberquestion = :numberquestion"),
    @NamedQuery(name = "Surveyuseranswer.findByAnswer1", query = "SELECT s FROM Surveyuseranswer s WHERE s.answer1 = :answer1"),
    @NamedQuery(name = "Surveyuseranswer.findByAnswer2", query = "SELECT s FROM Surveyuseranswer s WHERE s.answer2 = :answer2"),
    @NamedQuery(name = "Surveyuseranswer.findByAnswer3", query = "SELECT s FROM Surveyuseranswer s WHERE s.answer3 = :answer3"),
    @NamedQuery(name = "Surveyuseranswer.findByAnswer4", query = "SELECT s FROM Surveyuseranswer s WHERE s.answer4 = :answer4"),
    @NamedQuery(name = "Surveyuseranswer.findByAnswer5", query = "SELECT s FROM Surveyuseranswer s WHERE s.answer5 = :answer5"),
    @NamedQuery(name = "Surveyuseranswer.findByAnswer6", query = "SELECT s FROM Surveyuseranswer s WHERE s.answer6 = :answer6"),
    @NamedQuery(name = "Surveyuseranswer.findByAnswer7", query = "SELECT s FROM Surveyuseranswer s WHERE s.answer7 = :answer7"),
    @NamedQuery(name = "Surveyuseranswer.findByAnswer8", query = "SELECT s FROM Surveyuseranswer s WHERE s.answer8 = :answer8"),
    @NamedQuery(name = "Surveyuseranswer.findByAnswer9", query = "SELECT s FROM Surveyuseranswer s WHERE s.answer9 = :answer9"),
    @NamedQuery(name = "Surveyuseranswer.findByAnswer10", query = "SELECT s FROM Surveyuseranswer s WHERE s.answer10 = :answer10")})
public class Surveyuseranswer implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "iduseranswer")
    private int iduseranswer;
    @Basic(optional = false)
    @Column(name = "questiontype")
    private String questiontype;
    @Column(name = "numberquestion")
    private Integer numberquestion;
    @Column(name = "answer1")
    private String answer1;
    @Column(name = "answer2")
    private String answer2;
    @Column(name = "answer3")
    private String answer3;
    @Column(name = "answer4")
    private String answer4;
    @Column(name = "answer5")
    private String answer5;
    @Column(name = "answer6")
    private String answer6;
    @Column(name = "answer7")
    private String answer7;
    @Column(name = "answer8")
    private String answer8;
    @Column(name = "answer9")
    private String answer9;
    @Column(name = "answer10")
    private String answer10;
    @Column(name = "dateanswer")
    @Temporal(TemporalType.DATE)
    private Date dateanswer;
    @JoinColumn(name = "id_survey", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Survey idSurvey;

    public Surveyuseranswer() {
    }

    public Surveyuseranswer(Integer id) {
        this.id = id;
    }

    public Surveyuseranswer(Integer id, int iduseranswer) {
        this.id = id;
        this.iduseranswer = iduseranswer;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getIduseranswer() {
        return iduseranswer;
    }

    public void setIduseranswer(int iduseranswer) {
        this.iduseranswer = iduseranswer;
    }

    public Integer getNumberquestion() {
        return numberquestion;
    }

    public void setNumberquestion(Integer numberquestion) {
        this.numberquestion = numberquestion;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    public String getAnswer4() {
        return answer4;
    }

    public void setAnswer4(String answer4) {
        this.answer4 = answer4;
    }

    public String getAnswer5() {
        return answer5;
    }

    public void setAnswer5(String answer5) {
        this.answer5 = answer5;
    }

    public String getAnswer6() {
        return answer6;
    }

    public void setAnswer6(String answer6) {
        this.answer6 = answer6;
    }

    public String getAnswer7() {
        return answer7;
    }

    public void setAnswer7(String answer7) {
        this.answer7 = answer7;
    }

    public String getAnswer8() {
        return answer8;
    }

    public void setAnswer8(String answer8) {
        this.answer8 = answer8;
    }

    public String getAnswer9() {
        return answer9;
    }

    public void setAnswer9(String answer9) {
        this.answer9 = answer9;
    }

    public String getAnswer10() {
        return answer10;
    }

    public void setAnswer10(String answer10) {
        this.answer10 = answer10;
    }

    public Survey getIdSurvey() {
        return idSurvey;
    }

    public void setIdSurvey(Survey idSurvey) {
        this.idSurvey = idSurvey;
    }
    
    public Date getDateanswer() {
        return dateanswer;
    }

    public void setDateanswer(Date dateanswer) {
        this.dateanswer = dateanswer;
    }
    
    public String getQuestiontype() {
        return questiontype;
    }

    public void setQuestiontype(String questiontype) {
        this.questiontype = questiontype;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Surveyuseranswer)) {
            return false;
        }
        Surveyuseranswer other = (Surveyuseranswer) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.eng.edison.usersurvey_portlet.Surveyuseranswer[ id=" + id + " ]";
    }
    
}
