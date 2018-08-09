/**
 * 
 */
package org.gcube.informationsystem.model.relation.consistsof;

import org.gcube.informationsystem.impl.relation.consistsof.HasCoverageImpl;
import org.gcube.informationsystem.model.annotations.Abstract;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.CoverageFacet;
import org.gcube.informationsystem.model.relation.ConsistsOf;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#hasCoverage
 */
@Abstract
@JsonDeserialize(as=HasCoverageImpl.class)
public interface HasCoverage<Out extends Resource, In extends CoverageFacet> 
	extends ConsistsOf<Out, In> {

	public static final String NAME = "HasCoverage"; //HasCoverage.class.getSimpleName();
}
