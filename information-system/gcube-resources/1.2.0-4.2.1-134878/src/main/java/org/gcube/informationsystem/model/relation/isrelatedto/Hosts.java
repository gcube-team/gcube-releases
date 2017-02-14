/**
 * 
 */
package org.gcube.informationsystem.model.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.isrelatedto.HostsImpl;
import org.gcube.informationsystem.model.entity.resource.EService;
import org.gcube.informationsystem.model.entity.resource.HostingNode;
import org.gcube.informationsystem.model.relation.IsRelatedTo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#hosts
 */
@JsonDeserialize(as=HostsImpl.class)
public interface Hosts<Out extends HostingNode, In extends EService> 
	extends IsRelatedTo<Out, In> {
	
	public static final String NAME = "Hosts"; // Hosts.class.getSimpleName();
	
}
