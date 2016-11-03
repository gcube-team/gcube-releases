/**
 * 
 */
package org.gcube.informationsystem.impl.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.resource.Dataset;
import org.gcube.informationsystem.model.entity.resource.Schema;
import org.gcube.informationsystem.model.relation.isrelatedto.IsCompliantWith;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@JsonTypeName(value=IsCompliantWith.NAME)
public class IsCompliantWithImpl<Out extends Dataset, In extends Schema>
		extends IsRelatedToImpl<Out, In> implements
		IsCompliantWith<Out, In> {

	protected IsCompliantWithImpl(){
		super();
	}
	
	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public IsCompliantWithImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}

}
