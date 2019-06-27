/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.relation.consistsof;

import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.resourcemanagement.model.impl.relation.consistsof.HasTemporalCoverageImpl;
import org.gcube.resourcemanagement.model.reference.entity.facet.CoverageFacet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#hasTemporalCoverage
 */
@JsonDeserialize(as=HasTemporalCoverageImpl.class)
public interface HasTemporalCoverage<Out extends Resource, In extends CoverageFacet> 
	extends HasCoverage<Out, In> {

	public static final String NAME = "HasTemporalCoverage"; // HasTemporalCoverage.class.getSimpleName();
}
