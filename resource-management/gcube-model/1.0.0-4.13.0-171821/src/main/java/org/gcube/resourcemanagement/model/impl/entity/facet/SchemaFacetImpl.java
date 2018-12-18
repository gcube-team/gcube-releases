/**
 * 
 */
package org.gcube.resourcemanagement.model.impl.entity.facet;

import java.net.URL;

import org.gcube.informationsystem.model.impl.entity.FacetImpl;
import org.gcube.resourcemanagement.model.reference.entity.facet.SchemaFacet;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=SchemaFacet.NAME)
public class SchemaFacetImpl extends FacetImpl implements SchemaFacet {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = -3443862680728281477L;
	
	protected String name;
	protected String description;
	protected URL schemaURL;
	
	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the description
	 */
	@Override
	public String getDescription() {
		return description;
	}
	
	/**
	 * @param description the description to set
	 */
	@Override
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @return the schemaURL
	 */
	@Override
	public URL getSchemaURL() {
		return schemaURL;
	}
	
	/**
	 * @param schemaURL the schemaURL to set
	 */
	@Override
	public void setSchemaURL(URL schemaURL) {
		this.schemaURL = schemaURL;
	}

}
