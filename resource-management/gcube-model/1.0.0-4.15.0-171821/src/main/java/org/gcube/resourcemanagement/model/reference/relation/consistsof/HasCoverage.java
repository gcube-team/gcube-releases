/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.relation.consistsof;

import org.gcube.informationsystem.model.reference.annotations.Abstract;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.informationsystem.model.reference.relation.ConsistsOf;
import org.gcube.resourcemanagement.model.impl.relation.consistsof.HasCoverageImpl;
import org.gcube.resourcemanagement.model.reference.entity.facet.CoverageFacet;

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
