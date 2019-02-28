/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.relation.consistsof;

import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.resourcemanagement.model.impl.relation.consistsof.HasSpatialCoverageImpl;
import org.gcube.resourcemanagement.model.reference.entity.facet.CoverageFacet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#hasSpatialCoverage
 */
@JsonDeserialize(as=HasSpatialCoverageImpl.class)
public interface HasSpatialCoverage<Out extends Resource, In extends CoverageFacet> 
	extends HasCoverage<Out, In> {

	public static final String NAME = "HasSpatialCoverage"; // HasSpatialCoverage.class.getSimpleName();
}
