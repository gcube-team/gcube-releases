/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.entity.facet;

import java.net.URI;

import org.gcube.informationsystem.model.reference.annotations.ISProperty;
import org.gcube.informationsystem.model.reference.entity.Facet;
import org.gcube.resourcemanagement.model.impl.entity.facet.CoverageFacetImpl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Coverage_Facet
 */
@JsonDeserialize(as=CoverageFacetImpl.class)
public interface CoverageFacet extends Facet {
	
	public static final String NAME = "CoverageFacet"; // CoverageFacet.class.getSimpleName();
	public static final String DESCRIPTION = "Collect any \"extent\"-related information.";
	public static final String VERSION = "1.0.0";
	
	@ISProperty(mandatory=true, nullable=false)
	public String getValue();
	
	public void setValue(String value);

	@ISProperty(mandatory=true, nullable=false)
	public URI getSchema();
	
	public void setSchema(URI schema);

}
