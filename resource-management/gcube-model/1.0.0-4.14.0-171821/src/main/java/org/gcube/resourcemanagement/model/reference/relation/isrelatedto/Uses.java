/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.relation.isrelatedto;

import org.gcube.resourcemanagement.model.impl.relation.isrelatedto.UsesImpl;
import org.gcube.resourcemanagement.model.reference.entity.resource.EService;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonDeserialize(as=UsesImpl.class)
public interface Uses<Out extends EService, In extends EService> 
	extends CallsFor<Out, In> {

	public static final String NAME = "Uses"; // Uses.class.getSimpleName();
	
}
