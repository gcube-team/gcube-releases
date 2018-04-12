package org.gcube.common.core.resources;

/**
 * @author Andrea Manzi (ISTI-CNR)
 *
 */
public abstract class GCUBEGenericResource extends GCUBEResource{

	/**
	 * The type of the resource.
	 */
	public static final String TYPE="GenericResource";
	
	//in the following, some of the most common used secondary types
	/**
	 * Secondary type used for transformation programs
	 */
	public static final String SECONDARYTYPE_TP="TransformationProgram";
	
	/**
	 * Secondary type used for VREs 
	 */
	public static final String SECONDARYTYPE_VRE="VRE";
	
	/**
	 * Secondary type used for VOs 
	 */
	public static final String SECONDARYTYPE_VO="VO";
	
	/**
	 * Secondary type used for INFRASTRUCTUREs 
	 */
	public static final String SECONDARYTYPE_INFRASTRUCTURE="INFRASTRUCTURE";
	
	/**
	 * Secondary type used for user profiles 
	 */
	public static final String SECONDARYTYPE_USERPROFILE="UserProfile";
	
	/**
	 * Secondary type used for IndexDefinition 
	 */
	public static final String SECONDARYTYPE_INDEXDEFINITION="IndexDefinition";
	
	/**
	 * Secondary type used for search configuration
	 */
	public static final String SECONDARYTYPE_SEARCHCONFIG="SearchConfiguration";
	
	/**
	 * Secondary type used for portlet configuration
	 */
	public static final String SECONDARYTYPE_PORTLETCONFIG="PortletConfiguration";
	
	/**
	 * Secondary type used for grid resources
	 */
	public static final String SECONDARYTYPE_GRIDRESOURCE="GridResource";
	
	
	public GCUBEGenericResource() {
		this.type = TYPE;
	}
	
	private String name;
	
	private String description;
	
	private String body;

	private String secondaryType;

	/**
	 * Sets the sercondary type
	 * @return the secondaryType
	 */
	public String getSecondaryType() {
		return secondaryType;
	}

	/**
	 * Gets the secondary type
	 * @param secondaryType the secondaryType to set
	 */
	public void setSecondaryType(String secondaryType) {
		this.secondaryType = secondaryType;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (!super.equals(obj)) return false;
		
		final GCUBEGenericResource other = (GCUBEGenericResource) obj;
		
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (! body.equals(other.body))
			return false;
		
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (! name.equals(other.name))
			return false;
		
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (! description.equals(other.description))
			return false;
		
		
		return true;
	}
	
}
