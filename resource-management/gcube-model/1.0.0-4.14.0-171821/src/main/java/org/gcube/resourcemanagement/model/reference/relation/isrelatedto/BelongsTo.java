/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.relation.isrelatedto;

import org.gcube.informationsystem.model.reference.relation.IsRelatedTo;
import org.gcube.resourcemanagement.model.impl.relation.isrelatedto.BelongsToImpl;
import org.gcube.resourcemanagement.model.reference.entity.resource.LegalBody;
import org.gcube.resourcemanagement.model.reference.entity.resource.Person;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#belongsTo
 */
@JsonDeserialize(as=BelongsToImpl.class)
public interface BelongsTo<Out extends Person, In extends LegalBody> 
	extends IsRelatedTo<Out, In> {

	public static final String NAME = "BelongsTo"; //BelongsTo.class.getSimpleName();
	
}
