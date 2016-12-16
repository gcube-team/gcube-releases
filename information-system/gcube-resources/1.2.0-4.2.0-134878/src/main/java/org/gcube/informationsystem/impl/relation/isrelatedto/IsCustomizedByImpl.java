/**
 * 
 */
package org.gcube.informationsystem.impl.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.resource.Configuration;
import org.gcube.informationsystem.model.entity.resource.Service;
import org.gcube.informationsystem.model.relation.isrelatedto.IsCustomizedBy;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=IsCustomizedBy.NAME)
public class IsCustomizedByImpl<Out extends Service, In extends Configuration>
		extends IsRelatedToImpl<Out, In> implements
		IsCustomizedBy<Out, In> {

	protected IsCustomizedByImpl(){
		super();
	}
	
	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public IsCustomizedByImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}

}
