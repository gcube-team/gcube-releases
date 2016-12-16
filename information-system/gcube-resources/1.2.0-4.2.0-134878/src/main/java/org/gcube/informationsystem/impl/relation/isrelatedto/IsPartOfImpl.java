/**
 * 
 */
package org.gcube.informationsystem.impl.relation.isrelatedto;

import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.resource.ConcreteDataset;
import org.gcube.informationsystem.model.entity.resource.Dataset;
import org.gcube.informationsystem.model.relation.isrelatedto.IsPartOf;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=IsPartOf.NAME)
public class IsPartOfImpl<Out extends ConcreteDataset, In extends Dataset>
		extends IsCorrelatedToImpl<Out, In> implements
		IsPartOf<Out, In> {

	protected IsPartOfImpl(){
		super();
	}
	
	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public IsPartOfImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}

}
