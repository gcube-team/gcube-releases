/**
 * 
 */
package org.gcube.informationsystem.model.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.isrelatedto.IsOwnedByImpl;
import org.gcube.informationsystem.model.entity.resource.Actor;
import org.gcube.informationsystem.model.entity.resource.Site;
import org.gcube.informationsystem.model.relation.IsRelatedTo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#isOwnedBy
 */
@JsonDeserialize(as=IsOwnedByImpl.class)
public interface IsOwnedBy<Out extends Site, In extends Actor> 
	extends IsRelatedTo<Out, In> {

	public static final String NAME = "IsOwnedBy"; // IsOwnedBy.class.getSimpleName();
	
}
