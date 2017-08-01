/**
 * 
 */
package org.gcube.informationsystem.impl.relation;

import org.gcube.informationsystem.model.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.ConsistsOf;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=ConsistsOf.NAME)
public class DummyConsistsOf<Out extends Resource, In extends Facet> extends
		ConsistsOfImpl<Out, In> implements ConsistsOf<Out, In> {

	public DummyConsistsOf(){
		super();
	}
	
	public DummyConsistsOf(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}
	
}
