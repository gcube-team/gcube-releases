/**
 * 
 */
package org.gcube.informationsystem.impl.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.resource.EService;
import org.gcube.informationsystem.model.entity.resource.RunningPlugin;
import org.gcube.informationsystem.model.relation.isrelatedto.IsActivatedBy;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=IsActivatedBy.NAME)
public class IsActivatedByImpl<Out extends RunningPlugin, In extends EService>
		extends IsRelatedToImpl<Out, In> implements
		IsActivatedBy<Out, In> {
	
	protected IsActivatedByImpl(){
		super();
	}
	
	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public IsActivatedByImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}

}
