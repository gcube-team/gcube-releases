/**
 * 
 */
package org.gcube.informationsystem.model.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.isrelatedto.IsPoweredByImpl;
import org.gcube.informationsystem.model.entity.resource.HostingNode;
import org.gcube.informationsystem.model.entity.resource.Software;
import org.gcube.informationsystem.model.relation.IsRelatedTo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#isPoweredBy
 */
@JsonDeserialize(as=IsPoweredByImpl.class)
public interface IsPoweredBy<Out extends HostingNode, In extends Software> 
	extends IsRelatedTo<Out, In> {

	public static final String NAME = "IsPoweredBy"; // IsPoweredBy.class.getSimpleName();
	
}
