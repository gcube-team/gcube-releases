/**
 * 
 */
package org.gcube.informationsystem.model.entity.facet;

import org.gcube.informationsystem.impl.entity.facet.JSONSchemaFacetImpl;
import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.embedded.Embedded;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#XSD_Schema_Facet
 */
@JsonDeserialize(as=JSONSchemaFacetImpl.class)
public interface JSONSchemaFacet extends SchemaFacet {
	
	public static final String NAME = "JSONSchemaFacet"; // JSONSchemaFacet.class.getSimpleName();
	public static final String DESCRIPTION = "Provide a way to store a JSON Schema";
	public static final String VERSION = "1.0.0";
	
	@ISProperty(mandatory=true, nullable=false)
	public Embedded getContent();
	
	public void setContent(Embedded content);
	
}
