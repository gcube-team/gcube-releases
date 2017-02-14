/**
 * 
 */
package org.gcube.informationsystem.impl.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.resource.LegalBody;
import org.gcube.informationsystem.model.entity.resource.Person;
import org.gcube.informationsystem.model.relation.isrelatedto.BelongsTo;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=BelongsTo.NAME)
public class BelongsToImpl<Out extends Person, In extends LegalBody>
		extends IsRelatedToImpl<Out, In> implements
		BelongsTo<Out, In> {
	
	protected BelongsToImpl(){
		super();
	}
	
	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public BelongsToImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}

}
