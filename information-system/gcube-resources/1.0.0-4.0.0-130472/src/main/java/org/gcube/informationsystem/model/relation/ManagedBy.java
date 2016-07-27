/**
 * 
 */
package org.gcube.informationsystem.model.relation;

import org.gcube.informationsystem.model.resource.HostingNode;
import org.gcube.informationsystem.model.resource.Site;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public interface ManagedBy<Out extends HostingNode, In extends Site> 
	extends RelatedTo<Out, In> {

}
