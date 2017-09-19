package gr.cite.geoanalytics.dataaccess.entities.document;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "\"Document\"")
public class Document implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable
{

	@Id
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name = "\"DOC_ID\"", nullable = false)
	private UUID id = null;

	@Column(name = "\"DOC_Name\"", nullable = false, length = 250)
	private String name = null;

	@Lob
	@Type(type = "org.hibernate.type.TextType") //DEPWARN dependency to Hibernate and PostgreSQL (workaround for text~~bigint hibernate bug)
	@Column(name = "\"DOC_Description\"", nullable = true)
	private String description = null;

	@Column(name = "\"DOC_MimeType\"", nullable = true, length = 50)
	private String mimeType = null;

	@Column(name = "\"DOC_MimeSubType\"", nullable = true, length = 50)
	private String mimeSubType = null;

	/**
	 * The size of the specified document in bytes
	 */
	@Column(name = "\"DOC_Size\"", nullable = false)
	private int size = 0;

	@Column(name = "\"DOC_Url\"", nullable = true, length = 250)
	private String url = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"DOC_CreationDate\"", nullable = false)
	private Date creationDate = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"DOC_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;

	@ManyToOne
	@JoinColumn(name = "\"DOC_Creator\"", nullable = false)
	private Principal creator = null;

	@ManyToOne(optional=true)
	@JoinColumn(name = "\"DOC_Tenant\"", nullable = true)
	private Tenant tenant = null;

	public Document() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getMimeSubType() {
		return mimeSubType;
	}

	public void setMimeSubType(String mimeSubType) {
		this.mimeSubType = mimeSubType;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creation) {
		this.creationDate = creation;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Principal getCreator() {
		return creator;
	}

	public void setCreator(Principal creator) {
		this.creator = creator;
	}
	
	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	@Override
	public String toString()
	{
		return "Document(" + "id=" + getId() + " name=" + getName() + 
				" description=" + getDescription() +
				" mimeType=" + getMimeType() + " mimeSubType=" + getMimeSubType() +
				" size=" + getSize() + " url=" + getUrl() + 
				" creation=" + getCreationDate() + "lastUpdate=" + getLastUpdate() +
				" creator=" + (creator != null ? creator.getId() : null) +
				" customer=" + (tenant != null ? tenant.getId() : null);
	}
}
