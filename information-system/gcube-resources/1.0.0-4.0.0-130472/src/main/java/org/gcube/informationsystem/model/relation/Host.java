/**
 * 
 */
package org.gcube.informationsystem.model.relation;

import org.gcube.informationsystem.model.resource.EService;
import org.gcube.informationsystem.model.resource.HostingNode;


/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public interface Host<Out extends HostingNode, In extends EService> 
	extends RelatedTo<Out, In> {
	
}
