/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.entity.facet;

import org.gcube.informationsystem.model.reference.annotations.ISProperty;
import org.gcube.informationsystem.model.reference.entity.Facet;
import org.gcube.resourcemanagement.model.impl.entity.facet.CapabilityFacetImpl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Contact_Facet
 */
@JsonDeserialize(as=CapabilityFacetImpl.class)
public interface CapabilityFacet extends Facet {

	public static final String NAME = "CapabilityFacet"; // CapabilityFacet.class.getSimpleName();
	public static final String DESCRIPTION = "This facet is expected to "
			+ "capture a defined facility for performing a specified task "
			+ "supported by a given Service or Software";
	public static final String VERSION = "1.0.0";

	@ISProperty(mandatory=true, nullable=false)
	public String getName();

	public void setName(String name);

	@ISProperty
	public String getDescription();

	public void setDescription(String description);

	@ISProperty
	public String getQualifier();

	public void setQualifier(String qualifier);

	

}
