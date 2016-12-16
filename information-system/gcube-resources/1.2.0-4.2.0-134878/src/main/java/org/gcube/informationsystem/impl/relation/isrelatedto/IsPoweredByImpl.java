/**
 * 
 */
package org.gcube.informationsystem.impl.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.resource.HostingNode;
import org.gcube.informationsystem.model.entity.resource.Software;
import org.gcube.informationsystem.model.relation.isrelatedto.IsPoweredBy;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=IsPoweredBy.NAME)
public class IsPoweredByImpl<Out extends HostingNode, In extends Software>
		extends IsRelatedToImpl<Out, In> implements
		IsPoweredBy<Out, In> {

	protected IsPoweredByImpl(){
		super();
	}
	
	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public IsPoweredByImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}

}
