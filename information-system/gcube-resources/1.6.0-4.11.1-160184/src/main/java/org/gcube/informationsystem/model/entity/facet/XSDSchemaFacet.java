/**
 * 
 */
package org.gcube.informationsystem.model.entity.facet;

import org.gcube.informationsystem.impl.entity.facet.XSDSchemaFacetImpl;
import org.gcube.informationsystem.model.annotations.ISProperty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#XSD_Schema_Facet
 */
@JsonDeserialize(as=XSDSchemaFacetImpl.class)
public interface XSDSchemaFacet extends SchemaFacet {
	
	public static final String NAME = "XSDSchemaFacet"; // XSDSchemaFacet.class.getSimpleName();
	public static final String DESCRIPTION = "Provide a way to store a JSON Schema";
	public static final String VERSION = "1.0.0";
	
	@ISProperty(mandatory=true, nullable=false)
	public String getContent();
	
	public void setContent(String content);
	
}
