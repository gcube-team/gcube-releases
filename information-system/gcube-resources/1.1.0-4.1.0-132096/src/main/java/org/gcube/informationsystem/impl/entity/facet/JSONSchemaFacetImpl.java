/**
 * 
 */
package org.gcube.informationsystem.impl.entity.facet;

import org.gcube.informationsystem.model.embedded.Embedded;
import org.gcube.informationsystem.model.entity.facet.JSONSchemaFacet;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
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
