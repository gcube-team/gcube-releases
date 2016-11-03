/**
 * 
 */
package org.gcube.informationsystem.impl.relation.consistsof;

import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.CoverageFacet;
import org.gcube.informationsystem.model.relation.consistsof.HasSpatialCoverage;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@JsonTypeName(value=HasSpatialCoverage.NAME)
public class HasSpatialCoverageImpl<Out extends Resource, In extends CoverageFacet> 
	extends HasCoverageImpl<Out, In> implements HasSpatialCoverage<Out, In> {

	protected HasSpatialCoverageImpl(){
		super();
	}
	
	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public HasSpatialCoverageImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}

}
