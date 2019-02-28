/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.relation.consistsof;

import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.resourcemanagement.model.impl.relation.consistsof.HasPersistentMemoryImpl;
import org.gcube.resourcemanagement.model.reference.entity.facet.MemoryFacet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#hasPersistentMemory
 */
@JsonDeserialize(as=HasPersistentMemoryImpl.class)
public interface HasPersistentMemory<Out extends Resource, In extends MemoryFacet> 
	extends HasMemory<Out, In> {

	public static final String NAME = "HasPersistentMemory"; // HasPersistentMemory.class.getSimpleName();
}
