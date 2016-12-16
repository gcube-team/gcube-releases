/**
 * 
 */
package org.gcube.informationsystem.impl.relation.consistsof;

import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.ContactFacet;
import org.gcube.informationsystem.model.relation.consistsof.HasOwner;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=HasOwner.NAME)
public class HasOwnerImpl<Out extends Resource, In extends ContactFacet> 
	extends HasContactImpl<Out, In> implements HasOwner<Out, In> {

	protected HasOwnerImpl(){
		super();
	}
	
	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public HasOwnerImpl(Out source, In target, 
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}

}
