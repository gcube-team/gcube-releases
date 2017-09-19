package gr.cite.geoanalytics.dataaccess.entities.tag;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "\"Tag\"")
public class Tag implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable {

	@Id
	@Type(type = "org.hibernate.type.PostgresUUIDType")
	@Column(name = "\"TAG_ID\"", nullable = false)
	private UUID id = null;

	@Column(name = "\"TAG_Name\"", nullable = false, length = 250)
	private String name = null;

	@Column(name = "\"TAG_Description\"", nullable = true, length = 1000)
	private String description = null;

	@ManyToOne
	@JoinColumn(name = "\"TAG_Creator\"", nullable = false)
	@JsonIgnore
	private Principal creator = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"TAG_CreationDate\"", nullable = false)
	private Date creationDate = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"TAG_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;

	public Tag() {}

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

	public Principal getCreator() {
		return creator;
	}

	public void setCreator(Principal creator) {
		this.creator = creator;
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

	public Tag withId(String id) {
		this.id = UUID.fromString(id);
		return this;
	}

	public Tag withName(String name) {
		this.name = name;
		return this;
	}

	public Tag withDescription(String description) {
		this.description = description;
		return this;
	}

	public Tag withCreator(Principal creator) {
		this.creator = creator;
		return this;
	}

	@Override
	public String toString() {
		return "Tag [id=" + getId() + ", name=" + getName() + ", description=" + getDescription() + "]";
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if (other == null || other.getClass() != this.getClass()) {
			return false;
		}

		if (name != null && !name.equals(((Tag) other).getName())) {
			return false;
		}
		if (description != null && !description.equals(((Tag) other).getDescription())) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
