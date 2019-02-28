/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.relation.isrelatedto;

import org.gcube.resourcemanagement.model.impl.relation.isrelatedto.IsPartOfImpl;
import org.gcube.resourcemanagement.model.reference.entity.resource.ConcreteDataset;
import org.gcube.resourcemanagement.model.reference.entity.resource.Dataset;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#isPartOf
 */
@JsonDeserialize(as=IsPartOfImpl.class)
public interface IsPartOf<Out extends ConcreteDataset, In extends Dataset> 
	extends IsCorrelatedTo<Out, In> {

	public static final String NAME = "IsPartOf"; // IsPartOf.class.getSimpleName();
	
}
