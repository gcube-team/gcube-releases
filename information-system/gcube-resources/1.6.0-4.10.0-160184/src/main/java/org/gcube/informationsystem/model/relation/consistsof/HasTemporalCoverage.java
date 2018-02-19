/**
 * 
 */
package org.gcube.informationsystem.model.relation.consistsof;

import org.gcube.informationsystem.impl.relation.consistsof.HasTemporalCoverageImpl;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.CoverageFacet;

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
