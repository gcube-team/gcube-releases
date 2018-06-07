package org.gcube.data.analysis.tabulardata.metadata.tabularresource;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@NamedQueries({
	@NamedQuery(name="RelationLink.linksTo", 
			query="SELECT DISTINCT rl FROM RelationLink rl JOIN rl.linkedTabularResource str " +
					" WHERE str.id = :trid "),
	@NamedQuery(name="RelationLink.linkedBy", 
			query="SELECT DISTINCT rl FROM RelationLink rl JOIN rl.linksTotabularResource str " +
					" WHERE str.id = :trid ")
})
@Entity
@IdClass(ColumnId.class)
public class RelationLink {

	
	@Id
	long linkedTabularResourceId;
	
	@Id
	private String columnLocalId;

	
	@ManyToOne
	private StorableTabularResource linksTotabularResource;
	
	@ManyToOne
	private StorableTabularResource linkedTabularResource;
		
	public RelationLink() {
		super();
	}

	public RelationLink(StorableTabularResource linkedTabularResource, String columnLocalId, StorableTabularResource linksTotabularResource) {
		super();
		this.columnLocalId = columnLocalId;
		this.linkedTabularResourceId = linkedTabularResource.getId();
		this.linkedTabularResource = linkedTabularResource;
		this.linksTotabularResource = linksTotabularResource;
		this.columnLocalId = columnLocalId;
	}

	public String getColumnLocalId() {
		return columnLocalId;
	}

	public StorableTabularResource getLinksToTabulaResource() {
		return linksTotabularResource;
	}
			
	public StorableTabularResource getLinkedTabularResource() {
		return linkedTabularResource;
	}

	public void setLinksToTabularResource(
			StorableTabularResource linksTo) {
		this.linksTotabularResource = linksTo;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((columnLocalId == null) ? 0 : columnLocalId.hashCode());
		result = prime
				* result
				+ (int) (linkedTabularResourceId ^ (linkedTabularResourceId >>> 32));
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
		RelationLink other = (RelationLink) obj;
		if (columnLocalId == null) {
			if (other.columnLocalId != null)
				return false;
		} else if (!columnLocalId.equals(other.columnLocalId))
			return false;
		if (linkedTabularResourceId != other.linkedTabularResourceId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RelationLink [columnLocalId=" + columnLocalId
				+ ", tabularResource=" +(linksTotabularResource==null?null:linksTotabularResource.getId())
				+ ", linkedTabularResource=" + (linkedTabularResource==null?null:linkedTabularResource.getId()) + "]";
	}
	
}
