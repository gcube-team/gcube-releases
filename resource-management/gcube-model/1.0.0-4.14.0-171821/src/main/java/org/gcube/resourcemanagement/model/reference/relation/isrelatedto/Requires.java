/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.relation.isrelatedto;

import org.gcube.informationsystem.model.reference.relation.IsRelatedTo;
import org.gcube.resourcemanagement.model.impl.relation.isrelatedto.RequiresImpl;
import org.gcube.resourcemanagement.model.reference.entity.resource.Service;
import org.gcube.resourcemanagement.model.reference.entity.resource.Software;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#requires
 */
@JsonDeserialize(as=RequiresImpl.class)
public interface Requires<Out extends Software, In extends Service> 
	extends IsRelatedTo<Out, In> {
	
	public static final String NAME = "Requires"; // Requires.class.getSimpleName();
	
}
