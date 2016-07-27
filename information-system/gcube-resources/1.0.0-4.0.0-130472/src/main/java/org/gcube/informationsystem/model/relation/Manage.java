/**
 * 
 */
package org.gcube.informationsystem.model.relation;

import org.gcube.informationsystem.model.resource.Dataset;
import org.gcube.informationsystem.model.resource.Service;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public interface Manage<Out extends Service, In extends Dataset> 
	extends RelatedTo<Out, In> {

}
