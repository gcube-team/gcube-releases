/**
 * 
 */
package org.gcube.informationsystem.model.impl.relation;

import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.reference.entity.Facet;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.informationsystem.model.reference.relation.IsIdentifiedBy;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=IsIdentifiedBy.NAME)
public class IsIdentifiedByImpl<Out extends Resource, In extends Facet> extends
	ConsistsOfImpl<Out, In> implements IsIdentifiedBy<Out, In> {

	protected IsIdentifiedByImpl(){
		super();
	}
	
	public IsIdentifiedByImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}
	
}
