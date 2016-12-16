/**
 * 
 */
package org.gcube.informationsystem.impl.relation.consistsof;

import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.MemoryFacet;
import org.gcube.informationsystem.model.relation.consistsof.HasPersistentMemory;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=HasPersistentMemory.NAME)
public class HasPersistentMemoryImpl<Out extends Resource, In extends MemoryFacet> 
	extends HasMemoryImpl<Out, In> implements HasPersistentMemory<Out, In> {

	protected HasPersistentMemoryImpl(){
		super();
	}
	
	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public HasPersistentMemoryImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}

}
