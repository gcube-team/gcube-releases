/**
 * 
 */
package org.gcube.informationsystem.impl.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.resource.Actor;
import org.gcube.informationsystem.model.entity.resource.Dataset;
import org.gcube.informationsystem.model.relation.isrelatedto.Involves;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=Involves.NAME)
public class InvolvesImpl<Out extends Dataset, In extends Actor>
		extends IsRelatedToImpl<Out, In> implements
		Involves<Out, In> {

	protected InvolvesImpl(){
		super();
	}
	
	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public InvolvesImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}

}
