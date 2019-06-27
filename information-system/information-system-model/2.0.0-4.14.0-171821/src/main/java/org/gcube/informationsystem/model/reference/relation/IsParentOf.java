/**
 * 
 */
package org.gcube.informationsystem.model.reference.relation;

import org.gcube.informationsystem.model.impl.relation.IsParentOfImpl;
import org.gcube.informationsystem.model.reference.entity.Context;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#isParentOf
 */
@JsonDeserialize(as=IsParentOfImpl.class)
public interface IsParentOf<Out extends Context, In extends Context> 
		extends Relation<Out, In> {

	public static final String NAME = "IsParentOf"; //IsParentOf.class.getSimpleName();
	
}
