package gr.cite.geoanalytics.dataaccess.entities.annotation;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

@Entity
@Table(name="\"Annotation\"")
public class Annotation implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable {
	
	@Id	
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name="\"AN_ID\"", nullable = false)
	private UUID id = null;
	
	@Column(name="\"AN_Title\"", nullable = false, length = 100)
	private String title = null;
	
	@Column(name="\"AN_IsShared\"", nullable = false)
	private short isShared = 0;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"AN_Date\"", nullable = false) //TODO could be nullable? (see ref schema)
	private Date date = null;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"AN_CreationDate\"", nullable = false)
	private Date creationDate = null;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"AN_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;
	
	@Lob
	@Type(type = "org.hibernate.type.TextType") //DEPWARN dependency to Hibernate and PostgreSQL (workaround for text~~bigint hibernate bug)
	@Column(name="\"AN_Body\"")
	private String body = null;

	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name="\"AN_Target\"", nullable=true)
	private UUID target = null;
	
	@OneToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "\"AN_InResponseTo\"")
	private Annotation inResponseTo = null;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "\"AN_Creator\"", nullable = false)
	private Principal creator = null;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "\"AN_Tenant\"", nullable = false)
	private Tenant tenant = null;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean getIsShared() {
		return isShared == 0 ? false : true;
	}

	public void setIsShared(boolean isShared) {
		this.isShared = (short) (isShared == true ? 1 : 0);
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public UUID getTarget() {
		return target;
	}

	public void setTarget(UUID target) {
		this.target = target;
	}

	public Annotation getInResponseTo() {
		return inResponseTo;
	}

	public void setInResponseTo(Annotation inResponseTo) {
		this.inResponseTo = inResponseTo;
	}

	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public void setIsShared(short isShared) {
		this.isShared = isShared;
	}

	public Principal getCreator() {
		return creator;
	}

	public void setCreator(Principal creator) {
		this.creator = creator;
	}

	@Override
	public String toString() {
		return "Annotation(" + "id=" + getId() + " title=" + getTitle() + " isShared=" + getIsShared() + " date="
				+ getDate() + " creation=" + getCreationDate() + "lastUpdate=" + getLastUpdate() + " body=" + getBody();
	}
	
}
