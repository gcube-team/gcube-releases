/**
 * 
 */
package org.gcube.informationsystem.model.impl.relation;

import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.reference.entity.Facet;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.informationsystem.model.reference.relation.ConsistsOf;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=ConsistsOf.NAME)
public class ConsistsOfImpl<Out extends Resource, In extends Facet> extends
		RelationImpl<Out, In> implements ConsistsOf<Out, In> {

	protected ConsistsOfImpl(){
		super();
	}
	
	public ConsistsOfImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}
}
