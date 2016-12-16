/**
 * 
 */
package org.gcube.informationsystem.impl.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.resource.Software;
import org.gcube.informationsystem.model.entity.resource.VirtualService;
import org.gcube.informationsystem.model.relation.isrelatedto.Demands;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=Demands.NAME)
public class DemandsImpl<Out extends VirtualService, In extends Software>
		extends IsRelatedToImpl<Out, In> implements
		Demands<Out, In> {

	protected DemandsImpl(){
		super();
	}
	
	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public DemandsImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}

}
