/**
 * 
 */
package org.gcube.informationsystem.impl.relation.consistsof;

import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.CoverageFacet;
import org.gcube.informationsystem.model.relation.consistsof.HasTemporalCoverage;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@JsonTypeName(value=HasTemporalCoverage.NAME)
public class HasTemporalCoverageImpl<Out extends Resource, In extends CoverageFacet> 
	extends HasCoverageImpl<Out, In> implements HasTemporalCoverage<Out, In> {

	protected HasTemporalCoverageImpl(){
		super();
	}
	
	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public HasTemporalCoverageImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}


}
