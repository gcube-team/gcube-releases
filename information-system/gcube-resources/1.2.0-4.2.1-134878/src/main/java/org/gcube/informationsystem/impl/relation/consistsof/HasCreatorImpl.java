/**
 * 
 */
package org.gcube.informationsystem.impl.relation.consistsof;

import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.ContactFacet;
import org.gcube.informationsystem.model.relation.consistsof.HasCreator;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=HasCreator.NAME)
public class HasCreatorImpl<Out extends Resource, In extends ContactFacet> 
	extends HasContactImpl<Out, In> implements HasCreator<Out, In> {

	protected HasCreatorImpl(){
		super();
	}
	
	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public HasCreatorImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}

	
}
