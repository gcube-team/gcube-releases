/**
 * 
 */
package org.gcube.informationsystem.model.relation.consistsof;

import org.gcube.informationsystem.impl.relation.consistsof.HasPersistentMemoryImpl;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.MemoryFacet;

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
