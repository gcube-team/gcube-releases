/**
 * 
 */
package org.gcube.informationsystem.model.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.isrelatedto.RunsImpl;
import org.gcube.informationsystem.model.entity.resource.EService;
import org.gcube.informationsystem.model.entity.resource.Software;
import org.gcube.informationsystem.model.relation.IsRelatedTo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#runs
 */
@JsonDeserialize(as=RunsImpl.class)
public interface Runs<Out extends EService, In extends Software> 
	extends IsRelatedTo<Out, In> {

	public static final String NAME = "Runs"; // Runs.class.getSimpleName();
	
}
