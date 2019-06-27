/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.relation.isrelatedto;

import org.gcube.informationsystem.model.reference.relation.IsRelatedTo;
import org.gcube.resourcemanagement.model.impl.relation.isrelatedto.ActivatesImpl;
import org.gcube.resourcemanagement.model.reference.entity.resource.Service;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#activates
 */
@JsonDeserialize(as=ActivatesImpl.class)
public interface Activates<Out extends Service, In extends Service> 
	extends IsRelatedTo<Out, In> {

	public static final String NAME = "Activates"; //CallsFor.class.getSimpleName();
	
}
