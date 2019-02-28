/**
 * 
 */
package org.gcube.resourcemanagement.model.impl.entity.facet;

import org.gcube.informationsystem.model.reference.embedded.Embedded;
import org.gcube.resourcemanagement.model.reference.entity.facet.JSONSchemaFacet;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=JSONSchemaFacet.NAME)
public class JSONSchemaFacetImpl extends SchemaFacetImpl implements JSONSchemaFacet {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = 2196637871529229113L;
	
	protected Embedded content;

	/**
	 * @return the embedded
	 */
	@Override
	public Embedded getContent() {
		return content;
	}

	/**
	 * @param embedded the embedded to set
	 */
	@Override
	public void setContent(Embedded content) {
		this.content = content;
	}
	
	

}
