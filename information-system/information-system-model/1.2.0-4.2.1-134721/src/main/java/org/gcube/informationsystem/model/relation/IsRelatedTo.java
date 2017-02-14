/**
 * 
 */
package org.gcube.informationsystem.model.relation;

import org.gcube.informationsystem.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.entity.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#isRelatedTo
 */
@JsonDeserialize(as=IsRelatedToImpl.class)
public interface IsRelatedTo<Out extends Resource, In extends Resource>
		extends Relation<Out, In> {
	
	public static final String NAME = "IsRelatedTo"; //IsRelatedTo.class.getSimpleName();
	
}
