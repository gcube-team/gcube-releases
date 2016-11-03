/**
 * 
 */
package org.gcube.informationsystem.impl.relation;

import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.IsIdentifiedBy;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@JsonTypeName(value=IsIdentifiedBy.NAME)
public class IsIdentifiedByImpl<Out extends Resource, In extends Facet> extends
	ConsistsOfImpl<Out, In> implements IsIdentifiedBy<Out, In> {

	protected IsIdentifiedByImpl(){
		super();
	}
	
	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public IsIdentifiedByImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}
	
}
