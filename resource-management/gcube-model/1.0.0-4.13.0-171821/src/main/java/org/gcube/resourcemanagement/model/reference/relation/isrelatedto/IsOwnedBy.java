/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.relation.isrelatedto;

import org.gcube.informationsystem.model.reference.relation.IsRelatedTo;
import org.gcube.resourcemanagement.model.impl.relation.isrelatedto.IsOwnedByImpl;
import org.gcube.resourcemanagement.model.reference.entity.resource.Actor;
import org.gcube.resourcemanagement.model.reference.entity.resource.Site;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#isOwnedBy
 */
@JsonDeserialize(as=IsOwnedByImpl.class)
public interface IsOwnedBy<Out extends Site, In extends Actor> 
	extends IsRelatedTo<Out, In> {

	public static final String NAME = "IsOwnedBy"; // IsOwnedBy.class.getSimpleName();
	
}
