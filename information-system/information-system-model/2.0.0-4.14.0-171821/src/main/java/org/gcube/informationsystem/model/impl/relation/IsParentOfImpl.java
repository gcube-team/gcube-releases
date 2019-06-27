/**
 * 
 */
package org.gcube.informationsystem.model.impl.relation;

import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.reference.entity.Context;
import org.gcube.informationsystem.model.reference.relation.IsParentOf;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=IsParentOf.NAME)
public class IsParentOfImpl<Out extends Context, In extends Context> extends
		RelationImpl<Out, In> implements IsParentOf<Out, In> {

	protected IsParentOfImpl(){
		super();
	}
	
	public IsParentOfImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
