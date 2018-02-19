/**
 * 
 */
package org.gcube.data.tr;

import java.io.File;

import org.gcube.data.tmf.api.SourceLifecycle;
import org.gcube.data.tmf.api.exceptions.InvalidRequestException;
import org.gcube.data.tmf.impl.LifecycleAdapter;
import org.gcube.data.tr.requests.BindSource;
import org.gcube.data.tr.requests.RequestBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * Extends {@link SourceLifecycle} for a {@link TreeSource}.
 * 
 * @author Fabio Simeoni
 *
 */
public class Lifecycle extends LifecycleAdapter {

	private static final long serialVersionUID = 1L;
	
	private static Logger log = LoggerFactory.getLogger(Lifecycle.class);

	private static final RequestBinder binder = new RequestBinder();
	
	
	private final TreeSource source;
	
		
	/**
	 * Creates an instance for a given {@link TreeSource}
	 * @param source the source
	 */
	public Lifecycle(TreeSource source) {
		this.source=source;
	}
	
	/**{@inheritDoc}*/
	@Override
	public void init() throws Exception {
		
		log.info("initialising source "+source.id());
		
		File location = source.environment().file(Constants.STORAGE_LOCATION);
		
		source.store().start(location);
	}
	
	/**{@inheritDoc}*/
	@Override
	public void reconfigure(Element DOMRequest) throws InvalidRequestException {
		
		log.info("reconfiguring source "+source.id());
		
		try {
			BindSource request = binder.bind(DOMRequest,BindSource.class);
			source.setMode(request.mode());
		}
		catch(Throwable t) {
			throw new InvalidRequestException(t);
		}
	}
	
	/**{@inheritDoc}*/
	@Override
	public void terminate() {
		log.info("removing source "+source.id());
		source.store().delete();
	}
	
	/**{@inheritDoc}*/
	@Override
	public void stop() {
		log.info("stopping source "+source.id()+" on container shutdown");
		source.store().stop();
	}
}
