package gr.cite.geoanalytics.dataaccess.entities.plugin;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;

@Entity
@Table(name="\"Plugin\"")
public class Plugin implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable{
	@Id	
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name="\"PLGN_ID\"", nullable = false)
	private UUID id = null;
	
	@Column(name="\"PLGN_Name\"", nullable = false, length = 255)
	private String name = "";
	
	@Column(name="\"PLGN_Description\"", nullable = false, length = 1000)
	private String descrtiption = "";
	
	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name="\"PLGN_Tenant\"")
	private Tenant tenant = null;
	
	@Type(type="gr.cite.geoanalytics.dataaccess.typedefinition.XMLType") //DEPWARN XML Type: Hibernate dependency, replace when JPA 2.1 annotation is available
	@Column(name="\"PLGN_Metadata\"", columnDefinition = "xml", nullable = false) //DEPWARN possible db portability issue
	private String metadata = "";
	
	@Column(name = "\"PLGN_Type\"", nullable = false)
	private short type = 0;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"PLGN_CreationDate\"", nullable = false)
	private Date creationDate = null;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"PLGN_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "plugin", cascade=CascadeType.REMOVE)
	private Set<PluginConfiguration> pluginConfiguration = new HashSet<PluginConfiguration>();
	
	@ManyToOne(fetch = FetchType.EAGER , cascade=CascadeType.ALL)
	@JoinColumn(name="\"PLGN_PLGNLIB\"", nullable = false)
	private PluginLibrary pluginLibrary = null;

	public PluginLibrary getPluginLibrary() {
		return pluginLibrary;
	}

	public void setPluginLibrary(PluginLibrary pluginLibrary) {
		this.pluginLibrary = pluginLibrary;
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

	public String getDescrtiption() {
		return descrtiption;
	}

	public void setDescrtiption(String descrtiption) {
		this.descrtiption = descrtiption;
	}

	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
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

	public Set<PluginConfiguration> getPluginConfiguration() {
		return pluginConfiguration;
	}

	public void setPluginConfiguration(Set<PluginConfiguration> pluginConfiguration) {
		this.pluginConfiguration = pluginConfiguration;
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
		if (!(obj instanceof Plugin)) {
			return false;
		}
		Plugin other = (Plugin) obj;
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
		return "Plugin [id=" + id + ", name=" + name + ", descrtiption=" + descrtiption + ", tenant=" + tenant
				+ ", metadata=" + metadata + ", type=" + type
				+ ", creationDate=" + creationDate
				+ ", lastUpdate=" + lastUpdate + "]";
	}
}
