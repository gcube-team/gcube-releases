/**
 * 
 */
package org.gcube.informationsystem.impl.relation;

import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.IsRelatedTo;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=IsRelatedTo.NAME)
public class IsRelatedToImpl<Out extends Resource, In extends Resource> extends
		RelationImpl<Out, In> implements IsRelatedTo<Out, In> {

	protected IsRelatedToImpl(){
		super();
	}
	
	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public IsRelatedToImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}
	
}
