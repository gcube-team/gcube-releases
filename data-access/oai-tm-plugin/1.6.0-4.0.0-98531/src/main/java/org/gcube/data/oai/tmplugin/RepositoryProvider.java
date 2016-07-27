/**
 * 
 */
package org.gcube.data.oai.tmplugin;

import org.gcube.data.oai.tmplugin.repository.Repository;
import org.gcube.data.oai.tmplugin.requests.Request;



/**
 * 
 * 
 * @author Fabio Simeoni
 *
 */
public interface RepositoryProvider {

	Repository newRepository(Request request);
	
}
