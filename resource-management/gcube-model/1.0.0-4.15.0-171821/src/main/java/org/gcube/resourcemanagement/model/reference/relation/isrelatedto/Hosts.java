/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.relation.isrelatedto;

import org.gcube.informationsystem.model.reference.relation.IsRelatedTo;
import org.gcube.resourcemanagement.model.impl.relation.isrelatedto.HostsImpl;
import org.gcube.resourcemanagement.model.reference.entity.resource.Service;
import org.gcube.resourcemanagement.model.reference.entity.resource.Site;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#hosts
 */
@JsonDeserialize(as=HostsImpl.class)
public interface Hosts<Out extends Site, In extends Service> 
	extends IsRelatedTo<Out, In> {
	
	public static final String NAME = "Hosts"; // Hosts.class.getSimpleName();
	
}
