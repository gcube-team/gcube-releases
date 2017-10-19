/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.edison.usersurvey_portlet.server.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "invitationtoken")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Invitationtoken.findAll", query = "SELECT i FROM Invitationtoken i"),
    @NamedQuery(name = "Invitationtoken.findById", query = "SELECT i FROM Invitationtoken i WHERE i.id = :id"),
    @NamedQuery(name = "Invitationtoken.findByIduseranswer", query = "SELECT i FROM Invitationtoken i WHERE i.iduseranswer = :iduseranswer"),
    @NamedQuery(name = "Invitationtoken.findByIdSurvey", query = "SELECT i FROM Invitationtoken i WHERE i.idSurvey = :idSurvey"),
    @NamedQuery(name = "Invitationtoken.findByUuid", query = "SELECT i FROM Invitationtoken i WHERE i.uuid = :uuid"),
    @NamedQuery(name = "Invitationtoken.findByField1", query = "SELECT i FROM Invitationtoken i WHERE i.field1 = :field1"),
    @NamedQuery(name = "Invitationtoken.findByField2", query = "SELECT i FROM Invitationtoken i WHERE i.field2 = :field2"),
    @NamedQuery(name = "Invitationtoken.findByField3", query = "SELECT i FROM Invitationtoken i WHERE i.field3 = :field3"),
    @NamedQuery(name = "Invitationtoken.findByField4", query = "SELECT i FROM Invitationtoken i WHERE i.field4 = :field4"),
    @NamedQuery(name = "Invitationtoken.findByField5", query = "SELECT i FROM Invitationtoken i WHERE i.field5 = :field5")})
public class Invitationtoken implements Serializable {

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
    @Column(name = "id_survey")
    private int idSurvey;
    @Basic(optional = false)
    @Column(name = "uuid")
    private String uuid;
    @Column(name = "field1")
    private String field1;
    @Column(name = "field2")
    private String field2;
    @Column(name = "field3")
    private String field3;
    @Column(name = "field4")
    private String field4;
    @Column(name = "field5")
    private String field5;

    public Invitationtoken() {
    }

    public Invitationtoken(Integer id) {
        this.id = id;
    }

    public Invitationtoken(Integer id, int iduseranswer, int idSurvey, String uuid) {
        this.id = id;
        this.iduseranswer = iduseranswer;
        this.idSurvey = idSurvey;
        this.uuid = uuid;
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

    public int getIdSurvey() {
        return idSurvey;
    }

    public void setIdSurvey(int idSurvey) {
        this.idSurvey = idSurvey;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }

    public String getField4() {
        return field4;
    }

    public void setField4(String field4) {
        this.field4 = field4;
    }

    public String getField5() {
        return field5;
    }

    public void setField5(String field5) {
        this.field5 = field5;
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
        if (!(object instanceof Invitationtoken)) {
            return false;
        }
        Invitationtoken other = (Invitationtoken) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.eng.edison.usersurvey_portlet.server.entity.Invitationtoken[ id=" + id + " ]";
    }
    
}
