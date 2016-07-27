/**
 * 
 */
package org.gcube.informationsystem.model.relation;

import org.gcube.informationsystem.model.resource.Configuration;
import org.gcube.informationsystem.model.resource.Service;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public interface ConfiguredBy<Out extends Service, In extends Configuration> 
	extends RelatedTo<Out, In> {

}
