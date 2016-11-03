/**
 * 
 */
package org.gcube.informationsystem.model.relation.consistsof;

import org.gcube.informationsystem.impl.relation.consistsof.HasSpatialCoverageImpl;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.CoverageFacet;
import org.gcube.informationsystem.model.relation.ConsistsOf;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#hasSpatialCoverage
 */
@JsonDeserialize(as=HasSpatialCoverageImpl.class)
public interface HasSpatialCoverage<Out extends Resource, In extends CoverageFacet> 
	extends ConsistsOf<Out, In> {

	public static final String NAME = "HasSpatialCoverage"; // HasSpatialCoverage.class.getSimpleName();
}
