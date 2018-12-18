package org.gcube.resourcemanagement.model.impl.relation.consistsof;

import org.gcube.informationsystem.model.impl.relation.ConsistsOfImpl;
import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.resourcemanagement.model.reference.entity.facet.CoverageFacet;
import org.gcube.resourcemanagement.model.reference.relation.consistsof.HasCoverage;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = HasCoverage.NAME)
public abstract class HasCoverageImpl<Out extends Resource, In extends CoverageFacet>
		extends ConsistsOfImpl<Out, In> implements HasCoverage<Out, In> {

	protected HasCoverageImpl() {
		super();
	}

	public HasCoverageImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
