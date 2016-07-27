/**
 * 
 */
package org.gcube.informationsystem.model.relation;

import org.gcube.informationsystem.model.resource.HostingNode;
import org.gcube.informationsystem.model.resource.Software;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public interface PoweredBy<Out extends HostingNode, In extends Software> 
	extends RelatedTo<Out, In> {

}
