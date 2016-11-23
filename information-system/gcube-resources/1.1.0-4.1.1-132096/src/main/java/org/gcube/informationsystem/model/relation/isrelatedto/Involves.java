/**
 * 
 */
package org.gcube.informationsystem.model.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.isrelatedto.InvolvesImpl;
import org.gcube.informationsystem.model.entity.resource.Actor;
import org.gcube.informationsystem.model.entity.resource.Dataset;
import org.gcube.informationsystem.model.relation.IsRelatedTo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#involves
 */
@JsonDeserialize(as=InvolvesImpl.class)
public interface Involves<Out extends Dataset, In extends Actor> 
	extends IsRelatedTo<Out, In> {

	public static final String NAME = "Involves"; // Involves.class.getSimpleName();
	
}
