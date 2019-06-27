/**
 * 
 */
package org.gcube.resourcemanagement.model.impl.entity.facet;

import org.gcube.resourcemanagement.model.reference.entity.facet.XSDSchemaFacet;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=XSDSchemaFacet.NAME)
public class XSDSchemaFacetImpl extends SchemaFacetImpl implements XSDSchemaFacet {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = -4625288950871784583L;
	
	protected String content;

	/**
	 * @return the embedded
	 */
	@Override
	public String getContent() {
		return content;
	}

	/**
	 * @param embedded the embedded to set
	 */
	@Override
	public void setContent(String content) {
		this.content = content;
	}
	
	

}
