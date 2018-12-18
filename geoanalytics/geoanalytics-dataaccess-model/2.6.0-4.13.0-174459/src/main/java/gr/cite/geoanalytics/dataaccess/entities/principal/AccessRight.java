package gr.cite.geoanalytics.dataaccess.entities.principal;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;

import java.util.Date;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

@Entity
@Table(name="\"AccessRight\"")
public class AccessRight implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable{

	@Id	
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name="\"ACCR_ID\"", nullable = false)
	private UUID id = null;
	
	@ManyToOne(fetch = FetchType.LAZY , cascade=CascadeType.ALL)
	@JoinColumn(name="\"ACCR_Principal\"", nullable = false)
	private Principal principal = null;
	
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name="\"ACCR_Right\"", nullable = false)
	private UUID right = null;
	
	@Column(name="\"ACCR_Value\"", nullable = false)
	private short value = 0;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"ACCR_CreationDate\"", nullable = false)
	private Date creationDate = null;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"ACCR_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Principal getPrincipal() {
		return principal;
	}

	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}

	public UUID getRight() {
		return right;
	}

	public void setRight(UUID right) {
		this.right = right;
	}

	public AccessRightStatus getValue() {
		return AccessRightStatus.fromCode(this.value);
	}

	public void setValue(AccessRightStatus value) {
		this.value = value.code();
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
	
}
