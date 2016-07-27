/**
 * 
 */
package org.gcube.informationsystem.model.relation;

import org.gcube.informationsystem.model.resource.Service;
import org.gcube.informationsystem.model.resource.Software;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public interface Require<Out extends Software, In extends Service> 
	extends RelatedTo<Out, In> {

}
