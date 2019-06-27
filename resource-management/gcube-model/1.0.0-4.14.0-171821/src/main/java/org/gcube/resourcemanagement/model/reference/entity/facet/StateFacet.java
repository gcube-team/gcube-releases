/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.entity.facet;

import org.gcube.informationsystem.model.reference.annotations.Abstract;
import org.gcube.informationsystem.model.reference.annotations.ISProperty;
import org.gcube.informationsystem.model.reference.entity.Facet;
import org.gcube.resourcemanagement.model.impl.entity.facet.StateFacetImpl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#State_Facet
 */
@Abstract
@JsonDeserialize(as=StateFacetImpl.class)
public interface StateFacet extends Facet {

	public static final String NAME = "StateFacet"; // StateFacet.class.getSimpleName();
	public static final String DESCRIPTION = "State Information";
	public static final String VERSION = "1.0.0";
	
	@ISProperty(mandatory=true, nullable=false)
	public String getValue();
	
	public void setValue(String value);
	
}
