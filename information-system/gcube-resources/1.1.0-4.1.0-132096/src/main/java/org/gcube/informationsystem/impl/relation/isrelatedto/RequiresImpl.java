/**
 * 
 */
package org.gcube.informationsystem.impl.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.resource.Service;
import org.gcube.informationsystem.model.entity.resource.Software;
import org.gcube.informationsystem.model.relation.isrelatedto.Requires;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@JsonTypeName(value=Requires.NAME)
public class RequiresImpl<Out extends Software, In extends Service>
		extends IsRelatedToImpl<Out, In> implements
		Requires<Out, In> {

	protected RequiresImpl(){
		super();
	}
	
	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public RequiresImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}

}
