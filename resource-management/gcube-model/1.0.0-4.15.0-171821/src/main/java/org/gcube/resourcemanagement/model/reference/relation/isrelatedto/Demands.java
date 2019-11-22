/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.relation.isrelatedto;

import org.gcube.informationsystem.model.reference.relation.IsRelatedTo;
import org.gcube.resourcemanagement.model.impl.relation.isrelatedto.DemandsImpl;
import org.gcube.resourcemanagement.model.reference.entity.resource.Software;
import org.gcube.resourcemanagement.model.reference.entity.resource.VirtualService;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#demands
 */
@JsonDeserialize(as=DemandsImpl.class)
public interface Demands<Out extends VirtualService, In extends Software> 
	extends IsRelatedTo<Out, In> {

	public static final String NAME = "Demands"; // Demands.class.getSimpleName();
	
}
