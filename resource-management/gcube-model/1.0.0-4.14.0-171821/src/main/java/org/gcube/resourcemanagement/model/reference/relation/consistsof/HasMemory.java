/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.relation.consistsof;

import org.gcube.informationsystem.model.reference.annotations.Abstract;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.informationsystem.model.reference.relation.ConsistsOf;
import org.gcube.resourcemanagement.model.impl.relation.consistsof.HasMemoryImpl;
import org.gcube.resourcemanagement.model.reference.entity.facet.MemoryFacet;

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
