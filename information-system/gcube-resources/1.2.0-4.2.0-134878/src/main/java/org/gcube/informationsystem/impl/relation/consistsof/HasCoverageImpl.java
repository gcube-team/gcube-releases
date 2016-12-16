/**
 * 
 */
package org.gcube.informationsystem.impl.relation.consistsof;

import org.gcube.informationsystem.impl.relation.ConsistsOfImpl;
import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.CoverageFacet;
import org.gcube.informationsystem.model.relation.consistsof.HasCoverage;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=HasCoverage.NAME)
public abstract class HasCoverageImpl<Out extends Resource, In extends CoverageFacet> 
	extends ConsistsOfImpl<Out, In> implements HasCoverage<Out, In> {

	protected HasCoverageImpl(){
		super();
	}
	
	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public HasCoverageImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}


}
