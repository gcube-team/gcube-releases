/**
 * 
 */
package org.gcube.informationsystem.model.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.isrelatedto.CallsForImpl;
import org.gcube.informationsystem.model.entity.resource.Service;
import org.gcube.informationsystem.model.relation.IsRelatedTo;

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
