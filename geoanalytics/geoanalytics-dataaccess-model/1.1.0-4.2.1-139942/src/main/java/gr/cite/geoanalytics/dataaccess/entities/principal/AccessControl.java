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
@Table(name="\"AccessControl\"")
public class AccessControl implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable{

	@Id	
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name="\"ACCN_ID\"", nullable = false)
	private UUID id = null;
	
	@ManyToOne(fetch = FetchType.LAZY , cascade=CascadeType.ALL)
	@JoinColumn(name="\"ACCN_Principal\"", nullable = false)
	private Principal principal = null;
	
	@Column(name="\"ACCN_EntityType\"", nullable = false, length = 500)
	private short entityType = 0;
	
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name="\"ACCN_Entity\"", nullable = false)
	private UUID entity = null;
	
	@Column(name="\"ACCN_Read\"", nullable = false)
	private short read = 0;
	
	@Column(name="\"ACCN_Edit\"", nullable = false)
	private short edit = 0;
	
	@Column(name="\"ACCN_Delete\"", nullable = false)
	private short delete = 0;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"ACCN_CreationDate\"", nullable = false)
	private Date creationDate = null;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"ACCN_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;

	public UUID getId() {
		return id;
	}

	public Principal getPrincipal() {
		return principal;
	}

	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}

	public UUID getEntity() {
		return entity;
	}

	public void setEntity(UUID entity) {
		this.entity = entity;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public short getEntityType() {
		return entityType;
	}

	public void setEntityType(short entityType) {
		this.entityType = entityType;
	}

	public short getRead() {
		return read;
	}

	public void setRead(short read) {
		this.read = read;
	}

	public short getEdit() {
		return edit;
	}

	public void setEdit(short edit) {
		this.edit = edit;
	}

	public short getDelete() {
		return delete;
	}

	public void setDelete(short delete) {
		this.delete = delete;
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
