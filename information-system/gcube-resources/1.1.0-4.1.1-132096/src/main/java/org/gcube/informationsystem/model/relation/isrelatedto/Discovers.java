/**
 * 
 */
package org.gcube.informationsystem.model.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.isrelatedto.DiscoversImpl;
import org.gcube.informationsystem.model.entity.resource.EService;
import org.gcube.informationsystem.model.relation.IsRelatedTo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#discovers
 */
@JsonDeserialize(as=DiscoversImpl.class)
public interface Discovers<Out extends EService, In extends EService> 
	extends IsRelatedTo<Out, In> {

	public static final String NAME = "Discovers"; // Discovers.class.getSimpleName();
	
}
