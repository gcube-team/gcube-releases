/**
 * 
 */
package org.gcube.informationsystem.model.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.isrelatedto.RequiresImpl;
import org.gcube.informationsystem.model.entity.resource.Service;
import org.gcube.informationsystem.model.entity.resource.Software;
import org.gcube.informationsystem.model.relation.IsRelatedTo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#requires
 */
@JsonDeserialize(as=RequiresImpl.class)
public interface Requires<Out extends Software, In extends Service> 
	extends IsRelatedTo<Out, In> {
	
	public static final String NAME = "Requires"; // Requires.class.getSimpleName();
	
}
