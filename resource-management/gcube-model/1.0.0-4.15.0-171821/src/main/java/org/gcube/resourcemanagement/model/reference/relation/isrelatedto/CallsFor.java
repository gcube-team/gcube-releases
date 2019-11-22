/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.relation.isrelatedto;

import org.gcube.informationsystem.model.reference.relation.IsRelatedTo;
import org.gcube.resourcemanagement.model.impl.relation.isrelatedto.CallsForImpl;
import org.gcube.resourcemanagement.model.reference.entity.resource.Service;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#callsFor
 */
@JsonDeserialize(as=CallsForImpl.class)
public interface CallsFor<Out extends Service, In extends Service> 
	extends IsRelatedTo<Out, In> {

	public static final String NAME = "CallsFor"; //CallsFor.class.getSimpleName();
	
}
