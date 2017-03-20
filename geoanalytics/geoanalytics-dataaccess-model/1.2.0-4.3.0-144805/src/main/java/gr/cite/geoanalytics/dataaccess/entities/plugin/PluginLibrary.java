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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.hibernate.metamodel.binding.FetchProfile.Fetch;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;

@Entity
@Table(name="\"PluginLibrary\"")
public class PluginLibrary implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable{
	@Id	
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name="\"PLGNLIB_ID\"", nullable = false)
	private UUID id = null;
	
	@Column(name="\"PLGNLIB_Name\"", nullable = false, length = 255)
	private String name = "";
	
	@Column(name = "\"PLGNLIB_Data\"", nullable = false)
	private byte[] data = null;
	
	@Column(name = "\"PLGNLIB_Checksum\"", nullable = false)
	private byte[] checksum = null;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"PLGNLIB_CreationDate\"", nullable = false)
	private Date creationDate = null;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"PLGNLIB_UpdateDate\"", nullable = false)
	private Date lastUpdate = null;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pluginLibrary", cascade=CascadeType.ALL)
	private Set<Plugin> plugins = new HashSet<Plugin>();
	
	public Set<Plugin> getPlugins() {
		return plugins;
	}
	public void setPlugins(Set<Plugin> plugins) {
		this.plugins = plugins;
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
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public byte[] getChecksum() {
		return checksum;
	}
	public void setChecksum(byte[] checksum) {
		this.checksum = checksum;
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
		if (!(obj instanceof PluginLibrary)) {
			return false;
		}
		PluginLibrary other = (PluginLibrary) obj;
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
		return "PluginLibrary [id=" + id + ", name=" + name + ", data=" + Arrays.toString(data) + ", checksum="
				+ Arrays.toString(checksum) + ", creationDate=" + creationDate + ", lastUpdate=" + lastUpdate + "]";
	}
}
