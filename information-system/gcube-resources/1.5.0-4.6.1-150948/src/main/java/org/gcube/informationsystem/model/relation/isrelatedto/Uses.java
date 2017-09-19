/**
 * 
 */
package org.gcube.informationsystem.model.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.isrelatedto.UsesImpl;
import org.gcube.informationsystem.model.entity.resource.EService;
import org.gcube.informationsystem.model.relation.IsRelatedTo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonDeserialize(as=UsesImpl.class)
public interface Uses<Out extends EService, In extends EService> 
	extends IsRelatedTo<Out, In> {

	public static final String NAME = "Uses"; // Uses.class.getSimpleName();
	
}
