/**
 * 
 */
package org.gcube.informationsystem.model.relation;

import org.gcube.informationsystem.model.resource.Service;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public interface CallFor<Out extends Service, In extends Service> 
	extends RelatedTo<Out, In> {

}
