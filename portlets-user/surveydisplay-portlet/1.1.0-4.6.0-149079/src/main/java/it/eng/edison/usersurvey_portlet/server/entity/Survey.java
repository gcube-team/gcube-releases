/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.edison.usersurvey_portlet.server.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "survey")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Survey.findAll", query = "SELECT s FROM Survey s"),
    @NamedQuery(name = "Survey.findById", query = "SELECT s FROM Survey s WHERE s.id = :id"),
    @NamedQuery(name = "Survey.findByTitlesurvey", query = "SELECT s FROM Survey s WHERE s.titlesurvey = :titlesurvey"),
    @NamedQuery(name = "Survey.findByIdusercreator", query = "SELECT s FROM Survey s WHERE s.idusercreator = :idusercreator"),
    @NamedQuery(name = "Survey.findByGroupid", query = "SELECT s FROM Survey s WHERE s.groupid = :groupid"),
    @NamedQuery(name = "Survey.findByDatesurvay", query = "SELECT s FROM Survey s WHERE s.datesurvay = :datesurvay"),
    @NamedQuery(name = "Survey.findByExpireddatesurvay", query = "SELECT s FROM Survey s WHERE s.expireddatesurvay = :expireddatesurvay"),
    @NamedQuery(name = "Survey.findByIsanonymous", query = "SELECT s FROM Survey s WHERE s.isanonymous = :isanonymous")})
public class Survey implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "titlesurvey")
    private String titlesurvey;
    @Basic(optional = false)
    @Column(name = "idusercreator")
    private int idusercreator;
    @Basic(optional = false)
    @Column(name = "groupid")
    private long groupid;
    @Column(name = "datesurvay")
    @Temporal(TemporalType.DATE)
    private Date datesurvay;
    @Column(name = "expireddatesurvay")
    @Temporal(TemporalType.DATE)
    private Date expireddatesurvay;
    @Column(name = "isanonymous")
    private Boolean isanonymous;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idSurvey")
    private Collection<Surveyuseranswer> surveyuseranswerCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idSurvey")
    private Collection<Surveyquestion> surveyquestionCollection;

    public Survey() {
    }

    public Survey(Integer id) {
        this.id = id;
    }

    public Survey(Integer id, String titlesurvey, int idusercreator) {
        this.id = id;
        this.titlesurvey = titlesurvey;
        this.idusercreator = idusercreator;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitlesurvey() {
        return titlesurvey;
    }

    public void setTitlesurvey(String titlesurvey) {
        this.titlesurvey = titlesurvey;
    }

    public int getIdusercreator() {
        return idusercreator;
    }

    public void setIdusercreator(int idusercreator) {
        this.idusercreator = idusercreator;
    }

    public Date getDatesurvay() {
        return datesurvay;
    }

    public void setDatesurvay(Date datesurvay) {
        this.datesurvay = datesurvay;
    }
    
    public Date getExpireddatesurvay() {
        return expireddatesurvay;
    }

    public void setExpiredDatesurvay(Date expireddatesurvay) {
        this.expireddatesurvay = expireddatesurvay;
    }
    
    public Boolean getIsanonymous() {
        return isanonymous;
    }

    public void setIsanonymous(Boolean isanonymous) {
        this.isanonymous = isanonymous;
    }
    
	public long getGroupid() {
		return groupid;
	}

	public void setGroupid(long groupid) {
		this.groupid = groupid;
	}

    @XmlTransient
    public Collection<Surveyuseranswer> getSurveyuseranswerCollection() {
        return surveyuseranswerCollection;
    }

    public void setSurveyuseranswerCollection(Collection<Surveyuseranswer> surveyuseranswerCollection) {
        this.surveyuseranswerCollection = surveyuseranswerCollection;
    }

    @XmlTransient
    public Collection<Surveyquestion> getSurveyquestionCollection() {
        return surveyquestionCollection;
    }

    public void setSurveyquestionCollection(Collection<Surveyquestion> surveyquestionCollection) {
        this.surveyquestionCollection = surveyquestionCollection;
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
        if (!(object instanceof Survey)) {
            return false;
        }
        Survey other = (Survey) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.eng.edison.usersurvey_portlet.Survey[ id=" + id + " ]";
    }

    
}
