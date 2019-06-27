/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.relation.isrelatedto;

import org.gcube.informationsystem.model.reference.relation.IsRelatedTo;
import org.gcube.resourcemanagement.model.impl.relation.isrelatedto.InvolvesImpl;
import org.gcube.resourcemanagement.model.reference.entity.resource.Actor;
import org.gcube.resourcemanagement.model.reference.entity.resource.Dataset;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#involves
 */
@JsonDeserialize(as=InvolvesImpl.class)
public interface Involves<Out extends Dataset, In extends Actor> 
	extends IsRelatedTo<Out, In> {

	public static final String NAME = "Involves"; // Involves.class.getSimpleName();
	
}
