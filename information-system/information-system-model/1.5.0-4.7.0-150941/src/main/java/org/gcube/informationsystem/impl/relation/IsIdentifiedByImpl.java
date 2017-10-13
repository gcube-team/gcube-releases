/**
 * 
 */
package org.gcube.informationsystem.impl.relation;

import org.gcube.informationsystem.model.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.IsIdentifiedBy;

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
