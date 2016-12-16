/**
 * 
 */
package org.gcube.informationsystem.impl.relation.consistsof;

import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.MemoryFacet;
import org.gcube.informationsystem.model.relation.consistsof.HasVolatileMemory;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=HasVolatileMemory.NAME)
public class HasVolatileMemoryImpl<Out extends Resource, In extends MemoryFacet> 
	extends HasMemoryImpl<Out, In> implements HasVolatileMemory<Out, In> {

	protected HasVolatileMemoryImpl(){
		super();
	}
	
	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public HasVolatileMemoryImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}

}
