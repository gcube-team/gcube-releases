/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.entity.facet;

import java.net.URI;

import org.gcube.informationsystem.model.reference.annotations.ISProperty;
import org.gcube.informationsystem.model.reference.entity.Facet;
import org.gcube.resourcemanagement.model.impl.entity.facet.DescriptiveMetadataFacetImpl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Descriptive_Metadata_Facet
 */
@JsonDeserialize(as=DescriptiveMetadataFacetImpl.class)
public interface DescriptiveMetadataFacet extends Facet {
	
	public static final String NAME = "DescriptiveMetadataFacet"; // DescriptiveMetadataFacet.class.getSimpleName();
	public static final String DESCRIPTION = "Collect any descriptive metadata about the resource";
	public static final String VERSION = "1.0.0";

	@ISProperty(mandatory=true, nullable=false)
	public String getValue();
	
	public void setValue(String value);

	@ISProperty(mandatory=true, nullable=false)
	public URI getSchema();
	
	public void setSchema(URI schema);
	
}
