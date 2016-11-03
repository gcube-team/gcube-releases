/**
 * 
 */
package org.gcube.data.oai.tmplugin;

import org.gcube.data.oai.tmplugin.repository.BaseRepository;
import org.gcube.data.oai.tmplugin.requests.Request;
import org.gcube.data.tmf.api.exceptions.InvalidRequestException;


/**
 * @author Fabio Simeoni
 *
 */
public class ProductionProvider implements RepositoryProvider {

	/**{@inheritDoc}*/
	@Override
	public BaseRepository newRepository(Request request) throws InvalidRequestException {
		try {
			return new BaseRepository(request);
		}
		catch(Exception e) {
			throw new InvalidRequestException(e);
		}
		
	}
	
	
}
