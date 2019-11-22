/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.relation.consistsof;

import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.resourcemanagement.model.impl.relation.consistsof.HasVolatileMemoryImpl;
import org.gcube.resourcemanagement.model.reference.entity.facet.MemoryFacet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#hasVolatileMemory
 */
@JsonDeserialize(as=HasVolatileMemoryImpl.class)
public interface HasVolatileMemory<Out extends Resource, In extends MemoryFacet> 
	extends HasMemory<Out, In> {

	public static final String NAME = "HasVolatileMemory"; // HasVolatileMemory.class.getSimpleName();
}
