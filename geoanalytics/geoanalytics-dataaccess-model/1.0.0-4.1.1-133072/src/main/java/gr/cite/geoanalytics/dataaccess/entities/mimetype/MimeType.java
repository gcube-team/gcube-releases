package gr.cite.geoanalytics.dataaccess.entities.mimetype;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;

import java.util.Date;
import java.util.UUID;

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
@Table(name = "\"MimeType\"")
public class MimeType implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable
{

	@Id
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name = "\"MT_ID\"", nullable = false)
	private UUID id = null;

	@Column(name = "\"MT_MimeType\"", length = 50, nullable = false)
	private String mimeType = null;

	@Column(name = "\"MT_MimeSubType\"", nullable = true, length = 255)
	private String mimeSubType = null;

	/**
	 * space or comma separated extensions for the files of the type. The default export extension is the fist (?)
	 */
	@Column(name = "\"MT_FileNameExtension\"", nullable = false, length = 255)
	private String fileNameExtension = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"MT_CreationDate\"", nullable = false)
	private Date creationDate = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"MT_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "\"MT_Creator\"", nullable = false)
	private Principal creator = null;

	public MimeType() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
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

	public String getFileNameExtension() {
		return fileNameExtension;
	}

	public void setFileNameExtention(String fileNameExtension) {
		this.fileNameExtension = fileNameExtension;
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
	
	@Override
	public String toString()
	{
		return "MimeType(" + "id=" + getId() + " mimeType=" + getMimeType() + 
				" mimeSubType=" + getMimeSubType() + " extension=" + getFileNameExtension() +
				" creation=" + getCreationDate() + "lastUpdate=" + getLastUpdate() +
				" creator=" + (creator != null ? creator.getId() : null);
	}
}
