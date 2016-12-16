/**
 * 
 */
package org.gcube.informationsystem.impl.relation.consistsof;

import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.ContactFacet;
import org.gcube.informationsystem.model.relation.consistsof.HasManager;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=HasManager.NAME)
public class HasManagerImpl<Out extends Resource, In extends ContactFacet> 
	extends HasContactImpl<Out, In> implements HasManager<Out, In> {

	protected HasManagerImpl(){
		super();
	}
	
	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public HasManagerImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}
	
}
