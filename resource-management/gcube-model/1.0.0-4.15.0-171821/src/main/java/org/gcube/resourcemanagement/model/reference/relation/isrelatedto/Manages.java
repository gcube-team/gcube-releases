/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.relation.isrelatedto;

import org.gcube.informationsystem.model.reference.relation.IsRelatedTo;
import org.gcube.resourcemanagement.model.impl.relation.isrelatedto.ManagesImpl;
import org.gcube.resourcemanagement.model.reference.entity.resource.Dataset;
import org.gcube.resourcemanagement.model.reference.entity.resource.Service;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#manages
 */
@JsonDeserialize(as=ManagesImpl.class)
public interface Manages<Out extends Service, In extends Dataset> 
	extends IsRelatedTo<Out, In> {
	
	public static final String NAME = "Manages"; // Manages.class.getSimpleName();
	
}
