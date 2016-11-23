/**
 * 
 */
package org.gcube.informationsystem.model.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.isrelatedto.IsPartOfImpl;
import org.gcube.informationsystem.model.entity.resource.ConcreteDataset;
import org.gcube.informationsystem.model.entity.resource.Dataset;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#isPartOf
 */
@JsonDeserialize(as=IsPartOfImpl.class)
public interface IsPartOf<Out extends ConcreteDataset, In extends Dataset> 
	extends IsCorrelatedTo<Out, In> {

	public static final String NAME = "IsPartOf"; // IsPartOf.class.getSimpleName();
	
}
