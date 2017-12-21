
package gr.cite.geoanalytics.dataaccess.entities.plugin;

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

import gr.cite.geoanalytics.dataaccess.entities.project.Project;
import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;

@Entity
@Table(name="\"PluginConfiguration\"")
public class PluginConfiguration implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable{
	@Id	
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name="\"PLGNC_ID\"", nullable = false)
	private UUID id = null;

	@ManyToOne(fetch = FetchType.EAGER )//, cascade=CascadeType.ALL)
	@JoinColumn(name="\"PLGNC_Plugin\"", nullable = false)
	private Plugin plugin = null;
	
	@ManyToOne(fetch = FetchType.EAGER )//, cascade=CascadeType.ALL)
	@JoinColumn(name="\"PLGNC_Project\"", nullable = false)
	private Project project = null;
	
	@Type(type="gr.cite.geoanalytics.dataaccess.typedefinition.XMLType") //DEPWARN XML Type: Hibernate dependency, replace when JPA 2.1 annotation is available
	@Column(name="\"PLGNC_Configuration\"", columnDefinition = "xml", nullable = false) //DEPWARN possible db portability issue
	private String configuration = "";
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"PLGNC_CreationDate\"", nullable = false)
	private Date creationDate = null;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"PLGNC_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public void setPlugin(Plugin plugin) {
		this.plugin = plugin;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PluginConfiguration)) {
			return false;
		}
		PluginConfiguration other = (PluginConfiguration) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "PluginConfiguration [id=" + id + ", plugin=" + plugin + ", project=" + project + ", configuration="
				+ configuration + ", creationDate=" + creationDate + ", lastUpdate=" + lastUpdate + "]";
	}
}