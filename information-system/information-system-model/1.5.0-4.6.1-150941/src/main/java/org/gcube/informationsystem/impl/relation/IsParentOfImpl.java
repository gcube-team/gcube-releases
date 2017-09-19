/**
 * 
 */
package org.gcube.informationsystem.impl.relation;

import org.gcube.informationsystem.model.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.entity.Context;
import org.gcube.informationsystem.model.relation.IsParentOf;

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
