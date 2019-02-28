/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.relation.isrelatedto;

import org.gcube.informationsystem.model.reference.relation.IsRelatedTo;
import org.gcube.resourcemanagement.model.impl.relation.isrelatedto.EnablesImpl;
import org.gcube.resourcemanagement.model.reference.entity.resource.Service;
import org.gcube.resourcemanagement.model.reference.entity.resource.Software;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#enables
 */
@JsonDeserialize(as=EnablesImpl.class)
public interface Enables<Out extends Service, In extends Software> 
	extends IsRelatedTo<Out, In> {

	public static final String NAME = "Runs"; // Runs.class.getSimpleName();
	
}
