/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.relation.isrelatedto;

import org.gcube.informationsystem.model.reference.relation.IsRelatedTo;
import org.gcube.resourcemanagement.model.impl.relation.isrelatedto.IsCorrelatedToImpl;
import org.gcube.resourcemanagement.model.reference.entity.resource.Dataset;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#isCorrelatedTo
 */
@JsonDeserialize(as=IsCorrelatedToImpl.class)
public interface IsCorrelatedTo<Out extends Dataset, In extends Dataset> 
	extends IsRelatedTo<Out, In> {

	public static final String NAME = "IsCorrelatedTo"; // IsCorrelatedTo.class.getSimpleName();
	
}
