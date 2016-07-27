
package org.gcube.common.core.resources;

import java.net.URI;
import java.util.Calendar;


public abstract class GCUBEMCollection  extends GCUBEResource {
	/**
	 * The type of the resource.
	 */
	public static final String TYPE="MetadataCollection";
	

	
	public GCUBEMCollection() {
		this.type = TYPE;
	}
	
	private String Description;
	private String Name;
	private boolean isUserCollection= false;
	private boolean isIndexable= false;
	private boolean IsEditable= false;
	private Calendar creationTime;
	private String creator;
	private int numberOfMembers;
	private Calendar lastUpdateTime;
	private Calendar previousUpdateTime;
	private String lastModifier;
	private String OID;
	private RelatedCollection relCollection;
	private MetadataFormat metaFormat;
	private GeneratedBy generateBy;



	public Calendar getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(Calendar creationTime) {
		this.creationTime = creationTime;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public GeneratedBy getGenerateBy() {
		return generateBy;
	}
	public void setGenerateBy(GeneratedBy generateBy) {
		this.generateBy = generateBy;
	}
	public boolean isEditable() {
		return IsEditable;
	}
	public void setEditable(boolean isEditable) {
		IsEditable = isEditable;
	}
	public boolean isIndexable() {
		return isIndexable;
	}
	public void setIndexable(boolean isIndexable) {
		this.isIndexable = isIndexable;
	}
	public boolean isUserCollection() {
		return isUserCollection;
	}
	public void setUserCollection(boolean isUserCollection) {
		this.isUserCollection = isUserCollection;
	}
	public String getLastModifier() {
		return lastModifier;
	}
	public void setLastModifier(String lastModifier) {
		this.lastModifier = lastModifier;
	}
	public Calendar getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Calendar lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public MetadataFormat getMetaFormat() {
		return metaFormat;
	}
	public void setMetaFormat(MetadataFormat metaFormat) {
		this.metaFormat = metaFormat;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public int getNumberOfMembers() {
		return numberOfMembers;
	}
	public void setNumberOfMembers(int numberOfMembers) {
		this.numberOfMembers = numberOfMembers;
	}
	public String getOID() {
		return OID;
	}
	public void setOID(String oid) {
		OID = oid;
	}
	public Calendar getPreviousUpdateTime() {
		return previousUpdateTime;
	}
	public void setPreviousUpdateTime(Calendar previousUpdateTime) {
		this.previousUpdateTime = previousUpdateTime;
	}
	public RelatedCollection getRelCollection() {
		return relCollection;
	}
	public void setRelCollection(RelatedCollection relCollection) {
		this.relCollection = relCollection;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		if (!super.equals(obj)) return false;
		
		final GCUBEMCollection other = (GCUBEMCollection) obj;
		
		if (creationTime == null) {
			if (other.creationTime != null)
				return false;
		} else if (! creationTime.equals(other.creationTime))
			return false;
		
		if (creator == null) {
			if (other.creator != null)
				return false;
		} else if (! creator.equals(other.creator))
			return false;
		
		if (isUserCollection != other.isUserCollection) return false;
		
		if (lastModifier == null) {
			if (other.lastModifier != null)
				return false;
		} else if (! lastModifier.equals(other.lastModifier))
			return false;
		
		if (lastUpdateTime == null) {
			if (other.lastUpdateTime != null)
				return false;
		} else if (! lastUpdateTime.equals(other.lastUpdateTime))
			return false;
		
		if (numberOfMembers != other.numberOfMembers) return false;
		
		if (previousUpdateTime == null) {
			if (other.previousUpdateTime != null)
				return false;
		} else if (! previousUpdateTime.equals(other.previousUpdateTime))
			return false;
		
		if (isIndexable != other.isIndexable) return false;
		
		if (generateBy == null) {
			if (other.generateBy != null)
				return false;
		} else if (! generateBy.equals(other.generateBy))
			return false;
		
		if (metaFormat == null) {
			if (other.metaFormat != null)
				return false;
		} else if (! metaFormat.equals(other.metaFormat))
			return false;
		
		if (relCollection == null) {
			if (other.relCollection != null)
				return false;
		} else if (! relCollection.equals(other.relCollection))
			return false;
		
		if (Name == null) {
			if (other.Name != null)
				return false;
		} else if (! Name.equals(other.Name))
			return false;
		
		if (Description == null) {
			if (other.Description != null)
				return false;
		} else if (! Description.equals(other.Description))
			return false;
		
		if (IsEditable != other.IsEditable) return false;
		
		if (OID == null) {
			if (other.OID != null)
				return false;
		} else if (! OID.equals(other.OID))
			return false;
		
		
		return true;
	}
	
	public static class RelatedCollection {
		private String collectionID;
		
		private String SecondaryRole;

		public String getCollectionID() {
			return collectionID;
		}

		public void setCollectionID(String collectionID) {
			this.collectionID = collectionID;
		}

		public String getSecondaryRole() {
			return SecondaryRole;
		}

		public void setSecondaryRole(String secondaryRole) {
			if (secondaryRole.startsWith("is")) SecondaryRole = secondaryRole;
			else SecondaryRole ="is-"+secondaryRole+"-by";
		}
		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			final RelatedCollection other = (RelatedCollection) obj;
			
			if (collectionID == null) {
				if (other.collectionID != null)
					return false;
			} else if (! collectionID.equals(other.collectionID))
				return false;
			
			if (SecondaryRole == null) {
				if (other.SecondaryRole != null)
					return false;
			} else if (! SecondaryRole.equals(other.SecondaryRole))
				return false;
			
			
			return true;
		}
	}
	
	public static class MetadataFormat {
		private URI schemaURI;
		private String language;
		private String name;
		public String getLanguage() {
			return language;
		}
		public void setLanguage(String language) {
			this.language = language;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public URI getSchemaURI() {
			return schemaURI;
		}
		public void setSchemaURI(URI schemaURI) {
			this.schemaURI = schemaURI;
		}
		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			final MetadataFormat other = (MetadataFormat) obj;
			
			if (schemaURI == null) {
				if (other.schemaURI != null)
					return false;
			} else if (! schemaURI.equals(other.schemaURI))
				return false;
			
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (! name.equals(other.name))
				return false;
			
			if (language == null) {
				if (other.language != null)
					return false;
			} else if (! language.equals(other.language))
				return false;
			
			
			return true;
		}
		
	}
	
	public static class GeneratedBy {
		private String collectionID;
		private URI sourceSchemaURI;
		public String getCollectionID() {
			return collectionID;
		}
		public void setCollectionID(String collectionID) {
			this.collectionID = collectionID;
		}
		public URI getSourceSchemaURI() {
			return sourceSchemaURI;
		}
		public void setSourceSchemaURI(URI sourceSchemaURI) {
			this.sourceSchemaURI = sourceSchemaURI;
		}
		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			final GeneratedBy other = (GeneratedBy) obj;
			
			if (collectionID == null) {
				if (other.collectionID != null)
					return false;
			} else if (! collectionID.equals(other.collectionID))
				return false;
			
			if (sourceSchemaURI == null) {
				if (other.sourceSchemaURI != null)
					return false;
			} else if (! sourceSchemaURI.equals(other.sourceSchemaURI))
				return false;
			
			
			return true;
		}
	}
}
