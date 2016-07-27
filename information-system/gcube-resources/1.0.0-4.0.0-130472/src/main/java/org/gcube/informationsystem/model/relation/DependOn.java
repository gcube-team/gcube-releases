/**
 * 
 */
package org.gcube.informationsystem.model.relation;

import org.gcube.informationsystem.model.resource.Software;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public interface DependOn<Out extends Software, In extends Software> 
	extends RelatedTo<Out, In> {

}
