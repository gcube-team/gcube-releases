/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.relation.isrelatedto;

import org.gcube.resourcemanagement.model.impl.relation.isrelatedto.DiscoversImpl;
import org.gcube.resourcemanagement.model.reference.entity.resource.EService;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#discovers
 */
@JsonDeserialize(as=DiscoversImpl.class)
public interface Discovers<Out extends EService, In extends EService> 
	extends CallsFor<Out, In> {

	public static final String NAME = "Discovers"; // Discovers.class.getSimpleName();
	
}
