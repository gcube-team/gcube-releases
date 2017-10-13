package org.gcube.informationsystem.impl.relation.consistsof;

import org.gcube.informationsystem.model.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.CoverageFacet;
import org.gcube.informationsystem.model.relation.consistsof.HasTemporalCoverage;

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
