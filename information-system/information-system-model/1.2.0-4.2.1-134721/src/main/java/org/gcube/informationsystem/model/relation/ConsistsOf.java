/**
 * 
 */
package org.gcube.informationsystem.model.relation;

import org.gcube.informationsystem.impl.relation.ConsistsOfImpl;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#consistsOf
 */
@JsonDeserialize(as=ConsistsOfImpl.class)
public interface ConsistsOf<Out extends Resource, In extends Facet>
		extends Relation<Out, In> {
	
	public static final String NAME = "ConsistsOf"; //ConsistsOf.class.getSimpleName();
	
}
