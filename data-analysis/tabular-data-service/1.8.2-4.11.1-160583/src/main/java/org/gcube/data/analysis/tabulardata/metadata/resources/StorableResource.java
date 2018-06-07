package org.gcube.data.analysis.tabulardata.metadata.resources;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.gcube.data.analysis.tabulardata.metadata.tabularresource.StorableTabularResource;
import org.gcube.data.analysis.tabulardata.model.resources.Resource;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;


@NamedQueries({
	@NamedQuery(name="RES.getById",query="SELECT DISTINCT sr FROM StorableResource sr LEFT JOIN sr.tabularResource str WHERE  "
			+ "str.id = :id ORDER BY sr.creationDate DESC"),
	@NamedQuery(name="RES.getByType",query="SELECT DISTINCT sr FROM StorableResource sr LEFT JOIN sr.tabularResource str WHERE  "
			+ "str.id = :id AND sr.type = :type ORDER BY sr.creationDate DESC")
})
@Entity
public class StorableResource {

	@Column
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long id;

	@Column
	private String description;

	@Column
	private String name;
	
	@Column
	private ResourceType type;

	@Column
	private Resource resource;

	@Column
	private long creatorId;

	@Temporal(TemporalType.TIMESTAMP)
	private Calendar creationDate = Calendar.getInstance();

	@Column
	private String columnId = null;

	@ManyToOne
	private StorableTabularResource tabularResource;

	public StorableResource(String name, String description, ResourceType type, long creatorId, 
			Resource resource) {
		super();
		this.name = name;
		this.description = description;
		this.type = type;
		this.resource = resource;
		this.creatorId = creatorId;
	}
		
	public StorableResource(String name, String description, ResourceType type, long creatorId, String columnId, 
			Resource resource) {
		this(name, description, type, creatorId, resource);
		this.columnId = columnId;
	}

	protected StorableResource(){}
	
	public StorableTabularResource getTabularResource() {
		return tabularResource;
	}

	public void setTabularResource(StorableTabularResource tabularResource) {
		this.tabularResource = tabularResource;
	}
		
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Calendar getCreationDate() {
		return creationDate;
	}

	public String getDescription() {
		return description;
	}

	public ResourceType getType() {
		return type;
	}

	public Resource getResource() {
		return resource;
	}

	public long getCreatorId() {
		return creatorId;
	}

	public String getColumnId() {
		return columnId;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((columnId == null) ? 0 : columnId.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StorableResource other = (StorableResource) obj;
		if (columnId == null) {
			if (other.columnId != null)
				return false;
		} else if (!columnId.equals(other.columnId))
			return false;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StorableResource [id=" + id + ", description=" + description
				+ ", type=" + type + ", resource=" + resource.getStringValue() + ", creatorId="
				+ creatorId + ", creationDate=" + creationDate + ", columnId="
				+ columnId + ", tabularResource=" + tabularResource + "]";
	}





}
