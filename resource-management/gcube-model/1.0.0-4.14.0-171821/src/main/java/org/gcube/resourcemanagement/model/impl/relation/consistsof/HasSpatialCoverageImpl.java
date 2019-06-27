package org.gcube.resourcemanagement.model.impl.relation.consistsof;

import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.resourcemanagement.model.reference.entity.facet.CoverageFacet;
import org.gcube.resourcemanagement.model.reference.relation.consistsof.HasSpatialCoverage;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = HasSpatialCoverage.NAME)
public class HasSpatialCoverageImpl<Out extends Resource, In extends CoverageFacet>
		extends HasCoverageImpl<Out, In> implements HasSpatialCoverage<Out, In> {

	protected HasSpatialCoverageImpl() {
		super();
	}

	public HasSpatialCoverageImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
