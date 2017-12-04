/**
 * 
 */
package org.gcube.informationsystem.model.relation.consistsof;

import org.gcube.informationsystem.impl.relation.consistsof.HasMemoryImpl;
import org.gcube.informationsystem.model.annotations.Abstract;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.MemoryFacet;
import org.gcube.informationsystem.model.relation.ConsistsOf;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#hasMemory
 */
@Abstract
@JsonDeserialize(as=HasMemoryImpl.class)
public interface HasMemory<Out extends Resource, In extends MemoryFacet> 
	extends ConsistsOf<Out, In> {

	public static final String NAME = "HasMemory"; // HasMemory.class.getSimpleName();
}
