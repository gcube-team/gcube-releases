package org.gcube.resourcemanagement.model.impl.relation.consistsof;

import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.resourcemanagement.model.reference.entity.facet.CoverageFacet;
import org.gcube.resourcemanagement.model.reference.relation.consistsof.HasTemporalCoverage;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = HasTemporalCoverage.NAME)
public class HasTemporalCoverageImpl<Out extends Resource, In extends CoverageFacet>
		extends HasCoverageImpl<Out, In> implements
		HasTemporalCoverage<Out, In> {

	protected HasTemporalCoverageImpl() {
		super();
	}

	public HasTemporalCoverageImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
