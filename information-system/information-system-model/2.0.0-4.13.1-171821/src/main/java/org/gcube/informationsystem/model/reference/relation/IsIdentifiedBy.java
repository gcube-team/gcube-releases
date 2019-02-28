/**
 * 
 */
package org.gcube.informationsystem.model.reference.relation;

import org.gcube.informationsystem.model.impl.relation.IsIdentifiedByImpl;
import org.gcube.informationsystem.model.reference.entity.Facet;
import org.gcube.informationsystem.model.reference.entity.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#isIdentifiedBy
 */
@JsonDeserialize(as=IsIdentifiedByImpl.class)
public interface IsIdentifiedBy<Out extends Resource, In extends Facet>
		extends ConsistsOf<Out, In> {
	
	public static final String NAME = "IsIdentifiedBy"; //IsIdentifiedBy.class.getSimpleName();
	
}
