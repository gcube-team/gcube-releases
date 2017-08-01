/**
 * 
 */
package org.gcube.informationsystem.model.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.isrelatedto.DemandsImpl;
import org.gcube.informationsystem.model.entity.resource.Software;
import org.gcube.informationsystem.model.entity.resource.VirtualService;
import org.gcube.informationsystem.model.relation.IsRelatedTo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#demands
 */
@JsonDeserialize(as=DemandsImpl.class)
public interface Demands<Out extends VirtualService, In extends Software> 
	extends IsRelatedTo<Out, In> {

	public static final String NAME = "Demands"; // Demands.class.getSimpleName();
	
}
