/**
 * 
 */
package org.gcube.informationsystem.model.entity.facet;

import org.gcube.informationsystem.impl.entity.facet.ServiceStateFacetImpl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Service_State_Facet
 */
@JsonDeserialize(as=ServiceStateFacetImpl.class)
public interface ServiceStateFacet extends StateFacet {

	public static final String NAME = "ServiceStateFacet"; // ServiceStateFacet.class.getSimpleName();
	public static final String DESCRIPTION = "State Information";
	public static final String VERSION = "1.0.0";
	
}
