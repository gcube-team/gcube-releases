/**
 * 
 */
package org.gcube.informationsystem.model.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.isrelatedto.ManagesImpl;
import org.gcube.informationsystem.model.entity.resource.Dataset;
import org.gcube.informationsystem.model.entity.resource.Service;
import org.gcube.informationsystem.model.relation.IsRelatedTo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#manages
 */
@JsonDeserialize(as=ManagesImpl.class)
public interface Manages<Out extends Service, In extends Dataset> 
	extends IsRelatedTo<Out, In> {
	
	public static final String NAME = "Manages"; // Manages.class.getSimpleName();
	
}
