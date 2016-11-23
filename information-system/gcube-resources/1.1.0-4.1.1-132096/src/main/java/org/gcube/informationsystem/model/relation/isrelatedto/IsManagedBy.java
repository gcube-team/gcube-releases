/**
 * 
 */
package org.gcube.informationsystem.model.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.isrelatedto.IsManagedByImpl;
import org.gcube.informationsystem.model.entity.resource.HostingNode;
import org.gcube.informationsystem.model.entity.resource.Site;
import org.gcube.informationsystem.model.relation.IsRelatedTo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#isManagedBy
 */
@JsonDeserialize(as=IsManagedByImpl.class)
public interface IsManagedBy<Out extends HostingNode, In extends Site> 
	extends IsRelatedTo<Out, In> {

	public static final String NAME = "IsManagedBy"; // IsManagedBy.class.getSimpleName();
	
}
