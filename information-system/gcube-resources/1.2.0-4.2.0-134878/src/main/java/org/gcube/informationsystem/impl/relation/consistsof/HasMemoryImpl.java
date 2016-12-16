/**
 * 
 */
package org.gcube.informationsystem.impl.relation.consistsof;

import org.gcube.informationsystem.impl.relation.ConsistsOfImpl;
import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.MemoryFacet;
import org.gcube.informationsystem.model.relation.consistsof.HasMemory;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=HasMemory.NAME)
public abstract class HasMemoryImpl<Out extends Resource, In extends MemoryFacet> 
	extends ConsistsOfImpl<Out, In> implements HasMemory<Out, In> {

	protected HasMemoryImpl(){
		super();
	}
	
	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public HasMemoryImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}

}
