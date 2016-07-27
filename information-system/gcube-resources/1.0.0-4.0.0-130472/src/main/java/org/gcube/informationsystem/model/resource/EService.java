/**
 * 
 */
package org.gcube.informationsystem.model.resource;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public interface EService extends Service {
	
	public static final String NAME = EService.class.getSimpleName();
	public static final String DESCRIPTION = "Collect Electronic Service (aka Running Service) information through the list of its facets";
	public static final String VERSION = "1.0.0";
}
