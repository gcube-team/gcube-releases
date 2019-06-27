/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.relation.isrelatedto;

import org.gcube.informationsystem.model.reference.relation.IsRelatedTo;
import org.gcube.resourcemanagement.model.impl.relation.isrelatedto.IsCompliantWithImpl;
import org.gcube.resourcemanagement.model.reference.entity.resource.Dataset;
import org.gcube.resourcemanagement.model.reference.entity.resource.Schema;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#isCompliantWith
 */
@JsonDeserialize(as=IsCompliantWithImpl.class)
public interface IsCompliantWith<Out extends Dataset, In extends Schema> 
	extends IsRelatedTo<Out, In> {

	public static final String NAME = "IsCompliantWith"; // IsCompliantWith.class.getSimpleName();
	
}
